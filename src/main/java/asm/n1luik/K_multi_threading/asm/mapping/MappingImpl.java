package asm.n1luik.K_multi_threading.asm.mapping;

import asm.n1luik.K_multi_threading.asm.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class MappingImpl {
    protected final Map<String,String> map = new HashMap<>();

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

    public String mapClass(String name){
        return map_(name);
    }
}
