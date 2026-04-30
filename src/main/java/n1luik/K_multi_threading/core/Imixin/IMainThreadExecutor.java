package n1luik.K_multi_threading.core.Imixin;

import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;

import java.util.concurrent.Executor;

public interface IMainThreadExecutor extends Executor {
    void k_multi_threading$pushThread();
    boolean k_multi_threading$notCallPollTask();
    boolean isCall();
    void setM2(boolean m2);
    void k_multi_threading$setMultiThreading(int size);
    void KMT$setParaServerChunkProvider(ParaServerChunkProvider paraServerChunkProvider);
    Thread getCallThread();
    Object getLockCall();
}
