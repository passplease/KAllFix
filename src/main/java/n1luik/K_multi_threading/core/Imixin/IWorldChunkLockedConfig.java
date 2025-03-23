package n1luik.K_multi_threading.core.Imixin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface IWorldChunkLockedConfig {
    void pushThread(long id);
    long pushThread();
    void pop(long id);
    void pop();
    List<Thread> getGeneratorThread1();
    Thread getGeneratorThread2();
    void execTasks();
    void execTask(Runnable task);
    void execWaitTask(Runnable task);
}
