package com.mm.util;

import java.util.function.BiPredicate;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/21/25
 * <br>Time: 4:05 PM
 * <br>@author Miguel Muñoz</p>
 */
@SuppressWarnings("unused")
public enum PredicateUtils {
  ;

  /**
   * <p>Convenience method to negate a predicate that's less verbose. This is primarily useful when you're passing a
   * function pointer to a method that takes a predicate, and you want to negate the function. This way, we can write
   * this</p>
   * <pre>
   *   stream.filter(negate(Character::isWhitespace)) ...
   * </pre>
   * <p>instead of this</p>
   * <pre>
   *   {@literal stream.filter(((Predicate<Character>) Character::isWhitespace).negate())} ...
   * </pre>
   * <p>or this</p>
   * <pre>
   *   stream.filter(c -> ! Character.isWhitespace(c)) ...
   * </pre>
   *
   * @param p A predicate
   * @return The negated predicate
   */
  public static <T> Predicate<? extends T> negate(Predicate<T> p) {
    return p.negate();
  }

  /**
   * <p>Convenience method to negate an {@code IntPredicate} that's less verbose. This way, we can write this</p>
   * <pre>
   *   stream.filter(negateInt(i -> isValid(i))) ...
   * </pre>
   * <p>instead of this</p>
   * <pre>
   *   stream.filter(((IntPredicate) i -> isValid(i)).negate()) ...
   * </pre>
   *
   * @param p A predicate
   * @return The negated predicate
   */
  public static IntPredicate negateInt(IntPredicate p) {
    return p.negate();
  }

  /**
   * <p>Convenience method to negate a {@code LongPredicate} that's less verbose. This way, we can write this</p>
   * <pre>
   *   stream.filter(negateLong(l -> exceedsThreshold(l))) ...
   * </pre>
   * <p>instead of this</p>
   * <pre>
   *   stream.filter(((LongPredicate) l -> exceedsThreshold(l)).negate()) ...
   * </pre>
   * @param p A LongPredicate
   * @return The negated predicate
   */
  public static LongPredicate negateLong(LongPredicate p) {
    return p.negate();
  }

  /**
   * <p>Convenience method to negate a {@code DoublePredicate} that's less verbose. This way, we can write this</p>
   * <pre>
   *   stream.filter(negateDouble(d) -> isValid(d))) ...
   * </pre>
   * <p>instead of this</p>
   * <pre>
   *   stream.filter(((DoublePredicate) (d) -> isValid(d)).negate()) ...
   * </pre>
   */
  public static DoublePredicate negateDouble(DoublePredicate p) {
    return p.negate();
  }

  /**
   * <p>Convenience method to negate a {@code BiPredicate} that's less verbose. This way, we can write this</p>
   * <pre>
   *   stream.filter(negateBi((a, b) -> isValid(a, b))) ...
   * </pre>
   * <p>instead of this</p>
   * <pre>
   *   stream.filter(((BiPredicate) (a, b) -> isValid(a, b)).negate()) ...
   * </pre>
   */
  public static BiPredicate<?, ?> negateBi(BiPredicate<?, ?> p) {
    return p.negate();
  }
}
