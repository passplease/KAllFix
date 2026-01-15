package asm.n1luik.K_multi_threading.asm.JavaAgent;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ArgsUtil {
    public static List<Map<String, List<String>>> loadArgs(){
        return loadArgs(System.getProperty("K_multi_threading.agent.args"));
    }
    public static List<Map<String, List<String>>> loadArgs(String agentArgs){

        if (agentArgs == null) return new ArrayList<>();
        List<Map<String, List<String>>> list = new ArrayList<>();
        if (!agentArgs.isEmpty()) {
            String[] args = agentArgs.split(";");
            for (String arg : args) {
                File file = new File(arg);
                if (!file.isFile()) {
                    log.error("文件不存在: {}", arg);
                    continue;
                }
                try {
                    String bytes = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    String[] a = bytes.split("\n|\r\n");
                    Map<String, List<String>> map = new HashMap<>();
                    list.add(map);

                    for (String s : a) {
                        if (!s.contains(":")) {
                            log.error("格式错误: {}", s);
                            continue;
                        }
                        String[] b = s.split(":", 2);
                        map.computeIfAbsent(b[0], k -> new ArrayList<>()).add(b[1]);
                    }

                } catch (Exception e) {
                    log.error("读取文件失败: {}", arg, e);
                }
            }
        }
        return list;
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
                    log.error("文件不存在: {}", arg);
                    continue;
                }
                try {
                    String bytes = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    String[] a = bytes.split("\n|\r\n");

                    for (String s : a) {
                        if (!s.contains(":")) {
                            log.error("格式错误: {}", s);
                            continue;
                        }
                        String[] b = s.split(":", 2);
                        map.computeIfAbsent(b[0], k -> new ArrayList<>()).add(b[1]);
                    }

                } catch (Exception e) {
                    log.error("读取文件失败: {}", arg, e);
                }
            }
        }
        return map;
    }
}
