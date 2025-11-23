package n1luik.K_multi_threading.install;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ModLocator extends AbstractJarFileModLocator {
    public final File file;
    private final List<File> libs;

    public ModLocator() {
        this.file = loadModFile();
        // 注册关闭钩子，在 JVM 关闭时删除文件
        libs = new ArrayList<>();
        libs.add(file);
        //if (Boolean.getBoolean("KAF-SaveFileCompression")) {
        //    File file1 = new File("rocksdbjni-6.12.7.jar");
        //    if (file1.isFile()){
        //        libs.add(file1);
        //    }else {
        //        file1 = new File("./lib/rocksdbjni-6.12.7.jar");
        //        if (file1.isFile()){
        //            libs.add(file1);
        //        }else {
        //            throw new RuntimeException("rocksdbjni-6.12.7.jar");
        //        }
        //    }
        //}
        //没用我服了
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
    
    /**
     * 加载模组文件到临时目录
     * @return 加载后的模组文件
     */
    private File loadModFile() {
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
                        return file;
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
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return libs.stream().map(File::toPath);
    }
}
