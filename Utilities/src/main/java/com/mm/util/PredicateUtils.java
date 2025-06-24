package com.mm.util;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/21/25
 * <br>Time: 4:05 PM
 * <br>@author Miguel Muñoz</p>
 */
public enum PredicateUtils {
  ;

  /**
   * <p>Convenience method to negate a predicate that's less verbose. This way, we can write this</p>
   * <pre>
   *   stream.filter(negate(Character::isWhitespace)) ...
   * </pre>
   * <p>instead of this</p>
   * <pre>
   *  {@literal stream.filter((Predicate<Character>)} Character::isWhitespace).negate() ...
   * </pre>
   *
   * @param p A predicate
   * @return The negated predicate
   */
  public static <T> Predicate<? extends T> negate(Predicate<T> p) {
//      System.out.println("Predicate");
    return p.negate();
  }

  /**
   * <p>Convenience method to negate an {@code IntPredicate} that's less verbose. This way, we can write this</p>
   * <pre>
   *   stream.filter(negate(Character::isWhitespace)) ...
   * </pre>
   * <p>instead of this</p>
   * <pre>
   *   stream.filter((IntPredicate) Character::isWhitespace).negate() ...
   * </pre>
   *
   * @param p A predicate
   * @return The negated predicate
   */
  public static IntPredicate negateInt(IntPredicate p) {
//      System.out.println("IntPredicate");
    return p.negate();
  }
}
