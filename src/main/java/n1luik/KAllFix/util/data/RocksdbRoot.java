package n1luik.KAllFix.util.data;

import lombok.Getter;
import org.rocksdb.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RocksdbRoot {
    private static final RocksdbRoot INSTANCE = new RocksdbRoot();
    
    @Getter
    private final Map<String, RocksDB> databases = new ConcurrentHashMap<>();
    
    private RocksdbRoot() {
        // 初始化RocksDB
        RocksDB.loadLibrary();
    }
    
    public static RocksdbRoot getInstance() {
        return INSTANCE;
    }
    
    /**
     * 打开或获取一个RocksDB实例
     * @param dbPath 数据库路径
     * @return RocksDB实例
     * @throws RocksDBException 当打开数据库失败时抛出
     */
    public RocksDB getOrOpenDatabase(String dbPath) throws RocksDBException {
        return databases.computeIfAbsent(dbPath, path -> {
            try {
                Options options = new Options();
                options.setCreateIfMissing(true);
                options.setMaxOpenFiles(100);
                options.setWriteBufferSize(67108864); // 64MB
                options.setMaxWriteBufferNumber(3);
                options.setTargetFileSizeBase(67108864); // 64MB
                
                // 创建目录 - 使用更可靠的方法，先确保父目录存在
                File dir = new File(path);
                if (!dir.exists()) {
                    // 确保父目录存在
                    File parentDir = dir.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        boolean parentCreated = parentDir.mkdirs();
                        if (!parentCreated) {
                            throw new IOException("Failed to create parent directory: " + parentDir.getAbsolutePath());
                        }
                    }
                    // 创建目标目录
                    boolean created = dir.mkdirs();
                    if (!created && !dir.exists()) {
                        throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
                    }
                }
                
                // 验证目录权限
                if (!dir.canRead() || !dir.canWrite()) {
                    throw new IOException("Insufficient permissions for directory: " + dir.getAbsolutePath());
                }
                
                return RocksDB.open(options, path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to prepare directory for RocksDB at " + path, e);
            } catch (RocksDBException e) {
                throw new RuntimeException("Failed to open RocksDB at " + path, e);
            }
        });
    }
    
    /**
     * 压缩算法枚举
     */
    public enum CompressionAlgorithm {
        NONE(CompressionType.NO_COMPRESSION, "none"),
        SNAPPY(CompressionType.SNAPPY_COMPRESSION, "snappy"),
        LZ4(CompressionType.LZ4_COMPRESSION, "lz4"),
        ZSTD(CompressionType.ZSTD_COMPRESSION, "zstd");
        
        private final CompressionType compressionType;
        private final String name;
        
        CompressionAlgorithm(CompressionType compressionType, String name) {
            this.compressionType = compressionType;
            this.name = name;
        }
        
        public CompressionType getCompressionType() {
            return compressionType;
        }
        
        public String getName() {
            return name;
        }
        
        /**
         * 根据名称获取压缩算法枚举
         * @param name 压缩算法名称
         * @return 压缩算法枚举
         * @throws IllegalArgumentException 如果名称无效
         */
        public static CompressionAlgorithm fromName(String name) {
            for (CompressionAlgorithm algorithm : values()) {
                if (algorithm.name.equalsIgnoreCase(name)) {
                    return algorithm;
                }
            }
            throw new IllegalArgumentException("Unsupported compression algorithm: " + name);
        }
    }
    
    /**
     * 快速设置数据库的压缩算法
     * @param dbPath 数据库路径
     * @param compressionType 压缩算法类型
     * @throws RocksDBException 当设置失败时抛出
     */
    public void setCompressionType(String dbPath, CompressionType compressionType) throws RocksDBException {
        Options options = new Options();
        options.setCreateIfMissing(true);
        options.setCompressionType(compressionType);
        
        // 如果数据库已打开，需要先关闭再重新打开
        if (databases.containsKey(dbPath)) {
            closeDatabase(dbPath);
        }
        
        // 重新打开数据库以应用新的压缩算法
        RocksDB db = RocksDB.open(options, dbPath);
        databases.put(dbPath, db);
    }
    
    /**
     * 使用枚举设置数据库的压缩算法
     * @param dbPath 数据库路径
     * @param algorithm 压缩算法枚举
     * @throws RocksDBException 当设置失败时抛出
     */
    public void setCompressionAlgorithm(String dbPath, CompressionAlgorithm algorithm) throws RocksDBException {
        setCompressionType(dbPath, algorithm.getCompressionType());
    }
    
    /**
     * 使用默认选项快速打开数据库
     * @param dbPath 数据库路径
     * @param options 自定义选项
     * @return RocksDB实例
     * @throws RocksDBException 当打开失败时抛出
     */
    public RocksDB openWithOptions(String dbPath, Options options) throws RocksDBException {
        try {
            // 创建目录 - 使用更可靠的方法
            File dir = new File(dbPath);
            if (!dir.exists()) {
                // 确保父目录存在
                File parentDir = dir.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    boolean parentCreated = parentDir.mkdirs();
                    if (!parentCreated) {
                        throw new IOException("Failed to create parent directory: " + parentDir.getAbsolutePath());
                    }
                }
                // 创建目标目录
                boolean created = dir.mkdirs();
                if (!created && !dir.exists()) {
                    throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
                }
            }
            
            // 验证目录权限
            if (!dir.canRead() || !dir.canWrite()) {
                throw new IOException("Insufficient permissions for directory: " + dir.getAbsolutePath());
            }
            
            // 关闭已存在的数据库
            if (databases.containsKey(dbPath)) {
                closeDatabase(dbPath);
            }
            
            RocksDB db = RocksDB.open(options, dbPath);
            databases.put(dbPath, db);
            return db;
        } catch (IOException e) {
            throw new RuntimeException("Failed to prepare directory for RocksDB at " + dbPath, e);
        }
    }
    
    /**
     * 关闭并移除指定的数据库
     * @param dbPath 数据库路径
     */
    public void closeDatabase(String dbPath) {
        RocksDB db = databases.remove(dbPath);
        if (db != null) {
            db.close();
        }
    }
    
    /**
     * 关闭所有打开的数据库
     */
    public void closeAllDatabases() {
        for (RocksDB db : databases.values()) {
            db.close();
        }
        databases.clear();
    }
    
    /**
     * 向指定数据库写入数据
     * @param dbPath 数据库路径
     * @param key 键
     * @param value 值
     * @throws RocksDBException 当写入失败时抛出
     */
    public void put(String dbPath, byte[] key, byte[] value) throws RocksDBException {
        RocksDB db = getOrOpenDatabase(dbPath);
        db.put(key, value);
    }
    /**
     * 向指定数据库写入数据
     * @param dbPath 数据库路径
     * @param key 键
     * @param value 值
     * @throws RocksDBException 当写入失败时抛出
     */
    public void put(String dbPath, String key, byte[] value) throws RocksDBException {
        RocksDB db = getOrOpenDatabase(dbPath);
        db.put(key.getBytes(StandardCharsets.UTF_8), value);
    }

    /**
     * 从指定数据库读取数据
     * @param dbPath 数据库路径
     * @param key 键
     * @return 值，如果不存在则返回null
     * @throws RocksDBException 当读取失败时抛出
     */
    public byte[] get(String dbPath, byte[] key) throws RocksDBException {
        RocksDB db = getOrOpenDatabase(dbPath);
        return db.get(key);
    }
    
    /**
     * 从指定数据库删除数据
     * @param dbPath 数据库路径
     * @param key 键
     * @throws RocksDBException 当删除失败时抛出
     */
    public void delete(String dbPath, byte[] key) throws RocksDBException {
        RocksDB db = getOrOpenDatabase(dbPath);
        db.delete(key);
    }
    
    /**
     * 将指定数据库的内存数据刷新到磁盘
     * @param dbPath 数据库路径
     * @param flushOptions 刷新选项
     * @throws RocksDBException 当刷新失败时抛出
     */
    public void flush(String dbPath, FlushOptions flushOptions) throws RocksDBException {
        RocksDB db = getOrOpenDatabase(dbPath);
        db.flush(flushOptions);
    }
    
    /**
     * 使用默认刷新选项将指定数据库的内存数据刷新到磁盘
     * @param dbPath 数据库路径
     * @throws RocksDBException 当刷新失败时抛出
     */
    public void flush(String dbPath) throws RocksDBException {
        FlushOptions flushOptions = new FlushOptions();
        flushOptions.setWaitForFlush(true);
        flush(dbPath, flushOptions);
    }
    
    /**
     * 获取指定键的输入流
     * @param dbPath 数据库路径
     * @param key 键
     * @return 包含键对应值的输入流，如果键不存在则返回null
     * @throws RocksDBException 当读取失败时抛出
     */
    public InputStream getInputStream(String dbPath, byte[] key) throws RocksDBException {
        byte[] value = get(dbPath, key);
        return value != null ? new ByteArrayInputStream(value) : null;
    }
    
    /**
     * 创建一个输出流，用于写入数据到指定键
     * @param dbPath 数据库路径
     * @param key 键
     * @return 输出流，关闭时会将数据写入数据库
     */
    public OutputStream createOutputStream(String dbPath, byte[] key) {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                try {
                    put(dbPath, key, this.toByteArray());
                } catch (RocksDBException e) {
                    throw new IOException("Failed to write data to RocksDB", e);
                }
            }
        };
    }
    
    /**
     * 使用自定义写选项创建输出流
     * @param dbPath 数据库路径
     * @param key 键
     * @param writeOptions 写选项
     * @return 输出流，关闭时会将数据写入数据库
     */
    public OutputStream createOutputStream(String dbPath, byte[] key, WriteOptions writeOptions) {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                try {
                    RocksDB db = getOrOpenDatabase(dbPath);
                    WriteBatch batch = new WriteBatch();
                    batch.put(key, this.toByteArray());
                    db.write(writeOptions, batch);
                    batch.close();
                } catch (RocksDBException e) {
                    throw new IOException("Failed to write data to RocksDB", e);
                }
            }
        };
    }
    
    /**
     * 为字符串键创建输出流
     * @param dbPath 数据库路径
     * @param key 键（字符串）
     * @return 输出流，关闭时会将数据写入数据库
     */
    public OutputStream createOutputStream(String dbPath, String key) {
        return createOutputStream(dbPath, key.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 使用自定义写选项为字符串键创建输出流
     * @param dbPath 数据库路径
     * @param key 键（字符串）
     * @param writeOptions 写选项
     * @return 输出流，关闭时会将数据写入数据库
     */
    public OutputStream createOutputStream(String dbPath, String key, WriteOptions writeOptions) {
        return createOutputStream(dbPath, key.getBytes(StandardCharsets.UTF_8), writeOptions);
    }
    
    /**
     * 使用迭代器遍历数据库中的所有键值对
     * @param dbPath 数据库路径
     * @param handler 处理每个键值对的回调函数
     * @throws RocksDBException 当遍历失败时抛出
     */
    public void iterate(String dbPath, KeyValueHandler handler) throws RocksDBException {
        RocksDB db = getOrOpenDatabase(dbPath);
        try (RocksIterator iterator = db.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                if (!handler.handle(iterator.key(), iterator.value())) {
                    break;
                }
            }
        }
    }
    
    /**
     * 处理键值对的回调接口
     */
    public interface KeyValueHandler {
        /**
         * 处理键值对
         * @param key 键
         * @param value 值
         * @return 如果继续遍历返回true，否则返回false
         */
        boolean handle(byte[] key, byte[] value);
    }
    
    /**
     * RocksDB处理器类，提供更高级的数据库操作接口
     */
    public final static class RocksDBHandler implements AutoCloseable {
        private final String dbPath;
        private RocksDB db;
        
        /**
         * 创建一个RocksDB处理器
         * @param dbPath 数据库路径
         * @throws RocksDBException 当打开数据库失败时抛出
         */
        public RocksDBHandler(String dbPath) throws RocksDBException {
            this.dbPath = dbPath;
            this.db = RocksdbRoot.getInstance().getOrOpenDatabase(dbPath);
        }
        
        /**
         * 写入键值对
         * @param key 键
         * @param value 值
         * @throws RocksDBException 当写入失败时抛出
         */
        public void put(byte[] key, byte[] value) throws RocksDBException {
            db.put(key, value);
        }

        /**
         * 写入键值对
         * @param key 键
         * @param value 值
         * @throws RocksDBException 当写入失败时抛出
         */
        public void put(String key, byte[] value) throws RocksDBException {
            db.put(key.getBytes(StandardCharsets.UTF_8), value);
        }

        /**
         * 写入键值对
         * @param key 键
         * @param value 值
         * @throws RocksDBException 当写入失败时抛出
         */
        public void put(String key, byte[] value, int start, int len) throws RocksDBException {
            byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
            db.put(bytes, 0, bytes.length, value, start, len);
        }
        
        /**
         * 写入字符串键值对
         * @param key 键（字符串）
         * @param value 值（字符串）
         * @throws RocksDBException 当写入失败时抛出
         */
        public void putString(String key, String value) throws RocksDBException {
            db.put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
        }
        
        /**
         * 读取值
         * @param key 键
         * @return 值，如果不存在则返回null
         * @throws RocksDBException 当读取失败时抛出
         */
        public byte[] get(byte[] key) throws RocksDBException {
            return db.get(key);
        }
        
        /**
         * 读取字符串值
         * @param key 键（字符串）
         * @return 值（字符串），如果不存在则返回null
         * @throws RocksDBException 当读取失败时抛出
         */
        public String getString(String key) throws RocksDBException {
            byte[] value = db.get(key.getBytes(StandardCharsets.UTF_8));
            return value != null ? new String(value, StandardCharsets.UTF_8) : null;
        }
        
        /**
         * 删除键值对
         * @param key 键
         * @throws RocksDBException 当删除失败时抛出
         */
        public void delete(byte[] key) throws RocksDBException {
            db.delete(key);
        }
        
        /**
         * 删除字符串键
         * @param key 键（字符串）
         * @throws RocksDBException 当删除失败时抛出
         */
        public void deleteString(String key) throws RocksDBException {
            db.delete(key.getBytes(StandardCharsets.UTF_8));
        }
        
        /**
         * 使用指定刷新选项将内存数据刷新到磁盘
         * @param flushOptions 刷新选项
         * @throws RocksDBException 当刷新失败时抛出
         */
        public void flush(FlushOptions flushOptions) throws RocksDBException {
            db.flush(flushOptions);
        }
        
        /**
         * 使用默认刷新选项将内存数据刷新到磁盘
         * @throws RocksDBException 当刷新失败时抛出
         */
        public void flush() throws RocksDBException {
            FlushOptions flushOptions = new FlushOptions();
            flushOptions.setWaitForFlush(true);
            flush(flushOptions);
        }
        
        /**
         * 检查键是否存在
         * @param key 键
         * @return 如果键存在则返回true，否则返回false
         * @throws RocksDBException 当检查失败时抛出
         */
        public boolean contains(byte[] key) throws RocksDBException {
            return db.get(key) != null;
        }
        
        /**
         * 检查字符串键是否存在
         * @param key 键（字符串）
         * @return 如果键存在则返回true，否则返回false
         * @throws RocksDBException 当检查失败时抛出
         */
        public boolean containsString(String key) throws RocksDBException {
            return db.get(key.getBytes(StandardCharsets.UTF_8)) != null;
        }
        
        /**
         * 获取输入流
         * @param key 键
         * @return 输入流，如果键不存在则返回null
         * @throws RocksDBException 当读取失败时抛出
         */
        public InputStream getInputStream(byte[] key) throws RocksDBException {
            byte[] value = get(key);
            return value != null ? new ByteArrayInputStream(value) : null;
        }
        
        /**
         * 获取字符串键的输入流
         * @param key 键（字符串）
         * @return 输入流，如果键不存在则返回null
         * @throws RocksDBException 当读取失败时抛出
         */
        public InputStream getInputStream(String key) throws RocksDBException {
            return getInputStream(key.getBytes(StandardCharsets.UTF_8));
        }
        
        /**
         * 创建输出流
         * @param key 键
         * @return 输出流，关闭时会将数据写入数据库
         */
        public OutputStream createOutputStream(byte[] key) {
            return new ByteArrayOutputStream() {
                @Override
                public void close() throws IOException {
                    super.close();
                    try {
                        put(key, this.toByteArray());
                    } catch (RocksDBException e) {
                        throw new IOException("Failed to write data to RocksDB", e);
                    }
                }
            };
        }
        
        /**
         * 为字符串键创建输出流
         * @param key 键（字符串）
         * @return 输出流，关闭时会将数据写入数据库
         */
        public OutputStream createOutputStream(String key) {
            return createOutputStream(key.getBytes(StandardCharsets.UTF_8));
        }
        
        /**
         * 使用自定义写选项创建输出流
         * @param key 键
         * @param writeOptions 写选项
         * @return 输出流，关闭时会将数据写入数据库
         */
        public OutputStream createOutputStream(byte[] key, WriteOptions writeOptions) {
            return new ByteArrayOutputStream() {
                @Override
                public void close() throws IOException {
                    super.close();
                    try {
                        WriteBatch batch = new WriteBatch();
                        batch.put(key, this.toByteArray());
                        db.write(writeOptions, batch);
                        batch.close();
                    } catch (RocksDBException e) {
                        throw new IOException("Failed to write data to RocksDB", e);
                    }
                }
            };
        }
        
        /**
         * 使用自定义写选项为字符串键创建输出流
         * @param key 键（字符串）
         * @param writeOptions 写选项
         * @return 输出流，关闭时会将数据写入数据库
         */
        public OutputStream createOutputStream(String key, WriteOptions writeOptions) {
            return createOutputStream(key.getBytes(StandardCharsets.UTF_8), writeOptions);
        }
        
        /**
         * 遍历数据库中的所有键值对
         * @param handler 处理每个键值对的回调函数
         * @throws RocksDBException 当遍历失败时抛出
         */
        public void iterate(KeyValueHandler handler) throws RocksDBException {
            try (RocksIterator iterator = db.newIterator()) {
                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    if (!handler.handle(iterator.key(), iterator.value())) {
                        break;
                    }
                }
            }
        }
        
        /**
         * 执行批量写操作
         * @param writeBatch 批量写操作
         * @throws RocksDBException 当执行失败时抛出
         */
        public void write(WriteBatch writeBatch) throws RocksDBException {
            db.write(new WriteOptions(), writeBatch);
        }
        
        /**
         * 使用自定义写选项执行批量写操作
         * @param writeOptions 写选项
         * @param writeBatch 批量写操作
         * @throws RocksDBException 当执行失败时抛出
         */
        public void write(WriteOptions writeOptions, WriteBatch writeBatch) throws RocksDBException {
            db.write(writeOptions, writeBatch);
        }
        
        /**
         * 执行事务操作
         * @param action 事务操作回调
         * @throws Exception 当事务执行失败时抛出
         */
        public void transact(TransactionAction action) throws Exception {
            WriteBatch batch = new WriteBatch();
            try {
                action.execute(batch);
                db.write(new WriteOptions(), batch);
            } finally {
                batch.close();
            }
        }
        
        /**
         * 使用自定义写选项执行事务操作
         * @param writeOptions 写选项
         * @param action 事务操作回调
         * @throws Exception 当事务执行失败时抛出
         */
        public void transact(WriteOptions writeOptions, TransactionAction action) throws Exception {
            WriteBatch batch = new WriteBatch();
            try {
                action.execute(batch);
                db.write(writeOptions, batch);
            } finally {
                batch.close();
            }
        }
        
        /**
         * 获取数据库实例
         * @return RocksDB实例
         */
        public RocksDB getDb() {
            return db;
        }
        
        /**
         * 获取数据库路径
         * @return 数据库路径
         */
        public String getDbPath() {
            return dbPath;
        }
        
        /**
         * 快速设置压缩算法
         * @param compressionType 压缩算法类型
         * @throws RocksDBException 当设置失败时抛出
         */
        public void setCompressionType(CompressionType compressionType) throws RocksDBException {
            RocksdbRoot.getInstance().setCompressionType(dbPath, compressionType);
            // 重新获取数据库实例
            this.db = RocksdbRoot.getInstance().getOrOpenDatabase(dbPath);
        }
        
        /**
         * 使用枚举设置压缩算法
         * @param algorithm 压缩算法枚举
         * @throws RocksDBException 当设置失败时抛出
         */
        public void setCompressionAlgorithm(RocksdbRoot.CompressionAlgorithm algorithm) throws RocksDBException {
            RocksdbRoot.getInstance().setCompressionAlgorithm(dbPath, algorithm);
            // 重新获取数据库实例
            this.db = RocksdbRoot.getInstance().getOrOpenDatabase(dbPath);
        }
        
        /**
         * 根据名称设置压缩算法
         * @param algorithmName 压缩算法名称
         * @throws RocksDBException 当设置失败时抛出
         */
        public void setCompressionAlgorithm(String algorithmName) throws RocksDBException {
            setCompressionAlgorithm(RocksdbRoot.CompressionAlgorithm.fromName(algorithmName));
        }
        
        /**
         * 关闭处理器
         */
        @Override
        public void close() {
            // 注意：这里不关闭数据库，因为数据库由RocksdbRoot统一管理
            // 如果需要关闭数据库，请调用RocksdbRoot.getInstance().closeDatabase(dbPath)
            RocksdbRoot.getInstance().closeDatabase(dbPath);
        }
        
        /**
         * 事务操作回调接口
         */
        public interface TransactionAction {
            /**
             * 执行事务操作
             * @param batch 批量写操作
             * @throws Exception 当操作失败时抛出
             */
            void execute(WriteBatch batch) throws Exception;
        }
    }
}
