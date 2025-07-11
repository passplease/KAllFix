package n1luik.KAllFix.fix.biolith;

import n1luik.K_multi_threading.core.util.Unsafe;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.LevelStem;

import java.io.InputStream;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BiolithCall {
    public static final Consumer<Registry<LevelStem>> call;
    static {
        Class<?> run;
        try {
            Class<?> clazz = Class.forName("com.terraformersmc.biolith.impl.biome.InterfaceBiomeSource");
            Method biolith$getDimensionType = clazz.getMethod("biolith$getDimensionType");
            if (biolith$getDimensionType.getReturnType() == Holder.class) {
                run = Class.forName("n1luik.KAllFix.fix.biolith.Fun_biolith");
            }else {
                run = Class.forName("n1luik.KAllFix.fix.biolith.Fun_biolith_Forge");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType interfaceMethodType = MethodType.methodType(void.class, Registry.class);
        try {
            ;
            call = (Consumer<Registry<LevelStem>>) Unsafe.metafactory(lookup, Consumer.class.getMethod("accept", Object.class), lookup.findStatic(run, "fixBiomeSource", interfaceMethodType)).dynamicInvoker().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public static void fixBiomeSource(Registry<LevelStem> levelStemTypeRegistry) {
        call.accept(levelStemTypeRegistry);

    }

    public static void fixBiomeSource(RegistryAccess.Frozen registryAccess) {
        call.accept(registryAccess.registryOrThrow(Registries.LEVEL_STEM));
    }
}
