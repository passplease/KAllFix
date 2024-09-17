package n1luik.K_multi_threading.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import cpw.mods.modlauncher.TransformingClassLoader;
import n1luik.K_multi_threading.core.Imixin.IMainThreadExecutor;
import n1luik.K_multi_threading.core.Imixin.IMinecraftServerTickMixin1;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.LevelStem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

public class SetterWorldConfigCommand {
    public static void register(CommandDispatcher<CommandSourceStack> p_214446_, CommandBuildContext commandBuildContext) {
        p_214446_.register(Commands.literal("SetterWorldConfig").requires((p_137777_) -> {
                return p_137777_.hasPermission(3);
            }).then(Commands.literal("world").then(
                    Commands.argument("world", DimensionArgument.dimension())
                            .then(
                                    Commands.literal("setM2").then(
                                            Commands.argument("bool", BoolArgumentType.bool()).executes(v->{
                                                ServerLevel world = DimensionArgument.getDimension(v, "world");
                                                if (world.getChunkSource().mainThreadProcessor instanceof IMainThreadExecutor iMainThreadExecutor) {
                                                    iMainThreadExecutor.setM2(BoolArgumentType.getBool(v, "bool"));
                                                    return 1;
                                                }else {
                                                    return 0;
                                                }
                                            })
                                    )
                            ).then(Commands.literal("setMultiThreading").then(
                                    Commands.argument("size", IntegerArgumentType.integer(0)).executes(v->{
                                        ServerLevel world = DimensionArgument.getDimension(v, "world");
                                        if (world.getChunkSource().mainThreadProcessor instanceof IMainThreadExecutor iMainThreadExecutor) {
                                            iMainThreadExecutor.setMultiThreading(IntegerArgumentType.getInteger(v, "size"));
                                            return 1;
                                        }else {
                                            return 0;
                                        }
                                    })
                            )))
        ).then(Commands.literal("ClearErrorSize").executes(v->{
            ((IMinecraftServerTickMixin1)v.getSource().getServer()).setK_multi_threading$removeErrorSize(0);
            return 1;
        })).then(Commands.literal("RemoveRemoveErrorSize").executes(v->{
            ((IMinecraftServerTickMixin1)v.getSource().getServer()).setK_multi_threading$removeErrorSize(0x80000000);
            return 1;
        })));
    }
}
