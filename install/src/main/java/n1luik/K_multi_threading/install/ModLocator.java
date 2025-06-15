package n1luik.K_multi_threading.install;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ModLocator extends AbstractJarFileModLocator {
    public File file;
    public ModLocator() {
        File file1;
        try {
            InputStream resourceAsStream = Install.class.getResourceAsStream("/k_multi_threading-base.jar");
            byte[] b = resourceAsStream.readAllBytes();
            long hash = Arrays.hashCode(b) & 0xFFFFFFFFL;

            File file = new File(System.getProperty("java.io.tmpdir"), hash+"_KAF.jar");
            try{
                Files.delete(file.toPath());
            }catch (NoSuchFileException e) {
                //没有文件
            }catch (IOException e) {
                //检查文件是不是一样如果一样就直接使用
                try{
                    FileInputStream fileInputStream = new FileInputStream(file);
                    if (Arrays.equals(fileInputStream.readAllBytes(), b)){
                        fileInputStream.close();
                        return;
                    }else {
                        fileInputStream.close();
                    }
                }catch (IOException e2) {
                    e2.printStackTrace();
                }
                file = new File(System.getProperty("java.io.tmpdir"), "KAF_" + System.currentTimeMillis() + ".jar");
            }

            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(b);
            fileOutputStream.close();
            resourceAsStream.close();
            file1 = file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.file = file1;
        // 注册关闭钩子，在 JVM 关闭时删除文件
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public String name() {
        return "K_multi_threading.install";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }

    @Override
    public Stream<Path> scanCandidates() {
        return Stream.of(file.toPath());
    }
}
