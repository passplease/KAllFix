package asm.n1luik.K_multi_threading.asm;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class AllAddSynchronized_Asm implements ITransformer<ClassNode> {
    public static final List<String> stringsList = new ArrayList<>(List.of(
            //canary
            ForgeAsm.minecraft_map.mapClass("com/abdelaziz/canary/common/world/listeners/WorldBorderListenerOnceMulti"),
            //lithium
            ForgeAsm.minecraft_map.mapClass("me/jellysquid/mods/lithium/common/world/listeners/WorldBorderListenerOnceMulti")
    ));

    int posfilter = Opcodes.ACC_PUBLIC;
    int negfilter = /*Opcodes.ACC_STATIC |*/ Opcodes.ACC_SYNTHETIC/* | Opcodes.ACC_NATIVE */| Opcodes.ACC_ABSTRACT
            /*| Opcodes.ACC_ABSTRACT*/ | Opcodes.ACC_BRIDGE;

    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {

        for (String strings : stringsList) {
            if (input.name.equals(strings)){
                boolean debug_add1 = false;
                for (MethodNode method : input.methods) {
                    debug_add1 = true;
                    if (!(method.name.equals("<init>") || method.name.equals("<clinit>"))){
                        if ((input.access & Opcodes.ACC_STATIC) == 0) {
                            if ((input.access & Opcodes.ACC_INTERFACE) == 0) {
                                log.info("add [{}, {}, {}] synchronized", input.name, method.name, method.desc);

                                if (!input.name.contains("$")) {
                                    method.access |= Opcodes.ACC_SYNCHRONIZED;
                                } else {
                                    String parent = null;
                                    String map = null;
                                    for (FieldNode fn : input.fields) {
                                        if (fn.name.equals("this$0") || ForgeAsm.srg$Forge$_map.mapField(input.name + "." + fn.name)[1].equals("this$0")) {

                                            map = fn.name;
                                            parent = fn.desc;
                                        }
                                    }
                                    if (parent == null || map == null) {
                                        method.access |= Opcodes.ACC_SYNCHRONIZED;
                                        log.error("Inner class faliure; parent not found " + (parent == null ? "null" : parent) + " " + (map == null ? "null" : map) + " [{}, {}, {}]", input.name, method.name, method.desc);
                                        continue;
                                    }
                                    InsnList start;
                                    InsnList end;
                                    if ((method.access & negfilter) == 0 && !method.name.equals("<init>")) {
                                        start = new InsnList();
                                        start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        start.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                        start.add(new InsnNode(Opcodes.MONITORENTER));
                                        end = new InsnList();
                                        end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                        end.add(new InsnNode(Opcodes.MONITOREXIT));
                                        InsnList il = method.instructions;
                                        AbstractInsnNode ain = il.getFirst();
                                        while (ain != null) {
                                            if (ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                                                    || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                                                    || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                                                il.insertBefore(ain, end);
                                                end = new InsnList();
                                                end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                                end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                                end.add(new InsnNode(Opcodes.MONITOREXIT));
                                            }
                                            ain = ain.getNext();
                                        }
                                        il.insertBefore(il.getFirst(), start);
                                    }
                                    log.info("sync_fu " + input.name + " InnerClass Transformer Complete");
                                }
                            } else {
                                log.info("add [{}, {}, {}] synchronized", input.name, method.name, method.desc);
                                InsnList start;
                                InsnList end;
                                if ((method.access & negfilter) == 0 && !method.name.equals("<init>")) {
                                    start = new InsnList();
                                    start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    start.add(new InsnNode(Opcodes.MONITORENTER));
                                    end = new InsnList();
                                    end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    end.add(new InsnNode(Opcodes.MONITOREXIT));
                                    InsnList il = method.instructions;
                                    AbstractInsnNode ain = il.getFirst();
                                    while (ain != null) {
                                        if (ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                                                || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                                                || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                                            il.insertBefore(ain, end);
                                            end = new InsnList();
                                            end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                            end.add(new InsnNode(Opcodes.MONITOREXIT));
                                        }
                                        ain = ain.getNext();
                                    }
                                    il.insertBefore(il.getFirst(), start);
                                }
                            }
                        } else {
                            input.access &= Opcodes.ACC_SYNTHETIC;
                        }
                    }
                }

                if (!debug_add1){
                    log.warn("Not mapping error: {}" , input.name);
                }
            }
        }


        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {

        File f = new File("config/K_multi_threading-all-sync-ModMethod-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals("")))
                        .map(ForgeAsm.minecraft_map::mapClass).forEach(stringsList::add);
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
                        // 这个文件是用于对单独的函数添加synchronized
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache
                        //直接写类名
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> list = new ArrayList<>();
        for (String strings : stringsList) {
            if (!list.contains(strings)) {
                list.add(strings);
            }
        }

        return Set.of(list.stream().map(Target::targetClass).toArray(Target[]::new));
    }
}
