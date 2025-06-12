package asm.n1luik.K_multi_threading.asm.mod.valkyrienskies;

import asm.n1luik.K_multi_threading.asm.AddMapConcurrent_ASM;
import asm.n1luik.K_multi_threading.asm.data.MapConcurrentData;
import com.google.gson.Gson;
import cpw.mods.modlauncher.TransformingClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

public class AddMapConcurrent {
    private static final Gson gson = new Gson();
    //public static final BiFunction<TransformingClassLoader, String, byte[]> getclass;
    //static {
    //    try {
    //        Method buildTransformedClassNodeFor = TransformingClassLoader.class.getDeclaredMethod("buildTransformedClassNodeFor", String.class, String.class);
    //        buildTransformedClassNodeFor.setAccessible(true);
    //        getclass = (transformingClassLoader, s) ->
    //        {
    //            try {
    //                return (byte[]) buildTransformedClassNodeFor.invoke(transformingClassLoader, s, s);
    //            } catch (IllegalAccessException | InvocationTargetException e) {
    //                throw new RuntimeException(e);
    //            }
    //        };
    //    } catch (NoSuchMethodException e) {
    //        throw new RuntimeException(e);
    //    }
    //}

    public static void init(AddMapConcurrent_ASM addMapConcurrentAsm) {
        try {
            init2_3_0$7b(addMapConcurrentAsm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void init2_3_0$7b(AddMapConcurrent_ASM addMapConcurrentAsm) throws IOException {
        addMapConcurrentAsm.stringsList.add(
                new AddMapConcurrent_ASM.AsmTarget("org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld", false, new String[]{
                        "dimensionsAddedThisTick",
                        "dimensionsRemovedThisTick",
                        "voxelShapeUpdatesList",
                }, new AddMapConcurrent_ASM.MethodInfo[]{
                        new AddMapConcurrent_ASM.MethodInfo("<init>", null, false, false),
                        new AddMapConcurrent_ASM.MethodInfo("clearNewUpdatedDeletedShipObjectsAndVoxelUpdates", "()V", false, true),
                        new AddMapConcurrent_ASM.MethodInfo("addDimension", "(Ljava/lang/String;Lorg/valkyrienskies/core/api/world/LevelYRange;)V", false, true),
                        new AddMapConcurrent_ASM.MethodInfo("removeDimension", "(Ljava/lang/String;)V", false, true),
                        new AddMapConcurrent_ASM.MethodInfo("addTerrainUpdates", "(Ljava/lang/String;Ljava/util/List;)V", false, true),
                        new AddMapConcurrent_ASM.MethodInfo("postTick", "()V", false, true)
                })
        );
        File allPath = new File("./config/KAllFix/DataCollectors/ValkyrienSkies");
        if (allPath.isFile()) {
            FileInputStream fileInputStream = new FileInputStream(allPath);
            byte[] bytes = fileInputStream.readAllBytes();
            fileInputStream.close();
            AddMapConcurrent_ASM.AsmTarget e = gson.fromJson(new String(bytes), Data.class).mapConcurrentData1.getAsmTarget();
            addMapConcurrentAsm.stringsList.add(e);
        }

    }
    public static class Data{
        public int fileHash1;
        public MapConcurrentData mapConcurrentData1;
    }
}
