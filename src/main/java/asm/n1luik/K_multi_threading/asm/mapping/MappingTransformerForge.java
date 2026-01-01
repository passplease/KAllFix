package asm.n1luik.K_multi_threading.asm.mapping;

import me.lucko.spark.lib.asm.Opcodes;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.valkyrienskies.core.impl.shadow.P;

import javax.tools.JavaFileObject;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class MappingTransformerForge extends MappingTransformer {
    public static final Pattern FIELD_PATTERN = Pattern.compile("^f_\\d+_$");
    public static final Pattern METHOD_PATTERN = Pattern.compile("^m_\\d+_$");

    public MappingTransformerForge(MappingImpl mappingImpl) {
        super(new MappingImpl() {
            @Override
            public String[] mapMethod(String name) {
                String orDefault = mappingImpl.map_(name);
                String[] split = orDefault.split("\\.");
                String[] split1 = split[1].split("\\(");
                if (METHOD_PATTERN.matcher(split1[0]).matches()) {
                    split1[0] = mappingImpl.map_(split1[0]);
                }
                return new String[]{split[0], split1[0], "(" + split1[1]};
            }

            @Override
            public String[] mapField(String name) {
                String orDefault = mappingImpl.map_(name);
                String[] split = orDefault.split("\\.");
                if (FIELD_PATTERN.matcher(split[1]).matches()) {
                    split[1] = mappingImpl.map_(split[1]);
                }
                return new String[]{split[0], split[1]};
            }

            @Override
            public String map_(String name) {
                return mappingImpl.map_(name);
            }

            @Override
            public String mapClass(String name) {
                return mappingImpl.mapClass(name);
            }
        });
    }
    @Override
    public ClassNode transform(ClassNode input) {
        String[] names = fixMixin(input);
        Map<String, String> mixinNames = new HashMap<>();
        if (names == null || names.length == 0) {
            return input;
        }
        for (FieldNode field : input.fields) {
            if (fixShadow(field)) {
                var name = input.name+"."+field.name;
                field.name = mappingImpl.mapField(names[0]+"."+field.name)[1];
                mixinNames.put(name, input.name+"."+field.name);
            }
        }
        for (MethodNode method : input.methods) {
            if (fixShadow(method)) {
                var name = input.name+"."+method.name+method.desc;
                String[] strings = mappingImpl.mapMethod(names[0] + "." + method.name + method.desc);
                method.name = strings[1];
                method.desc = strings[2];
                mixinNames.put(name, input.name+"."+method.name+method.desc);
                continue;
            }

        }
        input = super.transform(input, new MappingImpl() {
            @Override
            public String map_(String name) {
                var map = mixinNames.get(name);
                if (map != null) {
                    return map;
                }
                return super.map_(name);
            }
        });
        return input;
    }
    public boolean fixMixinMethod(AnnotationNode annotationNode, String[] names, MethodNode methodNode) {
        List<Object> values = annotationNode.values;
        switch (annotationNode.desc) {
            case "Lorg/spongepowered/asm/mixin/gen/Accessor;":{
                for (int i = 0; i < values.size(); i += 2) {
                    String name = (String) values.get(i);
                    switch (name) {
                        case "value": {
                            Object o = values.get(i + 1);
                            if (o instanceof String s) {
                                String[] strings = mappingImpl.mapField(names[0]+"."+s);
                                values.set(i + 1, strings[1]);
                            }
                            return true;
                        }
                    }
                }
            }
            case "Lorg/spongepowered/asm/mixin/gen/Invoker;":{
                for (int i = 0; i < values.size(); i += 2) {
                    String name = (String) values.get(i);
                    switch (name) {
                        case "value": {
                            Object o = values.get(i + 1);
                            if (o instanceof String s) {
                                String[] strings = mappingImpl.mapMethod(names[0]+"."+s+methodNode.desc);
                                values.set(i + 1, strings[1]);
                            }
                            return true;
                        }
                    }
                }
            }
            case "Lorg/spongepowered/asm/mixin/injection/Redirect;":
            case "Lorg.spongepowered.asm.mixin.injection.Inject;": {
                boolean remap = true;
                for (int i = 0; i < values.size(); i += 2) {
                    String name = (String) values.get(i);
                    switch (name) {
                        case "remap": {
                            Object o = values.get(i + 1);
                            if (o instanceof Boolean b) {
                                remap = b;
                            }else {
                                return true;//是未知的mixin版本无法处理，直接返回true
                            }
                            break;
                        }
                        case "at": {

                            for (AnnotationNode annotationNode1 : (List<AnnotationNode>) values.get(i + 1)) {
                                if (!annotationNode1.desc.equals("Lorg/spongepowered/asm/mixin/injection/At;")){
                                    continue;
                                }
                                boolean test = true;
                                List<Object> values1 = annotationNode1.values;
                                ArrayList<Object> nv = new ArrayList<>(values1.size());
                                String type = null;
                                remap = true;
                                int targetPod = -1;
                                for (int i2 = 0; i2 < values1.size(); i2 += 2) {
                                    String name2 = (String) values1.get(i2);
                                    switch (name2) {
                                        case "value": {
                                            Object o = values1.get(i2+1);
                                            if (o instanceof String s) {
                                                type = s;
                                            }
                                            break;
                                        }
                                        case "remap": {
                                            Object o = values1.get(i2+1);
                                            if (o instanceof Boolean b) {
                                                remap = b;
                                            }
                                            break;
                                        }
                                        case "target": {
                                            Object o = values1.get(i2+1);
                                            if (o instanceof String s) {
                                                targetPod = i2+1;
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (!remap){
                                    if (type == null){
                                        continue;
                                    }
                                    if (targetPod == -1){
                                        continue;
                                    }
                                    switch (type) {
                                        //懒得写MIXINEXTRAS:EXPRESSION
                                        case "FIELD":{
                                            Object o = values1.get(targetPod);
                                            if (o instanceof String s) {
                                                int p1 = s.indexOf(";");
                                                String className = s.substring(1, p1);
                                                int endIndex = s.indexOf(":", p1);
                                                String fieldName = s.substring(p1 + 1, endIndex);
                                                String[] strings = mappingImpl.mapField(className + "." + fieldName);
                                                values1.set(targetPod, "L"+strings[0] + ";" + strings[1]+":"+(s.substring(endIndex+1)));
                                            }
                                            break;
                                        }
                                        case "INVOKE_ASSIGN":
                                        case "INVOKE":
                                        case "NEW": {
                                            Object o = values1.get(targetPod);
                                            if (o instanceof String s) {
                                                int p1 = s.indexOf(";");
                                                String className = s.substring(1, p1);
                                                int p2 = s.indexOf("(", p1);

                                                String[] strings = mappingImpl.mapMethod(className + (p2 != -1 ? ".": "}{") + s.substring(p1+1));
                                                values1.set(targetPod, "L"+strings[0] + ";" + strings[1]+(p2 != -1 ? strings[2] : ""));
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }

                    }
                }
                if (!remap){
                    for (int i = 0; i < values.size(); i += 2) {
                        String name = (String) values.get(i);
                        switch (name) {
                            case "method": {
                                Object o = values.get(i + 1);
                                if (o instanceof String s) {
                                    if (s.contains("(")) {
                                        String[] strings = mappingImpl.mapMethod(names[0] + "." + s);
                                        values.set(i + 1, strings[1] + strings[2]);
                                    }else {
                                        String[] strings = mappingImpl.mapMethod(names[0] + "}{" + s);
                                        values.set(i + 1, strings[1]);
                                    }
                                }else {
                                    return true;//是未知的mixin版本无法处理，直接返回true
                                }
                                break;
                            }

                        }
                    }

                }
                return true;
            }
            default:
                return false;
        }

    }
    public void fixMixinMethod(MethodNode classNode, String[] names) {
        for (AnnotationNode visibleAnnotation : classNode.visibleTypeAnnotations) {
             fixMixinMethod(visibleAnnotation, names, classNode);
        }
        for (AnnotationNode visibleAnnotation : classNode.visibleAnnotations) {
            fixMixinMethod(visibleAnnotation, names, classNode);
        }
    }
    public boolean fixShadow(FieldNode classNode) {
        for (AnnotationNode visibleAnnotation : classNode.visibleTypeAnnotations) {
            int i = fixShadow(visibleAnnotation);
            if (i != 0)return i != 1;
        }
        for (AnnotationNode visibleAnnotation : classNode.visibleAnnotations) {
            int i = fixShadow(visibleAnnotation);
            if (i != 0)return i != 1;
        }
        return false;
    }
    public boolean fixShadow(MethodNode classNode) {
        for (AnnotationNode visibleAnnotation : classNode.visibleTypeAnnotations) {
            int i = fixShadow(visibleAnnotation);
            if (i != 0)return i != 1;
        }
        for (AnnotationNode visibleAnnotation : classNode.visibleAnnotations) {
            int i = fixShadow(visibleAnnotation);
            if (i != 0)return i != 1;
        }
        return false;
    }
    public int fixShadow(AnnotationNode annotationNode) {
        List<Object> values = annotationNode.values;
        switch (annotationNode.desc) {
            case "Lorg/spongepowered/asm/mixin/Overwrite;":
            case "Lorg/spongepowered/asm/mixin/Shadow;": {
                boolean remap = true;
                for (int i = 0; i < values.size(); i += 2) {
                    String name = (String) values.get(i);
                    switch (name) {
                        case "remap": {
                            Object o = values.get(i + 1);
                            if (o instanceof Boolean b) {
                                remap = b;
                            }else {
                                return 1;//是未知的mixin版本无法处理，直接返回true
                            }
                            break;
                        }

                    }
                }
                return 2;
            }
            default:
                return 0;
        }
    }
    public String[] fixMixin(ClassNode classNode) {
        for (AnnotationNode visibleAnnotation : classNode.visibleTypeAnnotations) {
            String[] strings = fixMixin(visibleAnnotation);
            if (strings != null) {
                return strings;
            }
        }
        for (AnnotationNode visibleAnnotation : classNode.visibleAnnotations) {
            String[] strings = fixMixin(visibleAnnotation);
            if (strings != null) {
                return strings;
            }
        }
        return null;
    }
    public String[] fixMixin(AnnotationNode annotationNode) {
        List<Object> values = annotationNode.values;
        switch (annotationNode.desc) {
            case "Lorg/spongepowered/asm/mixin/Mixin;": {
                for (int i = 0; i < values.size(); i += 2) {
                    String name = (String) values.get(i);
                    switch (name) {
                        case "value": {
                            Object o = values.get(i + 1);
                            int size =0;
                            if (o instanceof List list) {
                                for (int i1 = 0; i1 < list.size(); i1++) {
                                    Object o1 = list.get(i1);
                                    if (o1 instanceof Type type) {
                                        list.set(i1, processType(type));
                                        if (type.getSort() == Type.OBJECT) {
                                            size++;
                                        }
                                    }
                                    String[] strings = new String[size];
                                    int j = 0;
                                    for (int i2 = 0; i2 < list.size(); i2++) {
                                        Object o2 = list.get(i2);
                                        if (o2 instanceof Type type) {
                                            if (type.getSort() == Type.OBJECT) {
                                                strings[j++] = type.getInternalName();
                                            }
                                        }
                                    }
                                    return strings;
                                }
                            }
                            break;
                        }
                    }
                }
                return null;
            }
            default:
                return null;
        }
    }

}
