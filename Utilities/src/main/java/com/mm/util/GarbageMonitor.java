package com.mm.util;

/*
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/7/14
 * <p>Time: 10:23 AM
 *
 * @author Miguel Mu\u00f1oz
 */


import java.io.PrintStream;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Class to watch objects get garbage collected in order to help track down memory leaks.
 * To use this, register objects using one of the {@code watchXxx()} methods. The GarbageMonitor
 * will perform a pre-defined action whenever objects are registered, and perform a separate pre-defined
 * action when those objects are garbage collected. The default action is to print messages to System.err, but
 * other actions may be defined.
 *
 * @author <a href="mailto:miguel.munoz@castandcrew.com">Miguel Mu\u00f1oz</a>
 */
public final class GarbageMonitor {
	private static final AtomicReference<ReclaimAction> RECLAIM_ACTION_REFERENCE = new AtomicReference<>();

	/**
	 * Initialize the GarbageMonitor, specifying an optional ReclaimAction. This method need not be called, and
	 * may only be called once, but must never be called a second time. If so, it will throw an IllegalStateException.
	 * If you do call this method, you must do so before the first object is registered with one of the watch
	 * methods.
	 * If you never call this, the first call to {@code watch()} will initialize the GarbageMonitor with a default
	 * ReclaimAction. Once an object is registered, you may not call this method.
	 *
	 * @param action The ReclaimAction which specifies the one behavior when an object is registered, and a second
	 *               behavior when an object has been finalized. This ReclaimAction may be null, in which case
	 *               the default ReclaimAction is used. The default action prints messages to System.err.
	 * @see DefaultReclaimAction
	 */
	public static void initialize(@Nullable final ReclaimAction action) {
		@SuppressWarnings("BooleanVariableAlwaysNegated")
		boolean success;
		if (action == null) {
			success = RECLAIM_ACTION_REFERENCE.compareAndSet(null, new DefaultReclaimAction());
		} else {
			success = RECLAIM_ACTION_REFERENCE.compareAndSet(null, action);
		}

		if (!success) {
			//noinspection StringConcatenation
			throw new IllegalStateException("Multiple initialization of GarbageMonitor: " + RECLAIM_ACTION_REFERENCE.get().getClass());
		}
		MonitorInstance.MONITOR_INSTANCE.launch(); // forces load of MonitorInstance inner class.
	}

	private static void initializeIfNeeded() {
		if (RECLAIM_ACTION_REFERENCE.get() == null) {
			initialize(null);
		}
	}

	private GarbageMonitor() {
		assert false : "Never instantiate!";
	}

	/**
	 * Watch (register) the specified object, using the default name. The default name will be the full
	 * name of the class, followed by a unique integer. Note that integers are unique across all objects.
	 * So if you watch one Grape, followed by two Figs, followed by one more Grape, their default names will be
	 * Grape 1, Fig 2, Fig 3, Grape 4.
	 *
	 * @param object The object to watch.
	 */
	public static void watch(final Object object) {
		initializeIfNeeded();
		NamedPhantom reference = NamedPhantom.createNamedPhantom(object, null, null);
		MonitorInstance.MONITOR_INSTANCE.watchObject(reference);
	}

	/**
	 * Watch (register) the specified object, identifying it by the specified name.
	 *
	 * @param referent The object to watch
	 * @param name     The name of the object
	 */
	public static void watch(final Object referent, final String name) {
		initializeIfNeeded();
		NamedPhantom reference = NamedPhantom.createNamedPhantom(referent, name, null);
		MonitorInstance.MONITOR_INSTANCE.watchObject(reference);
	}

	/**
	 * Watch (register) the specified object, with the specified tag appended to the default name. So if watching an
	 * ActionListener, with the tag "Green", after registering 31 prior objects, the name would be
	 * java.awt.event.ActionListener 32 Green.
	 *
	 * @param object The object to watch.
	 * @param tag    The tag to be appended to the default name.
	 */
	public static void watchTagged(final Object object, final String tag) {
		initializeIfNeeded();
		NamedPhantom reference = NamedPhantom.createNamedPhantom(object, null, tag);
		MonitorInstance.MONITOR_INSTANCE.watchObject(reference);
	}

	/**
	 * Returns an iterator of all the currently registered objects that have not been
	 * reclaimed by the garbage collector. This iterator does not allow you to delete objects.
	 *
	 * @return An Iterator of all registered objects that have not yet been reclaimed.
	 */
	public static Iterator<NamedPhantom> getActiveObjects() {
		return MonitorInstance.MONITOR_INSTANCE.iterator();
	}

	/**
	 * Prints a list of the names of all currently registered objects that have not yet been reclaimed.
	 */
	public static void printActiveObjects(final PrintStream printer) {
		printer.println("GarbageMonitor: Active Objects:");
		for (final NamedPhantom phantom : MonitorInstance.MONITOR_INSTANCE) {
			printer.println("  " + phantom.name);
		}
	}

	public static final class NamedPhantom extends PhantomReference<Object> {
		private static final AtomicInteger COUNT = new AtomicInteger(0);
		private static final ReferenceQueue<Object> QUEUE = new ReferenceQueue<>();
		private final String name;

		private NamedPhantom(final Object referent, final String name) {
			super(referent, QUEUE);
			this.name = name;
		}

