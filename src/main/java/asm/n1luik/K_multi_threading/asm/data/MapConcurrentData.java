package asm.n1luik.K_multi_threading.asm.data;

import asm.n1luik.K_multi_threading.asm.AddMapConcurrent_ASM;
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
    public AddMapConcurrent_ASM.AsmTarget getAsmTarget() {
        return new AddMapConcurrent_ASM.AsmTarget(className, fixNull, mappingFields.toArray(String[]::new), mappingMethods.stream().map(methodInfo -> new AddMapConcurrent_ASM.MethodInfo(methodInfo.name, methodInfo.desc, methodInfo.mappingLocal, methodInfo.mappingAll)).toArray(AddMapConcurrent_ASM.MethodInfo[]::new));
    }
}
