package n1luik.KAllFix.mixin.mixinfix.ae;

import appeng.api.stacks.KeyCounter;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyCounter.class)
public class KeyCounterMixin {

    @Mutable
    @Shadow @Final private Reference2ObjectMap lists;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void fix1(CallbackInfo ci){
        lists = new Reference2ObjectLinkedOpenHashMap();
    }
}