package asm.n1luik.K_multi_threading.asm.util;

import asm.n1luik.K_multi_threading.asm.Util;
import asm.n1luik.K_multi_threading.asm.mapping.MappingImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
public class Neo21MapppingMap implements BiFunction<String, String, String> {
    public static final MappingImpl map = new MappingImpl(){
        {
            map.put("net/minecraft/world/level/chunk/ChunkStatus", "net/minecraft/world/level/chunk/status/ChunkStatus");
        }
    };
    @Override
    public String apply(String old, String data) {
        String[] split = data.split("\\.");
        split[0] = map.mapClass(split[0]);
        if (split.length == 2){
            String[] split1 = split[1].split("\\(");
            if (split1.length == 1)return split[0] + "." + split[1];
            return split[0] + "." + split1[0] + map.mapMethodDesc(split1[1]);
        }else if (split.length == 1){
            return split[0];
        }else  {
            throw new RuntimeException();
        }

    }
}
