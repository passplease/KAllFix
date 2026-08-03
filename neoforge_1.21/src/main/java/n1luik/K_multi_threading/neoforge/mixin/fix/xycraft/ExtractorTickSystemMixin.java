package n1luik.K_multi_threading.neoforge.mixin.fix.xycraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import tv.soaryn.xycraft.core.content.systems.BlockTickSystemLevelAttachment;
import tv.soaryn.xycraft.machines.content.systems.ExtractorTickSystem;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Mixin(value = ExtractorTickSystem.class)
public class ExtractorTickSystemMixin {
    @Unique
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @WrapMethod(method = "resortList", remap = false)
    public void resortList(ServerLevel level, Operation<Void> original) {
        lock.writeLock().lock();
        try {
            original.call(level);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @WrapMethod(method = "tickBatchBlocks", remap = false)
    public void tickBatchBlocks(ServerLevel level, BlockTickSystemLevelAttachment tickData, BlockPos.MutableBlockPos pos, BlockState state, long currentStep, Operation<Void> original) {
        lock.readLock().lock();
        try {
            original.call(level, tickData, pos, state, currentStep);
        } finally {
            lock.readLock().unlock();
        }
    }

}