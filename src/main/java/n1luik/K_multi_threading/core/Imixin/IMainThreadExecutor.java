package n1luik.K_multi_threading.core.Imixin;

public interface IMainThreadExecutor {
    void pushThread();
    boolean notCallPollTask();
    boolean isCall();
    void setM2(boolean m2);
    void setMultiThreading(int size);
    Thread getCallThread();
}
