package asm.n1luik.K_multi_threading.asm.mod.curios;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class CurioInventory_Asm extends ITransformer2 {
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
    public final Map<String, List<String>> fieldMappings = new HashMap<>();
    //public final Map<String, List<String>> compatible = new HashMap<>();
    public final Map<String, String> typeMapping = new HashMap<>();
    {
        typeMapping.put("java/util/LinkedHashMap", "java/util/concurrent/ConcurrentHashMap");

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

        for (FieldNode field : input.fields) {
            field.desc = descMap(field.desc);
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
        for (MethodNode method : input.methods) {
            for (LocalVariableNode localVariableNode : method.localVariables) {
                localVariableNode.desc = descMap(localVariableNode.desc);
            }
            AbstractInsnNode[] abstractInsnNodes = method.instructions.toArray();
            method.instructions.clear();
            boolean add = false;
            for (AbstractInsnNode instruction : abstractInsnNodes) {
                if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode) {
                    switch (methodInsnNode.owner) {
                        case "java/util/LinkedHashMap":
                            if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false));
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
                        case "java/util/LinkedHashMap" ->
                                method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap"));
                        default -> method.instructions.add(instruction);
                    }
                } else {
                    method.instructions.add(instruction);
                }
            }
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

            if (add) {
                method.maxStack++;
            }
        }
        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of("top/theillusivec4/curios/common/capability/CurioInventory");
    }
}
