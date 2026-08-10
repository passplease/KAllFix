package n1luik.K_multi_threading.core.mixin.fix.create;

import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public abstract class ChainConveyorBlockEntityMixin {

    @Shadow(remap = false) public Set<BlockPos> connections;

    @Shadow(remap = false) private Map<BlockPos, List<ChainConveyorPackage>> travellingPackages;

    @Shadow(remap = false) public Map<BlockPos, ChainConveyorBlockEntity.ConnectionStats> connectionStats;

    @Shadow(remap = false) public abstract void notifyUpdate();

    @Overwrite(remap = false)
    public void transform(BlockEntity be, StructureTransform transform) {
        if (this.connections != null && !this.connections.isEmpty()) {
            var var10003 = this.connections.stream();
            Objects.requireNonNull(transform);
            Set<BlockPos> objects = ConcurrentHashMap.newKeySet();
            objects.addAll(var10003.map(transform::applyWithoutOffset).toList());
            this.connections = objects;
            HashMap<BlockPos, List<ChainConveyorPackage>> newMap = new HashMap();
            this.travellingPackages.entrySet().forEach((e) -> newMap.put(transform.applyWithoutOffset(e.getKey()), e.getValue()));
            this.travellingPackages = newMap;
            this.connectionStats = null;
            this.notifyUpdate();
        }
    }
}