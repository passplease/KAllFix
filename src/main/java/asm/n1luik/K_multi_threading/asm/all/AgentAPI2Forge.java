package asm.n1luik.K_multi_threading.asm.all;

import asm.n1luik.K_multi_threading.asm.JavaAgent.AgentAPI;
import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import asm.n1luik.K_multi_threading.asm.util.AsmApi2;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2Forge;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@AllArgsConstructor
public class AgentAPI2Forge implements ITransformationService {
    public static final Function<ITransformer2,  ITransformer<ClassNode>> aNew;
    static {
        try {
            Class<?> aClass;
            if (AsmApi2.bootType == AsmApi2.BootType.FORGE) {
                aClass = Class.forName("asm.n1luik.K_multi_threading.asm.util.ITransformer2Forge", false, AgentAPI2Forge.class.getClassLoader());
            }else {
                aClass = Class.forName("asm.n1luik.K_multi_threading.asm.util.ITransformer2NeoForge", false, AgentAPI2Forge.class.getClassLoader());
            }
            aNew = (Function<ITransformer2, ITransformer<ClassNode>>) metafactory(
                    MethodHandles.lookup(),
                    Function.class.getMethod("apply", Object.class),
                    MethodHandles.lookup().findConstructor(aClass, MethodType.methodType(void.class, ITransformer2.class))
            ).dynamicInvoker().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static CallSite metafactory(MethodHandles.Lookup loader, Method impl, MethodHandle call) throws LambdaConversionException {
        MethodType type = call.type();
        MethodType methodType = MethodType.methodType(impl.getDeclaringClass());

        return LambdaMetafactory.metafactory(
                loader,
                impl.getName(),
                methodType,
                MethodType.methodType(impl.getReturnType(), impl.getParameterTypes()),
                call,
                type);
    }
    public final AgentAPI agentAPI;
    @Override
    public @NotNull String name() {
        return agentAPI.name;
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        return (List)(agentAPI.transformers().stream().map(aNew).toList());
    }
}
