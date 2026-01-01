package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class AddArgSynchronized_Asm extends ITransformer2 {

    public record ReadBuf(String[] data, int index) {

    }
    public final List<ReadBuf> stringsList = new ArrayList<>(List.of(
            new ReadBuf(ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.protoChunkToFullChunk(Lnet/minecraft/server/level/ChunkHolder;)Ljava/util/concurrent/CompletableFuture;"), 1)
    ));
    {
    }

    int posfilter = Opcodes.ACC_PUBLIC;
    int negfilter = /*Opcodes.ACC_STATIC |*/ Opcodes.ACC_SYNTHETIC/* | Opcodes.ACC_NATIVE */| Opcodes.ACC_ABSTRACT
            /*| Opcodes.ACC_ABSTRACT*/ | Opcodes.ACC_BRIDGE;


    @Override
    public @NotNull ClassNode transform(ClassNode input) {

        for (ReadBuf strings : stringsList) {
            String[] data = strings.data;
            if (input.name.equals(data[0])){
                boolean debug_add1 = false;
                for (MethodNode method : input.methods) {
                    if ((method.name.equals(data[1]) && method.desc.equals(data[2]))) {
                        debug_add1 = true;
                        log.info("add {} synchronized", strings);
                        LabelNode startLabel = new LabelNode();
                        startLabel.getLabel().info = startLabel;
                        LabelNode endLabel = new LabelNode();
                        endLabel.getLabel().info = endLabel;
                        InsnList il = method.instructions;
                        int maxLocals = method.maxLocals;
                        method.visitLocalVariable("AddArgSynchronized_Asm", "Ljava/lang/Object;", null, endLabel.getLabel(), endLabel.getLabel(), maxLocals);
                        method.maxLocals++;




                        InsnList start;
                        InsnList end;
                        start = new InsnList();
                        start.add(startLabel);
                        start.add(new VarInsnNode(Opcodes.ALOAD, strings.index));
                        start.add(new VarInsnNode(Opcodes.ASTORE, maxLocals));
                        start.add(new VarInsnNode(Opcodes.ALOAD, maxLocals));
                        start.add(new InsnNode(Opcodes.MONITORENTER));
                        end = new InsnList();
                        end.add(new VarInsnNode(Opcodes.ALOAD, maxLocals));
                        end.add(new InsnNode(Opcodes.MONITOREXIT));
                        AbstractInsnNode ain = il.getFirst();
                        while (ain != null) {
                            if (ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                                    || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                                    || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                                il.insertBefore(ain, end);
                                end = new InsnList();
                                end.add(new VarInsnNode(Opcodes.ALOAD, maxLocals));
                                end.add(new InsnNode(Opcodes.MONITOREXIT));
                            }
                            ain = ain.getNext();
                        }
                        il.insert(start);
                    }
                }

                if (!debug_add1){
                    throw new RuntimeException("Not mapping error: " + strings);
                }
            }
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {

        File f = new File("config/K_multi_threading-arg-sync-Method-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals("")))
                        .map(name -> {
                            String[] split = name.split("\\|");
                            if (split.length != 2) {
                                throw new RuntimeException("错误的格式[class.method|index]" + name);
                            }
                            return new ReadBuf(ForgeAsm.minecraft_map.mapMethod(split[0]), Integer.parseInt(split[1]));
                        }).forEach(stringsList::add);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write("""
                        // 使用//或#屏蔽
                        // 这个文件是用于对单独的函数添加对输入参数的synchronized
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache.removeEntity(Lnet/minecraft/world/entity/Entity;)V|index
                        //net/minecraft/server/level/ServerChunkCache.removeEntity(Lnet/minecraft/world/entity/Entity;)V|1
                        //跟映射表格式一样simple
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> list = new ArrayList<>();
        for (var strings : stringsList) {
            String[] data = strings.data;
            if (!list.contains(data[0])) {
                list.add(data[0]);
            }
        }

        return Set.of(list.stream().toArray(String[]::new));
    }
}
