package n1luik.KAllFix.fix.biolith;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;

public class Fun_biolith {
    public static void fixBiomeSource(RegistryAccess.Frozen registryAccess) {
        fixBiomeSource(registryAccess.registryOrThrow(Registries.LEVEL_STEM));
    }
    public static void fixBiomeSource(Registry<LevelStem> levelStemTypeRegistry) {
        for (LevelStem levelStem : levelStemTypeRegistry.stream().toList()) {
            ChunkGenerator chunkGenerator = levelStem.generator();
            if (chunkGenerator.getBiomeSource() instanceof com.terraformersmc.biolith.impl.biome.InterfaceBiomeSource noiseSource) {
                if (noiseSource.biolith$getDimensionType() == null) {
                    noiseSource.biolith$setDimensionType(levelStem.type());
                }
            }

        }
    }
}
