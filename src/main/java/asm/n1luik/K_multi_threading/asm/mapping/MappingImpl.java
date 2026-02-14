package asm.n1luik.K_multi_threading.asm.mapping;

import asm.n1luik.K_multi_threading.asm.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MappingImpl implements Function<String,String>{
    public final Map<String,String> map;
    public MappingImpl(int size) {
        map = new HashMap<>(size);
    }
    public MappingImpl() {
        map = new HashMap<>();
    }

    public static String mapMethodDesc(String desc, Function<String,String> mapper){

        StringBuilder buffer = new StringBuilder("(");

        String[] strings = Util.toDescList(desc);
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (string.startsWith("[")) {

                String replace = string.replace("[", "");
                if (!Util.isDefaultClass(replace)) {
                    String substring = replace.substring(1, replace.length() - 1);
                    strings[i] = "[".repeat(Util.countStr(string, "[")) + "L" + mapper.apply(substring) + ";";
                }
            }else if (!Util.isDefaultClass(string)) {
                strings[i] = "L"+mapper.apply(string.substring(1, string.length() - 1))+";";
            }
        }

        for (int i = 0; i < strings.length; i++) {
            if (i == strings.length-1){
                buffer.append(")");
                buffer.append(strings[i]);
                continue;
            }
            buffer.append(strings[i]);
        }
        return buffer.toString();
    }

    public static int mapLocalSignatureEnd(String signature, int pos, int max){
        int i = pos;
        while (i++<max) {
            var c = signature.charAt(i);
            switch (c) {
                case '<':
                case ';':
                case '+':
                case '-':
                case '*':
                case '(':
                case ')':
                case ',':
                    return i;
            }
        }
        throw new IllegalArgumentException("signature not end with ';' or '<' or '+' or '-' or '*'");
    }
    public static int mapLocalSignatureName(String signature, int pos, int max){

        int i = pos;
        while ((i+=1)<max) {
            var c = signature.charAt(i);
            switch (c) {
                case '<':
                case '+':
                case '-':
                case '*':
                case '(':
                case ')':
                case '/':
                case ';':
                    return -1;
                case ',':
                case ':':
                    return i;
            }
        }
        return -1;
    }
    public static String mapLocalSignature(String signature, Function<String,String> mapper){
        boolean contains = signature.contains("<");
        if (signature.endsWith(";") || contains){
            if (contains){
                StringBuilder buffer = new StringBuilder();
                int max = signature.length();
                int i = 0;
                while (i<max){
                    var c = signature.charAt(i);
                    switch (c){
                        case '(':
                        case ')':
                        case '+':
                        case '-':
                        case '*':
                            buffer.append(c);
                            i++;
                            continue;
                        case '<':
                        case ',':
                        case ';': {
                            buffer.append(c);
                            i++;
                            int e = mapLocalSignatureName(signature, i, max);
                            if (e > 0){
                                i = e+1;
                                buffer.append(signature, e, (i+1));
                            }

                            continue;
                        }
                        case 'L': {
                            buffer.append(c);
                            int e = mapLocalSignatureEnd(signature, i+=1, max);
                            buffer.append(mapper.apply(signature.substring(i, e)));
                            i = e;
                            continue;
                        }
                        default:
                            buffer.append(c);
                            i++;
                    }
                }
                return buffer.toString();
            }else {
                if (signature.startsWith("L")){

                    var s = signature.substring(1, signature.length() - 1);
                    if (s.length() > 1){
                        if (Util.isDefaultClass(s)){
                            return signature;
                        }
                    }
                    return "L"+mapper.apply(s)+";";
                }else {
                    return signature;
                }
            }
        }else {
            return signature;
        }
    }
    public String mapLocalSignature(String signature){
        return mapLocalSignature(signature, this);
    }

    public String mapMethodDesc(String desc){

        StringBuilder buffer = new StringBuilder("(");

        String[] strings = Util.toDescList(desc);
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (string.startsWith("[")) {

                String replace = string.replace("[", "");
                if (!Util.isDefaultClass(replace)) {
                    String substring = replace.substring(1, replace.length() - 1);
                    strings[i] = "[".repeat(Util.countStr(string, "[")) + "L" + mapClass(substring) + ";";
                }
            }else if (!Util.isDefaultClass(string)) {
                strings[i] = "L"+mapClass(string.substring(1, string.length() - 1))+";";
            }
        }

        for (int i = 0; i < strings.length; i++) {
            if (i == strings.length-1){
                buffer.append(")");
                buffer.append(strings[i]);
                continue;
            }
            buffer.append(strings[i]);
        }
        return buffer.toString();
    }
    public String[] mapMethod(String name){
        String orDefault = map_(name);
        String[] split = orDefault.split("\\.");
        String[] split1 = split[1].split("\\(");
        return new String[]{split[0],split1[0],"("+split1[1]};
    }
    // 有的没实现这个
    public String[] mapMethodNull(String name){
        String orDefault = mapNull_(name);
        if (orDefault == null) return null;
        String[] split = orDefault.split("\\.");
        String[] split1 = split[1].split("\\(");
        return new String[]{split[0],split1[0],"("+split1[1]};
    }

    public String[] mapField(String name) {
        String orDefault = map_(name);
        String[] split = orDefault.split("\\.");
        return new String[]{split[0], split[1]};
    }
    public String map_(String name) {
        return map.getOrDefault(name, name);
    }
    // 有的没实现这个
    public String mapNull_(String name) {
        return map.get(name);
    }
    /**
     * 映射类名
     * @param name 类名，格式为全限定名，例如：java/lang/Object
     * @return 映射后的类名
     */
    public String mapClass(String name){
        return map_(name);
    }

    @Override
    public String apply(String s) {
        return mapClass(s);
    }
}
