package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;

@Slf4j
public class AddMapConcurrentV2_ASM extends ITransformer2 {
    /*public static BiFunction<TransformingClassLoader, String, byte[]> getclass;

    static {
        try {
            Method buildTransformedClassNodeFor = TransformingClassLoader.class.getDeclaredMethod("buildTransformedClassNodeFor", String.class, String.class);
            buildTransformedClassNodeFor.setAccessible(true);
            getclass = (transformingClassLoader, s) ->
            {
                try {
                    return (byte[]) buildTransformedClassNodeFor.invoke(transformingClassLoader, s, s);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }*/
    private final static AsmTarget Empty = new AsmTarget("", false);
    public final List<AsmTarget> stringsList = new ArrayList<>(List.of(
            new AsmTarget("com.simibubi.create.content.fluids.FluidTransportBehaviour", false, new String[0],
                    new MethodInfo[]{
                            new MethodInfo(null, null, false, false)
                    }),
            new AsmTarget("com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler", false),
            new AsmTarget("com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity", false),
            new AsmTarget("aztech.modern_industrialization.machines.multiblocks.world.ChunkEventListeners", false),
            new AsmTarget("com.ldtteam.structurize.util.BlockUtils", false),
            new AsmTarget("net.minecraft.world.level.block.RedstoneTorchBlock", false),
            new AsmTarget("net.minecraft.world.level.timers.TimerQueue", false)
    ));
    public final Map<String, AsmTarget> nameMap = new HashMap<>();
    public final Map<String, List<String>> fieldMappings = new HashMap<>();
    //public final Map<String, List<String>> compatible = new HashMap<>();
    public final Map<String, String> typeMapping = new HashMap<>();
    {
        typeMapping.put("java.util.PriorityQueue".replace('.', '/'), "java.util.concurrent.PriorityBlockingQueue".replace('.', '/'));
        typeMapping.put("java.util.IdentityHashMap".replace('.', '/'), "n1luik.K_multi_threading.core.util.concurrent.LockIdentityHashMap".replace('.', '/'));
    }
    public String descMap(String desc){
        int array = 0;
        while (desc.charAt(array) == '['){
            array++;
        }
        if (desc.charAt(array) == 'L') {
            String desc1 = typeMapping.get(desc.substring(array + 1, desc.length() - 1));
            if (desc1 != null){
                return  "[".repeat(array) + "L" + desc1 + ";";
            }
        }
        return desc;
    }


