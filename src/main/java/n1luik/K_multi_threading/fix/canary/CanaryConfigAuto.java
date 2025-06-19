package n1luik.K_multi_threading.fix.canary;

import n1luik.KAllFix.DataCollectors;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;

public class CanaryConfigAuto extends DataCollectors.CollectTools<CanaryConfigAuto.Data>{
    public CanaryConfigAuto() {
        super("CanaryConfig", "1", Data.class);
    }

    @Override
    public boolean test(Data data) {
        Properties props = new Properties();
        if (!new File("./config/canary.properties").isFile()) {
            return false;
        }
        try (FileInputStream fin = new FileInputStream("./config/canary.properties")) {
            props.load(fin);
            if (props.getProperty("mixin.world.tick_scheduler", "true").equals("true")) {
                fin.close();
                return false;
            }
            fin.close();
            return !props.getProperty("mixin.collections.entity_by_type", "true").equals("true");
        } catch (IOException e) {
            throw new RuntimeException("Could not load config file", e);
        }
    }

    @Override
    public boolean job() {
        return Boolean.getBoolean("KAF-FixConfigAuto") && DataCollectors.isModLoaded("canary");
    }

    @Override
    public Data get() {
        Properties props = new Properties();
        File file = new File("./config/canary.properties");
        if (!file.isFile()) {
            try {
                Files.write(file.toPath(), """
                "# This is the configuration file for Canary.
                "# This file exists for debugging purposes and should not be configured otherwise.
                "# Before configuring anything, take a backup of the worlds that will be opened.
                "#
                "# You can find information on editing this file and all the available options here:
                "# https://github.com/AbdElAziz333/Canary/wiki/Configuration-File
                "#
                "# By default, this file will be empty except for this notice.
                
                # KMT
                mixin.collections.entity_by_type=false
                mixin.world.tick_scheduler=false
                """.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        try {
            FileInputStream fin = new FileInputStream(file);
            props.load(fin);
            boolean tick_scheduler = props.getProperty("mixin.world.tick_scheduler", "true").equals("true");
            boolean entity_class_groups = props.getProperty("mixin.collections.entity_by_type", "true").equals("true");
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write('\n');


            if (tick_scheduler) {
                fileOutputStream.write("mixin.world.tick_scheduler=false\n".getBytes());
            }
            if (entity_class_groups) {
                fileOutputStream.write("mixin.collections.entity_by_type=false\n".getBytes());
            }
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static class Data{}
}
