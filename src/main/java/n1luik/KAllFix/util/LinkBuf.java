package n1luik.KAllFix.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class LinkBuf<T> implements Iterable<T>{
    private static final int LINK_LIST_NODE_SIZE = 256;
    private LinkBufNode root;
    private LinkBufNode newBuf;
    private final int newNodeSize;

    public LinkBuf(){
        this(LINK_LIST_NODE_SIZE);
    }
    public LinkBuf(int newNodeSize){
        this.newNodeSize = newNodeSize;
        root = newBuf = new LinkBufNode(null, new LinkBufInfo(), new Object[newNodeSize]);
    }

    public void add(T t){
        if(newBuf.info.size >= newBuf.value.length){
            newBuf = new LinkBufNode(newBuf, new LinkBufInfo(), new Object[newNodeSize]);
        }
        newBuf.value[newBuf.info.size++] = t;
    }

    public int sizeFast(){
        return newBuf.info.size + (newBuf.next == null ? 0 : newBuf.next.info.size);
    }
    public int size(){

        int size = 0;
        LinkBufNode node = newBuf;
        while (node != null){
            size += node.info.size;
            node = node.next;
        }
        return size;
    }
    public void rebuf(){
        if (newBuf.next == null){
            newBuf.info.size = 0;
            return;
        }
        root = newBuf = new LinkBufNode(newBuf, new LinkBufInfo(), new Object[size()]);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<>() {
            LinkBufNode thisNode = newBuf;
            int pos = thisNode.info.size - 1;

            @Override
            public boolean hasNext() {
                return thisNode != null;
            }

            @Override
            public T next() {
                T t = (T) thisNode.value[pos--];
                if(pos < 0){
                    thisNode = thisNode.next;
                    if(thisNode != null)pos = thisNode.info.size - 1;
                }
                return t;
            }
        };
    }

    public static record LinkBufNode(LinkBufNode next, LinkBufInfo info, Object[] value) {}


    public static class LinkBufInfo{
        private int size;
    }
}