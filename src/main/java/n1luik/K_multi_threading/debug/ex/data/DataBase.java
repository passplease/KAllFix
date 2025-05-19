package n1luik.K_multi_threading.debug.ex.data;

import java.util.ArrayList;
import java.util.List;

public class DataBase {
    public JvmCallLogData data;
    public long time;
    public List<DataBase> next;

    public DataBase() {

    }

    public DataBase(JvmCallLogData data, long time, List<DataBase> next) {
        this.data = data;
        this.time = time;
        this.next = next;
    }

    public DataBase copy() {
        return new DataBase(data.copy(), time, new ArrayList<>(next.stream().map(DataBase::copy).toList()));
    }


}
