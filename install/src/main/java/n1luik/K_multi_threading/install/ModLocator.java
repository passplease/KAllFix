package n1luik.K_multi_threading.install;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ModLocator extends AbstractJarFileModLocator {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(ModLocator.class);
    public final File file;
    private final List<File> libs;

    public ModLocator() {
        logger.info("Initializing ModLocator...");
        this.file = loadModFile();
        // Register shutdown hook to delete file when JVM exits
        libs = new ArrayList<>();
        libs.add(file);
        var maps = loadArgs2();
        for (String s : maps.computeIfAbsent("jar", k -> new ArrayList<>())) {
            File e = new File(s);
            if (!e.isFile()) {
                logger.error("文件不存在: {}", e.getAbsolutePath());
                continue;
            }
            libs.add(e);
        }

        logger.debug("Added mod file to library list: {}", file.getAbsolutePath());
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
        //Not useful, I give up
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.debug("Shutdown hook triggered, deleting temporary file: {}", file.getAbsolutePath());
                Files.deleteIfExists(file.toPath());
                logger.info("Temporary file deleted successfully: {}", file.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Failed to delete temporary file: {}", file.getAbsolutePath(), e);
            }
        }));
        logger.info("ModLocator initialized successfully");
    }
    
    /**
     * Load mod file to temporary directory
     * @return Loaded mod file
     */
    private File loadModFile() {
        try {
            logger.info("Loading mod file from resources...");
            InputStream resourceAsStream = Install.class.getResourceAsStream("/k_multi_threading-base.jar");
            if (resourceAsStream == null) {
                logger.error("Failed to find mod file resource: /k_multi_threading-base.jar");
                throw new IOException("Resource not found: /k_multi_threading-base.jar");
            }
            
            logger.debug("Reading mod file bytes...");
            byte[] b = resourceAsStream.readAllBytes();
            long hash = Arrays.hashCode(b) & 0xFFFFFFFFL;
            logger.debug("Mod file hash calculated: 0x{}", Long.toHexString(hash));

            File file = new File(System.getProperty("java.io.tmpdir"), hash+"_KAF.jar");
            logger.debug("Trying to use temporary file: {}", file.getAbsolutePath());
            
            try{
                logger.debug("Attempting to delete existing file: {}", file.getAbsolutePath());
                Files.delete(file.toPath());
                logger.debug("Deleted existing file successfully");
            }catch (NoSuchFileException e) {
                logger.debug("No existing file found, proceeding to create new one");
            }catch (IOException e) {
                logger.warn("Failed to delete existing file, checking if content matches: {}", file.getAbsolutePath(), e);
                //Check if file content is the same, if yes then use it directly
                try{
                    FileInputStream fileInputStream = new FileInputStream(file);
                    logger.debug("Comparing existing file content with resource content");
                    if (Arrays.equals(fileInputStream.readAllBytes(), b)){
                        logger.info("Existing file content matches, reusing file: {}", file.getAbsolutePath());
                        fileInputStream.close();
                        resourceAsStream.close();
                        return file;
                    }else {
                        logger.debug("Existing file content differs, will create new file");
                        fileInputStream.close();
                    }
                }catch (IOException e2) {
                    logger.error("Error reading existing file: {}", file.getAbsolutePath(), e2);
                }
                file = new File(System.getProperty("java.io.tmpdir"), "KAF_" + System.currentTimeMillis() + ".jar");
                logger.debug("Using timestamp-based filename instead: {}", file.getAbsolutePath());
            }

            logger.debug("Creating new temporary file: {}", file.getAbsolutePath());
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            logger.debug("Writing mod content to temporary file");
            fileOutputStream.write(b);
            fileOutputStream.close();
            resourceAsStream.close();
            logger.info("Mod file loaded successfully: {}", file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            logger.error("Failed to load mod file", e);
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
        logger.info("Scanning {} mod candidate files...", libs.size());
        Stream<Path> pathStream = libs.stream().map(File::toPath);
        libs.forEach(file -> logger.debug("Found mod candidate: {}", file.getAbsolutePath()));
        return pathStream;
    }

    public static Map<String, List<String>> loadArgs2(){
        return loadArgs2(System.getProperty("K_multi_threading.agent.args"));
    }
    public static Map<String, List<String>> loadArgs2(String agentArgs){
        if (agentArgs == null) return new HashMap<>();
        Map<String, List<String>> map = new HashMap<>();

        if (!agentArgs.isEmpty()) {
            String[] args = agentArgs.split(";");
            for (String arg : args) {
                File file = new File(arg);
                if (!file.isFile()) {
                    logger.error("文件不存在: {}", arg);
                    continue;
                }
                try {
                    String bytes = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    String[] a = bytes.split("\n|\r\n");

                    for (String s : a) {
                        if (!s.contains(":")) {
                            logger.error("格式错误: {}", s);
                            continue;
                        }
                        String[] b = s.split(":", 2);
                        map.computeIfAbsent(b[0], k -> new ArrayList<>()).add(b[1]);
                    }

                } catch (Exception e) {
                    logger.error("读取文件失败: {}", arg, e);
                }
            }
        }
        return map;
    }
}
