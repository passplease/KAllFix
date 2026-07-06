package n1luik.K_multi_threading.core.mixin.minecraftfix;

import com.google.common.collect.Table;
import net.minecraft.world.level.timers.TimerQueue;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(value = TimerQueue.class, priority = 900)
public class TimerQueueFix1<T> {

    @Shadow @Final private Queue<TimerQueue.Event<T>> queue;

    @Shadow @Final private Table<String, Long, TimerQueue.Event<T>> events;

    //@Redirect(method = "<init>(Lnet/minecraft/world/level/timers/TimerCallbacks;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/timers/TimerQueue;queue:Ljava/util/Queue;", opcode = Opcodes.PUTFIELD))
    //private void fix1(){
    //}

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick(T p_82257_, long p_82258_) {
        while(true) {
            TimerQueue.Event<T> event = this.queue.peek();
            if (event == null || event.triggerTime > p_82258_) {
                return;
            }
            var e2 = this.queue.poll();
            if (e2 != event){
                if(e2 != null) {
                    this.queue.add(e2);
                    if (e2.triggerTime > p_82258_) return;
                } else {
                    return;
                }

            }

            this.events.remove(event.id, p_82258_);
            event.callback.handle(p_82257_, (TimerQueue<T>)(Object)this, p_82258_);
        }
    }
}