package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.mc1_19.LevelChunk_Asm;
import asm.n1luik.K_multi_threading.asm.mc1_19.TruePacketThreadTestAsm;
import asm.n1luik.K_multi_threading.asm.mod.*;
import asm.n1luik.K_multi_threading.asm.mod.Modernfix.ModernfixGetChunkSynchronized_Asm;
import asm.n1luik.K_multi_threading.asm.mod.ae2.PathingCalculation_Asm;
import asm.n1luik.K_multi_threading.asm.mod.biolith.FixMixinServerWorld1_Asm;
import asm.n1luik.K_multi_threading.asm.mod.c2me.*;
import asm.n1luik.K_multi_threading.asm.mod.canary.ServerChunkCacheMixin_Asm;
import asm.n1luik.K_multi_threading.asm.mod.create.CreateGeneratingKineticBlockEntity_Asm;
import asm.n1luik.K_multi_threading.asm.mod.create.CreateTrackBlockSynchronized_Asm;
import asm.n1luik.K_multi_threading.asm.mod.create.CreateTrackGraphSynchronized_Asm;
import asm.n1luik.K_multi_threading.asm.mod.createenchantmentindustry.FluidTankBlockIsNullFix1_Asm;
import asm.n1luik.K_multi_threading.asm.mod.gtceu.ImplMetaMachine1_Asm;
import asm.n1luik.K_multi_threading.asm.mod.lithium.Lithium$TypeFilterableListMixin_Asm;
import asm.n1luik.K_multi_threading.asm.mod.lithium.LithiumGetChunkSynchronized_Asm;
import asm.n1luik.K_multi_threading.asm.mod.mek.MekanismNetworkAcceptorCacheSynchronized_Asm;
import asm.n1luik.K_multi_threading.asm.mod.noisium.NoiseChunkGeneratorMixinFix1_Asm;
import asm.n1luik.K_multi_threading.asm.mod.valkyrienskies.ShipObjectServerWorld_Asm;
import asm.n1luik.K_multi_threading.asm.mod.vmp.MixinTACSCancelSendingFixAsm;
import asm.n1luik.K_multi_threading.asm.mod.vmp.MixinTypeFilterableListAsm;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ForgeAsm implements ITransformationService{
    public static final MappingImpl minecraft_map;
    public static final MappingImpl srg$Forge$_map;
    static {
        InputStream resourceAsStream = MappingTsrgImpl.class.getResourceAsStream("/K_multi_threading.mapping/map.tsrg");
        InputStream resourceAsStream2 = MappingSrgImpl.class.getResourceAsStream("/K_multi_threading.mapping/map_srg.srg");

        try {
            if (resourceAsStream == null)throw new IOException("找不到映射表[/K_multi_threading.mapping/map.tsrg]，可以尝试检查是否正确编译");
            if (resourceAsStream2 == null)throw new IOException("找不到映射表[/K_multi_threading.mapping/map_srg.srg]，可以尝试检查是否正确编译");
            minecraft_map = new MappingTsrgImpl(new String(resourceAsStream.readAllBytes()));
            srg$Forge$_map = new MappingSrgImpl(new String(resourceAsStream2.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*static {
        new File("./mods").mkdirs();
        File file = new File("./mods/K_multi_threading-modInfo.jar");
        try {
            file.delete();
        }catch (SecurityException e){//文件可能无法删除
            System.out.println("""
                               
                               无法无法重置mods/K_multi_threading-modInfo.jar
                               可能导致部分模组无法正常获取模组版本和运行mixin
                               可以考虑手动删除这个文件
                               """);
        }
        try {
            file.createNewFile();
            JarOutputStream zipOutputStream = new JarOutputStream(new FileOutputStream(file));
            for (String s : List.of(
                    "mixins.K_multi_threading.refmap.json",
                    "mixins.K_multi_threading.json",
                    "META-INF/mods.toml",
                    "META-INF/MANIFEST.MF",
                    "pack.mcmeta"//,
                    //"K_multi_threading.mapping/map.tsrg",
                    //"n1luik/K_multi_threading/forge/ModInit.class"
            )) {
                zipOutputStream.putNextEntry(new JarEntry(s));
                zipOutputStream.write(ForgeAsm.class.getResourceAsStream("/" + s).readAllBytes());
            }

            for (String s : List.of(
                    "n1luik/K_multi_threading/forge/ModInit.class"//复制的class文件
            )) {
                zipOutputStream.putNextEntry(new JarEntry("info/"+s));

                ClassWriter writer = new ClassWriter(Opcodes.ASM9);
                ClassRemapper remapper = new ClassRemapper(writer, new SimpleRemapper(new HashMap<>(){
                    @Override
                    public String get(Object key) {
                        if (key instanceof String s && !s.contains(".") && s.startsWith("n1luik/K_multi_threading/")) return "info/"+s;
                        return super.get(key);
                    }
                }));

                 new ClassReader(ForgeAsm.class.getResourceAsStream("/" + s).readAllBytes()).accept(remapper,0);

                zipOutputStream.write(writer.toByteArray());
            }
            zipOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public @NotNull String name() {
        return "K_multi_threading";
    }

    @Override
    public void initialize(IEnvironment environment) {
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    public static boolean isModLoaded(String modId) {
        //if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        //}
        //return ModList.get().isLoaded(modId);
    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        if (System.getProperty("KMT_D") != null || (FMLLoader.getDist().isClient() && !Boolean.getBoolean("KMT_Client")))
            return List.of();
        List<ITransformer> iTransformers = new ArrayList<>(List.of(
                //new SyncImplGetterChunk_ASM(),
                new ImplLevel1_Asm(),
                new ShipObjectServerWorld_Asm(),
                new ImplMetaMachine1_Asm(),
                new FixMixinServerWorld1_Asm(),
                new ImplServerLevel1_Asm(),
                new MixinMinecraftServer2_Asm(),
                new MixinMinecraftServer_Asm(),
                new MixinServerChunkManager3_Asm(),
                new MixinServerChunkManager2_Asm(),
                new MixinServerChunkManager_Asm(),
                new ImplLevelChunkTicks1_Asm(),
                //new MekanismStructureSynchronized_Asm(),//jvm检测过不了
                //new MekanismVoxelPlaneSynchronized_Asm(),
                new PathingCalculation_Asm(),
                new ImplDistanceManager1_Asm(),
                new RemoveMixin_ASM(),//new LevelMixin_Asm(),
                //new ChunkMap_Asm(),
                new MekanismNetworkAcceptorCacheSynchronized_Asm(),
                new CreateTrackBlockSynchronized_Asm(),
                new CreateTrackGraphSynchronized_Asm(),
                new FluidTankBlockIsNullFix1_Asm(),
                //new ServerChunkCache_Asm(),
                //new AddServerLevelSync1_Asm(),
                new ModernfixGetChunkSynchronized_Asm(),
                new LithiumGetChunkSynchronized_Asm(),
                //new Lithium$TypeFilterableListMixin_Asm(),
                new CreateGeneratingKineticBlockEntity_Asm(),
                new ServerChunkCacheMixin_Asm(),
                new AddMapConcurrent_ASM(),
                new NoiseChunkGeneratorMixinFix1_Asm(),
                new MixinTypeFilterableListAsm(),
                //new ChunkMapSynchronized_Asm(),
                new FastUtilTransformerService()
        ));
        //在这里不可以使用插件
        if (Boolean.getBoolean("KAF-FixConfigAuto")) {
            //iTransformers.add(new CanaryConfigAsm());

        }
        String s = FMLLoader.versionInfo().mcVersion();
        if (s.startsWith("1.19.")){
            iTransformers.add(new TruePacketThreadTestAsm());
            iTransformers.add(new LevelChunk_Asm());
        }
        if (isModLoaded("vmp")){
            iTransformers.add(new MixinTACSCancelSendingFixAsm());
        }
        iTransformers.add(new SafeIndependenceAddSynchronized_Asm());
        iTransformers.add(new AddSynchronized_Asm());
        iTransformers.add(new SafeAddSynchronized_Asm());
        iTransformers.add(new IndependenceAddSynchronized_Asm());
        iTransformers.add(new NotErrorAddSynchronized_Asm());
        iTransformers.add(new AllAddSynchronized_Asm());
        return iTransformers;
    }
}
