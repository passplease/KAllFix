package asm.n1luik.K_multi_threading.asm.all;

import asm.n1luik.K_multi_threading.asm.JavaAgent.AgentAPI;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2Forge;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class AgentAPI2Forge implements ITransformationService {
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
        return (List)(agentAPI.transformers().stream().map(ITransformer2Forge::new).toList());
    }
}
