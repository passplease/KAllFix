package n1luik.K_multi_threading.core.mixin.minecraftfix;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ChunkMap.class)
public abstract class ChunkMapFix1 {
    @Shadow @Final private ServerLevel level;

    @Unique
    private ReentrantLock K_multi_threading$lock;
    @Unique
    private Condition K_multi_threading$condition;
    //@Redirect(method = "lambda$prepareTickingChunk$41", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;postProcessGeneration()V"))
    //public void fix1(LevelChunk instance){
    //    if(level.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
    //        iWorldChunkLockedConfig.execWaitTask(instance::postProcessGeneration);
    //    }else {
    //        instance.postProcessGeneration();
    //    }
    //}


    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(ServerLevel p_214836_, LevelStorageSource.LevelStorageAccess p_214837_, DataFixer p_214838_, StructureTemplateManager p_214839_, Executor p_214840_, BlockableEventLoop p_214841_, LightChunkGetter p_214842_, ChunkGenerator p_214843_, ChunkProgressListener p_214844_, ChunkStatusUpdateListener p_214845_, Supplier p_214846_, int p_214847_, boolean p_214848_, CallbackInfo ci){
        K_multi_threading$lock = new java.util.concurrent.locks.ReentrantLock();
        K_multi_threading$condition = K_multi_threading$lock.newCondition();
    }
    //@Shadow @Final private BlockableEventLoop<Runnable> mainThreadExecutor;

    //@Redirect(method = "prepareTickingChunk", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenApplyAsync(Ljava/util/function/Function;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", ordinal = 1))
    //public <T extends Either<LevelChunk, ChunkHolder. ChunkLoadingFailure>> CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>
    //fix1(CompletableFuture<T> instance, Function<? super T, Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> fn, Executor executor){
    //    if(level.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
    //        //不知道为什么会增加250%的开销？？？？
    //        //return instance.thenApplyAsync(v->
    //        //            v.ifLeft((p_214960_) -> {
    //        //                if (iWorldChunkLockedConfig.isGeneratorWait()) {
    //        //                    VoidAsyncWait voidAsyncWait = new VoidAsyncWait(p_214960_::postProcessGeneration);

    @Shadow public abstract CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> schedule(ChunkHolder p_140293_, ChunkStatus p_140294_);

    @Shadow protected abstract void processUnloads(BooleanSupplier p_140354_);

    ////
    //        //                    level.getChunkSource().mainThreadProcessor.execute(voidAsyncWait);
    //        //                    voidAsyncWait.waitTask();
    //        //                }else {
    //        //                    iWorldChunkLockedConfig.execWaitTask(p_214960_::postProcessGeneration);
    //        //                }
    //        //                this.level.startTickingChunk(p_214960_);
    //        //            })
    //        //, executor);
    //        //if(level.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
    //        return instance.thenApplyAsync(fn//v->
    //                        //v.ifLeft((p_214960_) -> {
    //                        //    iWorldChunkLockedConfig.KMT$addRun(() -> {
    //                        //        p_214960_.postProcessGeneration();
    //                        //        this.level.startTickingChunk(p_214960_);
    //                        //    });
    //                        //})
    //                , iWorldChunkLockedConfig::KMT$addRun);//executor);
//
    //        //}else {
    //        //    return instance.thenApplyAsync(fn, executor);
    //        //}
    //    }else {
    //        return instance.thenApplyAsync(fn, executor);
    //    }
    //}
    //@Redirect(method = "lambda$protoChunkToFullChunk$34", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;registerAllBlockEntitiesAfterLevelLoad()V"))
    //public void fix2(LevelChunk instance){
    //    mainThreadExecutor.tell(instance::registerAllBlockEntitiesAfterLevelLoad);
    //}

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/ProcessorMailbox;create(Ljava/util/concurrent/Executor;Ljava/lang/String;)Lnet/minecraft/util/thread/ProcessorMailbox;", ordinal = 1))
    public ProcessorMailbox<Runnable> fix3(Executor p_18752_, String p_18753_){
        return ProcessorMailbox.create(Base.getEx(), p_18753_);
    }

    //lambda$protoChunkToFullChunk$34
    @Redirect(method = "m_214854_", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"), remap = false)
    public synchronized boolean fix4(IEventBus instance, Event event){
        if (level.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
        //    if (iWorldChunkLockedConfig.isGeneratorWait()) {
        //        VoidAsyncWait task = new VoidAsyncWait(K_multi_threading$lock, K_multi_threading$condition, () -> instance.post(event));
        //        iWorldChunkLockedConfig.KMT$genTestTickRun(task);
        //        task.waitTask();
        //    }else {
        //        //K_multi_threading$lock_FreshBlockEntities.lock();
        //        //K_multi_threading$isLock = true;
        //        instance.post(event);
        //    }
            long l = iWorldChunkLockedConfig.pushThread();
            boolean post = instance.post(event);
            if (l > 0)iWorldChunkLockedConfig.pop(l);
            return post;
        }else {
            //K_multi_threading$lock_FreshBlockEntities.lock();
            //K_multi_threading$isLock = true;
            return instance.post(event);
        }
    }

    //@Inject(method = "schedule", at = @At(value = "RETURN", ordinal = 0))
    //public void fix4(ChunkHolder p_140293_, ChunkStatus p_140294_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
    //    p_140293_.getOrScheduleFuture()
    //    cir.getReturnValue()
    //}
}
