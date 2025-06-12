package n1luik.K_multi_threading.core.dataCollectors.data;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class MapConcurrentData {
    public String className;
    public boolean fixNull;
    public List<String> mappingFields;
    public List<MethodInfo> mappingMethods;
    public MapConcurrentData() {

    }



    @AllArgsConstructor
    public static class MethodInfo {
        public @Nullable String name;
        public @Nullable String desc;
        public boolean mappingLocal;
        public boolean mappingAll;
        public MethodInfo() {

        }
    }
}
