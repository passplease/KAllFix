package n1luik.K_multi_threading.forge;

import com.mojang.brigadier.arguments.StringArgumentType;
import cpw.mods.modlauncher.TransformingClassLoader;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.debug.GetterClassFileCommand;
import n1luik.K_multi_threading.debug.InfRunPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

@Mod(Base.MOD_ID)
public class ModInit {
    public ModInit(){

        //MinecraftForge.EVENT_BUS.addListener(this::a);
    }
    //@SubscribeEvent
    //public void a(ServerStartedEvent e){
    //    new InfRunPos().start();
    //}
}
