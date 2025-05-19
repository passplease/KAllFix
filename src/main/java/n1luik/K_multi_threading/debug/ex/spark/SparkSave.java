package n1luik.K_multi_threading.debug.ex.spark;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.spark.common.monitor.cpu.CpuMonitor;
import me.lucko.spark.common.monitor.disk.DiskUsage;
import me.lucko.spark.common.monitor.memory.GarbageCollectorStatistics;
import me.lucko.spark.common.monitor.memory.MemoryInfo;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.sampler.node.MergeMode;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.common.util.IndexedListBuilder;
import me.lucko.spark.common.util.MethodDisambiguator;
import me.lucko.spark.forge.ForgeClassSourceLookup;
import me.lucko.spark.proto.SparkProtos;
import me.lucko.spark.proto.SparkSamplerProtos;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.debug.ex.DebugLog;
import n1luik.K_multi_threading.debug.ex.data.*;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.*;

import static n1luik.K_multi_threading.debug.ex.data.KMTDebugCommand.GSON;

public class SparkSave {
    //public static final ForgeClassSourceLookup CLASS_SOURCE_LOOKUP = new ForgeClassSourceLookup();
    public final LogRoot src;
    public NodeSave link;
    public final long linkThreadId;

    public SparkSave(LogRoot src, long linkThreadId) {
        this.src = src;
        this.linkThreadId = linkThreadId;
    }

    private DataBase linkAutoGetNext(JvmCallLogData jvmCallLogData, DataBase data) {
        for (DataBase dataBase : data.next) {
            if (dataBase.data.equals(jvmCallLogData)) {
                return dataBase;
            }
        }
        return null;

    }
    private boolean link() {
        if (!linkCopy())return false;
        for (RelationshipSave relationshipSave : src.relationshipSave) {
            if (relationshipSave.emptyTag) {
                continue;
            }
            DataBase linkStart = link.start;
            for (JvmCallLogData jvmCallLogData : relationshipSave.tag) {
                DataBase linkStart1 = linkAutoGetNext(jvmCallLogData, linkStart);
                if (linkStart1 == null) {
                    linkStart.next.add(new DataBase(jvmCallLogData, relationshipSave.stopTime - relationshipSave.startTime, new ArrayList<>(1)));
                }else {
                    linkStart = linkStart1;
                }
            }
            linkStart.next.addAll(relationshipSave.data.stream().filter(v->v.threadId != linkThreadId).map(v->v.start).toList());
        }
        return true;
    }
    private boolean linkCopy() {
        for (RelationshipSave relationshipSave : src.relationshipSave) {
            for (NodeSave datum : relationshipSave.data) {
                if (datum.threadId == linkThreadId) {
                    link = datum.copy();
                    return true;
                }
            }
        }
        return false;
    }

    public SparkProtos.PlatformStatistics savePlatformStatistics() {
        return SparkProtos.PlatformStatistics.newBuilder()
                .setMemory(saveMemory())
                .setUptime(src.sumTime)
                .build();
    }

    private SparkProtos.PlatformStatistics.Memory saveMemory() {
        return SparkProtos.PlatformStatistics.Memory.newBuilder()
                .setHeap(saveHeap())
                .build();
    }

    private SparkProtos.PlatformStatistics.Memory.MemoryPool saveHeap() {
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return SparkProtos.PlatformStatistics.Memory.MemoryPool.newBuilder()
                .setUsed(memoryUsage.getUsed())
                .setTotal(memoryUsage.getCommitted())
                .build();
    }

    public SparkSamplerProtos.SamplerMetadata saveMetadata() {
        return SparkSamplerProtos.SamplerMetadata.newBuilder()
                //.setComment(null)
                .setNumberOfTicks(src.totalTicks)
                .setCreator(SparkProtos.CommandSenderMetadata.newBuilder()
                        .setType(SparkProtos.CommandSenderMetadata.Type.OTHER)
                        .setName(Base.MOD_ID)
                        .build())
                .setSystemStatistics(saveSystemStatistics())
                .setStartTime(src.startTime)
                .setInterval((int)src.interval_ms())
                .setThreadDumper(SparkSamplerProtos.SamplerMetadata.ThreadDumper.newBuilder()
                        .addAllIds(Collections::emptyIterator)
                        .setType(SparkSamplerProtos.SamplerMetadata.ThreadDumper.Type.UNRECOGNIZED)
                        .build())
                .setDataAggregator(SparkSamplerProtos.SamplerMetadata.DataAggregator.newBuilder()
                        .setType(SparkSamplerProtos.SamplerMetadata.DataAggregator.Type.SIMPLE)
                        .setThreadGrouper(SparkSamplerProtos.SamplerMetadata.DataAggregator.ThreadGrouper.AS_ONE)
                        .build())
                .setPlatformStatistics(savePlatformStatistics())
                .setPlatformMetadata(SparkProtos.PlatformMetadata.newBuilder()
                        .setType(SparkProtos.PlatformMetadata.Type.SERVER)
                        .setName(Base.MOD_ID)
                        .setVersion("1")
                        .setSparkVersion(PlatformInfo.DATA_VERSION))
                .build();
    }

