package n1luik.KAllFix.util;

import n1luik.KAllFix.Imixin.IOptimizeTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.function.Predicate;

public class UtilKAF {
    //ai生成，作者不会搓
    public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        // 计算 x 坐标差值的平方
        double dx = x2 - x1;
        // 计算 y 坐标差值的平方
        double dy = y2 - y1;
        // 计算 z 坐标差值的平方
        double dz = z2 - z1;
        // 使用 Math.sqrt 方法计算平方根
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    //ai生成，作者不会搓
    public static long hash64long(long[] data, int seed){
        return hash64long(data, data.length, seed);
    }
    public static long hash64long(long[] data, int length, int seed) {
        long h1 = (long) seed;
        long h2 = (long) seed;

        // 常量值
        final long c1 = 0x87c37b91114253d5L;
        final long c2 = 0x4cf5ad432745937fL;

        // 每次处理 16 个字节（4 个 int）
        int i = 0;
        int chunkCount = length / 2;

        for (int j = 0; j < chunkCount; j++) {
            // 读取 4 个 int（16 个字节）
            long k1 = data[i];
            long k2 = data[i + 1];
            i += 2;

            // 混合 k1
            k1 *= c1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= c2;
            h1 ^= k1;
            h1 = Long.rotateLeft(h1, 27);
            h1 = h1 * 5 + 0x52dce729;

            // 混合 k2
            k2 *= c2;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= c1;
            h2 ^= k2;
            h2 = Long.rotateLeft(h2, 31);
            h2 = h2 * 5 + 0x38495ab5;
        }

        // 处理剩余的 int
        int remaining = length % 2;
        if (remaining != 0) {
            long k1 = data[i];
            k1 *= c1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= c2;
            h1 ^= k1;
        }

        // 最终混合
        h1 ^= (long) length;
        h2 ^= (long) length;

        h1 += h2;
        h2 += h1;

        h1 = fmix64(h1);
        h2 = fmix64(h2);

        h1 += h2;
        h2 += h1;

        return h2;
    }

    public static long hash64int(int[] data, int seed){
        return hash64int(data, data.length, seed);
    }
    public static long hash64int(int[] data, int length, int seed) {
        long h1 = (long) seed;
        long h2 = (long) seed;

        // 常量值
        final long c1 = 0x87c37b91114253d5L;
        final long c2 = 0x4cf5ad432745937fL;

        // 每次处理 16 个字节（4 个 int）
        int i = 0;
        int chunkCount = length / 4;

        for (int j = 0; j < chunkCount; j++) {
            // 读取 4 个 int（16 个字节）
            long k1 = ((long) data[i] << 32) | (data[i + 1] & 0xFFFFFFFFL);
            long k2 = ((long) data[i + 2] << 32) | (data[i + 3] & 0xFFFFFFFFL);
            i += 4;

            // 混合 k1
            k1 *= c1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= c2;
            h1 ^= k1;
            h1 = Long.rotateLeft(h1, 27);
            h1 = h1 * 5 + 0x52dce729;

            // 混合 k2
            k2 *= c2;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= c1;
            h2 ^= k2;
            h2 = Long.rotateLeft(h2, 31);
            h2 = h2 * 5 + 0x38495ab5;
        }

        // 处理剩余的 int
        int remaining = length % 4;
        if (remaining != 0) {
            long k1 = 0;
            for (int j = 0; j < remaining; j++) {
                k1 |= ((long) data[i + j] & 0xFFFFFFFFL) << (j * 32);
            }
            k1 *= c1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= c2;
            h1 ^= k1;
        }

        // 最终混合
        h1 ^= (long) length;
        h2 ^= (long) length;

        h1 += h2;
        h2 += h1;

        h1 = fmix64(h1);
        h2 = fmix64(h2);

        h1 += h2;
        h2 += h1;

        return h2;
    }

    private static long fmix64(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;
        return k;
    }

    public static Integer[] sortPos(int[] array){

        // 创建一个包含数组元素索引的数组
        Integer[] indices = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            indices[i] = i;
        }

