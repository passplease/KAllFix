package n1luik.KAllFix.Imixin;

import it.unimi.dsi.fastutil.ints.IntArraySet;

/**
 * 优化标签，是否有这个，她只是一个简单的标签而且目前只有区块
 */
public interface IOptimizeTag {
    IntArraySet KAllFix$getAllTag();
    boolean KAllFix$getOptimizeTag(int id);
    void KAllFix$addOptimizeTag(int id);
    void KAllFix$removeOptimizeTag(int id);
}