    private SparkProtos.SystemStatistics saveSystemStatistics() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeBean.getUptime();
        SparkProtos.SystemStatistics.Builder builder = SparkProtos.SystemStatistics.newBuilder()
                .setCpu(SparkProtos.SystemStatistics.Cpu.newBuilder()
                        .setThreads(Runtime.getRuntime().availableProcessors())
                        .setProcessUsage(SparkProtos.SystemStatistics.Cpu.Usage.newBuilder()
                                .setLast1M(CpuMonitor.processLoad1MinAvg())
                                .setLast15M(CpuMonitor.processLoad15MinAvg())
                                .build()
                        )
                        .setSystemUsage(SparkProtos.SystemStatistics.Cpu.Usage.newBuilder()
                                .setLast1M(CpuMonitor.systemLoad1MinAvg())
                                .setLast15M(CpuMonitor.systemLoad15MinAvg())
                                .build()
                        )
                        .build()
                )
                .setMemory(SparkProtos.SystemStatistics.Memory.newBuilder()
                        .setPhysical(SparkProtos.SystemStatistics.Memory.MemoryPool.newBuilder()
                                .setUsed(MemoryInfo.getUsedPhysicalMemory())
                                .setTotal(MemoryInfo.getTotalPhysicalMemory())
                                .build()
                        )
                        .setSwap(SparkProtos.SystemStatistics.Memory.MemoryPool.newBuilder()
                                .setUsed(MemoryInfo.getUsedSwap())
                                .setTotal(MemoryInfo.getTotalSwap())
                                .build()
                        )
                        .build()
                )
                .setDisk(SparkProtos.SystemStatistics.Disk.newBuilder()
                        .setTotal(DiskUsage.getTotal())
                        .setUsed(DiskUsage.getUsed())
                        .build()
                )
                .setOs(SparkProtos.SystemStatistics.Os.newBuilder()
                        .setArch(System.getProperty("os.arch"))
                        .setName(System.getProperty("os.name"))
                        .setVersion(System.getProperty("os.version"))
                        .build()
                )
                .setJava(SparkProtos.SystemStatistics.Java.newBuilder()
                        .setVendor(System.getProperty("java.vendor", "unknown"))
                        .setVersion(System.getProperty("java.version", "unknown"))
                        .setVendorVersion(System.getProperty("java.vendor.version", "unknown"))
                        .setVmArgs(String.join(" ", runtimeBean.getInputArguments()))
                        .build())
                .setUptime(uptime);

        Map<String, GarbageCollectorStatistics> gcStats = GarbageCollectorStatistics.pollStats();
        gcStats.forEach((name, statistics) -> builder.putGc(
                name,
                SparkProtos.SystemStatistics.Gc.newBuilder()
                        .setTotal(statistics.getCollectionCount())
                        .setAvgTime(statistics.getAverageCollectionTime())
                        .setAvgFrequency(statistics.getAverageCollectionFrequency(uptime))
                        .build()
        ));

