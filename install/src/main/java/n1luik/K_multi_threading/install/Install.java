package n1luik.K_multi_threading.install;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Install {


    public static void main(String[] args) throws IOException, URISyntaxException {
        File path = args.length < 1 ? new File("./") : (args[0].toLowerCase().equals("-i") ? new File("./mods") : new File(args[0]));

        path.mkdirs();

        File file1 = new File(path, "k_multi_threading-base.jar");
        file1.delete();
        file1.createNewFile();
        File file = new File(path, "k_multi_threading-asm.jar");
        file.delete();
        file.createNewFile();

        //JarOutputStream jarBase = new JarOutputStream(new FileOutputStream(new File(path, "k_multi_threading-base.jar")));
        JarOutputStream jarAsm = new JarOutputStream(new FileOutputStream(file));
        InputStream resourceAsStream = Install.class.getResourceAsStream("/k_multi_threading-base.jar");
        //File file = new File(System.getProperty("java.io.tmpdir"), "KAF.jar");
        file1.delete();
        file1.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file1);
        fileOutputStream.write(resourceAsStream.readAllBytes());
        fileOutputStream.close();
        resourceAsStream.close();
        JarFile jarFile = new JarFile(new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI()));

        List<String> copy = List.of(
                //"META-INF"
        );
        List<String> mov = List.of(
                "K_multi_threading.mapping",
                "asm/n1luik/K_multi_threading",
                "asm/n1luik/KAllFix",
                "asm/KAllFix.fix/",
                "META-INF/services"
        );
        List<String> mov_not = List.of(
                "META-INF/services/net.minecraftforge.forgespi.locating.IModLocator"
        );
        List<String> names = new ArrayList<>();
        List<String> names2 = new ArrayList<>();
        for (JarEntry jarEntry : jarFile.stream().toList()) {
            String name = jarEntry.getName();
            if (name.endsWith("/")) continue;

            for (String s : copy) {
                boolean isNot = false;
                for (String s1 : mov_not) {
                    if (name.startsWith(s1)) {
                        isNot = true;
                        break;
                    }
                }
                if (isNot) continue;
                if (name.startsWith(s) && !names.contains(name)) {
                    names.add(name);
                    jarAsm.putNextEntry(jarEntry);
                    jarAsm.write(jarFile.getInputStream(jarEntry).readAllBytes());
                }
            }
            boolean isAsm = false;
            for (String s : mov) {
                if (name.startsWith(s)) {
                    boolean isNot = false;
                    for (String s1 : mov_not) {
                        if (name.startsWith(s1)) {
                            isNot = true;
                            break;
                        }
                    }
                    if (isNot) continue;
                    isAsm = true;
                    if (!names.contains(name)) {
                        names.add(name);
                        jarAsm.putNextEntry(jarEntry);
                        jarAsm.write(jarFile.getInputStream(jarEntry).readAllBytes());
                    }
                }
            }
            if (!isAsm && !names2.contains(name)) {

                names2.add(name);
                //jarBase.putNextEntry(jarEntry);
                //jarBase.write(jarFile.getInputStream(jarEntry).readAllBytes());
            }
        }

        //jarBase.close();
        jarAsm.close();
    }
}