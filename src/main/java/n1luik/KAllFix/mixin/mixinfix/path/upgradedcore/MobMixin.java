package n1luik.KAllFix.mixin.mixinfix.path.upgradedcore;

import n1luik.KAllFix.Imixin.upgradedcore.IUpgradedCoreArmorSlotsAPI;
import n1luik.KAllFix.data.upgradedcore.ArmorSlotsAPI;
import n1luik.KAllFix.data.upgradedcore.TrimUtil2;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin implements IUpgradedCoreArmorSlotsAPI {
    //这里兼容可能不太好
    @Shadow @Final private NonNullList<ItemStack> armorItems;
    @Unique
    private volatile int KAllFix$upgradedcore$result1 = 0;
    @Unique
    private volatile int KAllFix$upgradedcore$result2 = 0;
    @Unique
    private volatile int KAllFix$upgradedcore$writeTest = 0;
    @Inject(method = "setItemSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;onEquipItem(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
    private void setItemSlot(EquipmentSlot p_21416_, ItemStack p_21417_, CallbackInfo ci) {
        KAllFix$upgradedcore$up(p_21416_.getIndex(), p_21417_, true);
    }
    //0 1， 2 3分组
    @Unique
    private void KAllFix$upgradedcore$up(int index, ItemStack p_21452_, boolean up2) {
        int hash = TrimUtil2.getTrimHash(p_21452_);
        switch (index) {
            case 0: {
                int hash2 = TrimUtil2.getTrimHash(armorItems.get(1));
                if (hash2 != hash) {
                    if (hash2 == KAllFix$upgradedcore$result2) {
                        KAllFix$upgradedcore$result2 = hash;
                        KAllFix$upgradedcore$result1 = hash2;
                    }else {
                        KAllFix$upgradedcore$result1 = hash;
                    }
                }else {
                    //计算另一边要不然他可能被覆盖
                    KAllFix$upgradedcore$result1 = hash2;
                    if (up2) {
                        KAllFix$upgradedcore$up(2, p_21452_, false);
                    }
                }
                break;
            }
            case 1: {
                int hash2 = TrimUtil2.getTrimHash(armorItems.get(0));
                if (hash2 != hash) {
                    if (hash2 == KAllFix$upgradedcore$result2) {
                        KAllFix$upgradedcore$result2 = hash;
                        KAllFix$upgradedcore$result1 = hash2;
                    }else {
                        KAllFix$upgradedcore$result1 = hash;
                    }
                }else {
                    //计算另一边要不然他可能被覆盖
                    KAllFix$upgradedcore$result1 = hash2;
                    if (up2) {
                        KAllFix$upgradedcore$up(2, p_21452_, false);
                    }
                }
                break;
            }
            case 2: {
                int hash2 = TrimUtil2.getTrimHash(armorItems.get(3));
                if (hash2 != hash) {
                    if (hash2 == KAllFix$upgradedcore$result1) {
                        KAllFix$upgradedcore$result1 = hash;
                        KAllFix$upgradedcore$result2 = hash2;
                    } else {
                        KAllFix$upgradedcore$result2 = hash;
                    }
                } else {
                    //计算另一边要不然他可能被覆盖
                    KAllFix$upgradedcore$result2 = hash2;
                    if (up2) {
                        KAllFix$upgradedcore$up(1, p_21452_, false);
                    }
                }
                break;
            }
            case 3: {
                int hash2 = TrimUtil2.getTrimHash(armorItems.get(2));
                if (hash2 != hash) {
                    if (hash2 == KAllFix$upgradedcore$result1) {
                        KAllFix$upgradedcore$result1 = hash;
                        KAllFix$upgradedcore$result2 = hash2;
                    } else {
                        KAllFix$upgradedcore$result2 = hash;
                    }
                } else {
                    //计算另一边要不然他可能被覆盖
                    KAllFix$upgradedcore$result2 = hash2;
                    if (up2) {
                        KAllFix$upgradedcore$up(1, p_21452_, false);
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
        KAllFix$upgradedcore$writeTest = ArmorSlotsAPI.event_size;
    }
    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {
        //读取数据了需要重新计算
        if (armorItems.size() < 4)return;
        KAllFix$upgradedcore$result1 = TrimUtil2.getTrimHash(armorItems.get(0));
        KAllFix$upgradedcore$result2 = TrimUtil2.getTrimHash(armorItems.get(2));
        if (KAllFix$upgradedcore$result1 == KAllFix$upgradedcore$result2) {
            KAllFix$upgradedcore$result1 = TrimUtil2.getTrimHash(armorItems.get(1));
        }
        if (KAllFix$upgradedcore$result1 == KAllFix$upgradedcore$result2) {
            KAllFix$upgradedcore$result2 = TrimUtil2.getTrimHash(armorItems.get(3));
        }
        KAllFix$upgradedcore$writeTest = ArmorSlotsAPI.event_size;
    }

    @Override
    public boolean KAllFix$upgradedcore$writeTest() {
        return KAllFix$upgradedcore$writeTest-- > 0;
    }

    @Override
    public int KAllFix$upgradedcore$result1() {
        return KAllFix$upgradedcore$result1;
    }

    @Override
    public int KAllFix$upgradedcore$result2() {
        return KAllFix$upgradedcore$result2;
    }
}
