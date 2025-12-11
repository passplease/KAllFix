package n1luik.K_multi_threading.core.mixin.fix.openpartiesandclaims;

import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xaero.pac.common.server.command.CommandRequirementHelper;

import java.util.function.Predicate;

@Mixin(value = CommandRequirementHelper.class, remap = false)
public class CommandRequirementHelperFix1 {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static Predicate<CommandSourceStack> onServerThread(Predicate<CommandSourceStack> requirement) {
        return requirement::test;
    }

}
