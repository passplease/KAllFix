package n1luik.KAllFix.mixin.impl;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import n1luik.KAllFix.Imixin.IOptimizeTag;
import n1luik.KAllFix.util.OptimizeTagIntMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkAccess.class)
public class ChunkMixin implements IOptimizeTag {
    @Unique
    private final OptimizeTagIntMap optimizeTagSet = new OptimizeTagIntMap();

    @Override
    public IntArraySet KAllFix$getAllTag() {
        return optimizeTagSet.getKeys();
    }

    @Override
    public boolean KAllFix$getOptimizeTag(int id) {
        return optimizeTagSet.contains(id);
    }
    @Override
    public void KAllFix$addOptimizeTag(int id) {
        optimizeTagSet.add(id);
    }
    @Override
    public void KAllFix$removeOptimizeTag(int id) {
        optimizeTagSet.remove(id);
    }
}
