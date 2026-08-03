package n1luik.K_multi_threading.debug;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class DebugClass1<T> extends ConcurrentLinkedQueue<T> {
    public abstract void run(T t);

    public void runAll(){
        while (true){
            if (!isEmpty()) {
                T poll = poll();
                if (poll != null)run(poll);
            }
        }
    }
}
