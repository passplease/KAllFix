package n1luik.K_multi_threading.neoforge.mixin.minecraftfix;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Entity.class)
public abstract class EntityFix1 {

    @Shadow private BlockState inBlockState;

    @Shadow public abstract BlockPos blockPosition();

    @Shadow public abstract Level level();

    @Overwrite
    public BlockState getInBlockState() {
        if (this.inBlockState == null) {
            BlockState blockState = this.level().getBlockState(this.blockPosition());
            this.inBlockState = blockState;
            return blockState;
        }

        return this.inBlockState;
    }

}