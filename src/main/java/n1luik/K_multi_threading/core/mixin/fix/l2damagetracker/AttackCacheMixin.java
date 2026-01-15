package n1luik.K_multi_threading.core.mixin.fix.l2damagetracker;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.LogEntry;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AttackCache.class, remap = false)
public class AttackCacheMixin {
    @Unique
    public final ThreadLocal<LogEntry> logE = ThreadLocal.withInitial(()->null);
    @Redirect(method = "pushAttackPre", at = @At(value = "FIELD", target = "Ldev/xkmc/l2damagetracker/contents/attack/AttackCache;log:Ldev/xkmc/l2damagetracker/contents/attack/LogEntry;", opcode = Opcodes.PUTFIELD, remap = false))
    private void fix1(AttackCache instance, LogEntry value) {
        logE.set(value);
    }
    @Redirect(method = {"pushAttackPost", "pushHurtPre", "pushHurtPost",
            "pushDamagePre", "addHurtModifier", "addDealtModifier"
    }, at = @At(value = "FIELD", target = "Ldev/xkmc/l2damagetracker/contents/attack/AttackCache;log:Ldev/xkmc/l2damagetracker/contents/attack/LogEntry;", opcode = Opcodes.GETFIELD))
    private LogEntry fix2(AttackCache instance) {
        return logE.get();
    }
}