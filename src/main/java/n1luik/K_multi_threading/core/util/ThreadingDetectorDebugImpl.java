    /*
     * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
     *
     * This code is free software; you can redistribute it and/or modify it
     * under the terms of the GNU General Public License version 2 only, as
     * published by the Free Software Foundation.  Oracle designates this
     * particular file as subject to the "Classpath" exception as provided
     * by Oracle in the LICENSE file that accompanied this code.
     *
     * This code is distributed in the hope that it will be useful, but WITHOUT
     * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
     * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
     * version 2 for more details (a copy is included in the LICENSE file that
     * accompanied this code).
     *
     * You should have received a copy of the GNU General Public License version
     * 2 along with this work; if not, write to the Free Software Foundation,
     * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
     *
     * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
     * or visit www.oracle.com if you need additional information or have any
     * questions.
     */

    /*
     * This file is available under and governed by the GNU General Public
     * License version 2 only, as published by the Free Software Foundation.
     * However, the following notice accompanied the original version of this
     * file:
     *
     * Written by Doug Lea with assistance from members of JCP JSR-166
     * Expert Group and released to the public domain, as explained at
     * http://creativecommons.org/publicdomain/zero/1.0/
     */
package n1luik.K_multi_threading.core.util;

import sun.misc.Unsafe;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

    public interface ThreadingDetectorDebugImpl {
    String K_multi_threading$debugText();
    default Semaphore K_multi_threading$create(int $$$i$$$){

/**
 * Provides a framework for implementing blocking locks and related
 * synchronizers (semaphores, events, etc) that rely on
 * first-in-first-out (FIFO) wait queues.  This class is designed to
 * be a useful basis for most kinds of synchronizers that rely on a
 * single atomic {@code int} value to represent state. Subclasses
 * must define the protected methods that change this state, and which
 * define what that state means in terms of this object being acquired
 * or released.  Given these, the other methods in this class carry
 * out all queuing and blocking mechanics. Subclasses can maintain
 * other state fields, but only the atomically updated {@code int}
 * value manipulated using methods {@link #getState}, {@link
 * #setState} and {@link #compareAndSetState} is tracked with respect
 * to synchronization.
 *
 * <p>Subclasses should be defined as non-public internal helper
 * classes that are used to implement the synchronization properties
 * of their enclosing class.  Class
 * {@code AbstractQueuedSynchronizer} does not implement any
 * synchronization interface.  Instead it defines methods such as
 * {@link #acquireInterruptibly} that can be invoked as
 * appropriate by concrete locks and related synchronizers to
 * implement their public methods.
 *
 * <p>This class supports either or both a default <em>exclusive</em>
 * mode and a <em>shared</em> mode. When acquired in exclusive mode,
 * attempted acquires by other threads cannot succeed. Shared mode
 * acquires by multiple threads may (but need not) succeed. This class
 * does not &quot;understand&quot; these differences except in the
 * mechanical sense that when a shared mode acquire succeeds, the next
 * waiting thread (if one exists) must also determine whether it can
 * acquire as well. Threads waiting in the different modes share the
 * same FIFO queue. Usually, implementation subclasses support only
 * one of these modes, but both can come into play for example in a
 * {@link ReadWriteLock}. Subclasses that support only exclusive or
 * only shared modes need not define the methods supporting the unused mode.
 *
 * <p>This class defines a nested {@link ConditionObject} class that
 * can be used as a {@link Condition} implementation by subclasses
 * supporting exclusive mode for which method {@link
 * #isHeldExclusively} reports whether synchronization is exclusively
 * held with respect to the current thread, method {@link #release}
 * invoked with the current {@link #getState} value fully releases
 * this object, and {@link #acquire}, given this saved state value,
 * eventually restores this object to its previous acquired state.  No
 * {@code AbstractQueuedSynchronizer} method otherwise creates such a
 * condition, so if this constraint cannot be met, do not use it.  The
 * behavior of {@link ConditionObject} depends of course on the
 * semantics of its synchronizer implementation.
 *
 * <p>This class provides inspection, instrumentation, and monitoring
 * methods for the internal queue, as well as similar methods for
 * condition objects. These can be exported as desired into classes
 * using an {@code AbstractQueuedSynchronizer} for their
 * synchronization mechanics.
 *
 * <p>Serialization of this class stores only the underlying atomic
 * integer maintaining state, so deserialized objects have empty
 * thread queues. Typical subclasses requiring serializability will
 * define a {@code readObject} method that restores this to a known
 * initial state upon deserialization.
 *
 * <h2>Usage</h2>
 *
 * <p>To use this class as the basis of a synchronizer, redefine the
 * following methods, as applicable, by inspecting and/or modifying
 * the synchronization state using {@link #getState}, {@link
 * #setState} and/or {@link #compareAndSetState}:
 *
 * <ul>
 * <li>{@link #tryAcquire}
 * <li>{@link #tryRelease}
 * <li>{@link #tryAcquireShared}
 * <li>{@link #tryReleaseShared}
 * <li>{@link #isHeldExclusively}
 * </ul>
 *
 * Each of these methods by default throws {@link
 * UnsupportedOperationException}.  Implementations of these methods
 * must be internally thread-safe, and should in general be short and
 * not block. Defining these methods is the <em>only</em> supported
 * means of using this class. All other methods are declared
 * {@code final} because they cannot be independently varied.
 *
 * <p>You may also find the inherited methods from {@link
 * AbstractOwnableSynchronizer} useful to keep track of the thread
 * owning an exclusive synchronizer.  You are encouraged to use them
 * -- this enables monitoring and diagnostic tools to assist users in
 * determining which threads hold locks.
 *
 * <p>Even though this class is based on an internal FIFO queue, it
 * does not automatically enforce FIFO acquisition policies.  The core
 * of exclusive synchronization takes the form:
 *
 * <pre>
 * <em>Acquire:</em>
 *     while (!tryAcquire(arg)) {
 *        <em>enqueue thread if it is not already queued</em>;
 *        <em>possibly block current thread</em>;
 *     }
 *
 * <em>Release:</em>
 *     if (tryRelease(arg))
 *        <em>unblock the first queued thread</em>;
 * </pre>
 *
 * (Shared mode is similar but may involve cascading signals.)
 *
 * <p id="barging">Because checks in acquire are invoked before
 * enqueuing, a newly acquiring thread may <em>barge</em> ahead of
 * others that are blocked and queued.  However, you can, if desired,
 * define {@code tryAcquire} and/or {@code tryAcquireShared} to
 * disable barging by internally invoking one or more of the inspection
 * methods, thereby providing a <em>fair</em> FIFO acquisition order.
 * In particular, most fair synchronizers can define {@code tryAcquire}
 * to return {@code false} if {@link #hasQueuedPredecessors} (a method
 * specifically designed to be used by fair synchronizers) returns
 * {@code true}.  Other variations are possible.
 *
 * <p>Throughput and scalability are generally highest for the
 * default barging (also known as <em>greedy</em>,
 * <em>renouncement</em>, and <em>convoy-avoidance</em>) strategy.
 * While this is not guaranteed to be fair or starvation-free, earlier
 * queued threads are allowed to recontend before later queued
 * threads, and each recontention has an unbiased chance to succeed
 * against incoming threads.  Also, while acquires do not
 * &quot;spin&quot; in the usual sense, they may perform multiple
 * invocations of {@code tryAcquire} interspersed with other
 * computations before blocking.  This gives most of the benefits of
 * spins when exclusive synchronization is only briefly held, without
 * most of the liabilities when it isn't. If so desired, you can
 * augment this by preceding calls to acquire methods with
 * "fast-path" checks, possibly prechecking {@link #hasContended}
 * and/or {@link #hasQueuedThreads} to only do so if the synchronizer
 * is likely not to be contended.
 *
 * <p>This class provides an efficient and scalable basis for
 * synchronization in part by specializing its range of use to
 * synchronizers that can rely on {@code int} state, acquire, and
 * release parameters, and an internal FIFO wait queue. When this does
 * not suffice, you can build synchronizers from a lower level using
 * {@link java.util.concurrent.atomic atomic} classes, your own custom
 * {@link java.util.Queue} classes, and {@link LockSupport} blocking
 * support.
 *
 * <h2>Usage Examples</h2>
 *
 * <p>Here is a non-reentrant mutual exclusion lock class that uses
 * the value zero to represent the unlocked state, and one to
 * represent the locked state. While a non-reentrant lock
 * does not strictly require recording of the current owner
 * thread, this class does so anyway to make usage easier to monitor.
 * It also supports conditions and exposes some instrumentation methods:
 *
 * <pre> {@code
 * class Mutex implements Lock, java.io.Serializable {
 *
 *   // Our internal helper class
 *   private static class Sync extends AbstractQueuedSynchronizer {
 *     // Acquires the lock if state is zero
 *     public boolean tryAcquire(int acquires) {
 *       assert acquires == 1; // Otherwise unused
 *       if (compareAndSetState(0, 1)) {
 *         setExclusiveOwnerThread(Thread.currentThread());
 *         return true;
 *       }
 *       return false;
 *     }
 *
 *     // Releases the lock by setting state to zero
 *     protected boolean tryRelease(int releases) {
 *       assert releases == 1; // Otherwise unused
 *       if (!isHeldExclusively())
 *         throw new IllegalMonitorStateException();
 *       setExclusiveOwnerThread(null);
 *       setState(0);
 *       return true;
 *     }
 *
 *     // Reports whether in locked state
 *     public boolean isLocked() {
 *       return getState() != 0;
 *     }
 *
 *     public boolean isHeldExclusively() {
 *       // a data race, but safe due to out-of-thin-air guarantees
 *       return getExclusiveOwnerThread() == Thread.currentThread();
 *     }
 *
 *     // Provides a Condition
 *     public Condition newCondition() {
 *       return new ConditionObject();
 *     }
 *
 *     // Deserializes properly
 *     private void readObject(ObjectInputStream s)
 *         throws IOException, ClassNotFoundException {
 *       s.defaultReadObject();
 *       setState(0); // reset to unlocked state
 *     }
 *   }
 *
 *   // The sync object does all the hard work. We just forward to it.
 *   private final Sync sync = new Sync();
 *
 *   public void lock()              { sync.acquire(1); }
 *   public boolean tryLock()        { return sync.tryAcquire(1); }
 *   public void unlock()            { sync.release(1); }
 *   public Condition newCondition() { return sync.newCondition(); }
 *   public boolean isLocked()       { return sync.isLocked(); }
 *   public boolean isHeldByCurrentThread() {
 *     return sync.isHeldExclusively();
 *   }
 *   public boolean hasQueuedThreads() {
 *     return sync.hasQueuedThreads();
 *   }
 *   public void lockInterruptibly() throws InterruptedException {
 *     sync.acquireInterruptibly(1);
 *   }
 *   public boolean tryLock(long timeout, TimeUnit unit)
 *       throws InterruptedException {
 *     return sync.tryAcquireNanos(1, unit.toNanos(timeout));
 *   }
 * }}</pre>
 *
 * <p>Here is a latch class that is like a
 * {@link java.util.concurrent.CountDownLatch CountDownLatch}
 * except that it only requires a single {@code signal} to
 * fire. Because a latch is non-exclusive, it uses the {@code shared}
 * acquire and release methods.
 *
 * <pre> {@code
 * class BooleanLatch {
 *
 *   private static class Sync extends AbstractQueuedSynchronizer {
 *     boolean isSignalled() { return getState() != 0; }
 *
 *     protected int tryAcquireShared(int ignore) {
 *       return isSignalled() ? 1 : -1;
 *     }
 *
 *     protected boolean tryReleaseShared(int ignore) {
 *       setState(1);
 *       return true;
 *     }
 *   }
 *
 *   private final Sync sync = new Sync();
 *   public boolean isSignalled() { return sync.isSignalled(); }
 *   public void signal()         { sync.releaseShared(1); }
 *   public void await() throws InterruptedException {
 *     sync.acquireSharedInterruptibly(1);
 *   }
 * }}</pre>
 *
 * @since 1.5
 * @author Doug Lea
 */
        abstract class AbstractQueuedSynchronizer2
                extends AbstractOwnableSynchronizer
                implements java.io.Serializable {
            public abstract String debugText();

            @Serial
            private static final long serialVersionUID = 2512465972572414691L;

            /**
             * Creates a new {@code AbstractQueuedSynchronizer} instance
             * with initial synchronization state of zero.
             */
            protected AbstractQueuedSynchronizer2() { }
            /*
             * Overview.
             *
             * The wait queue is a variant of a "CLH" (Craig, Landin, and
             * Hagersten) lock queue. CLH locks are normally used for
             * spinlocks.  We instead use them for blocking synchronizers by
             * including explicit ("prev" and "next") links plus a "status"
             * field that allow nodes to signal successors when releasing
             * locks, and handle cancellation due to interrupts and timeouts.
             * The status field includes bits that track whether a thread
             * needs a signal (using LockSupport.unpark). Despite these
             * additions, we maintain most CLH locality properties.
             *
             * To enqueue into a CLH lock, you atomically splice it in as new
             * tail. To dequeue, you set the head field, so the next eligible
             * waiter becomes first.
             *
             *  +------+  prev +-------+       +------+
             *  | head | <---- | first | <---- | tail |
             *  +------+       +-------+       +------+
             *
             * Insertion into a CLH queue requires only a single atomic
             * operation on "tail", so there is a simple point of demarcation
             * from unqueued to queued. The "next" link of the predecessor is
             * set by the enqueuing thread after successful CAS. Even though
             * non-atomic, this suffices to ensure that any blocked thread is
             * signalled by a predecessor when eligible (although in the case
             * of cancellation, possibly with the assistance of a signal in
             * method cleanQueue). Signalling is based in part on a
             * Dekker-like scheme in which the to-be waiting thread indicates
             * WAITING status, then retries acquiring, and then rechecks
             * status before blocking. The signaller atomically clears WAITING
             * status when unparking.
             *
             * Dequeuing on acquire involves detaching (nulling) a node's
             * "prev" node and then updating the "head". Other threads check
             * if a node is or was dequeued by checking "prev" rather than
             * head. We enforce the nulling then setting order by spin-waiting
             * if necessary. Because of this, the lock algorithm is not itself
             * strictly "lock-free" because an acquiring thread may need to
             * wait for a previous acquire to make progress. When used with
             * exclusive locks, such progress is required anyway. However
             * Shared mode may (uncommonly) require a spin-wait before
             * setting head field to ensure proper propagation. (Historical
             * note: This allows some simplifications and efficiencies
             * compared to previous versions of this class.)
             *
             * A node's predecessor can change due to cancellation while it is
             * waiting, until the node is first in queue, at which point it
             * cannot change. The acquire methods cope with this by rechecking
             * "prev" before waiting. The prev and next fields are modified
             * only via CAS by cancelled nodes in method cleanQueue. The
             * unsplice strategy is reminiscent of Michael-Scott queues in
             * that after a successful CAS to prev field, other threads help
             * fix next fields.  Because cancellation often occurs in bunches
             * that complicate decisions about necessary signals, each call to
             * cleanQueue traverses the queue until a clean sweep. Nodes that
             * become relinked as first are unconditionally unparked
             * (sometimes unnecessarily, but those cases are not worth
             * avoiding).
             *
             * A thread may try to acquire if it is first (frontmost) in the
             * queue, and sometimes before.  Being first does not guarantee
             * success; it only gives the right to contend. We balance
             * throughput, overhead, and fairness by allowing incoming threads
             * to "barge" and acquire the synchronizer while in the process of
             * enqueuing, in which case an awakened first thread may need to
             * rewait.  To counteract possible repeated unlucky rewaits, we
             * exponentially increase retries (up to 256) to acquire each time
             * a thread is unparked. Except in this case, AQS locks do not
             * spin; they instead interleave attempts to acquire with
             * bookkeeping steps. (Users who want spinlocks can use
             * tryAcquire.)
             *
             * To improve garbage collectibility, fields of nodes not yet on
             * list are null. (It is not rare to create and then throw away a
             * node without using it.) Fields of nodes coming off the list are
             * nulled out as soon as possible. This accentuates the challenge
             * of externally determining the first waiting thread (as in
             * method getFirstQueuedThread). This sometimes requires the
             * fallback of traversing backwards from the atomically updated
             * "tail" when fields appear null. (This is never needed in the
             * process of signalling though.)
             *
             * CLH queues need a dummy header node to get started. But
             * we don't create them on construction, because it would be wasted
             * effort if there is never contention. Instead, the node
             * is constructed and head and tail pointers are set upon first
             * contention.
             *
             * Shared mode operations differ from Exclusive in that an acquire
             * signals the next waiter to try to acquire if it is also
             * Shared. The tryAcquireShared API allows users to indicate the
             * degree of propagation, but in most applications, it is more
             * efficient to ignore this, allowing the successor to try
             * acquiring in any case.
             *
             * Threads waiting on Conditions use nodes with an additional
             * link to maintain the (FIFO) list of conditions. Conditions only
             * need to link nodes in simple (non-concurrent) linked queues
             * because they are only accessed when exclusively held.  Upon
             * await, a node is inserted into a condition queue.  Upon signal,
             * the node is enqueued on the main queue.  A special status field
             * value is used to track and atomically trigger this.
             *
             * Accesses to fields head, tail, and state use full Volatile
             * mode, along with CAS. Node fields status, prev and next also do
             * so while threads may be signallable, but sometimes use weaker
             * modes otherwise. Accesses to field "waiter" (the thread to be
             * signalled) are always sandwiched between other atomic accesses
             * so are used in Plain mode. We use jdk.internal Unsafe versions
             * of atomic access methods rather than VarHandles to avoid
             * potential VM bootstrap issues.
             *
             * Most of the above is performed by primary internal method
             * acquire, that is invoked in some way by all exported acquire
             * methods.  (It is usually easy for compilers to optimize
             * call-site specializations when heavily used.)
             *
             * There are several arbitrary decisions about when and how to
             * check interrupts in both acquire and await before and/or after
             * blocking. The decisions are less arbitrary in implementation
             * updates because some users appear to rely on original behaviors
             * in ways that are racy and so (rarely) wrong in general but hard
             * to justify changing.
             *
             * Thanks go to Dave Dice, Mark Moir, Victor Luchangco, Bill
             * Scherer and Michael Scott, along with members of JSR-166
             * expert group, for helpful ideas, discussions, and critiques
             * on the design of this class.
             */

            // Node status bits, also used as argument and return values
            static final int WAITING   = 1;          // must be 1
            static final int CANCELLED = 0x80000000; // must be negative
            static final int COND      = 2;          // in a condition wait

            /** CLH Nodes */
            abstract static class Node {
                volatile Node prev;       // initially attached via casTail
                volatile Node next;       // visibly nonnull when signallable
                Thread waiter;            // visibly nonnull when enqueued
                volatile int status;      // written by owner, atomic bit ops by others

                // methods for atomic operations
                final boolean casPrev(Node c, Node v) {  // for cleanQueue
                    return U.compareAndSwapObject(this, PREV, c, v);
                }
                final boolean casNext(Node c, Node v) {  // for cleanQueue
                    return U.compareAndSwapObject(this, NEXT, c, v);
                }
                final int getAndUnsetStatus(int v) {     // for signalling
                    return getAndBitwiseAndInt(this, STATUS, ~v);
                }
                final void setPrevRelaxed(Node p) {      // for off-queue assignment
                    U.putObject(this, PREV, p);
                }
                final void setStatusRelaxed(int s) {     // for off-queue assignment
                    U.putInt(this, STATUS, s);
                }
                final void clearStatus() {               // for reducing unneeded signals
                    U.putIntVolatile(this, STATUS, 0);
                }

                private static final long STATUS;

                static {
                    try {
                        STATUS = U.objectFieldOffset(Node.class.getDeclaredField("status"));
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }

                private static final long NEXT;

                static {
                    try {
                        NEXT = U.objectFieldOffset(Node.class.getDeclaredField("next"));
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }

                private static final long PREV;

                static {
                    try {
                        PREV = U.objectFieldOffset(Node.class.getDeclaredField("prev"));
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // Concrete classes tagged by type
            static final class ExclusiveNode extends Node { }
            static final class SharedNode extends Node { }

            static final class ConditionNode extends Node
                    implements ForkJoinPool.ManagedBlocker {
                ConditionNode nextWaiter;            // link to next waiting node

                /**
                 * Allows Conditions to be used in ForkJoinPools without
                 * risking fixed pool exhaustion. This is usable only for
                 * untimed Condition waits, not timed versions.
                 */
                public final boolean isReleasable() {
                    return status <= 1 || Thread.currentThread().isInterrupted();
                }

                public final boolean block() {
                    while (!isReleasable()) LockSupport.park();
                    return true;
                }
            }

            /**
             * Head of the wait queue, lazily initialized.
             */
            private transient volatile Node head;

            /**
             * Tail of the wait queue. After initialization, modified only via casTail.
             */
            private transient volatile Node tail;

            /**
             * The synchronization state.
             */
            private volatile int state;

            /**
             * Returns the current value of synchronization state.
             * This operation has memory semantics of a {@code volatile} read.
             * @return current state value
             */
            protected final int getState() {
                return state;
            }

            /**
             * Sets the value of synchronization state.
             * This operation has memory semantics of a {@code volatile} write.
             * @param newState the new state value
             */
            protected final void setState(int newState) {
                state = newState;
            }

            /**
             * Atomically sets synchronization state to the given updated
             * value if the current state value equals the expected value.
             * This operation has memory semantics of a {@code volatile} read
             * and write.
             *
             * @param expect the expected value
             * @param update the new value
             * @return {@code true} if successful. False return indicates that the actual
             *         value was not equal to the expected value.
             */
            protected final boolean compareAndSetState(int expect, int update) {
                return U.compareAndSwapInt(this, STATE, expect, update);
            }

            // Queuing utilities

            private boolean casTail(Node c, Node v) {
                return U.compareAndSwapObject(this, TAIL, c, v);
            }

            /** tries once to CAS a new dummy node for head */
            private void tryInitializeHead() {
                Node h = new ExclusiveNode();
                if (U.compareAndSwapObject(this, HEAD, null, h))
                    tail = h;
            }

            /**
             * Enqueues the node unless null. (Currently used only for
             * ConditionNodes; other cases are interleaved with acquires.)
             */
            final void enqueue(Node node) {
                if (node != null) {
                    for (;;) {
                        Node t = tail;
                        node.setPrevRelaxed(t);        // avoid unnecessary fence
                        if (t == null)                 // initialize
                            tryInitializeHead();
                        else if (casTail(t, node)) {
                            t.next = node;
                            if (t.status < 0)          // wake up to clean link
                                LockSupport.unpark(node.waiter);
                            break;
                        }
                    }
                }
            }

            /** Returns true if node is found in traversal from tail */
            final boolean isEnqueued(Node node) {
                for (Node t = tail; t != null; t = t.prev)
                    if (t == node)
                        return true;
                return false;
            }

            /**
             * Wakes up the successor of given node, if one exists, and unsets its
             * WAITING status to avoid park race. This may fail to wake up an
             * eligible thread when one or more have been cancelled, but
             * cancelAcquire ensures liveness.
             */
            private static void signalNext(Node h) {
                Node s;
                if (h != null && (s = h.next) != null && s.status != 0) {
                    s.getAndUnsetStatus(WAITING);
                    LockSupport.unpark(s.waiter);
                }
            }

            /** Wakes up the given node if in shared mode */
            private static void signalNextIfShared(Node h) {
                Node s;
                if (h != null && (s = h.next) != null &&
                        (s instanceof SharedNode) && s.status != 0) {
                    s.getAndUnsetStatus(WAITING);
                    LockSupport.unpark(s.waiter);
                }
            }

            /**
             * Main acquire method, invoked by all exported acquire methods.
             *
             * @param node null unless a reacquiring Condition
             * @param arg the acquire argument
             * @param shared true if shared mode else exclusive
             * @param interruptible if abort and return negative on interrupt
             * @param timed if true use timed waits
             * @param time if timed, the System.nanoTime value to timeout
             * @return positive if acquired, 0 if timed out, negative if interrupted
             */
            final int acquire(Node node, int arg, boolean shared,
                              boolean interruptible, boolean timed, long time) {
                Thread current = Thread.currentThread();
                byte spins = 0, postSpins = 0;   // retries upon unpark of first thread
                boolean interrupted = false, first = false;
                Node pred = null;                // predecessor of node when enqueued

                /*
                 * Repeatedly:
                 *  Check if node now first
                 *    if so, ensure head stable, else ensure valid predecessor
                 *  if node is first or not yet enqueued, try acquiring
                 *  else if node not yet created, create it
                 *  else if not yet enqueued, try once to enqueue
                 *  else if woken from park, retry (up to postSpins times)
                 *  else if WAITING status not set, set and retry
                 *  else park and clear WAITING status, and check cancellation
                 */

                for (;;) {
                    if (!first && (pred = (node == null) ? null : node.prev) != null &&
                            !(first = (head == pred))) {
                        if (pred.status < 0) {
                            cleanQueue();           // predecessor cancelled
                            continue;
                        } else if (pred.prev == null) {
                            Thread.onSpinWait();    // ensure serialization
                            continue;
                        }
                    }
                    if (first || pred == null) {
                        boolean acquired;
                        try {
                            if (shared)
                                acquired = (tryAcquireShared(arg) >= 0);
                            else
                                acquired = tryAcquire(arg);
                        } catch (Throwable ex) {
                            cancelAcquire(node, interrupted, false);
                            throw ex;
                        }
                        if (acquired) {
                            if (first) {
                                node.prev = null;
                                head = node;
                                pred.next = null;
                                node.waiter = null;
                                if (shared)
                                    signalNextIfShared(node);
                                if (interrupted)
                                    current.interrupt();
                            }
                            return 1;
                        }
                    }
                    if (node == null) {                 // allocate; retry before enqueue
                        if (shared)
                            node = new SharedNode();
                        else
                            node = new ExclusiveNode();
                    } else if (pred == null) {          // try to enqueue
                        node.waiter = current;
                        Node t = tail;
                        node.setPrevRelaxed(t);         // avoid unnecessary fence
                        if (t == null)
                            tryInitializeHead();
                        else if (!casTail(t, node))
                            node.setPrevRelaxed(null);  // back out
                        else
                            t.next = node;
                    } else if (first && spins != 0) {
                        --spins;                        // reduce unfairness on rewaits
                        Thread.onSpinWait();
                    } else if (node.status == 0) {
                        node.status = WAITING;          // enable signal and recheck
                    } else {
                        long nanos;
                        spins = postSpins = (byte)((postSpins << 1) | 1);
                        if (!timed)
                            LockSupport.park(this);
                        else if ((nanos = time - System.nanoTime()) > 0L)
                            LockSupport.parkNanos(this, nanos);
                        else
                            break;
                        node.clearStatus();
                        if ((interrupted |= Thread.interrupted()) && interruptible)
                            break;
                    }
                }
                return cancelAcquire(node, interrupted, interruptible);
            }

            /**
             * Possibly repeatedly traverses from tail, unsplicing cancelled
             * nodes until none are found. Unparks nodes that may have been
             * relinked to be next eligible acquirer.
             */
            private void cleanQueue() {
                for (;;) {                               // restart point
                    for (Node q = tail, s = null, p, n;;) { // (p, q, s) triples
                        if (q == null || (p = q.prev) == null)
                            return;                      // end of list
                        if (s == null ? tail != q : (s.prev != q || s.status < 0))
                            break;                       // inconsistent
                        if (q.status < 0) {              // cancelled
                            if ((s == null ? casTail(q, p) : s.casPrev(q, p)) &&
                                    q.prev == p) {
                                p.casNext(q, s);         // OK if fails
                                if (p.prev == null)
                                    signalNext(p);
                            }
                            break;
                        }
                        if ((n = p.next) != q) {         // help finish
                            if (n != null && q.prev == p) {
                                p.casNext(n, q);
                                if (p.prev == null)
                                    signalNext(p);
                            }
                            break;
                        }
                        s = q;
                        q = q.prev;
                    }
                }
            }

            /**
             * Cancels an ongoing attempt to acquire.
             *
             * @param node the node (may be null if cancelled before enqueuing)
             * @param interrupted true if thread interrupted
             * @param interruptible if should report interruption vs reset
             */
            private int cancelAcquire(Node node, boolean interrupted,
                                      boolean interruptible) {
                if (node != null) {
                    node.waiter = null;
                    node.status = CANCELLED;
                    if (node.prev != null)
                        cleanQueue();
                }
                if (interrupted) {
                    if (interruptible)
                        return CANCELLED;
                    else
                        Thread.currentThread().interrupt();
                }
                return 0;
            }

            // Main exported methods

            /**
             * Attempts to acquire in exclusive mode. This method should query
             * if the state of the object permits it to be acquired in the
             * exclusive mode, and if so to acquire it.
             *
             * <p>This method is always invoked by the thread performing
             * acquire.  If this method reports failure, the acquire method
             * may queue the thread, if it is not already queued, until it is
             * signalled by a release from some other thread. This can be used
             * to implement method {@link Lock#tryLock()}.
             *
             * <p>The default
             * implementation throws {@link UnsupportedOperationException}.
             *
             * @param arg the acquire argument. This value is always the one
             *        passed to an acquire method, or is the value saved on entry
             *        to a condition wait.  The value is otherwise uninterpreted
             *        and can represent anything you like.
             * @return {@code true} if successful. Upon success, this object has
             *         been acquired.
             * @throws IllegalMonitorStateException if acquiring would place this
             *         synchronizer in an illegal state. This exception must be
             *         thrown in a consistent fashion for synchronization to work
             *         correctly.
             * @throws UnsupportedOperationException if exclusive mode is not supported
             */
            protected boolean tryAcquire(int arg) {
                throw new UnsupportedOperationException();
            }

            /**
             * Attempts to set the state to reflect a release in exclusive
             * mode.
             *
             * <p>This method is always invoked by the thread performing release.
             *
             * <p>The default implementation throws
             * {@link UnsupportedOperationException}.
             *
             * @param arg the release argument. This value is always the one
             *        passed to a release method, or the current state value upon
             *        entry to a condition wait.  The value is otherwise
             *        uninterpreted and can represent anything you like.
             * @return {@code true} if this object is now in a fully released
             *         state, so that any waiting threads may attempt to acquire;
             *         and {@code false} otherwise.
             * @throws IllegalMonitorStateException if releasing would place this
             *         synchronizer in an illegal state. This exception must be
             *         thrown in a consistent fashion for synchronization to work
             *         correctly.
             * @throws UnsupportedOperationException if exclusive mode is not supported
             */
            protected boolean tryRelease(int arg) {
                throw new UnsupportedOperationException();
            }

            /**
             * Attempts to acquire in shared mode. This method should query if
             * the state of the object permits it to be acquired in the shared
             * mode, and if so to acquire it.
             *
             * <p>This method is always invoked by the thread performing
             * acquire.  If this method reports failure, the acquire method
             * may queue the thread, if it is not already queued, until it is
             * signalled by a release from some other thread.
             *
             * <p>The default implementation throws {@link
             * UnsupportedOperationException}.
             *
             * @param arg the acquire argument. This value is always the one
             *        passed to an acquire method, or is the value saved on entry
             *        to a condition wait.  The value is otherwise uninterpreted
             *        and can represent anything you like.
             * @return a negative value on failure; zero if acquisition in shared
             *         mode succeeded but no subsequent shared-mode acquire can
             *         succeed; and a positive value if acquisition in shared
             *         mode succeeded and subsequent shared-mode acquires might
             *         also succeed, in which case a subsequent waiting thread
             *         must check availability. (Support for three different
             *         return values enables this method to be used in contexts
             *         where acquires only sometimes act exclusively.)  Upon
             *         success, this object has been acquired.
             * @throws IllegalMonitorStateException if acquiring would place this
             *         synchronizer in an illegal state. This exception must be
             *         thrown in a consistent fashion for synchronization to work
             *         correctly.
             * @throws UnsupportedOperationException if shared mode is not supported
             */
            protected int tryAcquireShared(int arg) {
                throw new UnsupportedOperationException();
            }

            /**
             * Attempts to set the state to reflect a release in shared mode.
             *
             * <p>This method is always invoked by the thread performing release.
             *
             * <p>The default implementation throws
             * {@link UnsupportedOperationException}.
             *
             * @param arg the release argument. This value is always the one
             *        passed to a release method, or the current state value upon
             *        entry to a condition wait.  The value is otherwise
             *        uninterpreted and can represent anything you like.
             * @return {@code true} if this release of shared mode may permit a
             *         waiting acquire (shared or exclusive) to succeed; and
             *         {@code false} otherwise
             * @throws IllegalMonitorStateException if releasing would place this
             *         synchronizer in an illegal state. This exception must be
             *         thrown in a consistent fashion for synchronization to work
             *         correctly.
             * @throws UnsupportedOperationException if shared mode is not supported
             */
            protected boolean tryReleaseShared(int arg) {
                throw new UnsupportedOperationException();
            }

            /**
             * Returns {@code true} if synchronization is held exclusively with
             * respect to the current (calling) thread.  This method is invoked
             * upon each call to a {@link ConditionObject} method.
             *
             * <p>The default implementation throws {@link
             * UnsupportedOperationException}. This method is invoked
             * internally only within {@link ConditionObject} methods, so need
             * not be defined if conditions are not used.
             *
             * @return {@code true} if synchronization is held exclusively;
             *         {@code false} otherwise
             * @throws UnsupportedOperationException if conditions are not supported
             */
            protected boolean isHeldExclusively() {
                throw new UnsupportedOperationException();
            }

            /**
             * Acquires in exclusive mode, ignoring interrupts.  Implemented
             * by invoking at least once {@link #tryAcquire},
             * returning on success.  Otherwise the thread is queued, possibly
             * repeatedly blocking and unblocking, invoking {@link
             * #tryAcquire} until success.  This method can be used
             * to implement method {@link Lock#lock}.
             *
             * @param arg the acquire argument.  This value is conveyed to
             *        {@link #tryAcquire} but is otherwise uninterpreted and
             *        can represent anything you like.
             */
            public final void acquire(int arg) {
                if (!tryAcquire(arg))
                    acquire(null, arg, false, false, false, 0L);
            }

            /**
             * Acquires in exclusive mode, aborting if interrupted.
             * Implemented by first checking interrupt status, then invoking
             * at least once {@link #tryAcquire}, returning on
             * success.  Otherwise the thread is queued, possibly repeatedly
             * blocking and unblocking, invoking {@link #tryAcquire}
             * until success or the thread is interrupted.  This method can be
             * used to implement method {@link Lock#lockInterruptibly}.
             *
             * @param arg the acquire argument.  This value is conveyed to
             *        {@link #tryAcquire} but is otherwise uninterpreted and
             *        can represent anything you like.
             * @throws InterruptedException if the current thread is interrupted
             */
            public final void acquireInterruptibly(int arg)
                    throws InterruptedException {
                if (Thread.interrupted() ||
                        (!tryAcquire(arg) && acquire(null, arg, false, true, false, 0L) < 0))
                    throw new InterruptedException();
            }

            /**
             * Attempts to acquire in exclusive mode, aborting if interrupted,
             * and failing if the given timeout elapses.  Implemented by first
             * checking interrupt status, then invoking at least once {@link
             * #tryAcquire}, returning on success.  Otherwise, the thread is
             * queued, possibly repeatedly blocking and unblocking, invoking
             * {@link #tryAcquire} until success or the thread is interrupted
             * or the timeout elapses.  This method can be used to implement
             * method {@link Lock#tryLock(long, TimeUnit)}.
             *
             * @param arg the acquire argument.  This value is conveyed to
             *        {@link #tryAcquire} but is otherwise uninterpreted and
             *        can represent anything you like.
             * @param nanosTimeout the maximum number of nanoseconds to wait
             * @return {@code true} if acquired; {@code false} if timed out
             * @throws InterruptedException if the current thread is interrupted
             */
            public final boolean tryAcquireNanos(int arg, long nanosTimeout)
                    throws InterruptedException {
                if (!Thread.interrupted()) {
                    if (tryAcquire(arg))
                        return true;
                    if (nanosTimeout <= 0L)
                        return false;
                    int stat = acquire(null, arg, false, true, true,
                            System.nanoTime() + nanosTimeout);
                    if (stat > 0)
                        return true;
                    if (stat == 0)
                        return false;
                }
                throw new InterruptedException();
            }

            /**
             * Releases in exclusive mode.  Implemented by unblocking one or
             * more threads if {@link #tryRelease} returns true.
             * This method can be used to implement method {@link Lock#unlock}.
             *
             * @param arg the release argument.  This value is conveyed to
             *        {@link #tryRelease} but is otherwise uninterpreted and
             *        can represent anything you like.
             * @return the value returned from {@link #tryRelease}
             */
            public final boolean release(int arg) {
                if (tryRelease(arg)) {
                    signalNext(head);
                    return true;
                }
                return false;
            }

            /**
             * Acquires in shared mode, ignoring interrupts.  Implemented by
             * first invoking at least once {@link #tryAcquireShared},
             * returning on success.  Otherwise the thread is queued, possibly
             * repeatedly blocking and unblocking, invoking {@link
             * #tryAcquireShared} until success.
             *
             * @param arg the acquire argument.  This value is conveyed to
             *        {@link #tryAcquireShared} but is otherwise uninterpreted
             *        and can represent anything you like.
             */
            public final void acquireShared(int arg) {
                if (tryAcquireShared(arg) < 0)
                    acquire(null, arg, true, false, false, 0L);
            }

            /**
             * Acquires in shared mode, aborting if interrupted.  Implemented
             * by first checking interrupt status, then invoking at least once
             * {@link #tryAcquireShared}, returning on success.  Otherwise the
             * thread is queued, possibly repeatedly blocking and unblocking,
             * invoking {@link #tryAcquireShared} until success or the thread
             * is interrupted.
             * @param arg the acquire argument.
             * This value is conveyed to {@link #tryAcquireShared} but is
             * otherwise uninterpreted and can represent anything
             * you like.
             * @throws InterruptedException if the current thread is interrupted
             */
            public final void acquireSharedInterruptibly(int arg)
                    throws InterruptedException {
                if (Thread.interrupted() ||
                        (tryAcquireShared(arg) < 0 &&
                                acquire(null, arg, true, true, false, 0L) < 0))
                    throw new InterruptedException();
            }

            /**
             * Attempts to acquire in shared mode, aborting if interrupted, and
             * failing if the given timeout elapses.  Implemented by first
             * checking interrupt status, then invoking at least once {@link
             * #tryAcquireShared}, returning on success.  Otherwise, the
             * thread is queued, possibly repeatedly blocking and unblocking,
             * invoking {@link #tryAcquireShared} until success or the thread
             * is interrupted or the timeout elapses.
             *
             * @param arg the acquire argument.  This value is conveyed to
             *        {@link #tryAcquireShared} but is otherwise uninterpreted
             *        and can represent anything you like.
             * @param nanosTimeout the maximum number of nanoseconds to wait
             * @return {@code true} if acquired; {@code false} if timed out
             * @throws InterruptedException if the current thread is interrupted
             */
            public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
                    throws InterruptedException {
                if (!Thread.interrupted()) {
                    if (tryAcquireShared(arg) >= 0)
                        return true;
                    if (nanosTimeout <= 0L)
                        return false;
                    int stat = acquire(null, arg, true, true, true,
                            System.nanoTime() + nanosTimeout);
                    if (stat > 0)
                        return true;
                    if (stat == 0)
                        return false;
                }
                throw new InterruptedException();
            }

            /**
             * Releases in shared mode.  Implemented by unblocking one or more
             * threads if {@link #tryReleaseShared} returns true.
             *
             * @param arg the release argument.  This value is conveyed to
             *        {@link #tryReleaseShared} but is otherwise uninterpreted
             *        and can represent anything you like.
             * @return the value returned from {@link #tryReleaseShared}
             */
            public final boolean releaseShared(int arg) {
                if (tryReleaseShared(arg)) {
                    signalNext(head);
                    return true;
                }
                return false;
            }

            // Queue inspection methods

            /**
             * Queries whether any threads are waiting to acquire. Note that
             * because cancellations due to interrupts and timeouts may occur
             * at any time, a {@code true} return does not guarantee that any
             * other thread will ever acquire.
             *
             * @return {@code true} if there may be other threads waiting to acquire
             */
            public final boolean hasQueuedThreads() {
                for (Node p = tail, h = head; p != h && p != null; p = p.prev)
                    if (p.status >= 0)
                        return true;
                return false;
            }

            /**
             * Queries whether any threads have ever contended to acquire this
             * synchronizer; that is, if an acquire method has ever blocked.
             *
             * <p>In this implementation, this operation returns in
             * constant time.
             *
             * @return {@code true} if there has ever been contention
             */
            public final boolean hasContended() {
                return head != null;
            }

            /**
             * Returns the first (longest-waiting) thread in the queue, or
             * {@code null} if no threads are currently queued.
             *
             * <p>In this implementation, this operation normally returns in
             * constant time, but may iterate upon contention if other threads are
             * concurrently modifying the queue.
             *
             * @return the first (longest-waiting) thread in the queue, or
             *         {@code null} if no threads are currently queued
             */
            public final Thread getFirstQueuedThread() {
                Thread first = null, w; Node h, s;
                if ((h = head) != null && ((s = h.next) == null ||
                        (first = s.waiter) == null ||
                        s.prev == null)) {
                    // traverse from tail on stale reads
                    for (Node p = tail, q; p != null && (q = p.prev) != null; p = q)
                        if ((w = p.waiter) != null)
                            first = w;
                }
                return first;
            }

            /**
             * Returns true if the given thread is currently queued.
             *
             * <p>This implementation traverses the queue to determine
             * presence of the given thread.
             *
             * @param thread the thread
             * @return {@code true} if the given thread is on the queue
             * @throws NullPointerException if the thread is null
             */
            public final boolean isQueued(Thread thread) {
                if (thread == null)
                    throw new NullPointerException();
                for (Node p = tail; p != null; p = p.prev)
                    if (p.waiter == thread)
                        return true;
                return false;
            }

            /**
             * Returns {@code true} if the apparent first queued thread, if one
             * exists, is waiting in exclusive mode.  If this method returns
             * {@code true}, and the current thread is attempting to acquire in
             * shared mode (that is, this method is invoked from {@link
             * #tryAcquireShared}) then it is guaranteed that the current thread
             * is not the first queued thread.  Used only as a heuristic in
             * ReentrantReadWriteLock.
             */
            final boolean apparentlyFirstQueuedIsExclusive() {
                Node h, s;
                return (h = head) != null && (s = h.next)  != null &&
                        !(s instanceof SharedNode) && s.waiter != null;
            }

            /**
             * Queries whether any threads have been waiting to acquire longer
             * than the current thread.
             *
             * <p>An invocation of this method is equivalent to (but may be
             * more efficient than):
             * <pre> {@code
             * getFirstQueuedThread() != Thread.currentThread()
             *   && hasQueuedThreads()}</pre>
             *
             * <p>Note that because cancellations due to interrupts and
             * timeouts may occur at any time, a {@code true} return does not
             * guarantee that some other thread will acquire before the current
             * thread.  Likewise, it is possible for another thread to win a
             * race to enqueue after this method has returned {@code false},
             * due to the queue being empty.
             *
             * <p>This method is designed to be used by a fair synchronizer to
             * avoid <a href="AbstractQueuedSynchronizer.html#barging">barging</a>.
             * Such a synchronizer's {@link #tryAcquire} method should return
             * {@code false}, and its {@link #tryAcquireShared} method should
             * return a negative value, if this method returns {@code true}
             * (unless this is a reentrant acquire).  For example, the {@code
             * tryAcquire} method for a fair, reentrant, exclusive mode
             * synchronizer might look like this:
             *
             * <pre> {@code
             * protected boolean tryAcquire(int arg) {
             *   if (isHeldExclusively()) {
             *     // A reentrant acquire; increment hold count
             *     return true;
             *   } else if (hasQueuedPredecessors()) {
             *     return false;
             *   } else {
             *     // try to acquire normally
             *   }
             * }}</pre>
             *
             * @return {@code true} if there is a queued thread preceding the
             *         current thread, and {@code false} if the current thread
             *         is at the head of the queue or the queue is empty
             * @since 1.7
             */
            public final boolean hasQueuedPredecessors() {
                Thread first = null; Node h, s;
                if ((h = head) != null && ((s = h.next) == null ||
                        (first = s.waiter) == null ||
                        s.prev == null))
                    first = getFirstQueuedThread(); // retry via getFirstQueuedThread
                return first != null && first != Thread.currentThread();
            }

            // Instrumentation and monitoring methods

            /**
             * Returns an estimate of the number of threads waiting to
             * acquire.  The value is only an estimate because the number of
             * threads may change dynamically while this method traverses
             * internal data structures.  This method is designed for use in
             * monitoring system state, not for synchronization control.
             *
             * @return the estimated number of threads waiting to acquire
             */
            public final int getQueueLength() {
                int n = 0;
                for (Node p = tail; p != null; p = p.prev) {
                    if (p.waiter != null)
                        ++n;
                }
                return n;
            }

            /**
             * Returns a collection containing threads that may be waiting to
             * acquire.  Because the actual set of threads may change
             * dynamically while constructing this result, the returned
             * collection is only a best-effort estimate.  The elements of the
             * returned collection are in no particular order.  This method is
             * designed to facilitate construction of subclasses that provide
             * more extensive monitoring facilities.
             *
             * @return the collection of threads
             */
            public final Collection<Thread> getQueuedThreads() {
                ArrayList<Thread> list = new ArrayList<>();
                for (Node p = tail; p != null; p = p.prev) {
                    Thread t = p.waiter;
                    if (t != null)
                        list.add(t);
                }
                return list;
            }

            /**
             * Returns a collection containing threads that may be waiting to
             * acquire in exclusive mode. This has the same properties
             * as {@link #getQueuedThreads} except that it only returns
             * those threads waiting due to an exclusive acquire.
             *
             * @return the collection of threads
             */
            public final Collection<Thread> getExclusiveQueuedThreads() {
                ArrayList<Thread> list = new ArrayList<>();
                for (Node p = tail; p != null; p = p.prev) {
                    if (!(p instanceof SharedNode)) {
                        Thread t = p.waiter;
                        if (t != null)
                            list.add(t);
                    }
                }
                return list;
            }

            /**
             * Returns a collection containing threads that may be waiting to
             * acquire in shared mode. This has the same properties
             * as {@link #getQueuedThreads} except that it only returns
             * those threads waiting due to a shared acquire.
             *
             * @return the collection of threads
             */
            public final Collection<Thread> getSharedQueuedThreads() {
                ArrayList<Thread> list = new ArrayList<>();
                for (Node p = tail; p != null; p = p.prev) {
                    if (p instanceof SharedNode) {
                        Thread t = p.waiter;
                        if (t != null)
                            list.add(t);
                    }
                }
                return list;
            }

            /**
             * Returns a string identifying this synchronizer, as well as its state.
             * The state, in brackets, includes the String {@code "State ="}
             * followed by the current value of {@link #getState}, and either
             * {@code "nonempty"} or {@code "empty"} depending on whether the
             * queue is empty.
             *
             * @return a string identifying this synchronizer, as well as its state
             */
            public String toString() {

                return "{"+debugText()+"}|" + super.toString()
                        + "[State = " + getState() + ", "
                        + (hasQueuedThreads() ? "non" : "") + "empty queue]";
            }

            // Instrumentation methods for conditions

            /**
             * Queries whether the given ConditionObject
             * uses this synchronizer as its lock.
             *
             * @param condition the condition
             * @return {@code true} if owned
             * @throws NullPointerException if the condition is null
             */
            public final boolean owns(ConditionObject condition) {
                return condition.isOwnedBy(this);
            }

            /**
             * Queries whether any threads are waiting on the given condition
             * associated with this synchronizer. Note that because timeouts
             * and interrupts may occur at any time, a {@code true} return
             * does not guarantee that a future {@code signal} will awaken
             * any threads.  This method is designed primarily for use in
             * monitoring of the system state.
             *
             * @param condition the condition
             * @return {@code true} if there are any waiting threads
             * @throws IllegalMonitorStateException if exclusive synchronization
             *         is not held
             * @throws IllegalArgumentException if the given condition is
             *         not associated with this synchronizer
             * @throws NullPointerException if the condition is null
             */
            public final boolean hasWaiters(ConditionObject condition) {
                if (!owns(condition))
                    throw new IllegalArgumentException("Not owner");
                return condition.hasWaiters();
            }

            /**
             * Returns an estimate of the number of threads waiting on the
             * given condition associated with this synchronizer. Note that
             * because timeouts and interrupts may occur at any time, the
             * estimate serves only as an upper bound on the actual number of
             * waiters.  This method is designed for use in monitoring system
             * state, not for synchronization control.
             *
             * @param condition the condition
             * @return the estimated number of waiting threads
             * @throws IllegalMonitorStateException if exclusive synchronization
             *         is not held
             * @throws IllegalArgumentException if the given condition is
             *         not associated with this synchronizer
             * @throws NullPointerException if the condition is null
             */
            public final int getWaitQueueLength(ConditionObject condition) {
                if (!owns(condition))
                    throw new IllegalArgumentException("Not owner");
                return condition.getWaitQueueLength();
            }

            /**
             * Returns a collection containing those threads that may be
             * waiting on the given condition associated with this
             * synchronizer.  Because the actual set of threads may change
             * dynamically while constructing this result, the returned
             * collection is only a best-effort estimate. The elements of the
             * returned collection are in no particular order.
             *
             * @param condition the condition
             * @return the collection of threads
             * @throws IllegalMonitorStateException if exclusive synchronization
             *         is not held
             * @throws IllegalArgumentException if the given condition is
             *         not associated with this synchronizer
             * @throws NullPointerException if the condition is null
             */
            public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
                if (!owns(condition))
                    throw new IllegalArgumentException("Not owner");
                return condition.getWaitingThreads();
            }

            /**
             * Condition implementation for a {@link AbstractQueuedSynchronizer2}
             * serving as the basis of a {@link Lock} implementation.
             *
             * <p>Method documentation for this class describes mechanics,
             * not behavioral specifications from the point of view of Lock
             * and Condition users. Exported versions of this class will in
             * general need to be accompanied by documentation describing
             * condition semantics that rely on those of the associated
             * {@code AbstractQueuedSynchronizer}.
             *
             * <p>This class is Serializable, but all fields are transient,
             * so deserialized conditions have no waiters.
             */
            public class ConditionObject implements Condition, java.io.Serializable {
                private static final long serialVersionUID = 5231624872572414699L;
                /** First node of condition queue. */
                private transient ConditionNode firstWaiter;
                /** Last node of condition queue. */
                private transient ConditionNode lastWaiter;

                /**
                 * Creates a new {@code ConditionObject} instance.
                 */
                public ConditionObject() { }

                // Signalling methods

                /**
                 * Removes and transfers one or all waiters to sync queue.
                 */
                private void doSignal(ConditionNode first, boolean all) {
                    while (first != null) {
                        ConditionNode next = first.nextWaiter;
                        if ((firstWaiter = next) == null)
                            lastWaiter = null;
                        if ((first.getAndUnsetStatus(COND) & COND) != 0) {
                            enqueue(first);
                            if (!all)
                                break;
                        }
                        first = next;
                    }
                }

                /**
                 * Moves the longest-waiting thread, if one exists, from the
                 * wait queue for this condition to the wait queue for the
                 * owning lock.
                 *
                 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
                 *         returns {@code false}
                 */
                public final void signal() {
                    ConditionNode first = firstWaiter;
                    if (!isHeldExclusively())
                        throw new IllegalMonitorStateException();
                    if (first != null)
                        doSignal(first, false);
                }

                /**
                 * Moves all threads from the wait queue for this condition to
                 * the wait queue for the owning lock.
                 *
                 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
                 *         returns {@code false}
                 */
                public final void signalAll() {
                    ConditionNode first = firstWaiter;
                    if (!isHeldExclusively())
                        throw new IllegalMonitorStateException();
                    if (first != null)
                        doSignal(first, true);
                }

                // Waiting methods

                /**
                 * Adds node to condition list and releases lock.
                 *
                 * @param node the node
                 * @return savedState to reacquire after wait
                 */
                private int enableWait(ConditionNode node) {
                    if (isHeldExclusively()) {
                        node.waiter = Thread.currentThread();
                        node.setStatusRelaxed(COND | WAITING);
                        ConditionNode last = lastWaiter;
                        if (last == null)
                            firstWaiter = node;
                        else
                            last.nextWaiter = node;
                        lastWaiter = node;
                        int savedState = getState();
                        if (release(savedState))
                            return savedState;
                    }
                    node.status = CANCELLED; // lock not held or inconsistent
                    throw new IllegalMonitorStateException();
                }

                /**
                 * Returns true if a node that was initially placed on a condition
                 * queue is now ready to reacquire on sync queue.
                 * @param node the node
                 * @return true if is reacquiring
                 */
                private boolean canReacquire(ConditionNode node) {
                    // check links, not status to avoid enqueue race
                    Node p; // traverse unless known to be bidirectionally linked
                    return node != null && (p = node.prev) != null &&
                            (p.next == node || isEnqueued(node));
                }

                /**
                 * Unlinks the given node and other non-waiting nodes from
                 * condition queue unless already unlinked.
                 */
                private void unlinkCancelledWaiters(ConditionNode node) {
                    if (node == null || node.nextWaiter != null || node == lastWaiter) {
                        ConditionNode w = firstWaiter, trail = null;
                        while (w != null) {
                            ConditionNode next = w.nextWaiter;
                            if ((w.status & COND) == 0) {
                                w.nextWaiter = null;
                                if (trail == null)
                                    firstWaiter = next;
                                else
                                    trail.nextWaiter = next;
                                if (next == null)
                                    lastWaiter = trail;
                            } else
                                trail = w;
                            w = next;
                        }
                    }
                }

                /**
                 * Implements uninterruptible condition wait.
                 * <ol>
                 * <li>Save lock state returned by {@link #getState}.
                 * <li>Invoke {@link #release} with saved state as argument,
                 *     throwing IllegalMonitorStateException if it fails.
                 * <li>Block until signalled.
                 * <li>Reacquire by invoking specialized version of
                 *     {@link #acquire} with saved state as argument.
                 * </ol>
                 */
                public final void awaitUninterruptibly() {
                    ConditionNode node = new ConditionNode();
                    int savedState = enableWait(node);
                    LockSupport.setCurrentBlocker(this); // for back-compatibility
                    boolean interrupted = false, rejected = false;
                    while (!canReacquire(node)) {
                        if (Thread.interrupted())
                            interrupted = true;
                        else if ((node.status & COND) != 0) {
                            try {
                                if (rejected)
                                    node.block();
                                else
                                    ForkJoinPool.managedBlock(node);
                            } catch (RejectedExecutionException ex) {
                                rejected = true;
                            } catch (InterruptedException ie) {
                                interrupted = true;
                            }
                        } else
                            Thread.onSpinWait();    // awoke while enqueuing
                    }
                    LockSupport.setCurrentBlocker(null);
                    node.clearStatus();
                    acquire(node, savedState, false, false, false, 0L);
                    if (interrupted)
                        Thread.currentThread().interrupt();
                }

                /**
                 * Implements interruptible condition wait.
                 * <ol>
                 * <li>If current thread is interrupted, throw InterruptedException.
                 * <li>Save lock state returned by {@link #getState}.
                 * <li>Invoke {@link #release} with saved state as argument,
                 *     throwing IllegalMonitorStateException if it fails.
                 * <li>Block until signalled or interrupted.
                 * <li>Reacquire by invoking specialized version of
                 *     {@link #acquire} with saved state as argument.
                 * <li>If interrupted while blocked in step 4, throw InterruptedException.
                 * </ol>
                 */
                public final void await() throws InterruptedException {
                    if (Thread.interrupted())
                        throw new InterruptedException();
                    ConditionNode node = new ConditionNode();
                    int savedState = enableWait(node);
                    LockSupport.setCurrentBlocker(this); // for back-compatibility
                    boolean interrupted = false, cancelled = false, rejected = false;
                    while (!canReacquire(node)) {
                        if (interrupted |= Thread.interrupted()) {
                            if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                                break;              // else interrupted after signal
                        } else if ((node.status & COND) != 0) {
                            try {
                                if (rejected)
                                    node.block();
                                else
                                    ForkJoinPool.managedBlock(node);
                            } catch (RejectedExecutionException ex) {
                                rejected = true;
                            } catch (InterruptedException ie) {
                                interrupted = true;
                            }
                        } else
                            Thread.onSpinWait();    // awoke while enqueuing
                    }
                    LockSupport.setCurrentBlocker(null);
                    node.clearStatus();
                    acquire(node, savedState, false, false, false, 0L);
                    if (interrupted) {
                        if (cancelled) {
                            unlinkCancelledWaiters(node);
                            throw new InterruptedException();
                        }
                        Thread.currentThread().interrupt();
                    }
                }

                /**
                 * Implements timed condition wait.
                 * <ol>
                 * <li>If current thread is interrupted, throw InterruptedException.
                 * <li>Save lock state returned by {@link #getState}.
                 * <li>Invoke {@link #release} with saved state as argument,
                 *     throwing IllegalMonitorStateException if it fails.
                 * <li>Block until signalled, interrupted, or timed out.
                 * <li>Reacquire by invoking specialized version of
                 *     {@link #acquire} with saved state as argument.
                 * <li>If interrupted while blocked in step 4, throw InterruptedException.
                 * </ol>
                 */
                public final long awaitNanos(long nanosTimeout)
                        throws InterruptedException {
                    if (Thread.interrupted())
                        throw new InterruptedException();
                    ConditionNode node = new ConditionNode();
                    int savedState = enableWait(node);
                    long nanos = (nanosTimeout < 0L) ? 0L : nanosTimeout;
                    long deadline = System.nanoTime() + nanos;
                    boolean cancelled = false, interrupted = false;
                    while (!canReacquire(node)) {
                        if ((interrupted |= Thread.interrupted()) ||
                                (nanos = deadline - System.nanoTime()) <= 0L) {
                            if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                                break;
                        } else
                            LockSupport.parkNanos(this, nanos);
                    }
                    node.clearStatus();
                    acquire(node, savedState, false, false, false, 0L);
                    if (cancelled) {
                        unlinkCancelledWaiters(node);
                        if (interrupted)
                            throw new InterruptedException();
                    } else if (interrupted)
                        Thread.currentThread().interrupt();
                    long remaining = deadline - System.nanoTime(); // avoid overflow
                    return (remaining <= nanosTimeout) ? remaining : Long.MIN_VALUE;
                }

                /**
                 * Implements absolute timed condition wait.
                 * <ol>
                 * <li>If current thread is interrupted, throw InterruptedException.
                 * <li>Save lock state returned by {@link #getState}.
                 * <li>Invoke {@link #release} with saved state as argument,
                 *     throwing IllegalMonitorStateException if it fails.
                 * <li>Block until signalled, interrupted, or timed out.
                 * <li>Reacquire by invoking specialized version of
                 *     {@link #acquire} with saved state as argument.
                 * <li>If interrupted while blocked in step 4, throw InterruptedException.
                 * <li>If timed out while blocked in step 4, return false, else true.
                 * </ol>
                 */
                public final boolean awaitUntil(Date deadline)
                        throws InterruptedException {
                    long abstime = deadline.getTime();
                    if (Thread.interrupted())
                        throw new InterruptedException();
                    ConditionNode node = new ConditionNode();
                    int savedState = enableWait(node);
                    boolean cancelled = false, interrupted = false;
                    while (!canReacquire(node)) {
                        if ((interrupted |= Thread.interrupted()) ||
                                System.currentTimeMillis() >= abstime) {
                            if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                                break;
                        } else
                            LockSupport.parkUntil(this, abstime);
                    }
                    node.clearStatus();
                    acquire(node, savedState, false, false, false, 0L);
                    if (cancelled) {
                        unlinkCancelledWaiters(node);
                        if (interrupted)
                            throw new InterruptedException();
                    } else if (interrupted)
                        Thread.currentThread().interrupt();
                    return !cancelled;
                }

                /**
                 * Implements timed condition wait.
                 * <ol>
                 * <li>If current thread is interrupted, throw InterruptedException.
                 * <li>Save lock state returned by {@link #getState}.
                 * <li>Invoke {@link #release} with saved state as argument,
                 *     throwing IllegalMonitorStateException if it fails.
                 * <li>Block until signalled, interrupted, or timed out.
                 * <li>Reacquire by invoking specialized version of
                 *     {@link #acquire} with saved state as argument.
                 * <li>If interrupted while blocked in step 4, throw InterruptedException.
                 * <li>If timed out while blocked in step 4, return false, else true.
                 * </ol>
                 */
                public final boolean await(long time, TimeUnit unit)
                        throws InterruptedException {
                    long nanosTimeout = unit.toNanos(time);
                    if (Thread.interrupted())
                        throw new InterruptedException();
                    ConditionNode node = new ConditionNode();
                    int savedState = enableWait(node);
                    long nanos = (nanosTimeout < 0L) ? 0L : nanosTimeout;
                    long deadline = System.nanoTime() + nanos;
                    boolean cancelled = false, interrupted = false;
                    while (!canReacquire(node)) {
                        if ((interrupted |= Thread.interrupted()) ||
                                (nanos = deadline - System.nanoTime()) <= 0L) {
                            if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                                break;
                        } else
                            LockSupport.parkNanos(this, nanos);
                    }
                    node.clearStatus();
                    acquire(node, savedState, false, false, false, 0L);
                    if (cancelled) {
                        unlinkCancelledWaiters(node);
                        if (interrupted)
                            throw new InterruptedException();
                    } else if (interrupted)
                        Thread.currentThread().interrupt();
                    return !cancelled;
                }

                //  support for instrumentation

                /**
                 * Returns true if this condition was created by the given
                 * synchronization object.
                 *
                 * @return {@code true} if owned
                 */
                final boolean isOwnedBy(AbstractQueuedSynchronizer2 sync) {
                    return sync == AbstractQueuedSynchronizer2.this;
                }

                /**
                 * Queries whether any threads are waiting on this condition.
                 * Implements {@link AbstractQueuedSynchronizer2#hasWaiters(ConditionObject)}.
                 *
                 * @return {@code true} if there are any waiting threads
                 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
                 *         returns {@code false}
                 */
                protected final boolean hasWaiters() {
                    if (!isHeldExclusively())
                        throw new IllegalMonitorStateException();
                    for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                        if ((w.status & COND) != 0)
                            return true;
                    }
                    return false;
                }

                /**
                 * Returns an estimate of the number of threads waiting on
                 * this condition.
                 * Implements {@link AbstractQueuedSynchronizer2#getWaitQueueLength(ConditionObject)}.
                 *
                 * @return the estimated number of waiting threads
                 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
                 *         returns {@code false}
                 */
                protected final int getWaitQueueLength() {
                    if (!isHeldExclusively())
                        throw new IllegalMonitorStateException();
                    int n = 0;
                    for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                        if ((w.status & COND) != 0)
                            ++n;
                    }
                    return n;
                }

                /**
                 * Returns a collection containing those threads that may be
                 * waiting on this Condition.
                 * Implements {@link AbstractQueuedSynchronizer2#getWaitingThreads(ConditionObject)}.
                 *
                 * @return the collection of threads
                 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
                 *         returns {@code false}
                 */
                protected final Collection<Thread> getWaitingThreads() {
                    if (!isHeldExclusively())
                        throw new IllegalMonitorStateException();
                    ArrayList<Thread> list = new ArrayList<>();
                    for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                        if ((w.status & COND) != 0) {
                            Thread t = w.waiter;
                            if (t != null)
                                list.add(t);
                        }
                    }
                    return list;
                }
            }

            // Unsafe
            private static final Unsafe U = Unsafe.getUnsafe();
            static int getAndBitwiseAndInt(Object o, long offset, int mask) {
                int current;
                do {
                    current = U.getIntVolatile(o, offset);
                } while (!U.compareAndSwapInt(o, offset,
                        current, current & mask));
                return current;
            }
            private static final long STATE;

            static {
                try {
                    STATE = U.objectFieldOffset(AbstractQueuedSynchronizer2.class.getDeclaredField("state"));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }

            private static final long HEAD;

            static {
                try {
                    HEAD = U.objectFieldOffset(AbstractQueuedSynchronizer2.class.getDeclaredField("head"));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }

            private static final long TAIL;

            static {
                try {
                    TAIL = U.objectFieldOffset(AbstractQueuedSynchronizer2.class.getDeclaredField("tail"));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                Class<?> ensureLoaded = LockSupport.class;
            }
        }



        /**
         * A counting semaphore.  Conceptually, a semaphore maintains a set of
         * permits.  Each {@link #acquire} blocks if necessary until a permit is
         * available, and then takes it.  Each {@link #release} adds a permit,
         * potentially releasing a blocking acquirer.
         * However, no actual permit objects are used; the {@code Semaphore} just
         * keeps a count of the number available and acts accordingly.
         *
         * <p>Semaphores are often used to restrict the number of threads than can
         * access some (physical or logical) resource. For example, here is
         * a class that uses a semaphore to control access to a pool of items:
         * <pre> {@code
         * class Pool {
         *   private static final int MAX_AVAILABLE = 100;
         *   private final Semaphore available = new Semaphore(MAX_AVAILABLE, true);
         *
         *   public Object getItem() throws InterruptedException {
         *     available.acquire();
         *     return getNextAvailableItem();
         *   }
         *
         *   public void putItem(Object x) {
         *     if (markAsUnused(x))
         *       available.release();
         *   }
         *
         *   // Not a particularly efficient data structure; just for demo
         *
         *   protected Object[] items = ...; // whatever kinds of items being managed
         *   protected boolean[] used = new boolean[MAX_AVAILABLE];
         *
         *   protected synchronized Object getNextAvailableItem() {
         *     for (int i = 0; i < MAX_AVAILABLE; ++i) {
         *       if (!used[i]) {
         *         used[i] = true;
         *         return items[i];
         *       }
         *     }
         *     return null; // not reached
         *   }
         *
         *   protected synchronized boolean markAsUnused(Object item) {
         *     for (int i = 0; i < MAX_AVAILABLE; ++i) {
         *       if (item == items[i]) {
         *         if (used[i]) {
         *           used[i] = false;
         *           return true;
         *         } else
         *           return false;
         *       }
         *     }
         *     return false;
         *   }
         * }}</pre>
         *
         * <p>Before obtaining an item each thread must acquire a permit from
         * the semaphore, guaranteeing that an item is available for use. When
         * the thread has finished with the item it is returned back to the
         * pool and a permit is returned to the semaphore, allowing another
         * thread to acquire that item.  Note that no synchronization lock is
         * held when {@link #acquire} is called as that would prevent an item
         * from being returned to the pool.  The semaphore encapsulates the
         * synchronization needed to restrict access to the pool, separately
         * from any synchronization needed to maintain the consistency of the
         * pool itself.
         *
         * <p>A semaphore initialized to one, and which is used such that it
         * only has at most one permit available, can serve as a mutual
         * exclusion lock.  This is more commonly known as a <em>binary
         * semaphore</em>, because it only has two states: one permit
         * available, or zero permits available.  When used in this way, the
         * binary semaphore has the property (unlike many {@link java.util.concurrent.locks.Lock}
         * implementations), that the &quot;lock&quot; can be released by a
         * thread other than the owner (as semaphores have no notion of
         * ownership).  This can be useful in some specialized contexts, such
         * as deadlock recovery.
         *
         * <p>The constructor for this class optionally accepts a
         * <em>fairness</em> parameter. When set false, this class makes no
         * guarantees about the order in which threads acquire permits. In
         * particular, <em>barging</em> is permitted, that is, a thread
         * invoking {@link #acquire} can be allocated a permit ahead of a
         * thread that has been waiting - logically the new thread places itself at
         * the head of the queue of waiting threads. When fairness is set true, the
         * semaphore guarantees that threads invoking any of the {@link
         * #acquire() acquire} methods are selected to obtain permits in the order in
         * which their invocation of those methods was processed
         * (first-in-first-out; FIFO). Note that FIFO ordering necessarily
         * applies to specific internal points of execution within these
         * methods.  So, it is possible for one thread to invoke
         * {@code acquire} before another, but reach the ordering point after
         * the other, and similarly upon return from the method.
         * Also note that the untimed {@link #tryAcquire() tryAcquire} methods do not
         * honor the fairness setting, but will take any permits that are
         * available.
         *
         * <p>Generally, semaphores used to control resource access should be
         * initialized as fair, to ensure that no thread is starved out from
         * accessing a resource. When using semaphores for other kinds of
         * synchronization control, the throughput advantages of non-fair
         * ordering often outweigh fairness considerations.
         *
         * <p>This class also provides convenience methods to {@link
         * #acquire(int) acquire} and {@link #release(int) release} multiple
         * permits at a time. These methods are generally more efficient and
         * effective than loops. However, they do not establish any preference
         * order. For example, if thread A invokes {@code s.acquire(3}) and
         * thread B invokes {@code s.acquire(2)}, and two permits become
         * available, then there is no guarantee that thread B will obtain
         * them unless its acquire came first and Semaphore {@code s} is in
         * fair mode.
         *
         * <p>Memory consistency effects: Actions in a thread prior to calling
         * a "release" method such as {@code release()}
         * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
         * actions following a successful "acquire" method such as {@code acquire()}
         * in another thread.
         *
         * @since 1.5
         * @author Doug Lea
         */
        class Semaphore2 extends java.util.concurrent.Semaphore implements java.io.Serializable {
            @Serial
            private static final long serialVersionUID = 435778661600680210L;
            /** All mechanics via AbstractQueuedSynchronizer subclass */
            private final Sync sync;

            /**
             * Synchronization implementation for semaphore.  Uses AQS state
             * to represent permits. Subclassed into fair and nonfair
             * versions.
             */
            abstract class Sync extends AbstractQueuedSynchronizer2 {
                @Serial
                private static final long serialVersionUID = 1457457210091910933L;


                @Override
                public String debugText() {
                    return K_multi_threading$debugText();
                }
                Sync(int permits) {
                    setState(permits);
                }

                final int getPermits() {
                    return getState();
                }

                final int nonfairTryAcquireShared(int acquires) {
                    for (;;) {
                        int available = getState();
                        int remaining = available - acquires;
                        if (remaining < 0 ||
                                compareAndSetState(available, remaining))
                            return remaining;
                    }
                }

                protected final boolean tryReleaseShared(int releases) {
                    for (;;) {
                        int current = getState();
                        int next = current + releases;
                        if (next < current) // overflow
                            throw new Error("Maximum permit count exceeded");
                        if (compareAndSetState(current, next))
                            return true;
                    }
                }

                final void reducePermits(int reductions) {
                    for (;;) {
                        int current = getState();
                        int next = current - reductions;
                        if (next > current) // underflow
                            throw new Error("Permit count underflow");
                        if (compareAndSetState(current, next))
                            return;
                    }
                }

                final int drainPermits() {
                    for (;;) {
                        int current = getState();
                        if (current == 0 || compareAndSetState(current, 0))
                            return current;
                    }
                }
            }

            /**
             * NonFair version
             */
            final class NonfairSync extends Sync {
                @Serial
                private static final long serialVersionUID = -2694183684443567898L;

                NonfairSync(int permits) {
                    super(permits);
                }

                @Override
                public String debugText() {
                    return K_multi_threading$debugText()+"1";
                }

                protected int tryAcquireShared(int acquires) {
                    return nonfairTryAcquireShared(acquires);
                }
            }

            /**
             * Fair version
             */
            final class FairSync extends Sync {
                @Serial
                private static final long serialVersionUID = 2014642818796000944L;

                FairSync(int permits) {
                    super(permits);
                }

                @Override
                public String debugText() {
                    return K_multi_threading$debugText()+"1";
                }

                protected int tryAcquireShared(int acquires) {
                    for (;;) {
                        if (hasQueuedPredecessors())
                            return -1;
                        int available = getState();
                        int remaining = available - acquires;
                        if (remaining < 0 ||
                                compareAndSetState(available, remaining))
                            return remaining;
                    }
                }
        }

            /**
             * Creates a {@code Semaphore} with the given number of
             * permits and nonfair fairness setting.
             *
             * @param permits the initial number of permits available.
             *        This value may be negative, in which case releases
             *        must occur before any acquires will be granted.
             */
            public Semaphore2(int permits) {
                super(permits);
                sync = new NonfairSync(permits);
            }

            /**
             * Creates a {@code Semaphore} with the given number of
             * permits and the given fairness setting.
             *
             * @param permits the initial number of permits available.
             *        This value may be negative, in which case releases
             *        must occur before any acquires will be granted.
             * @param fair {@code true} if this semaphore will guarantee
             *        first-in first-out granting of permits under contention,
             *        else {@code false}
             */
            public Semaphore2(int permits, boolean fair) {
                super(permits, fair);
                sync = fair ? new FairSync(permits) : new NonfairSync(permits);
            }

            /**
             * Acquires a permit from this semaphore, blocking until one is
             * available, or the thread is {@linkplain Thread#interrupt interrupted}.
             *
             * <p>Acquires a permit, if one is available and returns immediately,
             * reducing the number of available permits by one.
             *
             * <p>If no permit is available then the current thread becomes
             * disabled for thread scheduling purposes and lies dormant until
             * one of two things happens:
             * <ul>
             * <li>Some other thread invokes the {@link #release} method for this
             * semaphore and the current thread is next to be assigned a permit; or
             * <li>Some other thread {@linkplain Thread#interrupt interrupts}
             * the current thread.
             * </ul>
             *
             * <p>If the current thread:
             * <ul>
             * <li>has its interrupted status set on entry to this method; or
             * <li>is {@linkplain Thread#interrupt interrupted} while waiting
             * for a permit,
             * </ul>
             * then {@link InterruptedException} is thrown and the current thread's
             * interrupted status is cleared.
             *
             * @throws InterruptedException if the current thread is interrupted
             */
            public void acquire() throws InterruptedException {
                sync.acquireSharedInterruptibly(1);
            }

            /**
             * Acquires a permit from this semaphore, blocking until one is
             * available.
             *
             * <p>Acquires a permit, if one is available and returns immediately,
             * reducing the number of available permits by one.
             *
             * <p>If no permit is available then the current thread becomes
             * disabled for thread scheduling purposes and lies dormant until
             * some other thread invokes the {@link #release} method for this
             * semaphore and the current thread is next to be assigned a permit.
             *
             * <p>If the current thread is {@linkplain Thread#interrupt interrupted}
             * while waiting for a permit then it will continue to wait, but the
             * time at which the thread is assigned a permit may change compared to
             * the time it would have received the permit had no interruption
             * occurred.  When the thread does return from this method its interrupt
             * status will be set.
             */
            public void acquireUninterruptibly() {
                sync.acquireShared(1);
            }

            /**
             * Acquires a permit from this semaphore, only if one is available at the
             * time of invocation.
             *
             * <p>Acquires a permit, if one is available and returns immediately,
             * with the value {@code true},
             * reducing the number of available permits by one.
             *
             * <p>If no permit is available then this method will return
             * immediately with the value {@code false}.
             *
             * <p>Even when this semaphore has been set to use a
             * fair ordering policy, a call to {@code tryAcquire()} <em>will</em>
             * immediately acquire a permit if one is available, whether or not
             * other threads are currently waiting.
             * This &quot;barging&quot; behavior can be useful in certain
             * circumstances, even though it breaks fairness. If you want to honor
             * the fairness setting, then use
             * {@link #tryAcquire(long, TimeUnit) tryAcquire(0, TimeUnit.SECONDS)}
             * which is almost equivalent (it also detects interruption).
             *
             * @return {@code true} if a permit was acquired and {@code false}
             *         otherwise
             */
            public boolean tryAcquire() {
                return sync.nonfairTryAcquireShared(1) >= 0;
            }

            /**
             * Acquires a permit from this semaphore, if one becomes available
             * within the given waiting time and the current thread has not
             * been {@linkplain Thread#interrupt interrupted}.
             *
             * <p>Acquires a permit, if one is available and returns immediately,
             * with the value {@code true},
             * reducing the number of available permits by one.
             *
             * <p>If no permit is available then the current thread becomes
             * disabled for thread scheduling purposes and lies dormant until
             * one of three things happens:
             * <ul>
             * <li>Some other thread invokes the {@link #release} method for this
             * semaphore and the current thread is next to be assigned a permit; or
             * <li>Some other thread {@linkplain Thread#interrupt interrupts}
             * the current thread; or
             * <li>The specified waiting time elapses.
             * </ul>
             *
             * <p>If a permit is acquired then the value {@code true} is returned.
             *
             * <p>If the current thread:
             * <ul>
             * <li>has its interrupted status set on entry to this method; or
             * <li>is {@linkplain Thread#interrupt interrupted} while waiting
             * to acquire a permit,
             * </ul>
             * then {@link InterruptedException} is thrown and the current thread's
             * interrupted status is cleared.
             *
             * <p>If the specified waiting time elapses then the value {@code false}
             * is returned.  If the time is less than or equal to zero, the method
             * will not wait at all.
             *
             * @param timeout the maximum time to wait for a permit
             * @param unit the time unit of the {@code timeout} argument
             * @return {@code true} if a permit was acquired and {@code false}
             *         if the waiting time elapsed before a permit was acquired
             * @throws InterruptedException if the current thread is interrupted
             */
            public boolean tryAcquire(long timeout, TimeUnit unit)
                    throws InterruptedException {
                return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
            }

            /**
             * Releases a permit, returning it to the semaphore.
             *
             * <p>Releases a permit, increasing the number of available permits by
             * one.  If any threads are trying to acquire a permit, then one is
             * selected and given the permit that was just released.  That thread
             * is (re)enabled for thread scheduling purposes.
             *
             * <p>There is no requirement that a thread that releases a permit must
             * have acquired that permit by calling {@link #acquire}.
             * Correct usage of a semaphore is established by programming convention
             * in the application.
             */
            public void release() {
                sync.releaseShared(1);
            }

            /**
             * Acquires the given number of permits from this semaphore,
             * blocking until all are available,
             * or the thread is {@linkplain Thread#interrupt interrupted}.
             *
             * <p>Acquires the given number of permits, if they are available,
             * and returns immediately, reducing the number of available permits
             * by the given amount. This method has the same effect as the
             * loop {@code for (int i = 0; i < permits; ++i) acquire();} except
             * that it atomically acquires the permits all at once:
             *
             * <p>If insufficient permits are available then the current thread becomes
             * disabled for thread scheduling purposes and lies dormant until
             * one of two things happens:
             * <ul>
             * <li>Some other thread invokes one of the {@link #release() release}
             * methods for this semaphore and the current thread is next to be assigned
             * permits and the number of available permits satisfies this request; or
             * <li>Some other thread {@linkplain Thread#interrupt interrupts}
             * the current thread.
             * </ul>
             *
             * <p>If the current thread:
             * <ul>
             * <li>has its interrupted status set on entry to this method; or
             * <li>is {@linkplain Thread#interrupt interrupted} while waiting
             * for a permit,
             * </ul>
             * then {@link InterruptedException} is thrown and the current thread's
             * interrupted status is cleared.
             * Any permits that were to be assigned to this thread are instead
             * assigned to other threads trying to acquire permits, as if
             * permits had been made available by a call to {@link #release()}.
             *
             * @param permits the number of permits to acquire
             * @throws InterruptedException if the current thread is interrupted
             * @throws IllegalArgumentException if {@code permits} is negative
             */
            public void acquire(int permits) throws InterruptedException {
                if (permits < 0) throw new IllegalArgumentException();
                sync.acquireSharedInterruptibly(permits);
            }

            /**
             * Acquires the given number of permits from this semaphore,
             * blocking until all are available.
             *
             * <p>Acquires the given number of permits, if they are available,
             * and returns immediately, reducing the number of available permits
             * by the given amount. This method has the same effect as the
             * loop {@code for (int i = 0; i < permits; ++i) acquireUninterruptibly();}
             * except that it atomically acquires the permits all at once:
             *
             * <p>If insufficient permits are available then the current thread becomes
             * disabled for thread scheduling purposes and lies dormant until
             * some other thread invokes one of the {@link #release() release}
             * methods for this semaphore and the current thread is next to be assigned
             * permits and the number of available permits satisfies this request.
             *
             * <p>If the current thread is {@linkplain Thread#interrupt interrupted}
             * while waiting for permits then it will continue to wait and its
             * position in the queue is not affected.  When the thread does return
             * from this method its interrupt status will be set.
             *
             * @param permits the number of permits to acquire
             * @throws IllegalArgumentException if {@code permits} is negative
             */
            public void acquireUninterruptibly(int permits) {
                if (permits < 0) throw new IllegalArgumentException();
                sync.acquireShared(permits);
            }

            /**
             * Acquires the given number of permits from this semaphore, only
             * if all are available at the time of invocation.
             *
             * <p>Acquires the given number of permits, if they are available, and
             * returns immediately, with the value {@code true},
             * reducing the number of available permits by the given amount.
             *
             * <p>If insufficient permits are available then this method will return
             * immediately with the value {@code false} and the number of available
             * permits is unchanged.
             *
             * <p>Even when this semaphore has been set to use a fair ordering
             * policy, a call to {@code tryAcquire} <em>will</em>
             * immediately acquire a permit if one is available, whether or
             * not other threads are currently waiting.  This
             * &quot;barging&quot; behavior can be useful in certain
             * circumstances, even though it breaks fairness. If you want to
             * honor the fairness setting, then use {@link #tryAcquire(int,
             * long, TimeUnit) tryAcquire(permits, 0, TimeUnit.SECONDS)}
             * which is almost equivalent (it also detects interruption).
             *
             * @param permits the number of permits to acquire
             * @return {@code true} if the permits were acquired and
             *         {@code false} otherwise
             * @throws IllegalArgumentException if {@code permits} is negative
             */
            public boolean tryAcquire(int permits) {
                if (permits < 0) throw new IllegalArgumentException();
                return sync.nonfairTryAcquireShared(permits) >= 0;
            }

            /**
             * Acquires the given number of permits from this semaphore, if all
             * become available within the given waiting time and the current
             * thread has not been {@linkplain Thread#interrupt interrupted}.
             *
             * <p>Acquires the given number of permits, if they are available and
             * returns immediately, with the value {@code true},
             * reducing the number of available permits by the given amount.
             *
             * <p>If insufficient permits are available then
             * the current thread becomes disabled for thread scheduling
             * purposes and lies dormant until one of three things happens:
             * <ul>
             * <li>Some other thread invokes one of the {@link #release() release}
             * methods for this semaphore and the current thread is next to be assigned
             * permits and the number of available permits satisfies this request; or
             * <li>Some other thread {@linkplain Thread#interrupt interrupts}
             * the current thread; or
             * <li>The specified waiting time elapses.
             * </ul>
             *
             * <p>If the permits are acquired then the value {@code true} is returned.
             *
             * <p>If the current thread:
             * <ul>
             * <li>has its interrupted status set on entry to this method; or
             * <li>is {@linkplain Thread#interrupt interrupted} while waiting
             * to acquire the permits,
             * </ul>
             * then {@link InterruptedException} is thrown and the current thread's
             * interrupted status is cleared.
             * Any permits that were to be assigned to this thread, are instead
             * assigned to other threads trying to acquire permits, as if
             * the permits had been made available by a call to {@link #release()}.
             *
             * <p>If the specified waiting time elapses then the value {@code false}
             * is returned.  If the time is less than or equal to zero, the method
             * will not wait at all.  Any permits that were to be assigned to this
             * thread, are instead assigned to other threads trying to acquire
             * permits, as if the permits had been made available by a call to
             * {@link #release()}.
             *
             * @param permits the number of permits to acquire
             * @param timeout the maximum time to wait for the permits
             * @param unit the time unit of the {@code timeout} argument
             * @return {@code true} if all permits were acquired and {@code false}
             *         if the waiting time elapsed before all permits were acquired
             * @throws InterruptedException if the current thread is interrupted
             * @throws IllegalArgumentException if {@code permits} is negative
             */
            public boolean tryAcquire(int permits, long timeout, TimeUnit unit)
                    throws InterruptedException {
                if (permits < 0) throw new IllegalArgumentException();
                return sync.tryAcquireSharedNanos(permits, unit.toNanos(timeout));
            }

            /**
             * Releases the given number of permits, returning them to the semaphore.
             *
             * <p>Releases the given number of permits, increasing the number of
             * available permits by that amount.
             * If any threads are trying to acquire permits, then one thread
             * is selected and given the permits that were just released.
             * If the number of available permits satisfies that thread's request
             * then that thread is (re)enabled for thread scheduling purposes;
             * otherwise the thread will wait until sufficient permits are available.
             * If there are still permits available
             * after this thread's request has been satisfied, then those permits
             * are assigned in turn to other threads trying to acquire permits.
             *
             * <p>There is no requirement that a thread that releases a permit must
             * have acquired that permit by calling {@link Semaphore2#acquire acquire}.
             * Correct usage of a semaphore is established by programming convention
             * in the application.
             *
             * @param permits the number of permits to release
             * @throws IllegalArgumentException if {@code permits} is negative
             */
            public void release(int permits) {
                if (permits < 0) throw new IllegalArgumentException();
                sync.releaseShared(permits);
            }

            /**
             * Returns the current number of permits available in this semaphore.
             *
             * <p>This method is typically used for debugging and testing purposes.
             *
             * @return the number of permits available in this semaphore
             */
            public int availablePermits() {
                return sync.getPermits();
            }

            /**
             * Acquires and returns all permits that are immediately
             * available, or if negative permits are available, releases them.
             * Upon return, zero permits are available.
             *
             * @return the number of permits acquired or, if negative, the
             * number released
             */
            public int drainPermits() {
                return sync.drainPermits();
            }

            /**
             * Shrinks the number of available permits by the indicated
             * reduction. This method can be useful in subclasses that use
             * semaphores to track resources that become unavailable. This
             * method differs from {@code acquire} in that it does not block
             * waiting for permits to become available.
             *
             * @param reduction the number of permits to remove
             * @throws IllegalArgumentException if {@code reduction} is negative
             */
            protected void reducePermits(int reduction) {
                if (reduction < 0) throw new IllegalArgumentException();
                sync.reducePermits(reduction);
            }

            /**
             * Returns {@code true} if this semaphore has fairness set true.
             *
             * @return {@code true} if this semaphore has fairness set true
             */
            public boolean isFair() {
                return sync instanceof FairSync;
            }

            /**
             * Queries whether any threads are waiting to acquire. Note that
             * because cancellations may occur at any time, a {@code true}
             * return does not guarantee that any other thread will ever
             * acquire.  This method is designed primarily for use in
             * monitoring of the system state.
             *
             * @return {@code true} if there may be other threads waiting to
             *         acquire the lock
             */
            //public final boolean hasQueuedThreads() {
            //    return sync.hasQueuedThreads();
            //}

            /**
             * Returns an estimate of the number of threads waiting to acquire.
             * The value is only an estimate because the number of threads may
             * change dynamically while this method traverses internal data
             * structures.  This method is designed for use in monitoring
             * system state, not for synchronization control.
             *
             * @return the estimated number of threads waiting for this lock
             */
            //public final int getQueueLength() {
            //    return sync.getQueueLength();
            //}

            /**
             * Returns a collection containing threads that may be waiting to acquire.
             * Because the actual set of threads may change dynamically while
             * constructing this result, the returned collection is only a best-effort
             * estimate.  The elements of the returned collection are in no particular
             * order.  This method is designed to facilitate construction of
             * subclasses that provide more extensive monitoring facilities.
             *
             * @return the collection of threads
             */
            protected Collection<Thread> getQueuedThreads() {
                return sync.getQueuedThreads();
            }

            /**
             * Returns a string identifying this semaphore, as well as its state.
             * The state, in brackets, includes the String {@code "Permits ="}
             * followed by the number of permits.
             *
             * @return a string identifying this semaphore, as well as its state
             */
            public String toString() {
                return super.toString() + "[Permits = " + sync.getPermits() + "]";
            }
        }

        return new Semaphore2($$$i$$$);
    }
}
