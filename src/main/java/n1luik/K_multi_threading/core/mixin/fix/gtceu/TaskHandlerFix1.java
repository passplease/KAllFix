package n1luik.K_multi_threading.core.mixin.fix.gtceu;

import com.gregtechceu.gtceu.utils.TaskHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = TaskHandler.class, remap = false)
public abstract class TaskHandlerFix1 {
    @Shadow
    private static void execute(List tasks) {
    }

    @Redirect(method = "onTickUpdate", at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/utils/TaskHandler;execute(Ljava/util/List;)V", remap = false), remap = false)
    private static void fix1(List tasks) {
        synchronized (tasks) {
            execute(tasks);
        }
    }
}