        return builder
                .build();
    }

    public SparkSamplerProtos.StackTraceNode saveBase2(DataBase dataBase, Iterable<Integer> childrenRefs, long startTime) {
        SparkSamplerProtos.StackTraceNode.Builder proto = SparkSamplerProtos.StackTraceNode.newBuilder()
                .setClassName(dataBase.data.declaringClass)
                .setMethodName(dataBase.data.method);
        {
            long start = startTime;
            for (long l = dataBase.time / src.interval; l > 0; l--) {
                proto.addTimes((double) start / 1000000);
                start += src.interval;
            }
        }

        if (dataBase.data.lineNumber >= 0) {
            proto.setLineNumber(dataBase.data.lineNumber);
        }

        if (dataBase.data.parentLineNumber >= 0) {
            proto.setParentLineNumber(dataBase.data.parentLineNumber);
        }


        proto.addAllChildrenRefs(childrenRefs);
        return proto.build();

    }
    public SparkSamplerProtos.ThreadNode saveTree2(String name, NodeSave node) {
        SparkSamplerProtos.ThreadNode.Builder proto = SparkSamplerProtos.ThreadNode.newBuilder()
                .setName(name == null ? node.threadName : name);
        //添加假的times
        {
            long start = node.startTime;
            for (long l = node.time / src.interval; l > 0; l--) {
                proto.addTimes((double) start / 1000000);
                start += src.interval;
            }
        }
        IndexedListBuilder<SparkSamplerProtos.StackTraceNode> nodesArray = new IndexedListBuilder();

        Deque<Node> stack = new ArrayDeque();
        List<Integer> childrenRefs = new LinkedList();

        for(DataBase child : node.start.next) {
            stack.push(new Node(child, childrenRefs));
        }

        while(!stack.isEmpty()) {
            Node node2 = stack.peek();
            if (node2.firstVisit) {
                for (DataBase child : node2.stackTraceNode.next) {
                    stack.push(new Node(child, node2.childrenRefs));
                }

                node2.firstVisit = false;
            } else {

                SparkSamplerProtos.StackTraceNode childProto = saveBase2(node2.stackTraceNode, node2.childrenRefs, node.startTime);
                int childIndex = nodesArray.add(childProto);
                node2.parentChildrenRefs.add(childIndex);
                stack.pop();
            }
        }

        proto.addAllChildrenRefs(childrenRefs);
        proto.addAllChildren(nodesArray.build());

        return proto.build();
    }

    public SparkSamplerProtos.SamplerData save(boolean all) {
        //ClassSourceLookup.Visitor visitor = ClassSourceLookup.createVisitor(CLASS_SOURCE_LOOKUP);
        //if (visitor.hasMappings()) {
        //    proto.putAllClassSources(visitor.getMapping());
        //}

        if(!link()) throw new RuntimeException("link failed");
        //window在时间很短的情况会增加很大的开销所以没有addTimeWindows
        SparkSamplerProtos.SamplerData.Builder builder = SparkSamplerProtos.SamplerData.newBuilder()
                .setMetadata(saveMetadata())
                .putAllTimeWindowStatistics(Map.of());

        builder.addThreads(saveTree2("kmt-link", link));
        if (all) {
            for (RelationshipSave relationshipSave : src.relationshipSave) {
                for (NodeSave datum : relationshipSave.data) {
                    builder.addThreads(saveTree2("[%s] -> [%s]".formatted(relationshipSave.track, datum.threadName), link));

                }

            }
        }

        return builder
                .build();
    }
    private static final class Node {
        private final DataBase stackTraceNode;
        private boolean firstVisit;
        private final List<Integer> childrenRefs;
        private final List<Integer> parentChildrenRefs;

        private Node(DataBase node, List<Integer> parentChildrenRefs) {
            this.firstVisit = true;
            this.childrenRefs = new LinkedList();
            this.stackTraceNode = node;
            this.parentChildrenRefs = parentChildrenRefs;
        }
    }
    public static class Command implements com.mojang.brigadier.Command<CommandSourceStack> {

        @Override
        public int run(CommandContext<CommandSourceStack> v) throws CommandSyntaxException {
            String fileName = StringArgumentType.getString(v, "fileName");
            Util.ioPool().execute(() -> {
                File file = new File(fileName + ".sparkprofile");
                if (file.isDirectory()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个跟文件名一样的目录|" + fileName + ".sparkprofile"));
                    return;
                }
                if (file.isFile()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个同名文件|" + fileName + ".sparkprofile"));
                }
                v.getSource().sendSystemMessage(Component.literal("正在生成"));
                LogRoot stop = DebugLog.stop();
                v.getSource().sendSystemMessage(Component.literal("正在保存"));
                SparkSamplerProtos.SamplerData all = new SparkSave(stop, v.getSource().getServer().getRunningThread().getId()).save(BoolArgumentType.getBool(v, "spark_all"));
                v.getSource().sendSystemMessage(Component.literal("正在写入文件"));
                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(all.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
            return 1;
        }
    }
    public static class CommandAll implements com.mojang.brigadier.Command<CommandSourceStack> {

        @Override
        public int run(CommandContext<CommandSourceStack> v) throws CommandSyntaxException {
            String fileName = StringArgumentType.getString(v, "fileName");
            Util.ioPool().execute(() -> {
                File file = new File(fileName + ".sparkprofile");
                File file2 = new File(fileName + ".json");
                if (file2.isDirectory()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个跟文件名一样的目录|" + fileName + ".json"));
                    return;
                }
                if (file2.isFile()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个同名文件|" + fileName + ".json"));
                }
                if (file.isDirectory()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个跟文件名一样的目录|" + fileName + ".sparkprofile"));
                    return;
                }
                if (file.isFile()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个同名文件|" + fileName + ".sparkprofile"));
                }
                v.getSource().sendSystemMessage(Component.literal("正在生成"));
                LogRoot stop = DebugLog.stop();
                v.getSource().sendSystemMessage(Component.literal("正在保存"));
                String json = GSON.toJson(stop);
                SparkSamplerProtos.SamplerData all = new SparkSave(stop, v.getSource().getServer().getRunningThread().getId()).save(BoolArgumentType.getBool(v, "spark_all"));
                v.getSource().sendSystemMessage(Component.literal("正在写入文件"));
                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(all.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    file2.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    fileOutputStream.write(json.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            });
            return 1;
        }
    }
}
