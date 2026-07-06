package n1luik.K_multi_threading.install;

import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.loading.LogMarkers;
import net.neoforged.fml.loading.StringUtils;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;
import net.neoforged.neoforgespi.locating.IncompatibleFileReporting;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;
import org.slf4j.Logger;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static n1luik.K_multi_threading.install.FileRead.*;

public class NeoForgeModLocator implements IModFileCandidateLocator {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(NeoForgeModLocator.class);
    private static final String SUFFIX = ".jar";
    public final File file;
    private final List<File> libs;

    public NeoForgeModLocator() {
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
    public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
        logger.debug(LogMarkers.SCAN, "Scanning mods dir {} for mods", this.libs);

        List<Path> directoryContent;
        try {
            directoryContent = libs.stream().map(File::toPath)
                    .filter(p -> StringUtils.toLowerCase(p.getFileName().toString()).endsWith(SUFFIX))
                    .sorted(Comparator.comparing(path -> StringUtils.toLowerCase(path.getFileName().toString())))
                    .toList();
        } catch (UncheckedIOException e) {
            throw new RuntimeException(e);
        }

        for (var file : directoryContent) {
            if (!Files.isRegularFile(file)) {
                pipeline.addIssue(ModLoadingIssue.warning("fml.modloadingissue.brokenfile.unknown").withAffectedPath(file));
                continue;
            }

            pipeline.addPath(file, ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.WARN_ALWAYS);
        }
    }


}
