package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class AddListRemoveIterator_Asm extends ITransformer2 {
    public final List<String> stringsList = new ArrayList<>(List.of(
            "net.pinkcats.createlazytick.mixin.OptElement.belt.BeltTickMixin"
    ));

    @Override
    public @NotNull ClassNode transform(ClassNode input) {
        log.info("[AddListRemoveIterator_Asm][{}]", input.name);

        for (MethodNode method : input.methods) {
            var instList = new InsnList();
            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction instanceof MethodInsnNode methodInsnNode){
                    if (methodInsnNode.owner.equals("java/util/List") || methodInsnNode.owner.equals("java/util/ArrayList")){
                        if (methodInsnNode.name.equals("iterator")){
                            instList.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "n1luik/K_multi_threading/core/util/ListRemoveIterator", "of", "(Ljava/util/List;)Ljava/util/Iterator;", false));
                        }else {
                            instList.add(instruction);
                        }
                    }else {
                        instList.add(instruction);
                    }

                }else {
                    instList.add(instruction);
                }
            }
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {

        File f = new File("config/K_multi_threading-AddListRemoveIterator-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals(""))).forEach(stringsList::add);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write("""
                        // 使用//或#屏蔽
                        // 这个文件是用于对单独的函数添加synchronized
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache
                        //跟映射表格式一样simple
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> list = new ArrayList<>();
        for (String strings : stringsList) {
            if (!list.contains(strings)) {
                list.add(strings);
            }
        }

        return Set.of(list.stream().toArray(String[]::new));
    }
}
