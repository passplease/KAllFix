package n1luik.KAllFix.Imixin.mod.upgradedcore;

import n1luik.KAllFix.data.upgradedcore.ArmorSlotsAPI;

public interface IUpgradedCoreArmorSlotsAPI {
    /**
     * 修改{@link ArmorSlotsAPI#event_size}修改触发次数，写入时触发，每运行一次扣除一次触发次数，触发次数为0时不触发
     * <p>
     * upgradedcore特化版，修改upgradedcore数据触发
     */
    boolean KAllFix$upgradedcore$writeTest();

    /**
     * 两项映四：
     * <p>
     * 一共有4个数据，匹配需要2个数据
     * <p>
     * 计算2个数据，如果一样就设置一样数据的哈希，如果2个不一样的就检查另一个目标如果另一个跟展示的数据一样就替换对方的哈希成另一个数据的哈希
     * <p>
     * 计算结果2个一样就是4个都一样
     * <p>
     * 理论上他可以匹配无限的数据只不过性能问题会非常严重
     * @return
     */
    int KAllFix$upgradedcore$result1();
    int KAllFix$upgradedcore$result2();
}
