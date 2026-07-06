package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.JavaAgent.AsmUtil;
import asm.n1luik.K_multi_threading.asm.JavaAgent.JavaAgent;
import asm.n1luik.K_multi_threading.asm.JavaAgent.all.AsmApiReplace;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.valkyrienskies.core.impl.shadow.F;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class GenFile {
    public static void run() throws ClassNotFoundException {

        ITransformer2 iTransformer2 = AsmUtil.newForge2MCPMap();
        AsmApiReplace asmApiReplace = new AsmApiReplace(JavaAgent.class.getClassLoader().loadClass("asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.AsmApiNeoForge"));
        String atc = asmApiReplace.targets().iterator().next().replace(".", "/");
        try {
            ZipInputStream zipFile3 = new ZipInputStream(GenFile.class.getProtectionDomain().getCodeSource().getLocation().openStream());
            ZipInputStream zipFile2 = new ZipInputStream(GenFile.class.getResourceAsStream("/k_multi_threading-base.jar"));
            Path path = Path.of("NEO.jar");
            if(Files.exists(path))Files.delete(path);
            Files.createFile(path);
            path = Path.of("NEO_base.jar");
            if(Files.exists(path))Files.delete(path);
            Files.createFile(path);
            ZipOutputStream fileOutputStream = new ZipOutputStream(new FileOutputStream("NEO.jar"));
            ZipOutputStream fileOutputStream2 = new ZipOutputStream(new FileOutputStream("NEO_base.jar"));
            for (int i = 0; i < 2; i++) {
                var zipFile = i == 0 ? zipFile3 : zipFile2;
                ZipOutputStream fileOutputStream1 = i == 0 ? fileOutputStream : fileOutputStream2;

                while (true) {
                    ZipEntry entry = zipFile.getNextEntry();
                    System.out.println(entry);
                    if (entry == null) break;
                    if (entry.getName().endsWith("k_multi_threading-base.jar"))continue;
                    if (!entry.getName().endsWith(".class")) {
                        byte[] bytes = zipFile.readAllBytes();
                        ZipEntry zipEntry = new ZipEntry(entry.getName());
                        zipEntry.setSize(bytes.length);
                        fileOutputStream1.putNextEntry(zipEntry);
                        fileOutputStream1.write(bytes);
                        fileOutputStream1.closeEntry();
                        continue;
                    }

                    byte[] bytes = zipFile.readAllBytes();
                    ClassReader classReader = new ClassReader(bytes);
                    ClassWriter classWriter = new ClassWriter(0);
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, 0);
                    iTransformer2.transform(classNode);
                    if(classNode.name.contains(atc))asmApiReplace.transform(classNode);
                    classNode.accept(classWriter);
                    bytes = classWriter.toByteArray();
                    ZipEntry zipEntry = new ZipEntry(entry.getName());
                    zipEntry.setSize(bytes.length);
                    fileOutputStream1.putNextEntry(zipEntry);
                    fileOutputStream1.write(bytes);
                    fileOutputStream1.closeEntry();
                }
            }
            fileOutputStream2.close();
            byte[] base = Files.readAllBytes(Path.of("NEO_base.jar"));

            ZipEntry zipEntry = new ZipEntry("/k_multi_threading-base.jar");
            zipEntry.setSize(base.length);
            fileOutputStream.putNextEntry(zipEntry);
            fileOutputStream.write(base);
            fileOutputStream.closeEntry();
            fileOutputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
