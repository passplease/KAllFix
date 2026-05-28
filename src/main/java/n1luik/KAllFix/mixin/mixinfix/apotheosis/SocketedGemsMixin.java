package n1luik.KAllFix.mixin.mixinfix.apotheosis;

import com.google.common.collect.ImmutableList;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketedGems;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = SocketedGems.class, remap = false)
public abstract class SocketedGemsMixin {

    @Shadow(remap = false) public abstract ImmutableList<GemInstance> gems();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int getDamageProtection(DamageSource source) {
        int total = 0;
        for (GemInstance inst : this.gems()) {
            if (inst.isValid()) {
                total += inst.getDamageProtection(source);
            }
        }
        return total;
    }
}