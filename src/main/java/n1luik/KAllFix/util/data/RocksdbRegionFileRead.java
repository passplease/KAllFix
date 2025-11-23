package n1luik.KAllFix.util.data;


import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import org.rocksdb.RocksDBException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;



public class RocksdbRegionFileRead {
    private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    public static String getChunkKey(int x, int z) {
        return x + "," + z;
    }

    private static int getNumSectors(int p_63641_) {
        return p_63641_ & 255;
    }

    private static int getSectorNumber(int p_63672_) {
        return p_63672_ >> 8 & 16777215;
    }
    private static int getOffsetIndex(int x, int z) {
        return (x & 31) + ((z & 31) * 32);
    }
    private static boolean isExternalStreamChunk(byte p_63639_) {
        return (p_63639_ & 128) != 0;
    }
    private static byte getExternalChunkVersion(byte p_63670_) {
        return (byte)(p_63670_ & -129);
    }
    public static class DataLenOut<T>{
        public T data;
        public int len;
        public DataLenOut() {}
        public DataLenOut(T data, int len) {
            this.data = data;
            this.len = len;
        }
    }

    /**
     * 解压数据接口，用于统一不同的解压方式
     */
    private interface Decompressor {
        /**
         * 读取一定量的数据
         * @param buffer 目标缓冲区
         * @param offset 偏移量
         * @param length 读取长度
         * @return 实际读取的字节数
         * @throws IOException IO异常
         */
        int read(byte[] buffer, int offset, int length) throws IOException;

        /**
         * 读取一定量的数据，默认从偏移量0开始读取全部数据
         * @param buffer 目标缓冲区
         * @return 实际读取的字节数
         * @throws IOException IO异常
         */
        default int read(byte[] buffer) throws IOException {
            return read(buffer, 0, buffer.length);
        }

        /**
         * 释放资源
         */
        void close();
    }

    /**
     * 计数解压器，用于记录读取字数并在读取报错时输出读取数量
     */
    private static class CountingDecompressor implements Decompressor {
        private final Decompressor delegate;
        private long totalReadBytes;
        private final String name;

        /**
         * 构造函数
         * @param delegate 被包装的解压器
         * @param name 解压器名称，用于标识
         */
        public CountingDecompressor(Decompressor delegate, String name) {
            this.delegate = delegate;
            this.name = name != null ? name : "unknown";
            this.totalReadBytes = 0;
        }
        public CountingDecompressor(Decompressor delegate) {
            this(delegate, "unknown");
        }

        /**
         * 获取已读取的总字节数
         * @return 已读取的总字节数
         */
        public long getTotalReadBytes() {
            return totalReadBytes;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            try {
                int bytesRead = delegate.read(buffer, offset, length);
                if (bytesRead > 0) {
                    totalReadBytes += bytesRead;
                }
                return bytesRead;
            } catch (IOException e) {
                System.err.println("解压错误 - " + name + " 已读取字节数: " + totalReadBytes);
                throw new IOException("解压错误 - " + name + " 已读取字节数: " + totalReadBytes, e);
            }
        }

        @Override
        public void close() {
            try {
                delegate.close();
            } catch (Throwable e) {
                System.err.println("关闭解压器错误 - " + name + " 已读取字节数: " + totalReadBytes);
                e.printStackTrace();
            }
        }
    }

    /**
     * Inflater实现的解压方式
     */
    private static class InflaterDecompressor implements Decompressor {
        private final Inflater inflater;

        public InflaterDecompressor(byte[] input, int offset, int length) {
            this.inflater = new Inflater();
            this.inflater.setInput(input, offset, length);
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            try {
                return inflater.inflate(buffer, offset, length);
            } catch (DataFormatException e) {
                throw new IOException("解压失败", e);
            }
        }

