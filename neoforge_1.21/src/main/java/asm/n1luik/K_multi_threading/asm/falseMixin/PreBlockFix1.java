package asm.n1luik.K_multi_threading.asm.falseMixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.Supplier;

@Mixin(value = Block.class)
public class PreBlockFix1 {
    @Unique
    private static final ThreadLocal<List<ItemEntity>> k_multi_threading$capturedDropsMT = new ThreadLocal<>();

    @Overwrite
    private static void popResource(Level level, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
        if (!level.isClientSide && !stack.isEmpty() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !level.restoringBlockSnapshots) {
            ItemEntity itementity = itemEntitySupplier.get();
            itementity.setDefaultPickUpDelay();
            // Neo: Add drops to the captured list if capturing is enabled.
            List<ItemEntity> itemEntities = k_multi_threading$capturedDropsMT.get();
            if (itemEntities != null) {
                itemEntities.add(itementity);
            }
            else {
                level.addFreshEntity(itementity);
            }
        }
    }


}