		private NamedPhantom(final Object referent) {
			this(referent, makeDefaultName(referent));
		}

		private NamedPhantom(final String tag, final Object referent) {
			this(referent, makeDefaultName(referent) + ' ' + tag);
		}

		private static String makeDefaultName(final Object referent) {
			return referent.getClass().getName() + ' ' + COUNT.incrementAndGet();
		}

		/**
		 * pmd checks don't like me to call a private constructor from outside this class, which is a stupid
		 * rule, but this factory method gets around that. Specify just a referent, or use a name or tag, but not both.
		 *
		 * @param referent The object to watch
		 * @param name     The name of the object, which may be null. If the name is not null, the name will
		 *                 be used and the tag will be ignored.
		 * @param tag      The tag to be appended to the default name, which may be null.
		 * @return A NamedPhantom
		 */
		private static NamedPhantom createNamedPhantom(final Object referent, @Nullable final String name, final @Nullable String tag) {
			NamedPhantom phantom;
			if (name == null) {
				if (tag == null) {
					phantom = new NamedPhantom(referent);
				} else {
					phantom = new NamedPhantom(tag, referent);
				}
			} else {
				phantom = new NamedPhantom(referent, name);
			}
			return phantom;
		}

		@Override
		public String toString() {
			return this.name;
		}

		public String getName() { return name; }
	}

	@SuppressWarnings("Singleton")
	private static final class MonitorInstance implements Runnable, Iterable<NamedPhantom> {
		private static final MonitorInstance MONITOR_INSTANCE = new MonitorInstance();
		private final Thread reclaimThread;

		private MonitorInstance() {
			reclaimThread = new Thread(this);
			reclaimThread.setDaemon(true);
			// Normally you shouldn't start a thread in a constructor, because it makes it hard to write a subclass.
			// But this class can't be subclassed -- it's private and final --, so this is okay.
			reclaimThread.start();
		}

		// The watchedObjects Set exists mainly to keep the PhantomReferences from getting garbage collected,
		// but may also be used to examine the currently active registered objects.
		// We use a CopyOnWriteArraySet because its iterator is fail-safe in that it iterates over a snapshot
		// of the current set. Adding new objects during iteration won't cause an exception, but they also won't
		// show up in the iteration.
		private final Set<NamedPhantom> watchedObjects = new CopyOnWriteArraySet<>();

		@Override
		public void run() {
			try {
				loop();
			} catch (final RuntimeException | Error e) {
				RECLAIM_ACTION_REFERENCE.get().reportProblem(e);
			}
		}

		private void loop() {
			assert Thread.currentThread().isDaemon();
			ReferenceQueue<Object> queue = NamedPhantom.QUEUE;
			//noinspection InfiniteLoopStatement
			while (true) {
				try {
					// Block while the queue is empty...
					NamedPhantom reference = (NamedPhantom) queue.remove(); // blocks!
					watchedObjects.remove(reference);
					RECLAIM_ACTION_REFERENCE.get().isReclaimed(reference);
					// CHECKSTYLE:OFF
				} catch (final InterruptedException ignored) {
					/* Empty block fails in CheckStyle */
				}
				// CHECKSTYLE:ON
			}
		}

		@Override
		public @NotNull Iterator<NamedPhantom> iterator() {
			final Iterator<NamedPhantom> iterator = MonitorInstance.MONITOR_INSTANCE.watchedObjects.iterator();
			return new Iterator<NamedPhantom>() {
				@Override
				public boolean hasNext() { return iterator.hasNext(); }

				@Override
				public NamedPhantom next() { return iterator.next(); }

				@Override
				public void remove() { throw new UnsupportedOperationException("Cannot be removed."); }
			};
		}

		public void watchObject(final NamedPhantom reference) {
			MonitorInstance.MONITOR_INSTANCE.watchedObjects.add(reference);
			RECLAIM_ACTION_REFERENCE.get().isCreated(reference);
		}

		/**
		 * launch simply forces the class to get loaded. Loading this class instantiates the GarbageMonitor.
		 */
		private void launch() { /* empty */ }
	}

	/**
	 * Default implementation of the ReclaimAction. This prints the name of the object to System.err,
	 * like this: GarbageMonitor: Reclaimed {@literal <object_name>} where {@literal <object_name>} is the
	 * name of the object specified when the object was created, or is the default name if none was specified.
	 */
	public static class DefaultReclaimAction implements ReclaimAction {
		@Override
		public void isReclaimed(final NamedPhantom reference) {
			PrintStream err = System.err;
			err.printf("GarbageMonitor: Reclaimed %s -- %d remaining%n", reference.name, MonitorInstance.MONITOR_INSTANCE.watchedObjects.size());
		}

		@Override
		public void isCreated(final NamedPhantom reference) {
			PrintStream err = System.err;
			err.printf("GarbageMonitor:  Watching %s -- %d remaining%n", reference.name, MonitorInstance.MONITOR_INSTANCE.watchedObjects.size());
		}

		@Override
		public void reportProblem(final Throwable problem) {
			problem.printStackTrace(System.err);
		}
	}

	/**
	 * The action to take when an object has been finalized and is ready to be reclaimed.
	 */
	public interface ReclaimAction {
		void isReclaimed(NamedPhantom reference);

		void isCreated(NamedPhantom reference);

		void reportProblem(Throwable problem);
	}
}
