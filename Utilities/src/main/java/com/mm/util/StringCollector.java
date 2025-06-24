package com.mm.util;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * <p>Immutable Collector to use when using an IntStream or{@literal Stream<Integer>} to filter text. I wrote this
 * because the {@code String.chars()} method returns an {@code IntStream}, which doesn't have a method that takes 
 * a Collector, which would look something like this:</p>
 * <pre>
 *   {@literal <R, A> R collect(Collector<? super T, A, R> collector);}
 * </pre>
 * <p>Instead, it has this clumsier method:</p>
 * <pre>
 * {@literal <R> R collect(
 *     Supplier<R> supplier,
 *     ObjIntConsumer<R> accumulator,
 *     BiConsumer<R, R> combiner);}
 * </pre>
 * <p>(This is because there are no Collectors that work with primitive types like {@code int}.)</p>
 * <p>Here's an example. To remove all characters from a String that match the specified {@code IntPredicate},
 * we can use this StringCollector to write this:</p>
 * <pre>
 *   private String customFilterOut(String input, IntPredicate cFilter) {
 *   return input.chars()                  // returns an IntStream
 *       .filter(cFilter)
 *       .boxed()                          // converts IntStream to a{@literal Stream<Integer>}
 *       .collect(StringCollector.instance());
 *   }
 * </pre>
 * 
 * <p>Without this StringCollector, it's still doable, but it's more verbose, clumsier, and harder to remember:</p>
 * 
 * <pre>
 *   public static String customFilterOut(String input, IntPredicate cFilter) {
 *   return input.chars()
 *       .filter(cFilter)
 *       .collect(
 *           StringBuilder::new,
 *           (sb, i) -> sb.append((char) i),
 *           (sb1, sb2) -> sb1.append(sb2.toString()) )
 *       .toString();
 *   }
 * </pre>
 * 
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 6/22/22</p>
 * <p>Time: 5:38 PM</p>
 *
 * @author Miguel Muñoz
 * @see #filterString(String, IntPredicate) 
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"unused", "UnnecessaryUnicodeEscape"})
public class StringCollector implements Collector<Integer, StringBuilder, String> {
  private final Supplier<StringBuilder> supplier = StringBuilder::new;
  private final BiConsumer<StringBuilder, Integer> accumulator = (sb, i) -> sb.append((char)i.intValue());
  private final BinaryOperator<StringBuilder> combiner = (t, u) -> t.append(u.toString());
  private final Function<StringBuilder, String> finisher = StringBuilder::toString;
  private final Set<Characteristics> characteristics = Collections.emptySet();
  
  private static final StringCollector instance = new StringCollector();

  @Override
  public Supplier<StringBuilder> supplier() {
    return supplier;
  }

  @Override
  public BiConsumer<StringBuilder, Integer> accumulator() {
    return accumulator;
  }

  @Override
  public BinaryOperator<StringBuilder> combiner() {
    return combiner;
  }

  @Override
  public Function<StringBuilder, String> finisher() {
    return finisher;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return characteristics;
  }
  
  public static StringCollector instance() { return instance; }

  /**
   * <p>Filter out specified characters in a String based on the specified IntPredicate,
   * using a sequential stream. For example, to remove all white space from a String, you would write this:</p>
   * <pre>
   *   String input = ...
   *   String cleanedInput = StringCollector.filterString(input,{@literal ((Predicate<Character>)} Character::isWhitespace).negate())
   * </pre>
   * <p>or</p>
   * <pre>
   *   String input = ...
   *   String cleanedInput = StringCollector.filterString(input, not(Character::isWhitespace))
   * </pre>
   * @param input The input String
   * @param filter Determines which characters to keep. This should return false for characters to reject.
   * @return A new String with only the characters that matched the provided {@code filter}
   */
  public static String filterString(String input, IntPredicate filter) {
    return input
        .chars()
        .sequential()
        .filter(filter)
        .boxed()
        .collect(instance);
  }

  /**
   * <p>Remaps all characters matching the predicate to the specified replacement character</p>
   * <p>For example, to replace all newline characters with spaces, you can do this:</p>
   * <pre>
   *   public static String replaceNewLineWithSpace(String input) {
   *     return reMapString(input, c-> (c=='\n') || (c=='\r'), ' ');
   *   }
   * </pre>
   * @param input The String to remap
   * @param filter The predicate filter. Characters for which this returns true get remapped.
   * @param replacement The replacement character
   * @return The remapped String
   */
  public static String reMapString(String input, IntPredicate filter, char replacement) {
    return input
        .chars()
        .sequential()
        .map(i -> filter.test(i) ? replacement : i)
        .boxed()
        .collect(instance);
  }
}
