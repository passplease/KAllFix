package asm.n1luik.K_multi_threading.asm;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;

@Slf4j
public class AddMapConcurrent_ASM implements ITransformer<ClassNode> {
    private final static AsmTarget Empty = new AsmTarget("", false);
    public final List<AsmTarget> stringsList = new ArrayList<>(List.of(
            new AsmTarget("net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler", false),
            new AsmTarget("mekanism.common.recipe.lookup.cache.type.BaseInputCache", false),
            new AsmTarget("net.minecraft.world.level.chunk.ChunkAccess", false),
            new AsmTarget("net.minecraft.util.ClassInstanceMultiMap", false),
            new AsmTarget("net.minecraft.world.level.entity.EntitySectionStorage", false),
            new AsmTarget("net.minecraft.world.level.entity.EntityLookup", false),
            new AsmTarget("appeng.hooks.ticking.TickHandler", true),
            new AsmTarget("net.minecraft.world.entity.ai.village.poi.PoiManager", false),
            new AsmTarget("appeng.me.service.PathingService", false),
            new AsmTarget("me.desht.pneumaticcraft.common.drone.DroneClaimManager", false),
            new AsmTarget("com.github.alexthe666.iceandfire.entity.util.MyrmexHive", false),
            new AsmTarget("baguchan.tofucraft.CommonEvents", false),
            new AsmTarget("mekanism.common.lib.transmitter.TransmitterNetworkRegistry", false),
            new AsmTarget("com.github.alexthe666.alexsmobs.event.ServerEvents", false),
            new AsmTarget("com.teammoeg.caupona.CPCommonBootStrap", false)
    ));
    public final Map<String, AsmTarget> nameMap = new HashMap<>();
    {
        for (AsmTarget asmTarget : stringsList) {
            nameMap.put(asmTarget.className.replace(".", "/"), asmTarget);
        }
    }


    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        log.info("[{}]", input.name);

        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>") || method.name.equals("<clinit>")){
                AbstractInsnNode[] abstractInsnNodes = method.instructions.toArray();
                method.instructions.clear();
                boolean add = false;
                for (AbstractInsnNode instruction : abstractInsnNodes) {
                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode) {
                        switch (methodInsnNode.owner) {
                            case "com/google/common/collect/Maps":
                                if (methodInsnNode.name.equals("newHashMap")) {
                                    add = true;
                                    if (nameMap.getOrDefault(input.name, Empty).fixNull){
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap"));
                                        method.instructions.add(new InsnNode(Opcodes.DUP));
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap", "<init>", "()V", false));
                                    }else{
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap"));
                                        method.instructions.add(new InsnNode(Opcodes.DUP));
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false));
                                    }
                                } else {
                                    method.instructions.add(instruction);
                                }
                                break;
                            case "com/google/common/collect/Sets":
                                if (methodInsnNode.name.equals("newHashSet")) {
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/concurrent/ConcurrentHashMap", "newKeySet", "()Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", false));
                                    //add = true;
                                    //method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArraySet"));
                                    //method.instructions.add(new InsnNode(Opcodes.DUP));
                                    //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArraySet", "<init>", "()V", false));
                                } else {
                                    method.instructions.add(instruction);
                                }
                                break;
                            case "com/google/common/collect/Lists":
                                if (methodInsnNode.name.equals("newArrayList")) {
                                    add = true;
                                    method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArrayList"));
                                    method.instructions.add(new InsnNode(Opcodes.DUP));
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
                                } else {
                                    method.instructions.add(instruction);
                                }
                                break;
                            default:
                                method.instructions.add(instruction);

                                break;
                        }
                    } else if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode) {
                        switch (methodInsnNode.owner) {
                            case "it/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap":
                                if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectLinkedOpenHashMap", "<init>", "()V", false));
                                } else {
                                    method.instructions.add(instruction);
                                }
                                break;
                            case "java/util/HashMap":
                                if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                    if (nameMap.getOrDefault(input.name, Empty).fixNull) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap", "<init>", "()V", false));
                                    }else {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false));
                                    }
                                } else {
                                    method.instructions.add(instruction);
                                }
                                break;
                            case "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap":
                                if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ObjectOpenHashMap", "<init>", "()V", false));
                                } else {
                                    method.instructions.add(instruction);
                                }
                                break;
                            case "java/util/ArrayList":
                                if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
                                } else {
                                    method.instructions.add(instruction);
                                }
                                break;
                            case "it/unimi/dsi/fastutil/longs/LongOpenHashSet":
                            case "java/util/HashSet":
                            case "it/unimi/dsi/fastutil/objects/ObjectOpenHashSet":
                                if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                    method.instructions.add(new InsnNode(Opcodes.POP));
                                } else {
                                    method.instructions.add(instruction);
                                }
                                break;
                            default:
                                method.instructions.add(instruction);

                                break;
                        }
                    } else if (instruction.getOpcode() == Opcodes.NEW && instruction instanceof TypeInsnNode typeInsnNode) {
                        switch (typeInsnNode.desc) {
                            case "it/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap" ->
                                    method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectLinkedOpenHashMap"));
                            case "java/util/HashMap" -> {
                                if (nameMap.getOrDefault(input.name, Empty).fixNull) {
                                    method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap"));
                                }else{
                                    method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap"));
                                }
                            }
                            case "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap" ->
                                    method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ObjectOpenHashMap"));
                            case "java/util/ArrayList" ->
                                    method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArrayList"));
                            case "it/unimi/dsi/fastutil/objects/ObjectOpenHashSet"->
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                            "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                            "concurrentObjectSet",
                                            "()Lit/unimi/dsi/fastutil/objects/ObjectSortedSet;"));
                            case "it/unimi/dsi/fastutil/longs/LongOpenHashSet" ->
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                            "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                            "concurrentLongSet",
                                            "()Lit/unimi/dsi/fastutil/longs/LongSortedSet;"));
                            case "java/util/HashSet" ->
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                            "java/util/concurrent/ConcurrentHashMap",
                                            "newKeySet",
                                            "()Ljava/util/concurrent/ConcurrentHashMap$KeySetView;",
                                            false));
                            default -> method.instructions.add(instruction);
                        }
                    } else {
                        method.instructions.add(instruction);
                    }
                }
                if (add) {
                    method.maxStack++;
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

        File f = new File("config/K_multi_threading-AddMapConcurrent-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals("")))
                        .map(n->{
                            String[] split = n.split("\\|");
                            return new AsmTarget(split[0], Boolean.parseBoolean(split[1]));
                        })
                        .forEach(stringsList::add);
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
                        #类名|是否修复null
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache|false
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Set.of(stringsList.stream().map(AsmTarget::className).map(Target::targetClass).toArray(Target[]::new));
    }
    public static record AsmTarget(String className, boolean fixNull) { }

}
