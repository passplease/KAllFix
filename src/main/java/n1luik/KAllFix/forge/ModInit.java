package n1luik.KAllFix.forge;

import com.abdelaziz.canary.common.Canary;
import cpw.mods.modlauncher.TransformingClassLoader;
import lombok.extern.slf4j.Slf4j;
import n1luik.KAllFix.DataCollectors;
import n1luik.KAllFix.api.OptimizeTagManager;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.dataCollectors.ValkyrienSkies;
import n1luik.K_multi_threading.debug.GetterClassFileCommand;
import n1luik.K_multi_threading.fix.canary.CanaryConfigAuto;
import n1luik.K_multi_threading.fix.lithium.LithiumConfigAuto;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static n1luik.K_multi_threading.forge.ModInit.getclass;

@Slf4j
@Mod(Base.MOD_ID2)
public class ModInit {
    public static final OptimizeTagManager OPTIMIZE_TAG_MANAGER = new OptimizeTagManager();

    //懒得折腾了，拉一点吧
    public static synchronized void registerKeyBinding(KeyMapping key)
    {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, key);
    }
    public static void run1(){

        File file = new File("./_kmt_outc.txt");
        if (file.isFile()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                int i = 0;
                for (String v : new String(fileInputStream.readAllBytes()).split("(\\r\\n|\\n)+")) {
                    if (v.isEmpty())continue;

                    String name = v.replace("/", ".");
                    //try {
                    TransformingClassLoader classLoader = (TransformingClassLoader) GetterClassFileCommand.class.getClassLoader();
                    //classLoader.loadClass(name);
                    byte[] bytes = getclass.apply(classLoader, name);
                    try {
                        File file2 = new File("debug_save_"+(i++)+".class");
                        file2.createNewFile();
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        fileOutputStream.write(bytes);
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //} catch (ClassNotFoundException e) {
                    //    e.printStackTrace();
                    //}
                }
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean INIT_DATA_COLLECTORS_EVENT = false;
    public ModInit(){
        try {
            if (Boolean.getBoolean("KAF-LoginProtectionMod")) {
                MinecraftForge.EVENT_BUS.register(Class.forName("n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent", true, ModInit.class.getClassLoader()));
            }
            if (Boolean.getBoolean("KAF-packetOptimize")) {
                Class.forName("n1luik.KAllFix.forge.PacketOptimizeAll", true, ModInit.class.getClassLoader());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        MinecraftForge.EVENT_BUS.register(new EventRun());

    }

    public synchronized static void initDataCollectors(){
        if (INIT_DATA_COLLECTORS_EVENT) return;
        INIT_DATA_COLLECTORS_EVENT = true;
        InitDataCollectorsEvent event = new InitDataCollectorsEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            event.dataCollectors.addTools(new ValkyrienSkies());
            event.dataCollectors.addTools(new CanaryConfigAuto());
            event.dataCollectors.addTools(new LithiumConfigAuto());
        }

        try {
            event.dataCollectors.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class EventRun{
        //@SubscribeEvent
        //public void onInitDataCollectorsEvent(InitDataCollectorsEvent event){
        //    log.debug("K_multi_threading onInitDataCollectorsEvent");
        //    if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
        //        event.dataCollectors.addTools(new ValkyrienSkies());
        //    }
        //}
    }
}
