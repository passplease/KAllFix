package n1luik.K_multi_threading.core.util;

public class WyHash64 {
    // 选定的 wyhash 常量 (wyhash final4 版本)
    private static final long WY_CONSTANT = 0x9E3779B97F4A7C15L;
    private static final long WY_CONSTANT2 = 0xBF58476D1CE4E5B9L;
    private static final long WY_CONSTANT3 = 0x94D049BB133111EBL;

    // 主要混合函数
    private static long wyhashMix(long a, long b) {
        long result = (a ^ WY_CONSTANT) * (b ^ WY_CONSTANT2);
        return result ^ (result >>> 47); // 类似 wyhash 的最终雪崩步骤
    }

    public static long hash32(int input) {
        long key = input & 0xFFFFFFFFL; // 将 int 转为无符号长整型
        
        // 对 4 字节(32位)进行混合: 模拟 wyhash 处理小数据的方式
        long hash = wyhashMix(key, WY_CONSTANT3 ^ 4); 
        
        // 最终雪崩 (Avalanche), 确保每一位都充分混合
        hash ^= hash >>> 32;
        hash *= WY_CONSTANT;
        hash ^= hash >>> 29;
        hash *= WY_CONSTANT2;
        hash ^= hash >>> 32;
        return hash;
    }
}