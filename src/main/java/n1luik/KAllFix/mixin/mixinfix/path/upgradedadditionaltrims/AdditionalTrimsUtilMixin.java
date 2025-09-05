package n1luik.KAllFix.mixin.mixinfix.path.upgradedadditionaltrims;

import com.rolfmao.additionaltrims.resources.registries.ATMaterials;
import com.rolfmao.upgradedadditionaltrims.utils.AdditionalTrimsUtil;
import n1luik.KAllFix.data.upgradedcore.TrimUtil2;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = AdditionalTrimsUtil.class, remap = false)
public class AdditionalTrimsUtilMixin {
    @Unique
    private final static String FEATHER = ATMaterials.FEATHER.location().toString();
    @Unique
    private final static String SPIDER = ATMaterials.SPIDER.location().toString();
    @Unique
    private final static String WITHER = ATMaterials.WITHER.location().toString();
    @Unique
    private final static String PRISMARINE = ATMaterials.PRISMARINE.location().toString();
    @Unique
    private final static String BLAZE = ATMaterials.BLAZE.location().toString();
    @Unique
    private final static String ECHO = ATMaterials.ECHO.location().toString();
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static boolean fullFeatherTrim(LivingEntity livingEntity) {
        return TrimUtil2.countTrim4(livingEntity, FEATHER);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static boolean fullSpiderTrim(LivingEntity livingEntity) {
        return TrimUtil2.countTrim4(livingEntity, SPIDER);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static boolean fullWitherTrim(LivingEntity livingEntity) {
        return TrimUtil2.countTrim4(livingEntity, WITHER);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static boolean fullPrismarineTrim(LivingEntity livingEntity) {
        return TrimUtil2.countTrim4(livingEntity, PRISMARINE);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static boolean fullBlazeTrim(LivingEntity livingEntity) {
        return TrimUtil2.countTrim4(livingEntity, BLAZE);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static boolean fullEchoTrim(LivingEntity livingEntity) {
        return TrimUtil2.countTrim4(livingEntity, ECHO);
    }
    //不好优化countFeatherTrim countPrismarineTrim countEnderTrim onApplyEffect，懒
}