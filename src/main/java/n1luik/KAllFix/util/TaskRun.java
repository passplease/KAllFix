package n1luik.KAllFix.util;

import n1luik.K_multi_threading.core.util.Unsafe;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

public class TaskRun implements Executor {
    public final Thread TaskRun;
    protected volatile boolean isRunning = false;
    protected final Object StateLock = new Object();
    private final ReentrantLock isPark = new ReentrantLock();
    protected final CopyOnWriteArrayList<Runnable> Tasks = new CopyOnWriteArrayList<>();
    public TaskRun(String name) {
        TaskRun = new Thread(name){
            @Override
            public void run(){
                while (true) {
                    synchronized (StateLock) {
                        isRunning = true;
                    }
                    while (!Tasks.isEmpty()){
                        Tasks.remove(0).run();
                    }
                    synchronized (StateLock) {
                        if (!Tasks.isEmpty())continue;
                        isRunning = false;
                    }
                    isPark.lock();
                    if (!Tasks.isEmpty())continue;
                    Unsafe.unsafe.park(false, 0L);
                    isPark.unlock();
                }
            }
        };
    }

    @Override
    public void execute(@NotNull Runnable command) {
        synchronized (StateLock) {
            Tasks.add(command);
            if (isPark.isLocked()) {
                Unsafe.unsafe.unpark(TaskRun);
            }else if (isPark.tryLock()) {
                isPark.unlock();
            }else {
                Unsafe.unsafe.unpark(TaskRun);
            }
        }
    }
}
