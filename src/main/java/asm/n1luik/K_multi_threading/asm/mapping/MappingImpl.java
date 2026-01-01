package asm.n1luik.K_multi_threading.asm.mapping;

import java.util.HashMap;
import java.util.Map;

public abstract class MappingImpl {
    protected final Map<String,String> map = new HashMap<>();

    public String[] mapMethod(String name){
        String orDefault = map_(name);
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

    public String mapClass(String name){
        return map_(name);
    }
}
