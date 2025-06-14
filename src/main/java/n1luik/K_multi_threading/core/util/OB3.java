package n1luik.K_multi_threading.core.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OB3<T1, T2, T3> {
    public T1 t1;
    public T2 t2;
    public T3 t3;

    public OB3() {
    }

    public OB3(T1 t1, T2 t2, T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }
}
