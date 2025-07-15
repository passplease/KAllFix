package asm.n1luik.K_multi_threading.asm;

import appeng.shaded.flatbuffers.ReadBuf;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;
import java.util.function.IntFunction;


@Slf4j
public class IndependenceAddSynchronized_Asm implements ITransformer<ClassNode> {
    public final Map<String ,List<MethodNameInfo[]>> data = new HashMap<>();
    public final List<String> targetClass = new ArrayList<>();


    @Override
    public @NotNull Set<Target> targets() {
        List<ReadBuf> list2 = new ArrayList<>();


        list2.addAll(List.of(
                //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.startTickingChunk(Lnet/minecraft/world/level/chunk/LevelChunk;)V")
                new ReadBuf(ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V"), "kmt-ServerLevel_blockEvent"),
                new ReadBuf(ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.runBlockEvents()V"), "kmt-ServerLevel_blockEvent"),
                new ReadBuf(ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.addPlayer(Lnet/minecraft/core/SectionPos;Lnet/minecraft/server/level/ServerPlayer;)V"), "kmt-fixPlayer"),
                new ReadBuf(ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.removePlayer(Lnet/minecraft/core/SectionPos;Lnet/minecraft/server/level/ServerPlayer;)V"), "kmt-fixPlayer"),
                new ReadBuf(ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.startTickingChunk(Lnet/minecraft/world/level/chunk/LevelChunk;)V"), null),
                new ReadBuf(ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkHolder.broadcastChanges(Lnet/minecraft/world/level/chunk/LevelChunk;)V"), null),
                new ReadBuf(ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.addFreshBlockEntities(Ljava/util/Collection;)V"), null)
        ));
        File f = new File("config/K_multi_threading-independence-sync-Method-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals("")))
                        .forEach(name -> {
                            String[] split = name.split(":", 2);

                            String[] strings = ForgeAsm.minecraft_map.mapMethod(split[0]);
                            if (split.length > 1) {
                                list2.add(new ReadBuf(strings, split[1]));
                            }else {
                                list2.add(new ReadBuf(strings, null));
                            }
                        });
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
                        // 这个文件是用于对单独的函数添加synchronized但是是独立的但是可以在数据后面加“:”设置组，可以有空格
                        // 不同的类组是分开的哪怕同名也是互相独立的
                        // 组名尽量不要使用kmt-开头可能会跟内置的数据重叠
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache.removeEntity(Lnet/minecraft/world/entity/Entity;)V
                        
                        //net/minecraft/server/level/ServerChunkCache.removeEntity(Lnet/minecraft/world/entity/Entity;)V:a
                        //net/minecraft/server/level/ServerChunkCache._removeEntity()V:a
                        //设置多个组
                        //net/minecraft/server/level/ServerChunkCache._removeEntity()V:a
                        //net/minecraft/server/level/ServerChunkCache._removeEntity()V:b
                        //跟映射表格式一样simple
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String, Map<String, List<MethodNameInfo>>> map = new HashMap<>();
        for (ReadBuf readBuf : list2) {
            map.computeIfAbsent(readBuf.data()[0], k -> new HashMap<>())
                    .computeIfAbsent(readBuf.group(), k -> new ArrayList<>())
                    .add(new MethodNameInfo(readBuf.data()[1], readBuf.data()[2]));
        }

        //List<String> list = new ArrayList<>();
        //for (String[] strings : stringsList) {
        //    if (!list.contains(strings[0])) {
        //        list.add(strings[0]);
        //    }
        //}
        IntFunction<MethodNameInfo[]> aNew = MethodNameInfo[]::new;
        for (Map.Entry<String, Map<String, List<MethodNameInfo>>> stringMapEntry : map.entrySet()) {
            targetClass.add(stringMapEntry.getKey());

            Collection<List<MethodNameInfo>> values = stringMapEntry.getValue().values();
            List<MethodNameInfo[]> methodNameInfos = new ArrayList<>(values.size());
            for (List<MethodNameInfo> methodNameInfoList : values) {
                methodNameInfos.add(methodNameInfoList.toArray(aNew));
            }
            data.put(stringMapEntry.getKey(), methodNameInfos);
        }

        return Set.of(map.keySet().stream().map(Target::targetClass).toArray(Target[]::new));
    }


    int posfilter = Opcodes.ACC_PUBLIC;
    int negfilter = Opcodes.ACC_STATIC | /*Opcodes.ACC_SYNTHETIC | Opcodes.ACC_NATIVE |*/ Opcodes.ACC_ABSTRACT
            /*| Opcodes.ACC_ABSTRACT*/ | Opcodes.ACC_BRIDGE;


    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        try {

            if (targetClass.contains(input.name) && !input.name.contains("$")) {
                List<MethodNameInfo[]> list = data.get(input.name);
                assert list != null;
                for (MethodNameInfo[] methodNameInfos : list) {


                    String name = ("K_multi_threading$locked$" + Arrays.hashCode(methodNameInfos) + "L" +
                            methodNameInfos[0].desc().hashCode()).replace("-", "_");//.substring(1)
                    boolean reTest = true;
                    //防止变量重名
                    while (reTest){
                        reTest = false;
                        for (FieldNode fieldNode : input.fields) {
                            if (fieldNode.name.equals(name)) {
                                name = name + name.hashCode();
                                reTest = true;
                                break;
                            }
                        }
                    }
                    //.replaceAll("[;)/\\[]", "");

                    boolean createInit = false;
                    for (MethodNameInfo methodNameInfo : methodNameInfos) {
                        boolean debug_add1 = false;
                        boolean debug_add2 = false;

                        if ((input.access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_STATIC)) == 0) {
                            for (MethodNode method : input.methods) {//防止没有函数然后还添加了，会导致浪费一定的资源
                                if ((method.name.equals(methodNameInfo.name) && method.desc.equals(methodNameInfo.desc))) {
                                    debug_add2 = true;
                                    createInit = true;
                                    break;
                                }
                            }
                        }

                        for (MethodNode method : input.methods) {
                            if ((method.name.equals(methodNameInfo.name) && method.desc.equals(methodNameInfo.desc))) {
                                debug_add1 = true;
                                log.info("add [{}, {}] synchronized", methodNameInfo.name, methodNameInfo.desc);

                                InsnList start;
                                InsnList end;
                                if ((input.access & Opcodes.ACC_INTERFACE) == 0) {
                                    if ((input.access & Opcodes.ACC_STATIC) == 0) {
                                        if ((method.access & negfilter) == 0 && !method.name.equals("<init>")) {
                                            start = new InsnList();
                                            start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                            start.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Ljava/lang/Object;"));
                                            start.add(new InsnNode(Opcodes.MONITORENTER));
                                            end = new InsnList();
                                            end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                            end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Ljava/lang/Object;"));
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
                                                    end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Ljava/lang/Object;"));
                                                    end.add(new InsnNode(Opcodes.MONITOREXIT));
                                                }
                                                ain = ain.getNext();
                                            }
                                            il.insertBefore(il.getFirst(), start);
                                            method.maxStack++;
                                        }
                                    } else {
                                        input.access &= Opcodes.ACC_SYNTHETIC;
                                    }
                                } else {
                                    log.info("add [{}, {}] synchronized", methodNameInfo.name, methodNameInfo.desc);
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
                            }
                        }

                        if (!debug_add1 && !debug_add2) {
                            throw new RuntimeException("Not mapping error: " + methodNameInfo.name + " " + methodNameInfo.desc);
                        }

                    }

                    if (createInit) {
                        input.visitField(Opcodes.ACC_PRIVATE/* | Opcodes.ACC_FINAL*/, name
                                , "Ljava/lang/Object;", null, null);
                        for (MethodNode method2 : input.methods) {
                            if (method2.name.equals("<init>")) {
                                AbstractInsnNode[] array = method2.instructions.toArray();
                                InsnList instructions = new InsnList();
                                method2.instructions = instructions;
                                for (AbstractInsnNode instruction : array) {
                                    //确认是不是当前类的init
                                    if (instruction instanceof MethodInsnNode methodInsnNode && methodInsnNode.name.equals("<init>")) {
                                        if (methodInsnNode.owner.equals(input.superName)) {
                                            instructions.add(instruction);
                                            instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                            instructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Object"));
                                            instructions.add(new InsnNode(Opcodes.DUP));
                                            instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
                                            instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, input.name, name, "Ljava/lang/Object;"));

                                        } else instructions.add(instruction);
                                    } else instructions.add(instruction);
                                }
                                method2.maxStack+=2;
                            }
                        }
                    }
                }
            }


            return input;
        }catch (Throwable e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    public record ReadBuf(String[] data, @Nullable String group) {

    }

    public record MethodNameInfo(String name, String desc) {

    }

    //public record NameInfo(String className, MethodNameInfo[] data) {
    //}
}
