package n1luik.K_multi_threading.debug.ex;

import com.mojang.logging.LogUtils;
import n1luik.K_multi_threading.debug.ex.data.DataBase;
import n1luik.K_multi_threading.debug.ex.data.JvmCallLogData;
import n1luik.K_multi_threading.debug.ex.data.NodeSave;
import n1luik.K_multi_threading.debug.ex.data.RelationshipSave;
import org.slf4j.Logger;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Relationship implements BooleanSupplier {
    static final Logger LOGGER = LogUtils.getLogger();
    public long track = 0;
    public String trackName = null;
    public long trackTime = 0;
    public volatile long stop = 0;
    public volatile ThreadInfo tag = null;
    public Node[] nodes = null;
    public Node[] nodeCall = null;


    public void initNode(int size){
        nodes = new Node[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new Node();
        }
        nodeCall = nodes.clone();
    }

    public boolean isStop(){
        return stop >= nodes.length;
    }

    public void setTag(){
        tag = DebugLog.threadMXBean.getThreadInfo(Thread.currentThread().getId());
    }

    public RelationshipSave save(){
        RelationshipSave relationshipSave = new RelationshipSave();
        relationshipSave.track = trackName;
        relationshipSave.stopTime = stop;
        relationshipSave.startTime = trackTime;
        saveTrack(relationshipSave);
        List<NodeSave> nodeSaves = new ArrayList<>(nodes.length);
        for (Node node : nodes) {
            if (node == null) continue;
            nodeSaves.add(node.save());
        }

        relationshipSave.data = nodeSaves;
        relationshipSave.emptyTag = true;

        return relationshipSave;
    }

    public void saveTrack(RelationshipSave save){
        if (tag == null) {
            save.tag = null;
            save.emptyTag = false;
            save.track = "not found";
            return;
        }
        save.track = trackName;
        StackTraceElement[] stackTrace = tag.getStackTrace();
        List<JvmCallLogData> out = new ArrayList<>(stackTrace.length);
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().equals(Relationship.class.getName())
                    && stackTraceElement.getMethodName().equals("setTag")
            ) break;
            out.add(new JvmCallLogData(stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber()));
        }
        save.tag = out;
        save.emptyTag = true;
    }

    public void call(){
        if (nodes == null) return;
        for (int i = 0; i < nodeCall.length; i++) {
            Node node = nodeCall[i];
            if (node == null)continue;
            if (node.thread == null)continue;
            if (node.stop) {
                stop++;
                nodeCall[i] = null;
                continue;
            }
            node.tick();
        }
    }

    @Override
    public boolean getAsBoolean() {
        call();
        return isStop();
    }

    public static class Node {
        public volatile Thread thread;
        public volatile long threadId;
        public String threadNane;
        public volatile boolean stop = false;
        public volatile long startTime = -1;
        public volatile long endTime = -1;
        public volatile long time = 0;
        public final StackTraceNode root = new StackTraceNode();

        public Node(Thread thread, long startTime) {
            this.thread = thread;
            this.threadNane = thread.getName();
            this.startTime = startTime;
        }
        public Node() {
        }

        public void stop(){
            this.stop = true;
            endTime = System.nanoTime();
        }

        public NodeSave save(){
            DataBase save = root.save();
            return new NodeSave(save, threadNane, time, startTime, endTime, threadId);
        }
        
        public void start(Thread thread){
            startTime = System.nanoTime();
            this.threadNane = thread.getName();
            threadId = thread.getId();
            this.thread = thread;
            
        }

        public void tick() {
            if (thread != null) {
                ThreadInfo threadInfo = DebugLog.threadMXBean.getThreadInfo(threadId);
                if (threadInfo != null) {
                    try {
                        StackTraceElement[] stack = threadInfo.getStackTrace();
                        if (stack.length != 0) {
                            time += DebugLog.interval;
                            StackTraceNode node = root;
                            StackTraceElement previousElement = null;

                            for(int offset = 0; offset < stack.length; ++offset) {
                                StackTraceElement element = stack[stack.length - 1 - offset];
                                node = node.resolveChild(new StackTraceNode.Description(element.getClassName(), element.getMethodName(), element.getLineNumber(), previousElement == null ? -1 : previousElement.getLineNumber()));
                                node.time += DebugLog.interval;
                                previousElement = element;
                            }

                        }
                    } catch (Exception var4) {
                        LOGGER.error("", var4);
                    }

                }
            }
        }


    }
}
 