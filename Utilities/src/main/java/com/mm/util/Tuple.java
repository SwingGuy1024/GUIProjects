package com.mm.util;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/7/14
 * <p>Time: 10:27 AM
 *
 * <p>
 * Generic Tuple with equals method and hashCode. Whenever you need to group two objects together into
 * a single class, you may use Tuple to do so. Unlike the StrictTuple class, this class accepts null values.
 * </p><p>
 * This class has equals() and hashCode() methods that are consistent with each other, so instances are suitable
 * for use as a hash key for HashSet and HashMap.
 * </p><p>
 * In practice, this class should always be sub-classed for type-safety, like this:
 * </p><pre>
 *     private final class PayrollEmployeeCheck extends {@literal Tuple<PayrollEmployee, PayrollCheck>} {
 *         PayrollEmployeeCheck(final PayrollEmployee pe, final PayrollCheck pc) {
 *             super(pe, pc);
 *         }
 *     }
 * </pre><p>
 * This way, the {@code getClass() == obj.getClass()} comparison in the {@code equals()} method can't get
 * fooled by other subclasses of Tuple. This class has a protected constructor to enforce this rule.
 * </p><p>
 * Of course, subclasses are free to use better names for the two getter methods. (This is especially advisable
 * if the two members are of the same class):
 * <pre>
 *     private final class PayrollEmployeeCheck extends {@literal Tuple<PayrollEmployee, PayrollCheck>} {
 *         PayrollEmployeeCheck(final PayrollEmployee pe, final PayrollCheck pc) {
 *             super(pe, pc);
 *         }
 *
 *         public PayrollEmployee getPayrollEmployee() { return a; }
 *         public PayrollCheck getPayrollCheck() { return getSecond(); }
 *     }
 * </pre>
 * <p>(This example shows two valid ways to implement these methods.)
 * </p><p>
 * If I ever decide to implement Comparable, I should write a ComparableTuple subclass that requires
 * both A and B to implement Comparable.
 * </p>
 * @author Miguel Mu\u00f1oz
 */
public class Tuple<A, B> {
	protected final A a;
	protected final B b;

	/**
	 * Protected constructor to force users to subclass this class. (The equals method assumes we always work
	 * with direct subclasses.
	 *
	 * @param a The a value, which may be null.
	 * @param b The b value, which may be null.
	 */
	protected Tuple(final A a, final B b) {
		this.a = a;
		this.b = b;
		// see the comment in equals() for why.
		assert getClass().getSuperclass() == Tuple.class : "Subclasses of Tuple shouldn't be subclassed further.";
	}

	public final A getFirst() { return a; }

	public final B getSecond() { return b; }

	@Override
	public boolean equals(final Object obj) {
		boolean result = false;
		// This assumes we have subclassed this class. If we instantiate this class directly, this
		// could be fooled by two instances of Tuple with conflicting type parameters. That's why the
		// constructor is protected. It also assumes any subclass of Tuple is not subclassed further,
		// which shouldn't be necessary.
		if ((obj != null) && (getClass() == obj.getClass())) {
			@SuppressWarnings("unchecked")
			Tuple<A, B> other = (Tuple<A, B>) obj;
			result = compare(a, other.a) && compare(b, other.b);
		}
		return result;
	}

	/**
	 * Compare two values
	 *
	 * @param thisValue  may be null
	 * @param otherValue may be null
	 * @return True if they are both null or if equals() returns true
	 */
	private static boolean compare(final Object thisValue, final Object otherValue) {
		boolean result;
		if (thisValue == null) {
			result = otherValue == null;
			//noinspection ObjectEqualsNull,PointlessBooleanExpression
			assert (otherValue == null) || (otherValue.equals(null) == false); // NOPMD
		} else {
			result = thisValue.equals(otherValue);
			//noinspection ObjectEqualsNull,PointlessBooleanExpression
			assert thisValue.equals(null) == false; // NOPMD
		}
		return result;
	}

	static final int BOOST = 31; // Declaring this here prevents CheckStyle problems.

	@Override
	public int hashCode() {
		return (BOOST * getHash(a)) + getHash(b);
	}

	private static int getHash(final Object obj) {
		return (obj == null) ? 0 : obj.hashCode();
	}

	@Override
	public String toString() {
		//noinspection StringConcatenation,MagicCharacter
		return "(" + a + " : " + b + ')';
	}
}
