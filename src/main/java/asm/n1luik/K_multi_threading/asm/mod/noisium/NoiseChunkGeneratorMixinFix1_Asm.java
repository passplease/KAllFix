package asm.n1luik.K_multi_threading.asm.mod.noisium;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

@Slf4j
public class NoiseChunkGeneratorMixinFix1_Asm extends ITransformer2 {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {
        boolean add1 = false;
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/ChunkAccess.getSections()[Lnet/minecraft/world/level/chunk/LevelChunkSection;");

        for (MethodNode method : input.methods) {
            if (method.name.equals("noisium$populateNoiseInject")){
                InsnList instructions = method.instructions;
                InsnList instructions2 = method.instructions = new InsnList();



                for (AbstractInsnNode instruction : instructions) {

                    if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode){
                        if (methodInsnNode.owner.equals(strings[0]) && methodInsnNode.name.equals(strings[1]) && methodInsnNode.desc.equals(strings[2])){
                            instructions2.add(instruction);
                            instructions2.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "n1luik/K_multi_threading/fix/NoiseChunkGeneratorMixinFix1Fun", "fun1", "([Lnet/minecraft/world/level/chunk/LevelChunkSection;)[Lnet/minecraft/world/level/chunk/LevelChunkSection;"));
                            add1 = true;
                        }else
                            instructions2.add(instruction);

                    }else
                        instructions2.add(instruction);
                }
            }
        }

        if (!add1){
            String[] strings2 = ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/NoiseBasedChunkGenerator.doFill(Lnet/minecraft/world/level/levelgen/blending/Blender;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;II)Lnet/minecraft/world/level/chunk/ChunkAccess;");
            //对函数进行扫描，如果检测到getSections是高版本就直接退出因为没有这段代码不需要修改，如果检测到m_224284_就是老版本没有需要修复的代码
            boolean canExit = false;
            for (MethodNode method : input.methods) {
                if (method.name.equals(strings2[1]) && method.desc.equals(strings2[2])){
                    canExit = true;
                    break;
                }
            }
            for (MethodNode method : input.methods) {
                if (method.name.equals("m_224284_")){
                    canExit = false;
                    break;
                }
            }
            if (canExit)throw new RuntimeException("Not mapping error: io/github/steveplays28/noisium/mixin/NoiseChunkGeneratorMixin");
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "io/github/steveplays28/noisium/mixin/NoiseChunkGeneratorMixin",
                "io/github/steveplays28/noisium/mixin/compat/lithium/LithiumNoiseChunkGeneratorMixin");
    }
}
