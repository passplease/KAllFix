package n1luik.KAllFix.mixin.unsafe.path.TagKeyFast;

import n1luik.KAllFix.api.AddFinal;
import n1luik.KAllFix.api.CatInit;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = TagKey.class)
public class TagKeyMixin {
    @Shadow @Final private ResourceKey<? extends Registry<?>> registry;

    @Shadow @Final private ResourceLocation location;
    @AddFinal
    @Unique
    private int hash;
    @Unique
    @CatInit
    private void initHash(ResourceKey<? extends Registry<?>> registry, ResourceLocation location) {
        this.hash = (registry.location().hashCode() * 31) + location.hashCode();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public final int hashCode() {
        return this.hash;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TagKey<?> tagKey)) {
            return false;
        }
        if (tagKey.hashCode() != this.hashCode()) {
            return false;
        }
        return Objects.equals(this.registry, tagKey.registry()) && Objects.equals(this.location, tagKey.location());
    }
}