        // 根据原数组的元素值对索引数组进行排序
        Arrays.sort(indices, Comparator.comparingInt(a -> array[a]));
        return indices;
    }
    public static long hashAttributeModifier(AttributeModifier data){
        UUID id = data.getId();
        return hash64long(new long[]{Double.doubleToLongBits(data.getAmount()), id.getMostSignificantBits(), id.getLeastSignificantBits(), data.getOperation().ordinal() }, 0);
    }
    public static long hashAttributeInstanceList(Collection<AttributeInstance> list){
        //防止在过程中被修改
        List<AttributeInstance> copy = new ArrayList<>(list);
        int Hlen = 0;
        int Hwrite = 0;
        for (AttributeInstance attributeInstance : copy) {
            Hlen += 1  + (attributeInstance.permanentModifiers.size() * 4);
        }
        long[] buf = new long[Hlen];
        for (AttributeInstance data : copy) {
            buf[Hwrite++] = data.getAttribute().getDescriptionId().hashCode();// & 0xFFFFFFFFL;
            //buf[Hlen] |= data.getAttribute().isClientSyncable() ? 0b1000000000000000000000000000000000000000000000000000000000000000L : 0L;
            //buf[Hlen+1] = data.modifiersByOperation.keySet().hashCode();
            for (AttributeModifier modifier : data.permanentModifiers) {
                UUID id = modifier.getId();
                buf[Hwrite] = Double.doubleToLongBits(modifier.getAmount());
                buf[Hwrite+1] = id.getMostSignificantBits();
                buf[Hwrite+2] = id.getLeastSignificantBits();
                buf[Hwrite+3] = modifier.getOperation().ordinal();
                Hwrite += 4;
            }
        }
        return hash64long(buf, 0);
    }
    public static long hashAttributeInstanceSize(Collection<AttributeInstance> list){
        //防止在过程中被修改
        int Hwrite = 1;
        long[] buf = new long[list.size()+1];
        buf[0] = list.size();
        for (AttributeInstance data : list) {
            buf[Hwrite++] = data.permanentModifiers.size();
        }
        return hash64long(buf, 0);
    }
    public static long hashAttributeInstance(AttributeInstance data){
        int Hlen = 1  + (data.permanentModifiers.size() * 4);
        int Hwrite = 1;
        long[] buf = new long[Hlen];
        buf[0] = data.getAttribute().getDescriptionId().hashCode();// & 0xFFFFFFFFL;
        //buf[0] |= data.getAttribute().isClientSyncable() ? 0b1000000000000000000000000000000000000000000000000000000000000000L : 0L;
        //buf[1] = data.modifiersByOperation.keySet().hashCode();
        for (AttributeModifier modifier : data.permanentModifiers) {
            UUID id = modifier.getId();
            buf[Hwrite] = Double.doubleToLongBits(modifier.getAmount());
            buf[Hwrite+1] = id.getMostSignificantBits();
            buf[Hwrite+2] = id.getLeastSignificantBits();
            buf[Hwrite+3] = modifier.getOperation().ordinal();
            Hwrite += 4;
        }
        return hash64long(buf, 0);
    }


    public static record MethodInfo(String name, String desc, int nameHash, int descHash) {
        public MethodInfo(String name, String desc) {
            this(name, desc, name.hashCode(), desc.hashCode());
        }
    }
    public static record FieldInfo(String name, int nameHash) {
        public FieldInfo(String name) {
            this(name, name.hashCode());
        }
    }
    /**
     * 获取容许mixin的class哈希校验数据
     * @param classData 修改的数据
     */
    public static byte[] toMixinClassHashCheckDataByte(ClassNode classData){
        toMixinClassHashCheckData(classData);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classData.accept(classWriter);
        return classWriter.toByteArray();
    }
    /**
     * 获取容许mixin的class哈希校验数据
     * @param classFile 类数据
     */
    public static byte[] toMixinClassHashCheckDataByte(byte[] classFile){
        ClassNode classData = new ClassNode();
        new ClassReader(classFile).accept(classData, 0);
        toMixinClassHashCheckData(classData);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classData.accept(classWriter);
        return classWriter.toByteArray();
    }
    /**
     * 获取容许mixin的class哈希校验数据
     * @param classData 修改的数据
     */
    public static void toMixinClassHashCheckData(ClassNode classData){
        List<MethodInfo> methodInfos = new ArrayList<>();
        List<FieldInfo> fieldInfos = new ArrayList<>();
        List<FieldNode> fieldNodes = new ArrayList<>();
        for (MethodNode method : classData.methods) {
            if (method.visibleAnnotations == null) {
                continue;
            }
            for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;")) {
                    methodInfos.add(new MethodInfo(method.name, method.desc));
                    method.name = "mixinFix";
                    //移除这个注释
                    method.visibleAnnotations.remove(visibleAnnotation);
                    break;
                }
            }
        }
        for (FieldNode field : classData.fields) {
            if (field.visibleAnnotations == null) {
                continue;
            }
            for (AnnotationNode visibleAnnotation : field.visibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;")) {
                    //不知道会怎么样
                    fieldInfos.add(new FieldInfo(field.name));
                    fieldNodes.add(field);
                    //移除这个注释
                    field.visibleAnnotations.remove(visibleAnnotation);
                    break;
                }
            }
        }
        classData.fields = fieldNodes;
        for (MethodNode method : classData.methods) {
            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction instanceof MethodInsnNode methodInsnNode) {
                    for (MethodInfo methodInfo : methodInfos) {
                        if (methodInfo.nameHash == methodInsnNode.name.hashCode() && methodInfo.descHash == methodInsnNode.desc.hashCode()) {
                            if (methodInsnNode.name.equals(methodInfo.name) && methodInsnNode.desc.equals(methodInfo.desc)) {
                                methodInsnNode.name = "";
                            }
                        }
                    }
                }else if (instruction instanceof FieldInsnNode fieldInsnNode) {
                    for (FieldInfo fieldInfo : fieldInfos) {
                        if (fieldInfo.nameHash == fieldInsnNode.name.hashCode()) {
                            if (fieldInsnNode.name.equals(fieldInfo.name)) {
                                fieldInsnNode.name = "";
                            }
                        }
                    }
                }else if (instruction instanceof InvokeDynamicInsnNode invokeDynamicInsnNode) {
                    Handle bsm = invokeDynamicInsnNode.bsm;
                    if (bsm.getOwner().equals(classData.name)) {
                        for (MethodInfo methodInfo : methodInfos) {
                            if (methodInfo.nameHash == bsm.getName().hashCode() && methodInfo.descHash == bsm.getDesc().hashCode()) {
                                if (bsm.getName().equals(methodInfo.name) && bsm.getDesc().equals(methodInfo.desc)) {
                                    invokeDynamicInsnNode.bsm = new Handle(
                                            bsm.getTag(),
                                            bsm.getOwner(),
                                            "",
                                            bsm.getDesc(),
                                            bsm.isInterface()
                                    );
                                }
                            }
                        }
                    }
                    if (invokeDynamicInsnNode.bsmArgs != null) {
                        for (int i = 0; i < invokeDynamicInsnNode.bsmArgs.length; i++) {
                            Object bsmArg = invokeDynamicInsnNode.bsmArgs[i];
                            if (bsmArg instanceof Handle handle) {
                                switch (handle.getTag()) {
                                    case Opcodes.H_INVOKESTATIC, Opcodes.H_INVOKESPECIAL, Opcodes.H_INVOKEVIRTUAL, Opcodes.H_NEWINVOKESPECIAL, Opcodes.H_INVOKEINTERFACE -> {
                                        for (MethodInfo methodInfo : methodInfos) {
                                            if (methodInfo.nameHash == handle.getName().hashCode() && methodInfo.descHash == handle.getDesc().hashCode()) {
                                                if (handle.getName().equals(methodInfo.name) && handle.getDesc().equals(methodInfo.desc)) {
                                                    invokeDynamicInsnNode.bsmArgs[i] = new Handle(
                                                            handle.getTag(),
                                                            handle.getOwner(),
                                                            "",
                                                            handle.getDesc(),
                                                            handle.isInterface()
                                                    );
                                                }
                                            }
                                        }
                                    }
                                    case Opcodes.H_GETFIELD, Opcodes.H_GETSTATIC, Opcodes.H_PUTFIELD, Opcodes.H_PUTSTATIC -> {
                                        for (FieldInfo fieldInfo : fieldInfos) {
                                            if (fieldInfo.nameHash == handle.getName().hashCode()) {
                                                if (handle.getName().equals(fieldInfo.name)) {
                                                    invokeDynamicInsnNode.bsmArgs[i] = new Handle(
                                                            handle.getTag(),
                                                            handle.getOwner(),
                                                            "",
                                                            handle.getDesc(),
                                                            handle.isInterface()
                                                    );
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    //跟正常的有区别
    /**
     * 过滤区块标签的{@link net.minecraft.core.BlockPos#findClosestMatch}
     */
    public static Optional<BlockPos> tagFindClosestMatch(Level w, int id, BlockPos pos, int r, int h, Predicate<BlockPos> predicate) {
        //部分代码会运行2次，懒得修了
        int x = SectionPos.blockToSectionCoord(pos.getX());
        int z = SectionPos.blockToSectionCoord(pos.getZ());
        int xb = x * 16;
        int zb = z * 16;
        boolean xFlag = (pos.getX() % 16) != 0;
        boolean zFlag = (pos.getZ() % 16) != 0;
        int xEnd = (xFlag ? (
                SectionPos.blockToSectionCoord(pos.getX() + r) == SectionPos.blockToSectionCoord(pos.getX()) ?
                        (r >= 16 ? 1 : 2) : 1
        ) : 0) + (r / 16);
        int zEnd = (zFlag ? (
                SectionPos.blockToSectionCoord(pos.getZ() + r) == SectionPos.blockToSectionCoord(pos.getZ()) ?
                        (r >= 16 ? 1 : 2) : 1
        ) : 0) + (r / 16);
        int y = pos.getY();
        //System.out.printf("tagFindClosestMatch-1 %s %s %s %s %s %s %s %s %s%n"
        //        , pos, x, z, xb, zb, xFlag, zFlag, xEnd, zEnd);
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        if (xFlag || zFlag) {
            for (int tx = 0; tx <= xEnd; tx++) {
                for (int tz = 0; tz <= zEnd; tz++) {
                    for (int tn = 0; tn < 4; tn++) {

                        //System.out.println("tagFindClosestMatch-3");
                        boolean nx = (tn & 0b01) == 0;
                        boolean nz = (tn & 0b10) == 0;
                        int rcx = (nx ? x + tx : x - tx);
                        int rcz = (nz ? z + tz : z - tz);
                        IOptimizeTag chunkNow = (IOptimizeTag) w.getChunkSource().getChunkNow(rcx, rcz);
                        if (chunkNow == null || !chunkNow.KAllFix$getOptimizeTag(id)) {
                            //System.out.printf("tagFindClosestMatch-3 %s %s %s %s%n", rcx, rcz, chunkNow == null, chunkNow == null ? false : chunkNow.KAllFix$getOptimizeTag(id));
                            continue;
                        }
                        //System.out.printf("tagFindClosestMatch-3 %s %s%n", rcx, rcz);
                        //System.out.println("tagFindClosestMatch-4");
                        if (tx == 0 || tz == 0 || tx == xEnd || tz == zEnd) {
                            int xo, zo, xoe, zoe;

                            if (tx == 0) {
                                if (nx) {
                                    xo = (rcx * 16) + ((pos.getX()) - (SectionPos.blockToSectionCoord(pos.getX()) * 16));
                                    xoe = xo + 16;
                                } else {
                                    xo = rcx * 16;
                                    xoe = xo + (16 - (16 - ((pos.getX()) - (SectionPos.blockToSectionCoord(pos.getX()) * 16))));
                                }
                            } else {
                                if (nx) {
                                    xo = rcx * 16;
                                    if (tx == xEnd) {
                                        xoe = xo + ((pos.getX() + r) - (SectionPos.blockToSectionCoord(pos.getX() + r) * 16));
                                    } else {
                                        xoe = xo + 16;
                                    }
                                } else {
                                    if (tx == xEnd) {
                                        xoe = (rcx * 16) + 16;
                                        xo = (rcx * 16) + (16 - ((pos.getX() + r) - (SectionPos.blockToSectionCoord(pos.getX() + r) * 16)));
                                    } else {
                                        xo = rcx * 16;
                                        xoe = xo + 16;
                                    }
                                }
                            }

                            if (tz == 0) {
                                if (nz) {
                                    zo = (rcz * 16) + ((pos.getZ()) - (SectionPos.blockToSectionCoord(pos.getZ()) * 16));
                                    zoe = (rcz * 16) + 16;
                                } else {
                                    zo = rcz * 16;
                                    zoe = zo + (16 - (16 - ((pos.getZ()) - (SectionPos.blockToSectionCoord(pos.getZ()) * 16))));
                                }
                            } else {
                                if (nz) {
                                    zo = rcz * 16;
                                    if (tz == zEnd) {
                                        zoe = zo + ((pos.getZ() + r) - (SectionPos.blockToSectionCoord(pos.getZ() + r) * 16));
                                    } else {
                                        zoe = zo + 16;
                                    }
                                } else {
                                    if (tz == zEnd) {
                                        zoe = (rcz * 16) + 16;
                                        zo = (rcz * 16) + (16 - ((pos.getZ() + r) - (SectionPos.blockToSectionCoord(pos.getZ() + r) * 16)));
                                    } else {
                                        zo = rcz * 16;
                                        zoe = zo + 16;
                                    }
                                }
                            }

                            for (int ty = 0; ty <= h; ty++) {
                                for (int rn = 0; rn < 2; rn++) {
                                    if (rn == 0) {
                                        for (int tempx = xo; tempx < xoe; tempx++) {
                                            for (int tempz = zo; tempz < zoe; tempz++) {
                                                mp.set(tempx, (y + ty), tempz);
                                                if (predicate.test(mp)) {
                                                    return Optional.of(new BlockPos(mp));
                                                }
                                            }
                                        }
                                    }else {
                                        for (int tempx = xo; tempx < xoe; tempx++) {
                                            for (int tempz = zo; tempz < zoe; tempz++) {
                                                mp.set(tempx, (y - ty), tempz);
                                                if (predicate.test(mp)) {
                                                    return Optional.of(new BlockPos(mp));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            continue;
                        } else {
                            int xo = rcx * 16;
                            int zo = rcz * 16;
                            for (int ty = 0; ty <= h; ty++) {
                                for (int rn = 0; rn < 2; rn++) {
                                    if (rn == 0) {
                                        for (int tx2 = 0; tx2 < 16; tx2++) {
                                            for (int tz2 = 0; tz2 < 16; tz2++) {
                                                mp.set(xo + tx2, (y + ty), zo + tz2);
                                                if (predicate.test(mp)) {
                                                    return Optional.of(new BlockPos(mp));
                                                }
                                            }
                                        }
                                    }else {
                                        for (int tx2 = 0; tx2 < 16; tx2++) {
                                            for (int tz2 = 0; tz2 < 16; tz2++) {
                                                mp.set(xo + tx2, (y - ty), zo + tz2);
                                                if (predicate.test(mp)) {
                                                    return Optional.of(new BlockPos(mp));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            for (int tx = 0; tx <= xEnd; tx++) {
                for (int tz = 0; tz <= zEnd; tz++) {
                    for (int tn = 0; tn < 4; tn++) {
                        //int n = tn == 0 ? 1 : -1;
                        int rcx = ((tn & 0b01) == 0 ? tx : -tx) + x;
                        int rcz = ((tn & 0b10) == 0 ? tz : -tz) + z;
                        IOptimizeTag chunkNow = (IOptimizeTag) w.getChunkSource().getChunkNow(rcx, rcz);
                        if (chunkNow == null || !chunkNow.KAllFix$getOptimizeTag(id)) {
                            continue;
                        }
                        int xo = rcx * 16;
                        int zo = rcz * 16;
                        for (int ty = 0; ty <= h; ty++) {
                            for (int rn = 0; rn < 2; rn++) {
                                if (rn == 0) {
                                    for (int tx2 = 0; tx2 < 16; tx2++) {
                                        for (int tz2 = 0; tz2 < 16; tz2++) {
                                            mp.set(xo + tx2, (y + ty), zo + tz2);
                                            if (predicate.test(mp)) {
                                                return Optional.of(new BlockPos(mp));
                                            }
                                        }
                                    }
                                } else {
                                    for (int tx2 = 0; tx2 < 16; tx2++) {
                                        for (int tz2 = 0; tz2 < 16; tz2++) {
                                            mp.set(xo + tx2, (y - ty), zo + tz2);
                                            if (predicate.test(mp)) {
                                                return Optional.of(new BlockPos(mp));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
    public static Optional<BlockPos> tagAddFindClosestMatch(Level w, int id, BlockPos pos, int r, int h, Predicate<BlockPos> predicate){
        int x = SectionPos.blockToSectionCoord(pos.getX());
        int z = SectionPos.blockToSectionCoord(pos.getZ());
        boolean xFlag = (pos.getX() % 16) != 0;
        boolean zFlag = (pos.getZ() % 16) != 0;
        int xEnd = (xFlag ? (
                SectionPos.blockToSectionCoord(pos.getX() + r) == SectionPos.blockToSectionCoord(pos.getX()) ?
                        1 : 2
        ) : 0) + x + (r / 16);
        int zEnd = (zFlag ? (
                SectionPos.blockToSectionCoord(pos.getZ() + r) == SectionPos.blockToSectionCoord(pos.getZ()) ?
                        1 : 2
        ) : 0) + z + (r / 16);
        int y = pos.getY();
        int yEnd = pos.getY() + h;
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        if (xFlag || zFlag) {
            for (int tx = x; tx <= xEnd; tx++) {
                for (int tz = z; tz <= zEnd; tz++) {
                    IOptimizeTag chunkNow = (IOptimizeTag) w.getChunkSource().getChunkNow(tx, tz);
                    if (chunkNow == null || !chunkNow.KAllFix$getOptimizeTag(id)) {
                        continue;
                    }
                    if (tx == x || tz == z || tx == xEnd || tz == zEnd) {
                        int xo;
                        int zo;
                        int xoe;
                        int zoe;
                        if (tx == x) {
                            xo = (tx * 16) + ((pos.getX()) - (SectionPos.blockToSectionCoord(pos.getX()) * 16));
                            xoe = xo + 16;
                        }else {
                            xo = tx * 16;
                            if (tx == xEnd) {
                                xoe = xo + ((pos.getX() + r) - (SectionPos.blockToSectionCoord(pos.getX() + r) * 16));
                            }else {
                                xoe = xo + 16;
                            }
                        }
                        if (tz == z) {
                            zo = (tz * 16) + ((pos.getZ()) - (SectionPos.blockToSectionCoord(pos.getZ()) * 16));
                            zoe = zo + 16;
                        }else {
                            zo = tz * 16;
                            if (tz == zEnd) {
                                zoe = zo + ((pos.getZ() + r) - (SectionPos.blockToSectionCoord(pos.getZ() + r) * 16));
                            }else {
                                zoe = zo + 16;
                            }
                        }

                        for (int ty = y; ty <= yEnd; ty++) {
                            for (int tempx = xo; tempx < xoe; tempx++) {
                                for (int tempz = zo; tempz < zoe; tempz++) {
                                    mp.set(tempx, ty, tempz);
                                    if (predicate.test(mp)) {
                                        return Optional.of(new BlockPos(mp));
                                    }
                                }
                            }
                        }
                        continue;
                    }else {
                        int xo = tx * 16;
                        int zo = tz * 16;
                        for (int ty = y; ty <= yEnd; ty++) {
                            for (int tx2 = 0; tx2 < 16; tx2++) {
                                for (int tz2 = 0; tz2 < 16; tz2++) {
                                    mp.set(xo + tx2, ty, zo + tz2);
                                    if (predicate.test(mp)) {
                                        return Optional.of(new BlockPos(mp));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else {
            for (int tx = x; tx <= xEnd; tx++) {
                for (int tz = z; tz <= zEnd; tz++) {
                    IOptimizeTag chunkNow = (IOptimizeTag) w.getChunkSource().getChunkNow(tx, tz);
                    if (chunkNow == null || !chunkNow.KAllFix$getOptimizeTag(id)) {
                        continue;
                    }
                    int xo = tx * 16;
                    int zo = tz * 16;
                    for (int ty = y; ty <= yEnd; ty++) {
                        for (int tx2 = 0; tx2 < 16; tx2++) {
                            for (int tz2 = 0; tz2 < 16; tz2++) {
                                mp.set(xo + tx2, ty, zo + tz2);
                                if (predicate.test(mp)) {
                                    return Optional.of(new BlockPos(mp));
                                }
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
}
