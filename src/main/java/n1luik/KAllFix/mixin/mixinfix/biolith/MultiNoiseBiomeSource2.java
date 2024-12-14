package n1luik.KAllFix.mixin.mixinfix.biolith;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.terraformersmc.biolith.impl.biome.InterfaceBiomeSource;
import n1luik.KAllFix.util.ModTags;
import n1luik.KAllFix.util.TagPair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSource2 implements InterfaceBiomeSource {
    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();
    @Unique
    private volatile boolean KAllFix$initialized = false;

    @Mutable
    @Shadow @Final private Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;parameters:Lcom/mojang/datafixers/util/Either;", opcode = Opcodes.PUTFIELD))
    private void fix1(MultiNoiseBiomeSource instance, Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> value) {
        if (!ModTags.StartServerTag) {
            parameters = value.map(v -> {
                for (Pair<Climate.ParameterPoint, Holder<Biome>> parameterPointHolderPair : v.values()) {
                    if (parameterPointHolderPair instanceof TagPair<Climate.ParameterPoint, Holder<Biome>>) {
                        return Either.left(new Climate.ParameterList<>(v.values().stream().filter(v2 -> !(v2 instanceof TagPair<Climate.ParameterPoint, Holder<Biome>>)).toList()));
                    }
                }
                return Either.left(new Climate.ParameterList<>(v.values().stream().<Pair<Climate.ParameterPoint, Holder<Biome>>>map(TagPair::new).toList()));
            }, v -> value);
        }else {
            parameters = value;
        }
    }
    @Inject(method = "parameters", at = @At("HEAD"), cancellable = true)
    private void fix2(CallbackInfoReturnable<Climate.ParameterList<Holder<Biome>>> cir) {
        if (!ModTags.StartServerTag) {
            if (biolith$getDimensionType() == null) {
                LOGGER.warn("BiomeSource is not initialized yet, skipping biolith");
                cir.setReturnValue(this.parameters.map((p_275171_) -> {
                    return p_275171_;
                }, (p_275172_) -> {
                    return p_275172_.value().parameters();
                }));

            }
        }
    }
    @Inject(method = "parameters", at = @At("RETURN"))
    private void fix3(CallbackInfoReturnable<Climate.ParameterList<Holder<Biome>>> cir) {
        if (!KAllFix$initialized) {
            synchronized (this){
                if (!KAllFix$initialized) {
                    if (biolith$getDimensionType().is(BuiltinDimensionTypes.OVERWORLD) || biolith$getDimensionType().is(BuiltinDimensionTypes.NETHER)) {
                        List<ResourceKey<Biome>> map1 = parameters.map(v -> v.values().stream().filter(v3 -> !(v3 instanceof TagPair<Climate.ParameterPoint, Holder<Biome>>)).map(v3 -> v3.getSecond().unwrapKey().get()).toList(), v -> v.get().parameters().values().stream().filter(v3 -> !(v3 instanceof TagPair<Climate.ParameterPoint, Holder<Biome>>)).map(v3 -> v3.getSecond().unwrapKey().get()).toList());
                        cir.getReturnValue().values = cir.getReturnValue().values.stream().map(v2 -> map1.contains(v2.getSecond().unwrapKey().get()) ? v2 : new TagPair<>(v2)).toList();
                    }
                    KAllFix$initialized = true;
                }
            }
        }
    }
}