package asm.n1luik.KAllFix.asm.mod.gcyr;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class CanaryConfig {
    public final static boolean ENABLED;
    static {
        if (ForgeAsm.isModLoaded("gcyr")) {
            if (ForgeAsm.isModLoaded("canary")) {

                File file = new File("./config/canary.properties");
                if (file.isFile()) {
                    try (FileInputStream fin = new FileInputStream(file)) {
                        Properties props = new Properties();
                        props.load(fin);
                        if (props.getProperty("mixin.entity", "true").equals("false")) {
                            ENABLED = false;
                        }else {
                            if (props.getProperty("mixin.entity.collisions", "true").equals("false")) {
                                ENABLED = false;
                            }else {
                                //if (props.getProperty("mixin.entity.collisions.movement", "true").equals("false")) {
                                //    ENABLED = false;
                                //}else {
                                    ENABLED = true;
                                //}
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Could not load config file", e);
                    }
                }else {
                    ENABLED = true;
                }
            }else {
                ENABLED = false;
            }
        }else {
            ENABLED = false;
        }
        log.info("CanaryConfig.ENABLED = {}", ENABLED);
    }
}
