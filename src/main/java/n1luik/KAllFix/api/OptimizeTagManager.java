package n1luik.KAllFix.api;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class OptimizeTagManager {
    private final ConcurrentHashMap<ResourceLocation, OptimizeTagManagerNode> data = new ConcurrentHashMap<>();
    public OptimizeTagManagerNode create(ResourceLocation key) {
        return data.computeIfAbsent(key, (k)->new OptimizeTagManagerNode());
    }
    public static class OptimizeTagManagerNode {
        public int index = Integer.MIN_VALUE;
        public final ConcurrentHashMap<ResourceLocation, Integer> nameIdMap = new ConcurrentHashMap<>();
        public final ConcurrentHashMap<Integer, ResourceLocation> idNameMap = new ConcurrentHashMap<>();

        public int getIndex(ResourceLocation name) {
            return nameIdMap.computeIfAbsent(name, (k)->{
                int index1 = index;
                idNameMap.put(index1, name);
                index++;
                return index1;
            });
        }


        public ResourceLocation getName(int anInt) {
            return idNameMap.get(anInt);
        }
    }

}
