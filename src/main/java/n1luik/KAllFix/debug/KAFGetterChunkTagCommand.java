package n1luik.KAllFix.debug;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import cpw.mods.modlauncher.TransformingClassLoader;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import n1luik.KAllFix.Imixin.IOptimizeTag;
import n1luik.KAllFix.api.OptimizeTagManager;
import n1luik.KAllFix.forge.ModInit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.io.FileOutputStream;
import java.io.IOException;

public class KAFGetterChunkTagCommand {
    public static void register(CommandDispatcher<CommandSourceStack> p_214446_) {
        p_214446_.register(Commands.literal("kaf_getter_chunk_tag").requires((p_137777_) -> {
            return p_137777_.hasPermission(2);
        }).then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.argument("world", DimensionArgument.dimension())
                                .executes(v->{
                                    BlockPos pos = BlockPosArgument.getBlockPos(v, "pos");
                                    Level w = DimensionArgument.getDimension(v, "world");
                                    getChunkTag(v.getSource(), pos, w);
                                    return 1;
                                })
                        )
                .executes(v->{
                    BlockPos pos = BlockPosArgument.getBlockPos(v, "pos");
                    Level w = v.getSource().getLevel();
                    getChunkTag(v.getSource(), pos, w);
                    return 1;
                })
        ));
    }
    public static void getChunkTag(CommandSourceStack source, BlockPos pos, Level w) {
        OptimizeTagManager.OptimizeTagManagerNode chunk = ModInit.OPTIMIZE_TAG_MANAGER.create(new ResourceLocation("chunk"));
        source.sendSystemMessage(Component.literal("pos: "+pos));
        LevelChunk chunkNow = w.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        if (chunkNow != null) {
            IntArraySet integers = ((IOptimizeTag) chunkNow).KAllFix$getAllTag();
            StringBuilder stringBuilder = new StringBuilder("-----------------------------------------------\n");
            for (int anInt : integers) {
                stringBuilder.append(chunk.getName(anInt)).append("\n");
            }
            stringBuilder.append("-----------------------------------------------");
            source.sendSystemMessage(Component.literal(stringBuilder.toString()));
        }else {
            source.sendSystemMessage(Component.literal("区块未加载"));
        }
    }
}
