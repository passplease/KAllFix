package n1luik.KAllFix;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import lombok.AllArgsConstructor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataCollectors {
    static Logger logger = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    protected final List<CollectTools<?>> tools = new CopyOnWriteArrayList<>();
    protected final Map<String, CollectTools<?>> nameToolsMap = new ConcurrentHashMap<>();

    public void addTools(CollectTools<?> tools) {
        this.tools.add(tools);
        this.nameToolsMap.put(tools.name(), tools);
    }

    public static boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        }
        return ModList.get().isLoaded(modId);
    }

    public void run() throws IOException {
        List<CollectTools<?>> up = new ArrayList<>(tools);
        File allPath = new File("./config/KAllFix/DataCollectors");
        allPath.mkdirs();
        File versionPath = new File(allPath, "version.json");
        boolean isNew = !versionPath.isFile();
        Version version1 = null;
        if(!isNew){
            FileInputStream fileInputStream = new FileInputStream(versionPath);
            try {
                version1 = GSON.fromJson(new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8), Version.class);
                fileInputStream.close();
                for (Version.VersionData version : version1.versions) {
                    CollectTools<?> collectTools = nameToolsMap.get(version.name);
                    if (collectTools == null) {
                        continue;
                    }
                    if (!collectTools.version.equals(version.version)) {
                        continue;
                    }
                    if (!collectTools.job()) {
                        up.remove(collectTools);
                        continue;
                    }

                    File file = new File(allPath, collectTools.name);
                    if (file.isFile()) {
                        String s;
                        try (FileInputStream fileInputStream2 = new FileInputStream(file)) {
                            s = new String(fileInputStream2.readAllBytes(), StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            continue;
                        }
                        try {
                            if (s.hashCode() == version.fileHash && collectTools.autoTest(s)) {
                                up.remove(collectTools);
                            }
                        } catch (Throwable e) {
                            logger.error("""
                                    检测到文件{}出现问题，可能是文件损坏
                                    请删除文件{}
                                    或者手动删除文件{}
                                    或者联系作者
                                    """, collectTools.name, file.getAbsolutePath(), versionPath.getAbsolutePath());
                            continue;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("""
                        检测到文件{}出现问题，可能是文件损坏
                        请删除文件{}
                        或者手动删除文件{}
                        或者联系作者
                        """, versionPath.getAbsolutePath(), versionPath.getAbsolutePath(), versionPath.getAbsolutePath());
                fileInputStream.close();
            }
        }
        //需要生成的文件
        if (!up.isEmpty()){
            int upSize = 0;
            if (version1 == null) {
                version1 = new Version();
                version1.versions = new ArrayList<>();
            }
            List<Version.VersionData> versions = version1.versions;
            List<String> names = new ArrayList<>();
            for (CollectTools<?> collectTools : up) {
                if (!collectTools.job()) continue;
                upSize++;
                names.add(collectTools.name);
                if (!isNew){
                    for (int i = 0; i < versions.size(); i++) {
                        if (versions.get(i).name.equals(collectTools.name)) {
                            versions.remove(i);
                        }
                    }
                }
                String s = collectTools.autoGet();
                File file = new File(allPath, collectTools.name);
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.close();
                versions.add(new Version.VersionData(collectTools.name(), collectTools.version(), s.hashCode()));
            }
            versionPath.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(versionPath);
            fileOutputStream.write(GSON.toJson(version1).getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
            if (upSize > 0){
                for (String name : names) {
                    logger.info("{}",name);
                }
                logger.info("""
                        ---------------
                        需要重新启动
                        A reboot is required
                        Требуется перезагрузка
                        再起動が必要です
                        """);

                System.exit(0);
            }
        }
    }

    public static class Version {
        public List<VersionData> versions;
        @AllArgsConstructor
        public static class VersionData {
            public VersionData(){}
            public String name;
            public String version;
            public int fileHash;
        }
    }

    public static abstract class CollectTools<T> {
        private final String name;
        private final String version;
        private final Class<T> type;

        public CollectTools(String name, String version, Class<T> type) {
            this.name = name;
            this.version = version;
            this.type = type;
        }

        /**
         * 测试文件是否符合条件
         * */
        public abstract boolean test(T t);
        /**
         * 是否工作
         * */
        public abstract boolean job();
        public abstract T get();

        public String toJson(T t) {
            return GSON.toJson(t);
        }

        public T fromJson(String json) {
            return GSON.fromJson(json, type);
        }

        public boolean autoTest(String json) {
            T data = fromJson(json);
            return test(data);
        }
        public String autoGet() throws IOException {
            return toJson(get());
        }

        @Override
        public String toString() {
            return "CollectTools[" +
                    "name=" + name + ", " +
                    "version=" + version + ']';
        }

        public String name() {
            return name;
        }

        public String version() {
            return version;
        }

        public Class<T> type() {
            return type;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, version, type);
        }


    }
}
