package asm.n1luik.K_multi_threading.asm.falseMixin;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

import static n1luik.K_multi_threading.core.util.IngredientData.stackingIdsNull;
import static n1luik.K_multi_threading.core.util.IngredientData.stackingIdsVH;

@Mixin(value = Ingredient.class)
public abstract class PreIngredientFix1 {
    @Shadow @Nullable private IntList stackingIds;

    @Shadow protected abstract void markValid();

    @Shadow public abstract boolean checkInvalidation();

    @Shadow public abstract ItemStack[] getItems();

    @Overwrite
    public IntList getStackingIds() {
        IntList stackingIds1 = stackingIds;
        if (stackingIds1 == null || checkInvalidation()) {
            synchronized (this){

                stackingIds1 = (IntList) stackingIdsVH.getVolatile(this);
                if (stackingIds1 == null) {
                    stackingIdsVH.setVolatile(this, stackingIdsNull);
                }else {
                    while (stackingIds1 == null || stackingIds1 == stackingIdsNull){
                        stackingIds1 = (IntList) stackingIdsVH.getVolatile(this);
                    }
                    return stackingIds1;
                }
            }
                this.markValid();
                ItemStack[] aitemstack = this.getItems();
                stackingIds1 = new IntArrayList(aitemstack.length);

                for (ItemStack itemstack : aitemstack) {
                    stackingIds1.add(StackedContents.getStackingIndex(itemstack));
                }

                stackingIds1.sort(IntComparators.NATURAL_COMPARATOR);
                stackingIdsVH.setVolatile(this, stackingIds1);
            return stackingIds1;
        }

        return this.stackingIds;
    }
//
//    @Redirect(method = "getStackingIds", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/crafting/Ingredient;stackingIds:Lit/unimi/dsi/fastutil/ints/IntList;", ordinal = 0, opcode = Opcodes.GETFIELD))
//    public IntList fix1(Ingredient ingredient) {
//        IntList stackingIds1 = stackingIds;
//        if (stackingIds1 == null) {
//            synchronized (this){
//                stackingIds1 = (IntList) stackingIdsVH.getVolatile(ingredient);
//                if (stackingIds1 == null) {
//                    stackingIdsVH.setVolatile(this, stackingIdsNull);
//                }else {
//                    while (stackingIds1 == null || stackingIds1 == stackingIdsNull){
//                        stackingIds1 = (IntList) stackingIdsVH.getVolatile(ingredient);
//                    }
//                    return stackingIds1;
//                }
//
//            }
//        }
//        return stackingIds1;
//    }
}
