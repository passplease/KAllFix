package n1luik.KAllFix.data.upgradedcore;

import n1luik.KAllFix.Imixin.upgradedcore.IUpgradedCoreArmorSlotsAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TrimUtil2 {
    public static int getTrimHash(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        } else if (tag.contains("Trim")) {
            CompoundTag compoundtag = stack.getTagElement("Trim");
            if (compoundtag == null) {
                return 0;
            }
            String material = compoundtag.getString("material");
            return material.hashCode();
        } else {
            return 0;
        }
    }
    public static boolean getTrim(ItemStack stack, String trimMaterials) {
        if (!LivingEntity.getEquipmentSlotForItem(stack).equals(EquipmentSlot.FEET) && !LivingEntity.getEquipmentSlotForItem(stack).equals(EquipmentSlot.LEGS) && !LivingEntity.getEquipmentSlotForItem(stack).equals(EquipmentSlot.CHEST) && !LivingEntity.getEquipmentSlotForItem(stack).equals(EquipmentSlot.HEAD)) {
            return false;
        } else {
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                return false;
            } else if (tag.contains("Trim")) {
                CompoundTag compoundtag = stack.getTagElement("Trim");
                if (compoundtag == null) {
                    return false;
                }
                String material = compoundtag.getString("material");
                return material.hashCode() == trimMaterials.hashCode() && material.equals(trimMaterials);
            } else {
                return false;
            }
        }
    }

    public static boolean countTrim4(LivingEntity livingEntity, String trimMaterials) {
        Iterable<ItemStack> wearedList = livingEntity.getArmorSlots();
        if (!wearedList.iterator().hasNext()) {
            return false;
        }
        if (livingEntity instanceof IUpgradedCoreArmorSlotsAPI api) {
            int i = api.KAllFix$upgradedcore$result1();
            return i == api.KAllFix$upgradedcore$result2() && i != 0;
        }
        return countTrim(livingEntity, trimMaterials) >= 4;
    }
    public static int countTrim(LivingEntity livingEntity, ResourceKey trimMaterials) {
        return countTrim(livingEntity, trimMaterials.location().toString());
    }
    public static int countTrim(LivingEntity livingEntity, String trimMaterials) {
        Iterable<ItemStack> wearedList = livingEntity.getArmorSlots();
        if (!wearedList.iterator().hasNext()) {
            return 0;
        }

        int count = 0;


        for(ItemStack stack : wearedList) {
            if (getTrim(stack, trimMaterials)) {
                count = count + 1;
            }
        }

        return count;
    }
}
