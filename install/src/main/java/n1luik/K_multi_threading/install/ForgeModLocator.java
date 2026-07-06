package n1luik.K_multi_threading.install;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static n1luik.K_multi_threading.install.FileRead.*;

public class ForgeModLocator extends AbstractJarFileModLocator {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(ForgeModLocator.class);
    public final File file;
    private final List<File> libs;

    public ForgeModLocator() {
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
        delFile(file);
        logger.info("ModLocator initialized successfully");
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

}
