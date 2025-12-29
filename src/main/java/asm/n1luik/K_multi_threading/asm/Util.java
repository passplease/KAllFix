package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.mod.valkyrienskies.ShipObjectServerWorld_Asm;
import com.google.gson.Gson;
import n1luik.KAllFix.DataCollectors;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {


    public static final Gson GSON = new Gson();

    public static record MethodInfo(String name, String desc, int nameHash, int descHash) {
        public MethodInfo(String name, String desc) {
            this(name, desc, name.hashCode(), desc.hashCode());
        }
    }
    public static record FieldInfo(String name, int nameHash) {
        public FieldInfo(String name) {
            this(name, name.hashCode());
        }
    }
    /**
     * 获取容许mixin的class哈希校验数据
     * @param classData 修改的数据
     */
    public static byte[] toMixinClassHashCheckDataByte(ClassNode classData){
        toMixinClassHashCheckData(classData);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classData.accept(classWriter);
        return classWriter.toByteArray();
    }
    /**
     * 获取容许mixin的class哈希校验数据
     * @param classFile 类数据
     */
    public static byte[] toMixinClassHashCheckDataByte(byte[] classFile){
        ClassNode classData = new ClassNode();
        new ClassReader(classFile).accept(classData, 0);
        toMixinClassHashCheckData(classData);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classData.accept(classWriter);
        return classWriter.toByteArray();
    }
    /**
     * 获取容许mixin的class哈希校验数据
     * @param classData 修改的数据
     */
    public static void toMixinClassHashCheckData(ClassNode classData){
        List<MethodInfo> methodInfos = new ArrayList<>();
        List<FieldInfo> fieldInfos = new ArrayList<>();
        List<FieldNode> fieldNodes = new ArrayList<>();
        for (MethodNode method : classData.methods) {
            if (method.visibleAnnotations == null) {
                continue;
            }
            for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;")) {
                    methodInfos.add(new MethodInfo(method.name, method.desc));
                    method.name = "mixinFix";
                    //移除这个注释
                    method.visibleAnnotations.remove(visibleAnnotation);
                    break;
                }
            }
        }
        for (FieldNode field : classData.fields) {
            if (field.visibleAnnotations == null) {
                continue;
            }
            for (AnnotationNode visibleAnnotation : field.visibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;")) {
                    //不知道会怎么样
                    fieldInfos.add(new FieldInfo(field.name));
                    fieldNodes.add(field);
                    //移除这个注释
                    field.visibleAnnotations.remove(visibleAnnotation);
                    break;
                }
            }
        }
        classData.fields = fieldNodes;
        for (MethodNode method : classData.methods) {
            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction instanceof MethodInsnNode methodInsnNode) {
                    for (MethodInfo methodInfo : methodInfos) {
                        if (methodInfo.nameHash == methodInsnNode.name.hashCode() && methodInfo.descHash == methodInsnNode.desc.hashCode()) {
                            if (methodInsnNode.name.equals(methodInfo.name) && methodInsnNode.desc.equals(methodInfo.desc)) {
                                methodInsnNode.name = "";
                            }
                        }
                    }
                }else if (instruction instanceof FieldInsnNode fieldInsnNode) {
                    for (FieldInfo fieldInfo : fieldInfos) {
                        if (fieldInfo.nameHash == fieldInsnNode.name.hashCode()) {
                            if (fieldInsnNode.name.equals(fieldInfo.name)) {
                                fieldInsnNode.name = "";
                            }
                        }
                    }
                }else if (instruction instanceof InvokeDynamicInsnNode invokeDynamicInsnNode) {
                    Handle bsm = invokeDynamicInsnNode.bsm;
                    if (bsm.getOwner().equals(classData.name)) {
                        for (MethodInfo methodInfo : methodInfos) {
                            if (methodInfo.nameHash == bsm.getName().hashCode() && methodInfo.descHash == bsm.getDesc().hashCode()) {
                                if (bsm.getName().equals(methodInfo.name) && bsm.getDesc().equals(methodInfo.desc)) {
                                    invokeDynamicInsnNode.bsm = new Handle(
                                            bsm.getTag(),
                                            bsm.getOwner(),
                                            "",
                                            bsm.getDesc(),
                                            bsm.isInterface()
                                    );
                                }
                            }
                        }
                    }
                    if (invokeDynamicInsnNode.bsmArgs != null) {
                        for (int i = 0; i < invokeDynamicInsnNode.bsmArgs.length; i++) {
                            Object bsmArg = invokeDynamicInsnNode.bsmArgs[i];
                            if (bsmArg instanceof Handle handle) {
                                switch (handle.getTag()) {
                                    case Opcodes.H_INVOKESTATIC, Opcodes.H_INVOKESPECIAL, Opcodes.H_INVOKEVIRTUAL, Opcodes.H_NEWINVOKESPECIAL, Opcodes.H_INVOKEINTERFACE -> {
                                        for (MethodInfo methodInfo : methodInfos) {
                                            if (methodInfo.nameHash == handle.getName().hashCode() && methodInfo.descHash == handle.getDesc().hashCode()) {
                                                if (handle.getName().equals(methodInfo.name) && handle.getDesc().equals(methodInfo.desc)) {
                                                    invokeDynamicInsnNode.bsmArgs[i] = new Handle(
                                                            handle.getTag(),
                                                            handle.getOwner(),
                                                            "",
                                                            handle.getDesc(),
                                                            handle.isInterface()
                                                    );
                                                }
                                            }
                                        }
                                    }
                                    case Opcodes.H_GETFIELD, Opcodes.H_GETSTATIC, Opcodes.H_PUTFIELD, Opcodes.H_PUTSTATIC -> {
                                        for (FieldInfo fieldInfo : fieldInfos) {
                                            if (fieldInfo.nameHash == handle.getName().hashCode()) {
                                                if (handle.getName().equals(fieldInfo.name)) {
                                                    invokeDynamicInsnNode.bsmArgs[i] = new Handle(
                                                            handle.getTag(),
                                                            handle.getOwner(),
                                                            "",
                                                            handle.getDesc(),
                                                            handle.isInterface()
                                                    );
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     *
     * @param longStr 长字符串
     * @param mixStr 子字符串
     * @return 包含个数
     */
    public static int countStr(String longStr, String mixStr) {
        //如果确定传入的字符串不为空，可以把下面这个判断去掉，提高执行效率
//        if(longStr == null || mixStr == null || "".equals(longStr.trim()) || "".equals(mixStr.trim()) ){
//             return 0;
//        }
        int count = 0;
        int index = 0;
        while((index = longStr.indexOf(mixStr,index))!= -1){
            index = index + mixStr.length();
            count++;
        }
        return count;
    }

    public static final Pattern descIs = Pattern.compile("(|\\[+)(L(.+);|[VBZCSIFDJ])",Pattern.UNIX_LINES);
    public static String[] toDescList(String methodDescriptor){

        Matcher matcher = descIs.matcher(methodDescriptor.replaceAll("[()]", "").replace(";",";\n"));
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches.toArray(new String[0]);/*
        CharList bytes = new CharArrayList();
        List<String> ret = new ArrayList<>();
        boolean isClass = false;
        int a = 0;
        for (char aByte : methodDescriptor.toCharArray()) {
            if (aByte == '[') a++;
            else  {

                if (!isClass && aByte == 'L') {
                    isClass = true;
                }else if (isClass) {
                    if (aByte != ';')
                        bytes.add(aByte);
                    else {
                        ret.add("[".repeat(a)+new String(bytes.toCharArray()));
                        a=0;
                        isClass = false;
                    }
                } else {
                    ret.add("[".repeat(a)+String.valueOf(aByte));
                }
            }
        }
        return ret.toArray(new String[0]);*/
    }

    public static boolean isDefaultClass(String text) {
        return switch (text.replace("[","")){
            case "B", "J", "C", "Z", "F", "I", "S", "D", "V" -> true;
            default ->  false;
        };
    }
}
