import java.util.LinkedList;
import java.util.List;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 5/3/22
 * <p>Time: 2:12 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("unused")
public class Observable<T>{
  private final Object LOCK = new Object();
  private boolean changed = false;
  private final List<Observer<T>> obs;
  @SuppressWarnings({"unchecked", "ZeroLengthArrayAllocation"})
  private final Observer<T>[] EMPTY_ARRAY = (Observer<T>[]) new Observer<?>[0];

  /**
   * Construct an Observable with zero Observers.
   */

  public Observable() {
    obs = new LinkedList<>();
  }

  /**
   * Adds an observer to the set of observers for this object, provided
   * that it is not the same as some observer already in the set.
   * The order in which notifications will be delivered to multiple
   * observers is not specified. See the class comment.
   *
   * @param o an observer to be added.
   * @throws NullPointerException if the parameter o is null.
   */
  @SuppressWarnings("ProhibitedExceptionThrown")
  public void addObserver(Observer<T> o) {
    synchronized (LOCK) {
      if (o == null) {
        throw new NullPointerException("Observer");
      }
      if (!obs.contains(o)) {
        obs.add(o);
      }
    }
  }

  /**
   * Deletes an observer from the set of observers of this object.
   * Passing {@code null} to this method will have no effect.
   *
   * @param o the observer to be deleted.
   */
  public void deleteObserver(Observer<T> o) {
    synchronized (LOCK) {
      obs.remove(o);
    }
  }

//  /**
//   * If this object has changed, as indicated by the
//   * {@code hasChanged} method, then notify all of its observers
//   * and then call the {@code clearChanged} method to
//   * indicate that this object has no longer changed.
//   * <p>
//   * Each observer has its {@code update} method called with two
//   * arguments: this observable object and {@code null}. In other
//   * words, this method is equivalent to:
//   * <blockquote><tt>
//   * notifyObservers(null)</tt></blockquote>
//   *
//   * @see Observable#clearChanged()
//   * @see Observable#hasChanged()
//   * @see Observer#update(Observable, java.lang.Object)
//   */
//  public void notifyObservers() {
//    notifyObservers(null);
//  }
//
  /**
   * If this object has changed, as indicated by the
   * {@code hasChanged} method, then notify all of its observers
   * and then call the {@code clearChanged} method to indicate
   * that this object has no longer changed.
   * <p>
   * Each observer has its {@code update} method called with two
   * arguments: this observable object and the {@code arg} argument.
   *
   * @param arg any object.
   * @see Observable#clearChanged()
   * @see Observable#hasChanged()
   * @see Observer#update(Observable, java.lang.Object)
   */
  public void notifyObservers(T arg) {
    /*
     * a temporary array buffer, used as a snapshot of the state of
     * current Observers.
     */
    Observer<T>[] arrLocal;

    synchronized (LOCK) {
      /* We don't want the Observer doing callbacks into
       * arbitrary code while holding its own Monitor.
       * The code where we extract each Observable from
       * the Vector and store the state of the Observer
       * needs synchronization, but notifying observers
       * does not (should not).  The worst result of any
       * potential race-condition here is that:
       * 1) a newly-added Observer will miss a
       *   notification in progress
       * 2) a recently unregistered Observer will be
       *   wrongly notified when it doesn't care
       */
      if (!changed) {
        return;
      }
      arrLocal = obs.toArray(EMPTY_ARRAY);
      clearChanged();
    }

    for (int i = arrLocal.length - 1; i >= 0; i--) {
      arrLocal[i].update(this, arg);
    }
  }

  /**
   * Clears the observer list so that this object no longer has any observers.
   */
  public void deleteObservers() {
    synchronized (LOCK) {
      obs.clear();
    }
  }

  /**
   * Marks this <tt>Observable</tt> object as having been changed; the
   * <tt>hasChanged</tt> method will now return <tt>true</tt>.
   */
  protected void setChanged() {
    synchronized (LOCK) {
      changed = true;
    }
  }

  /**
   * Indicates that this object has no longer changed, or that it has
   * already notified all of its observers of its most recent change,
   * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
   * This method is called automatically by the
   * {@code notifyObservers} methods.
   *
//   * @see Observable#notifyObservers()
   * @see Observable#notifyObservers(java.lang.Object)
   */
  protected void clearChanged() {
    synchronized (LOCK) {
      changed = false;
    }
  }

  /**
   * Tests if this object has changed.
   *
   * @return {@code true} if and only if the {@code setChanged}
   * method has been called more recently than the
   * {@code clearChanged} method on this object;
   * {@code false} otherwise.
   * @see Observable#clearChanged()
   * @see Observable#setChanged()
   */
  public boolean hasChanged() {
    synchronized (LOCK) {
      return changed;
    }
  }

  /**
   * Returns the number of observers of this <tt>Observable</tt> object.
   *
   * @return the number of observers of this object.
   */
  public int countObservers() {
    synchronized (LOCK) {
      return obs.size();
    }
  }}