        @Override
        public void close() {
            try {
                inflater.end();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Inflater实现的解压方式
     */
    private static class NbtDecompressor implements Decompressor {
        public Decompressor decompressor;
        protected short[] typeBuf = new short[1024];
        protected byte[] getBuf = new byte[1024];
        protected int[] sizeBuf = new int[128];
        protected int sizeBufSize = 0;
        protected int typeBufSize = 0;
        //无条件读取字节数
        protected int remaining = 0;
        //读取数字到一半
        protected int readDataRemaining = 0;
        protected int readDataRemainingType = 0;
        //读取状态
        protected int readState = 0;
        protected boolean end = false;
        protected boolean complete = false;

        private NbtDecompressor(Decompressor decompressor) {
            this.decompressor = decompressor;
        }
        protected void addSize(int size) {
            if (sizeBufSize >= sizeBuf.length) {
                int[] newSizeBuf = new int[sizeBuf.length + 128];
                System.arraycopy(sizeBuf, 0, newSizeBuf, 0, sizeBufSize);
                sizeBuf = newSizeBuf;
            }
            sizeBuf[sizeBufSize++] = size;
        }
        protected boolean subSize() {
            if (sizeBuf[sizeBufSize-1]-- < 0){
                sizeBufSize--;
                return false;
            }
            return true;
        }
        protected int removeSize() {
            return sizeBuf[--sizeBufSize];
        }
        protected void addType(short type) {
            if (typeBufSize >= typeBuf.length) {
                short[] newTypeBuf = new short[typeBuf.length + 1024];
                System.arraycopy(typeBuf, 0, newTypeBuf, 0, typeBufSize);
                typeBuf = newTypeBuf;
            }
            typeBuf[typeBufSize++] = type;
        }
        protected short removeType() {
            return typeBuf[--typeBufSize];
        }
        protected short getType() {
            return typeBuf[typeBufSize-1];
        }
        protected static int typeSize(int type) {
            return switch (type){
                case 1->1;case 2->2;case 3, 5->4;case 4, 6->4;default->throw new IllegalArgumentException("未知类型" + type);
            };
        }

        public int readType(byte[] buffer, int offset, int length) throws IOException {
            int read = 0;
            int size = 0;
            return switch (readState) {
                case 0 -> {
                    size = decompressor.read(getBuf, 0, 1);
                    if (size == -1 || size == 0) {
                        yield  size;
                    }
                    read += size;
                    byte b0 = getBuf[0];
                    if (b0 == 0) {
                        buffer[0] = 0;
                        end = true;
                    }
                    addType((short) (b0&0xfF));
                    addType((short) -2);
                    readState = 1;
                    yield 1;
                }
                case 1 -> {
                    addType((short) -1);
                    readState = 2;
                    yield 1;
                }
                case 2 -> {
                    short i = removeType();
                    yield switch (i) {
                        case -2 -> {
                            end = true;
                            yield 0;
                        }
                        case -1, 8 -> {
                            if (readDataRemainingType == 0) {
                                //如果剩余读取是0就直接退出
                                if (length < 1) {
                                    addType(i);
                                    yield 0;
                                }
                                //检测剩余内存够不够2字节，不够就需要缓存这一字节
                                size = decompressor.read(getBuf, 0, 2);
                                if (size != 2) {
                                    if (size > 0) buffer[offset] = getBuf[0];
                                    //if (size > 1) buffer[offset + 1] = getBuf[1];
                                    end = true;
                                    yield size;
                                }
                                buffer[offset] = getBuf[0];
                                if (length < 2) {
                                    readDataRemainingType = 1;
                                    addType(i);
                                    yield 1;
                                }
                            }else {
                                size = 1;
                                offset++;
                                length--;
                            }
                            buffer[offset + 1] = getBuf[1];
                            //readUnsignedShort
                            int len = remaining = (getBuf[0] << 8) + (getBuf[1]);
                            read = Math.min(len, length - 2);
                            len = decompressor.read(buffer, offset + 2, read);
                            if (len != read) {
                                end = true;
                                yield size;
                            }
                            remaining -= len;
                            readDataRemainingType = 0;
                            yield len + size;
                        }
                        case 0 -> throw new IOException("TAG_End found without a TAG_Compound/TAG_List start");
                        case 1 -> {
                            //确认有剩余空间
                            if (length < 1) {
                                addType(i);
                                yield 0;
                            }
                            size = decompressor.read(buffer, offset, 1);
                            if (size != 1) {
                                end = true;
                                yield size;
                            }
                            yield size;
                        }
                        case 2 -> {// short
                            //确认有剩余空间
                            if (length < 1) {
                                addType(i);
                                yield 0;
                            }
                            if (length == 1){
                                remaining =  2 - length;
                                size = decompressor.read(buffer, offset, 1);
                                if (size != length) {
                                    end = true;
                                    yield size;
                                }
                                yield size;
                            }
                            size = decompressor.read(buffer, offset, 2);
                            if (size != 2) {
                                end = true;
                                yield size;
                            }
                            yield size;
                        }
                        case 3, 5 -> {// int, float
                            //确认有剩余空间
                            if (length < 1) {
                                addType(i);
                                yield 0;
                            }
                            yield switch (length){
                                case 1, 2, 3 -> {
                                    remaining =  4 - length;
                                    size = decompressor.read(buffer, offset, length);
                                    if (size != length) {
                                        end = true;
                                        yield size;
                                    }
                                    yield size;
                                }
                                default -> {
                                    size = decompressor.read(buffer, offset, 4);
                                    if (size != 4) {
                                        end = true;
                                        yield size;
                                    }
                                    yield size;
                                }
                            };
                        }
                        case 4, 6 -> {// long, double
                            //确认有剩余空间
                            if (length < 1) {
                                addType(i);
                                yield 0;
                            }
                            yield switch (length){
                                case 1, 7, 6, 5, 4, 3, 2 -> {
                                    remaining =  8 - length;
                                    size = decompressor.read(buffer, offset, length);
                                    if (size != length) {
                                        end = true;
                                        yield size;
                                    }
                                    yield size;
                                }
                                default -> {
                                    size = decompressor.read(buffer, offset, 8);
                                    if (size != 8) {
                                        end = true;
                                        yield size;
                                    }
                                    yield size;
                                }
                            };
                        }// ByteArrayTag
                        case 7, 11, 12 -> {
                            //确认有剩余空间
                            if (length < 1) {
                                addType(i);
                                yield 0;
                            }
                            if (readDataRemainingType == 0) {
                                //检测剩余内存够不够2字节，不够就需要缓存这一字节
                                size = decompressor.read(getBuf, 0, 4);
                                if (size < 1) {
                                    end = true;
                                    yield size;
                                }
                                if (size < 4) {
                                    if (size > 0) buffer[offset] = getBuf[0];
                                    if (size > 1 && length > 1) buffer[offset + 1] = getBuf[1];
                                    if (size > 2 && length > 2) buffer[offset + 2] = getBuf[2];
                                    addType((short) -2);
                                    yield size;
                                }
                                buffer[offset] = getBuf[0];
                                readDataRemainingType = length;
                                if (length < 4) {
                                    buffer[offset] = getBuf[0];
                                    if (length > 1) buffer[offset + 1] = getBuf[1];
                                    if (length > 2) buffer[offset + 2] = getBuf[2];
                                    addType(i);
                                    yield 1;
                                }
                            }
                            size = 0;
                            if (readDataRemainingType > 0) {
                                buffer[offset] = getBuf[0];
                                offset++;
                                length--;
                                size++;
                            }
                            if (readDataRemainingType > 1 && length > 0) {
                                buffer[offset] = getBuf[1];
                                offset++;
                                length--;
                                size++;
                            }
                            if (readDataRemainingType > 2 && length > 0) {
                                buffer[offset] = getBuf[2];
                                offset++;
                                length--;
                                size++;
                            }
                            if (readDataRemainingType > 3 && length > 0) {
                                buffer[offset] = getBuf[3];
                                offset++;
                                length--;
                                size++;
                            }
                            readDataRemainingType += length;
                            if (readDataRemainingType < 4) {
                                addType(i);
                                yield size;
                            }
                            readDataRemainingType = 0;
                            read = remaining = ((getBuf[0] << 24) +
                                    (getBuf[1] << 16) +
                                    (getBuf[2] << 8) +
                                    (getBuf[3])) * switch (i) {
                                        case 7 -> 1;
                                        case 11 -> 4;
                                        case 12 -> 8;
                                        default -> throw new IllegalStateException("Unexpected value: " + i);
                                    };
                            read = decompressor.read(buffer, offset + 2, Math.min(read, length - 2));
                            if (read < 1) {
                                end = true;
                                yield size;
                            }
                            remaining -= read;
                            yield size + read;
                        }// ListTag
                        case 9 -> {
                            //确认有剩余空间
                            if (length < 1) {
                                addType(i);
                                yield 0;
                            }
                            size = 0;
                            if (readDataRemainingType != -2) {
                                read = decompressor.read(getBuf, 0, 1);
                                if (read < 1) {
                                    end = true;
                                    yield 0;
                                }
                                buffer[offset] = getBuf[0];
                                if (length < 2) {
                                    addType(i);
                                    readDataRemainingType = -2;
                                    yield 1;
                                }
                                size++;
                                offset++;
                                length--;

                                getBuf[9] = getBuf[0];
                            }
                            yield switch (getBuf[9]){
                                case 1, 2, 3, 4, 5, 6 -> {

                                    if (readDataRemainingType == 0) {
                                        //检测剩余内存够不够2字节，不够就需要缓存这一字节
                                        read = decompressor.read(getBuf, 0, 4);
                                        if (read < 1) {
                                            end = true;
                                            yield read;
                                        }
                                        if (read < 4) {
                                            if (read > 0) buffer[offset] = getBuf[0];
                                            if (read > 1 && length > 1) buffer[offset + 1] = getBuf[1];
                                            if (read > 2 && length > 2) buffer[offset + 2] = getBuf[2];
                                            addType((short) -2);
                                            yield read + size;
                                        }
                                        buffer[offset] = getBuf[0];
                                        readDataRemainingType = length;
                                        if (length < 4) {
                                            buffer[offset] = getBuf[0];
                                            if (length > 1) buffer[offset + 1] = getBuf[1];
                                            if (length > 2) buffer[offset + 2] = getBuf[2];
                                            addType(i);
                                            yield 1;
                                        }
                                    }
                                    if (readDataRemainingType > 0) {
                                        buffer[offset] = getBuf[0];
                                        offset++;
                                        length--;
                                        size++;
                                    }
                                    if (readDataRemainingType > 1) {
                                        buffer[offset] = getBuf[1];
                                        offset++;
                                        length--;
                                        size++;
                                    }
                                    if (readDataRemainingType > 2 && length > 0) {
                                        buffer[offset] = getBuf[2];
                                        offset++;
                                        length--;
                                        size++;
                                    }
                                    if (readDataRemainingType > 3 && length > 0) {
                                        buffer[offset] = getBuf[3];
                                        offset++;
                                        length--;
                                        size++;
                                    }
                                    readDataRemainingType += length;
                                    if (readDataRemainingType < 4) {
                                        addType(i);
                                        yield size;
                                    }
                                    readDataRemainingType = 0;
                                    read = remaining = ((getBuf[0] << 24) +
                                            (getBuf[1] << 16) +
                                            (getBuf[2] << 8) +
                                            (getBuf[3])) * typeSize(getBuf[9]);
                                    read = decompressor.read(buffer, offset + 2, Math.min(read, length - 2));
                                    if (read < 1) {
                                        end = true;
                                        yield size;
                                    }
                                    remaining -= read;
                                    yield size + read;
                                }
                                default -> {
                                    if (readDataRemainingType == 0) {
                                        //检测剩余内存够不够2字节，不够就需要缓存这一字节
                                        read = decompressor.read(getBuf, 0, 4);
                                        if (read < 1) {
                                            end = true;
                                            yield read;
                                        }
                                        if (read < 4) {
                                            if (read > 0) buffer[offset] = getBuf[0];
                                            if (read > 1 && length > 1) buffer[offset + 1] = getBuf[1];
                                            if (read > 2 && length > 2) buffer[offset + 2] = getBuf[2];
                                            addType((short) -2);
                                            yield read + size;
                                        }
                                        buffer[offset] = getBuf[0];
                                        readDataRemainingType = length;
                                        if (length < 4) {
                                            buffer[offset] = getBuf[0];
                                            if (length > 1) buffer[offset + 1] = getBuf[1];
                                            if (length > 2) buffer[offset + 2] = getBuf[2];
                                            addType(i);
                                            yield 1;
                                        }
                                    }
                                    if (readDataRemainingType > 0) {
                                        buffer[offset] = getBuf[0];
                                        offset++;
                                        length--;
                                        size++;
                                    }
                                    if (readDataRemainingType > 1) {
                                        buffer[offset] = getBuf[1];
                                        offset++;
                                        length--;
                                        size++;
                                    }
                                    if (readDataRemainingType > 2 && length > 0) {
                                        buffer[offset] = getBuf[2];
                                        offset++;
                                        length--;
                                        size++;
                                    }
                                    if (readDataRemainingType > 3 && length > 0) {
                                        buffer[offset] = getBuf[3];
                                        offset++;
                                        length--;
                                        size++;
                                    }
                                    readDataRemainingType += length;
                                    if (readDataRemainingType < 4) {
                                        addType(i);
                                        yield size;
                                    }
                                    readDataRemainingType = 0;
                                    read = ((getBuf[0] << 24) +
                                            (getBuf[1] << 16) +
                                            (getBuf[2] << 8) +
                                            (getBuf[3]));
                                    if (read > 0) {
                                        addType((short) (getBuf[9] & 0xFF));
                                        addType((short) -3);
                                        addSize(read);
                                    }
                                    yield size;
                                }
                            };
                        }// ListTag
                        case -3 -> {
                            if (subSize()) {
                                short type = getType();
                                addType((short) -3);
                                addType(type);
                            }else{
                                removeType();
                            }
                            yield 0;
                        }// CompoundTag
                        case 10 -> {
                            if (length <= 0) {
                                addType(i);
                                yield 0;
                            }
                            read = decompressor.read(getBuf, 0, 1);
                            if (read < 1) {
                                end = true;
                                yield 0;
                            }
                            buffer[offset] = getBuf[0];
                            if (getBuf[0] != 0){
                                addType((short) (getBuf[0]&0xFF));
                                addType((short) -1);
                            }
                            yield 1;
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + i);
                    };
                }
                default -> throw new IllegalStateException("Unexpected value: " + readState);
            };
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            int read = 0;
            if (remaining > 0) {
                if (remaining >= length) {
                    remaining -= length;
                    return decompressor.read(buffer, offset, length);
                }
                read = decompressor.read(buffer, offset, remaining);
                if (read == -1 || read == 0) {
                    return read;
                }
                offset += read;
                length -= read;
                remaining -= read;
            }
            if (end) {
                return -1;
            }

            while (read < length && !end) {
                int size = readType(buffer, offset, length);
                if (size < 1) {
                    return read;
                }
                read += size;
                offset += size;
                length -= size;
            }

            return end ? (read == 0 ? -1 : read) : read;
        }
        public void init(){
            typeBufSize = 0;
            remaining = 0;
            readDataRemaining = 0;
            readDataRemainingType = 0;
            readState = 0;
            end = false;
            complete = false;
            sizeBufSize = 0;
        }

        @Override
        public void close() {
            decompressor.close();
        }
    }

    /**
     * GZIP实现的解压方式
     */
    private static class GzipDecompressor implements Decompressor {
        private final GZIPInputStream gzip;

        public GzipDecompressor(byte[] input, int offset, int length) throws IOException {
            this.gzip = new GZIPInputStream(new ByteArrayInputStream(input, offset, length));
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            return gzip.read(buffer, offset, length);
        }

        @Override
        public void close() {
            try {
                gzip.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通用解压数据函数
     * @param decompressor 解压器实例
     * @param outArray_ 输出数组
     * @param buf3 小缓冲区
     * @param chunks 数据块列表
     * @return 解压后的数据
     * @throws IOException 解压过程中的IO异常
     */
    private static void decompressData(Decompressor decompressor, DataLenOut<byte[]> outArray_, byte[] buf3,
                                       List<byte[]> chunks) throws IOException {
        try {
            byte[] outArray = outArray_.data;
            // 初始读取
            int read = decompressor.read(outArray, 0, outArray.length - 2);
            if (read == 0) {
                outArray_.len = 0; // 空数据
                return;
            }
            if (read < outArray.length - 2) {
                // 数据量小，直接返回
                outArray_.len = read;
                return;
            }

            // 读取额外的字节判断是否还有更多数据
            int totalLen = decompressor.read(buf3);
            outArray[read] = buf3[0];
            outArray[read + 1] = buf3[1];

            if (totalLen < 3) {
                // 数据量不大，直接返回
                int resultLength = read + totalLen;
                outArray_.len = resultLength;
                return;
            } else {
                // 处理更多数据
                buf3[0] = buf3[2];
                chunks.clear();
                chunks.add(outArray);

                // 继续读取
                totalLen = decompressor.read(buf3, 1, 2);
                totalLen += read + 2;

                if (totalLen > 2) {
                    // 持续读取剩余数据
                    int bufSize = outArray.length + 5000; // 添加额外空间避免频繁扩容

                    while (true) {
                        byte[] chunk = new byte[bufSize];
                        int bytesRead = decompressor.read(chunk);

                        if (bytesRead <= 0) {
                            break;
                        }

                        // 添加数据块
                        chunks.add(chunk);
                        totalLen += bytesRead;

                        // 检查是否还有更多数据
                        if (bytesRead < chunk.length) {
                            break;
                        }
                    }
                }

                // 合并所有数据块
                byte[] result = new byte[totalLen];
                int offset = 0;
                for (byte[] chunk : chunks) {
                    if (offset >= totalLen) {
                        break; // 防止数组越界
                    }
                    int copyLength = Math.min(chunk.length, totalLen - offset);
                    System.arraycopy(chunk, 0, result, offset, copyLength);
                    offset += copyLength;
                }

                outArray_.data = result;
                outArray_.len = totalLen;
            }
        } finally {
            // 确保资源被释放
            decompressor.close();
        }
    }

    /**
     * 从mcc文件读取数据并加载到ByteBuffer中
     * @param path 区域文件路径
     * @param px 区块X坐标
     * @param pz 区块Z坐标
     * @param b3 目标ByteBuffer
     * @param outArray 临时输出数组
     * @return 文件大小，如果失败返回-1
     */
    private static int loadFromMccFile(Path path, int px, int pz, ByteBuffer b3, DataLenOut<byte[]> outArray) {
        Path mccPath = path.resolve("c." + px + "." + pz + ".mcc");
        if (!Files.isRegularFile(mccPath)) {
            return -1;
        }

        try (InputStream inputStream = Files.newInputStream(mccPath)) {
            int size1 = Math.toIntExact(Files.size(mccPath));
            if (size1 > outArray.data.length) {
                outArray.data = new byte[size1];
            }
            if (size1 > b3.capacity()) {
                // 扩大缓存
                b3.clear();
                b3 = ByteBuffer.allocateDirect(size1);
            }
            b3.limit(size1);
            b3.position(0);
            size1 = inputStream.read(outArray.data, 0, size1);
            b3.put(outArray.data, 0, size1);
            b3.position(0);
            return size1;
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.printf("[%s]读取外部文件失败 %n", getChunkKey(px, pz));
            return -1;
        }
    }
    public static void main(String[] args) {
        var buffer = ByteBuffer.allocateDirect(8192);
        var b3 = ByteBuffer.allocateDirect(1024*1024);
        var buffer5 = ByteBuffer.allocateDirect(5);
        byte[] outArray = new byte[1024*1024];
        byte[] buf3 = new byte[3];
        byte[] tempArray = new byte[1024*1024];
        List<byte[]> chunks = new ArrayList<>();
        DataLenOut<byte[]> dlo = new DataLenOut<byte[]>(outArray, 0);
        NbtDecompressor nbtDecompressor = new NbtDecompressor(null);

        IntBuffer intBuffer = buffer.asIntBuffer();
        intBuffer.limit(1024);
        for (String arg : args) {
            File file = new File(arg);
            if (file.isDirectory()){
                File file1 = new File(file, "db.rocksdb");
                try {
                    RocksdbRoot.RocksDBHandler rocksDBHandler = new RocksdbRoot.RocksDBHandler(file1.getPath());
                    rocksDBHandler.setCompressionAlgorithm(RocksdbRoot.CompressionAlgorithm.ZSTD);
                    Path path1 = file.toPath();

                    File[] files = file.listFiles();
                    if (files == null) {
                        continue;
                    }
                    for (File listFile : files) {
                        if (listFile.isDirectory()){
                            System.out.printf("[%s]不是文件 %n", listFile.getPath());
                            continue;
                        }

                        Matcher matcher = REGEX.matcher(listFile.getName());
                        if (!matcher.matches()) {
                            System.out.printf("[%s]不规范 %n", listFile.getPath());
                            continue;
                        }
                        int fx = Integer.parseInt(matcher.group(1)) << 5;
                        int fz = Integer.parseInt(matcher.group(2)) << 5;
                        String name = listFile.getName();
                        if (!name.endsWith(".mca")){
                            System.out.printf("[%s]不是mca文件 %n", listFile.getPath());
                        }
                        Path path = listFile.toPath();
                        try (var file2 = FileChannel.open(path, StandardOpenOption.READ)) {
                            //从0读取1024个int
                            intBuffer.position(0);
                            buffer.position(0);
                            int i = file2.read(buffer, 0);
                            if (i < 8192) {
                                System.out.printf("Region file %s has truncated header: %s %n", path, i);
                                file2.close();
                                continue;
                            }
                            long j = Files.size(path);
                            buffer.position(0);
                            intBuffer = buffer.asIntBuffer();
                            intBuffer.limit(1024);
                            intBuffer.position(0);


                            for(int k = 0; k < 1024; ++k) {
                                int l = intBuffer.get(k);
                                if (l == 0) continue;
                                int sn = getSectorNumber(l);
                                int ns = getNumSectors(l);
                                int cx = (k & 31);
                                int cz = k / 32;
                                int px = cx + fx;
                                int pz = cz + fz;
                                if (sn < 2) {
                                    //System.out.printf("Region file %s has invalid sector at index: %s; sector %s overlaps with header [%s %s]%n", path, k, ns, px, pz);
                                    continue;
                                } else if (ns == 0) {
                                    System.out.printf("Region file %s has an invalid sector at index: %s; size has to be > 0 [%s %s]%n", path, k, px, pz);
                                    continue;
                                } else if ((long)sn * 4096L > j) {
                                    System.out.printf("Region file %s has an invalid sector at index: %s; sector %s is out of bounds [%s %s]%n", path, k, sn, px, pz);
                                    continue;
                                }

                                //从x*4096读取5个字节
                                int size = ns * 4096;
                                //从x*4096读取size个字节到另缓存3
                                if (size > b3.capacity()) {
                                    //扩大缓存3
                                    b3.clear();
                                    b3 = ByteBuffer.allocateDirect(size);
                                }
                                //设置大小
                                b3.limit(size);
                                b3.position(0);
                                int size1 = file2.read(b3, sn * 4096L);
                                if (size1 < 6) {
                                    continue;
                                }
                                if (size1 < 7) {
                                    rocksDBHandler.put(getChunkKey(px, pz), b3.array(), 0, 0);
                                    continue;
                                }
                                b3.position(0);
                                int d1 = b3.getInt();
                                byte b0 = b3.get();
                                if (d1 == 0) continue;
                                size1 -= 5;//减去前5个字节
                                int id;
                                if (isExternalStreamChunk(b0)) {
                                    id = getExternalChunkVersion(b0);
                                    // 调用独立函数从mcc文件加载数据
                                    dlo.data = outArray;
                                    dlo.len = outArray.length;
                                    int mccSize = loadFromMccFile(path1, px, pz, b3, dlo);
                                    if (mccSize == -1) {
                                        continue;
                                    }
                                    outArray = dlo.data;
                                    size1 = mccSize;
                                    //if (!Files.isRegularFile(this.getExternalChunkPath(p_63674_))) {
                                    //    return false;
                                    //}
                                }else {
                                    id = b0;
                                }
                                // 有效的ID是1-4
                                if (id < 1 || id > 4) {
                                    continue;
                                }
                                int i9 = size1 - 1;
                                if (i9 < 0 || i9 > 4096 * ns || d1 < 1) {
                                    continue;
                                }
                                dlo.data = outArray;
                                dlo.len = size1;
                                switch (id) {
                                    case 3 -> {
                                        // 直接缓冲区不支持array()方法，需要复制数据到临时数组
                                        if (size1 > tempArray.length) {
                                            tempArray = new byte[size1];
                                        }
                                        b3.get(tempArray, 0, size1);
                                        rocksDBHandler.put(getChunkKey(px, pz), tempArray, 0, size1);
                                    }
                                    case 2 -> {
                                        try {
                                            // 使用InflaterDecompressor进行解压
                                            // 直接缓冲区不支持array()方法，需要复制数据到临时数组
                                            if (size1 > tempArray.length) {
                                                tempArray = new byte[size1];
                                            }
                                            b3.get(tempArray, 0, size1);
                                            Decompressor decompressor = new InflaterDecompressor(tempArray, 0, size1);
                                            nbtDecompressor.decompressor = decompressor;
                                            nbtDecompressor.init();
                                            decompressData(decompressor, dlo, buf3, chunks);
                                            outArray = dlo.data;
                                            rocksDBHandler.put(getChunkKey(px, pz), outArray, 0, dlo.len);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            continue;
                                        }
                                    }
                                    case 1 -> {
                                        try {
                                            // 使用GzipDecompressor进行解压
                                            // 直接缓冲区不支持array()方法，需要复制数据到临时数组
                                            if (size1 > tempArray.length) {
                                                tempArray = new byte[size1];
                                            }
                                            b3.get(tempArray, 0, size1);
                                            Decompressor decompressor = new CountingDecompressor(new GzipDecompressor(tempArray, 0, size1));
                                            nbtDecompressor.decompressor = decompressor;
                                            nbtDecompressor.init();
                                            decompressData(decompressor, dlo, buf3, chunks);
                                            outArray = dlo.data;
                                            rocksDBHandler.put(getChunkKey(px, pz), outArray, 0, dlo.len);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            continue;
                                        }
                                    }
                                }

                                //else {
                                //    this.usedSectors.force(i1, j1);
                                //}
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.printf("[%s]读取失败 %n", listFile.getPath());
                        }
                    }
                    rocksDBHandler.flush();
                    rocksDBHandler.close();
                } catch (RocksDBException e) {
                    e.printStackTrace();
                    System.out.printf("[%s]打开失败 %n", file1.getPath());
                    continue;
                }
            }else {
                System.out.printf("[%s]不是目录 %n", arg);
            }
        }
    }
}
