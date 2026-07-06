package n1luik.K_multi_threading.core.util;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Unique;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class IngredientData {
    @Unique
    public static final VarHandle stackingIdsVH;
    @Unique
    public static final IntList stackingIdsNull = new IntArrayList();

    static {
        try {
            stackingIdsVH = Unsafe.lookup.findVarHandle(Ingredient.class, ForgeAsm.minecraft_map.mapField("net.minecraft.world.item.crafting.Ingredient".replace(".", "/")+".stackingIds")[1], IntList.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
