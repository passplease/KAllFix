package asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace;

import asm.n1luik.K_multi_threading.asm.JavaAgent.ArgsUtil;
import asm.n1luik.K_multi_threading.asm.JavaAgent.all.TransformerBootstrapLauncher;
import cpw.mods.cl.JarModuleFinder;
import cpw.mods.jarhandling.SecureJar;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class BootstrapLauncherAsm {
    //public Configuration resolveAndBind(Configuration c,
    //        ModuleFinder before,
    //                                    ModuleFinder after,
    //                                    Collection<String> roots)
    //{
    //    return Configuration.resolveAndBind(before, List.of(c), after, roots);
    //}//(Ljava.lang.module.Configuration;Ljava.lang.module.ModuleFinder;Ljava.lang.module.ModuleFinder;Ljava.util.Collection;)Ljava.lang.module.Configuration;

    public static JarModuleFinder of(SecureJar... jars) {
        List<Path> add = new ArrayList<>();
        add.add(new File(System.getProperty("K_multi_threading.TransformerBootstrapLauncher1")).toPath());
        var maps = ArgsUtil.loadArgs2();
        for (String s : maps.computeIfAbsent("lib", k -> new ArrayList<>())) {
            File file = new File(s);
            if (!file.exists()) {
                log.error("文件不存在: {}", file);
                continue;
            }
            add.add(file.toPath());
        }
        File libraries = new File("./libraries");
        if (!libraries.exists()) {
            libraries.mkdirs();
        }
        for (String s : maps.computeIfAbsent("mave", k -> new ArrayList<>())) {
            String[] split = s.split(":");
            if (split.length < 2) {
                log.error("mave 格式错误: {}", s);
                continue;
            }
            if (split.length < 3) {
                log.error("不支持此功能");
                continue;
            }
            String groupId = split[0].replace(".", "/");
            String artifactId = split[1];
            String version = split[2];
            String classifier = split.length == 4 ? split[3] : null;
            //下载jar
            String path = groupId + "/" + artifactId + "/" + version + "/";
            File pathFile = new File(libraries, path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String name = artifactId + "-" + (version.endsWith("-SNAPSHOT") ? version.substring(0, version.length()-"-SNAPSHOT".length()) : version) + (classifier == null ? "" : "-" + classifier) + ".jar";
            File jarFile = new File(pathFile, name);
            if (!jarFile.isFile()) {
                for (String mavenUrl : maps.computeIfAbsent("maven_url", k -> new ArrayList<>())) {
                    if (!mavenUrl.endsWith("/")) {
                        mavenUrl += "/";
                    }
                    String url = mavenUrl + path + name;
                    log.info("下载: {}", url);
                    try {
                        Files.copy(new URL(url).openStream(), jarFile.toPath());
                    } catch (Exception e) {
                        log.error("下载失败: {}", s, e);
                        continue;
                    }
                    add.add((jarFile.toPath()));
                }
            }else if(jarFile.isFile()){
                add.add((jarFile.toPath()));
            }

        }
        jars = Arrays.copyOf(jars, jars.length+1);
        jars[jars.length-1] = SecureJar.from(add.toArray(Path[]::new));


        return JarModuleFinder.of(jars);
    }
}
