package asm.n1luik.K_multi_threading.asm.mod.valkyrienskies;

import asm.n1luik.KAllFix.asm.util.StackUtil;
import asm.n1luik.K_multi_threading.asm.Util;
import asm.n1luik.K_multi_threading.asm.data.MapConcurrentData;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import java.util.Set;

public class ShipObjectServerWorld_Asm  extends ITransformer2{

    public static AbstractInsnNode findFieldWrite(AbstractInsnNode currentInsn, String owner, String name) {
        while (currentInsn != null) {
            if (currentInsn instanceof FieldInsnNode fieldInsnNode && fieldInsnNode.owner.equals(owner) && fieldInsnNode.name.equals(name)) {
                return currentInsn;
            }
            currentInsn = currentInsn.getPrevious();
        }
        return null;

    }
    public static class Data{
        public int fileHash1;
        public MapConcurrentData mapConcurrentData1;
    }
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {
        boolean debug_add1 = false;
        boolean debug_add2 = false;
        ClassNode d2 = new ClassNode();
        input.accept(d2);
        int i = Arrays.hashCode(Util.toMixinClassHashCheckDataByte(d2));
        System.setProperty("K_multi_threading_ShipObjectServerWorld_Asm_Id", Integer.toString(i));
        Data data = new Data();
        data.fileHash1 = i;

        for (MethodNode method : d2.methods) {
            if (method.name.equals("<init>")) {
                AbstractInsnNode shipToVoxelUpdates = findFieldWrite(method.instructions.getLast(), d2.name, "shipToVoxelUpdates").getPrevious();
                if (shipToVoxelUpdates != null) {
                    if (shipToVoxelUpdates instanceof MethodInsnNode methodInsnNode) {
                        data.mapConcurrentData1 = new MapConcurrentData(methodInsnNode.owner.replace('/', '.'), false, List.of(),
                                List.of(new MapConcurrentData.MethodInfo(methodInsnNode.name, methodInsnNode.desc, true, true)));
                    }
                    break;
                }else {
                    throw new RuntimeException("没有找到参数：shipToVoxelUpdates");
                }
            }
        }
        System.setProperty("K_multi_threading_ShipObjectServerWorld_Asm_Json", Util.GSON.toJson(data));

        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>")) {
                debug_add1 = true;
                InsnList instructions = method.instructions;
                InsnList al2 = method.instructions = new InsnList();
                AbstractInsnNode abstractInsnNode = StackUtil.previousStack(1, StackUtil.findField("shipToVoxelUpdates", instructions), true);
                for (AbstractInsnNode instruction : instructions) {
                    if (abstractInsnNode == instruction){
                        if (instruction instanceof MethodInsnNode methodInsnNode) {
                            debug_add2 = true;
                            al2.add(new MethodInsnNode(methodInsnNode.getOpcode(),
                                    "n1luik/K_multi_threading/fix/valkyrienskies/LongObjConcurrentMapUtil",
                                    "create",
                                    methodInsnNode.desc));
                        }else {
                            al2.add(instruction);
                        }
                    }else {
                        al2.add(instruction);
                    }
                }

            }
        }
        //ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        //input.accept(classWriter);
        //try {
        //    java.nio.file.Files.write(java.nio.file.Paths.get("./ValkyrienSkies.class"), classWriter.toByteArray());
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        //                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
        //                                    "concurrentMap",
        //                                    "(Ljava/util/Map;)Ljava/util/concurrent/ConcurrentHashMap;"))
        if (!(debug_add1 && debug_add2)){
            throw new RuntimeException("Not mapping error: org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld: %s %s ".formatted(debug_add1, debug_add2));
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld");
    }
}
