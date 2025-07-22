package n1luik.K_multi_threading.core.dataCollectors;

import asm.n1luik.K_multi_threading.asm.AddMapConcurrent_ASM;
import cpw.mods.modlauncher.TransformingClassLoader;
import n1luik.KAllFix.DataCollectors;
import n1luik.KAllFix.util.UtilKAF;
import n1luik.K_multi_threading.core.dataCollectors.data.MapConcurrentData;
import n1luik.K_multi_threading.core.util.Util;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static n1luik.K_multi_threading.forge.ModInit.getclass;

public class ValkyrienSkies extends DataCollectors.CollectTools<ValkyrienSkies.Data>{

    public ValkyrienSkies() {
        super("ValkyrienSkies", "1", ValkyrienSkies.Data.class);
    }

    @Override
    public boolean test(Data data) {
        if (data == null)return false;
        return data.fileHash1 == Arrays.hashCode(UtilKAF.toMixinClassHashCheckDataByte(getclass.apply((TransformingClassLoader) ValkyrienSkies.class.getClassLoader(), "org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld")));
    }

    @Override
    public boolean job() {
        return DataCollectors.isModLoaded("valkyrienskies");
    }

    @Override
    public Data get() {
        Data data = new Data();
        ClassNode classNode = new ClassNode();
        //try {
            byte[] classFile = getclass.apply((TransformingClassLoader) ValkyrienSkies.class.getClassLoader(), "org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld");
            new ClassReader(classFile).accept(classNode, 0);
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}

        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<init>")) {
                AbstractInsnNode shipToVoxelUpdates = findFieldWrite(method.instructions.getLast(), classNode.name, "shipToVoxelUpdates").getPrevious();
                if (shipToVoxelUpdates != null) {
                    if (shipToVoxelUpdates instanceof MethodInsnNode methodInsnNode) {
                        data.mapConcurrentData1 = new MapConcurrentData(methodInsnNode.owner.replace('/', '.'), false, List.of(),
                                List.of(new MapConcurrentData.MethodInfo(methodInsnNode.name, methodInsnNode.desc, true, true)));
                    }
                    break;
                }else {
                    throw new RuntimeException("没有找到参数：shipToVoxelUpdates");
                }
            }
        }
        byte[] mixinClassHashCheckDataByte = UtilKAF.toMixinClassHashCheckDataByte(classNode);
        //try {
        //    java.nio.file.Files.write(java.nio.file.Paths.get("./ValkyrienSkies.class"), mixinClassHashCheckDataByte);
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
        data.fileHash1 = Arrays.hashCode(mixinClassHashCheckDataByte);
        return data;
    }

    public static AbstractInsnNode findFieldWrite(AbstractInsnNode currentInsn, String owner, String name) {
        while (currentInsn != null) {
            if (currentInsn instanceof FieldInsnNode fieldInsnNode && fieldInsnNode.owner.equals(owner) && fieldInsnNode.name.equals(name)) {
                return currentInsn;
            }
            currentInsn = currentInsn.getPrevious();
        }
        return null;

    }
    public static class Data{
        public int fileHash1;
        public MapConcurrentData mapConcurrentData1;
    }
}
