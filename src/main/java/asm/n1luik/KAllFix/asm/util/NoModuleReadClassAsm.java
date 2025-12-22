package asm.n1luik.KAllFix.asm.util;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Set;

public class NoModuleReadClassAsm extends ITransformer2 {
    private final String file;
    private final String version;
    private final String target;
    private final Set<String> classs;
    
    public NoModuleReadClassAsm(String file, String version, String target, Set<String> classs) {
        this.file = file;
        this.version = version;
        this.target = target;
        this.classs = classs;
    }
    @Override
    public @NotNull ClassNode transform(ClassNode input) {
        try {
            ClassNode classNode = new ClassNode();
            byte[] bytes = NoModuleReadClassAsm.class.getResourceAsStream("/asm/KAllFix.fix/"+file+"/"+version+"/"+target+".fix").readAllBytes();
            bytes[0] = (byte)0xCA;
            bytes[1] = (byte)0xFE;
            bytes[2] = (byte)0xBA;
            bytes[3] = (byte)0xBE;
            new ClassReader(bytes).accept(classNode, 0);
            classNode.module = input.module;
            return classNode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return classs;
    }
}