    @Override
    public @NotNull ClassNode transform(ClassNode input) {
        log.info("[{}]", input.name);
        AsmTarget orDefault = nameMap.getOrDefault(input.name, Empty);

        if (orDefault.mappingFields.length > 0) {
            for (FieldNode field : input.fields) {
                for (String s : orDefault.mappingFields) {
                    if (field.name.equals(s)) {
                        field.desc = descMap(field.desc);
                    }
                }
            }
            for (MethodNode methodNode : input.methods) {
                for (AbstractInsnNode instruction : methodNode.instructions) {
                    if (instruction instanceof FieldInsnNode fieldInsnNode) {
                        List<String> strings = fieldMappings.get(fieldInsnNode.owner);
                        if (strings != null) {
                            if (strings.contains(fieldInsnNode.name)){
                                fieldInsnNode.desc = descMap(fieldInsnNode.desc);
                            }
                        }
                    }
                }

            }

        }
        for (MethodNode method : input.methods) {
            for (MethodInfo methodInfo : orDefault.methods) {
                if (methodInfo.test(method)) {
                    if (methodInfo.mappingLocal){
                        for (LocalVariableNode localVariableNode : method.localVariables) {
                            localVariableNode.desc = descMap(localVariableNode.desc);
                        }
                    }
                    AbstractInsnNode[] abstractInsnNodes = method.instructions.toArray();
                    method.instructions.clear();
                    boolean add = false;
                    for (AbstractInsnNode instruction : abstractInsnNodes) {
//                        if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode) {
//                            switch (methodInsnNode.owner) {
//                                case "com/google/common/collect/Maps":
//                                    if (methodInsnNode.name.equals("newHashMap")) {
//                                        add = true;
//                                        if (orDefault.fixNull) {
//                                            method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap"));
//                                            method.instructions.add(new InsnNode(Opcodes.DUP));
//                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap", "<init>", "()V", false));
//                                        } else {
//                                            method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap"));
//                                            method.instructions.add(new InsnNode(Opcodes.DUP));
//                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false));
//                                        }
//                                    } else {
//                                        method.instructions.add(instruction);
//                                    }
//                                    break;
//                                case "com/google/common/collect/Sets":
//                                    if (methodInsnNode.name.equals("newHashSet")) {
//                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/concurrent/ConcurrentHashMap", "newKeySet", "()Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", false));
//                                        //add = true;
//                                        //method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArraySet"));
//                                        //method.instructions.add(new InsnNode(Opcodes.DUP));
//                                        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArraySet", "<init>", "()V", false));
//                                    } else {
//                                        method.instructions.add(instruction);
//                                    }
//                                    break;
//                                case "com/google/common/collect/Lists":
//                                    if (methodInsnNode.name.equals("newArrayList")) {
//                                        add = true;
//                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArrayList"));
//                                        method.instructions.add(new InsnNode(Opcodes.DUP));
//                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
//                                    } else {
//                                        method.instructions.add(instruction);
//                                    }
//                                    break;
//                                default:
//                                    method.instructions.add(instruction);
//
//                                    break;
//                            }
//                        } else
                            if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode) {
                            switch (methodInsnNode.owner) {
                                case "java/util/IdentityHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        //method.instructions.add(new InsnNode(Opcodes.POP));
//                                        method.instructions.add(instruction);
//
//                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections",
//                                                "synchronizedMap", "(Ljava/util/Map;)Ljava/util/Map;", false));
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/LockIdentityHashMap", "<init>", methodInsnNode.desc, false));
                                    }
                                    break;
                                case "java/util/PriorityQueue":
                                    if (methodInsnNode.name.equals("<init>")) {
                                        if (methodInsnNode.desc.equals("(Ljava/util/Comparator;)V")){
                                            method.instructions.add(new LdcInsnNode(11));
                                            method.instructions.add(new InsnNode(Opcodes.SWAP));
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/PriorityBlockingQueue", "<init>", "(ILjava/util/Comparator;)V", false));
                                        }else{
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/PriorityBlockingQueue", "<init>", methodInsnNode.desc, false));
                                        }

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
                                case "java/util/PriorityQueue" -> method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/PriorityBlockingQueue"));
                                case "java/util/IdentityHashMap" -> {
//                                    method.instructions.add(instruction);
//                                    add = true;
//                                    method.instructions.add(new InsnNode(Opcodes.DUP));
//                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/IdentityHashMap", "<init>", "()V", false));
//
//                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections",
//                                            "synchronizedMap", "(Ljava/util/Map;)Ljava/util/Map;", false));
                                    method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/LockIdentityHashMap"));
                                }
                                default -> method.instructions.add(instruction);
                            }
                        } else {
                            method.instructions.add(instruction);
                        }
                    }
                    if (methodInfo.mappingAll) {
                        for (AbstractInsnNode instruction : abstractInsnNodes) {
                            switch (instruction.getOpcode()) {
                                case Opcodes.PUTFIELD, Opcodes.GETFIELD -> {
                                    if (instruction instanceof FieldInsnNode fieldInsnNode){
                                        if (fieldInsnNode.desc.startsWith("L")) {
                                            String substring = fieldInsnNode.desc.substring(1, fieldInsnNode.desc.length() - 1);
                                            fieldInsnNode.desc = "L"+typeMapping.getOrDefault(substring, substring)+";";
                                        }
                                    }
                                }
                                case Opcodes.INVOKEINTERFACE, Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESTATIC, Opcodes.INVOKESPECIAL -> {
                                    if (instruction instanceof MethodInsnNode methodInsnNode){
                                        methodInsnNode.owner = typeMapping.getOrDefault(methodInsnNode.owner, methodInsnNode.owner);
                                    }
                                }
                                case Opcodes.INVOKEDYNAMIC -> {
                                    if (instruction instanceof InvokeDynamicInsnNode node){
                                        for (int i = 0; i < node.bsmArgs.length; i++) {
                                            Object bsmArg = node.bsmArgs[i];
                                            if (bsmArg instanceof Handle handle) {
                                                node.bsmArgs[i] = new Handle(handle.getTag(), typeMapping.getOrDefault(handle.getOwner(), handle.getOwner()), handle.getName(), handle.getDesc(), handle.isInterface());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (add) {
                        method.maxStack++;
                    }
                }
            }
        }
        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {

        File f = new File("config/K_multi_threading-AddMapConcurrentV2-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.isEmpty()))
                        .map(n->{
                            String[] split = n.split("\\|", 4);
                            if (split.length < 2) throw new RuntimeException();
                            MethodInfo[] methods;
                            String[] blacklistField;
                            if (split.length > 2) {
                                if (split.length > 3) {
                                    String[] split1 = split[3].split(":");
                                    methods = new MethodInfo[split1.length];
                                    for (int i = 0; i < split1.length; i++) {
                                        String s = split1[i];
                                        if (s.isEmpty()) throw new RuntimeException();
                                        String[] split2 = s.split(",", 3);
                                        boolean mappingLocal;
                                        boolean mappingAll;
                                        if (split2.length > 1) {
                                            mappingLocal = Boolean.parseBoolean(split2[1]);
                                            if (split2.length > 2) {
                                                mappingAll = Boolean.parseBoolean(split2[2]);
                                            }else {
                                                mappingAll = false;
                                            }
                                        } else {
                                            mappingLocal = false;
                                            mappingAll = false;
                                        }
                                        String s1 = split2[0];
                                        if (s1.isEmpty()) {
                                            methods = mappingLocal ? AsmTarget.EMPTY_METHODS3 : AsmTarget.EMPTY_METHODS2;
                                            break;
                                        }
                                        //可以直接判断是不是只有desc
                                        if (s1.charAt(0) == '(') {
                                            methods[i] = new MethodInfo(null, s1, mappingLocal, mappingAll);
                                        } else {
                                            if (s1.contains("(")) {
                                                String[] split3 = s1.split("(?=\\()", 2);
                                                methods[i] = new MethodInfo(split3[0], split3[1], mappingLocal, mappingAll);
                                            } else {
                                                methods[i] = new MethodInfo(s1, null, mappingLocal, mappingAll);
                                            }
                                        }
                                    }
                                }else {
                                    methods = AsmTarget.EMPTY_METHODS;
                                }
                                if (split[2].isEmpty()){
                                    blacklistField = AsmTarget.EMPTY_STRING_ARRAY;
                                }else {
                                    blacklistField = split[2].split(":");
                                }

                            }else {
                                blacklistField = AsmTarget.EMPTY_STRING_ARRAY;
                                methods = AsmTarget.EMPTY_METHODS;
                            }
                            return new AsmTarget(split[0], Boolean.parseBoolean(split[1]), blacklistField, methods);
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
                        #类名|是否修复null|变量映射列表|函数名字(可以没有desc),映射局部变量默认false,映射全部的匹配类型默认false:下一个函数名
                        #函数名字如果是,[false, true]就会直接处理所有的函数
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache|false
                        //net/minecraft/server/level/ServerChunkCache|false||
                        //net/minecraft/server/level/ServerChunkCache|false||init,true
                        //net/minecraft/server/level/ServerChunkCache|false||init()V,true:
                        //net/minecraft/server/level/ServerChunkCache|false||init()V,true,false:
                        //net/minecraft/server/level/ServerChunkCache|false||()V:
                        //net/minecraft/server/level/ServerChunkCache|false|A:b|,true
                        //net/minecraft/server/level/ServerChunkCache|false|A:b|,true,false
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (AsmTarget asmTarget : stringsList) {
            nameMap.put(asmTarget.className.replace(".", "/"), asmTarget);
            nameMap.put(asmTarget.className, asmTarget);
        }
        for (AsmTarget asmTarget : stringsList) {
            if (asmTarget.mappingFields.length < 1) continue;
            List<String> stringStringMap = fieldMappings.computeIfAbsent(asmTarget.className.replace(".", "/"), k -> new ArrayList<>(asmTarget.mappingFields.length));
            stringStringMap.addAll(Arrays.asList(asmTarget.mappingFields));

        }
        return Set.of(stringsList.stream().map(AsmTarget::className).toArray(String[]::new));
    }

    public static record MethodInfo(@Nullable String name, @Nullable String desc, boolean mappingLocal, boolean mappingAll) {
        public boolean test(MethodNode node){
            return (name == null || name.equals(node.name)) && (desc == null || desc.equals(node.desc));
        }
    }
    public static record AsmTarget(String className, boolean fixNull, String[] mappingFields, MethodInfo[] methods) {
        private static final MethodInfo[] EMPTY_METHODS =  new MethodInfo[]{new MethodInfo("<init>", null, false, false), new MethodInfo("<clinit>", null, false, false)};
        private static final MethodInfo[] EMPTY_METHODS2 =  new MethodInfo[]{new MethodInfo(null, null, false, false)};
        private static final MethodInfo[] EMPTY_METHODS3 =  new MethodInfo[]{new MethodInfo(null, null, true, false)};
        private static final MethodInfo[] EMPTY_METHODS4 =  new MethodInfo[]{new MethodInfo(null, null, false, true)};
        private static final MethodInfo[] EMPTY_METHODS5 =  new MethodInfo[]{new MethodInfo(null, null, true, true)};
        private static final String[] EMPTY_STRING_ARRAY =  new String[0];
        public AsmTarget(String className, boolean fixNull) {
            this(className, fixNull, EMPTY_STRING_ARRAY, EMPTY_METHODS);
        }
    }

}
