package asm.n1luik.KAllFix.asm.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.constant.DynamicCallSiteDesc;

public class StackUtil {


    public static int descInSize(String desc) {
        String s = desc.split("\\)")[0];
        int pos = 1;
        int size = 0;
        int length = s.length();
        while (length > pos){
            switch (s.charAt(pos)) {
                case '[':
                    pos++;
                    break;
                case 'L':
                    size++;
                    pos += s.indexOf(';', pos) - pos + 1;
                    break;
                default:
                    pos++;
                    size++;
                    break;
            }
        }
        return size;
    }

    public static AsmInOutSizeData asmInOutSize(AbstractInsnNode abstractInsnNode) {
        return switch (abstractInsnNode.getOpcode()) {
            case Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESPECIAL, Opcodes.INVOKEINTERFACE -> {
                if (abstractInsnNode instanceof MethodInsnNode methodInsnNode1) {
                    yield new AsmInOutSizeData(descInSize(methodInsnNode1.desc)+1, methodInsnNode1.desc.endsWith("V") ? 0 : 1);
                }else {
                    throw  new RuntimeException("not MethodInsnNode");
                }
            }
            case Opcodes.INVOKESTATIC -> {
                if (abstractInsnNode instanceof MethodInsnNode methodInsnNode1) {
                    yield new AsmInOutSizeData(descInSize(methodInsnNode1.desc), methodInsnNode1.desc.endsWith("V") ? 0 : 1);
                }else {
                    throw  new RuntimeException("not MethodInsnNode");
                }
            }
            case Opcodes.INVOKEDYNAMIC -> {
                if (abstractInsnNode instanceof InvokeDynamicInsnNode invokeDynamicInsnNode) {
                    yield new AsmInOutSizeData(descInSize(invokeDynamicInsnNode.desc), invokeDynamicInsnNode.desc.endsWith("V") ? 0 : 1);
                }else {
                    throw  new RuntimeException("not MethodInsnNode");
                }
            }
            case Opcodes.MULTIANEWARRAY -> {
                if (abstractInsnNode instanceof MultiANewArrayInsnNode multiANewArrayInsnNode) {
                    yield new AsmInOutSizeData(multiANewArrayInsnNode.dims, 1);
                }else {
                    throw  new RuntimeException("not MethodInsnNode");
                }
            }
            default->STATIC_ASM_IN_OUT_SIZE[abstractInsnNode.getOpcode()];
        };
    }

    public static AbstractInsnNode findLatelyLocalWrite(int id, AbstractInsnNode abstractInsnNode) {
        while (abstractInsnNode != null) {
            if (
                      abstractInsnNode.getOpcode() == Opcodes.ISTORE
                    || abstractInsnNode.getOpcode() == Opcodes.LSTORE
                    || abstractInsnNode.getOpcode() == Opcodes.FSTORE
                    || abstractInsnNode.getOpcode() == Opcodes.DSTORE
                    || abstractInsnNode.getOpcode() == Opcodes.ASTORE) {
                if (abstractInsnNode instanceof VarInsnNode varInsnNode && varInsnNode.var == id) {
                    return abstractInsnNode.getPrevious();
                }
            }
            abstractInsnNode = abstractInsnNode.getPrevious();
        }
        throw new RuntimeException("没有找到这个变量");
    }
    public static AbstractInsnNode previousStack(int size, AbstractInsnNode abstractInsnNode, boolean skipDUP) {
        while (size > 0) {
            abstractInsnNode = abstractInsnNode.getPrevious();
            if (abstractInsnNode == null) return null;
            if (abstractInsnNode.getOpcode() == -1) continue;
            AsmInOutSizeData asmInOutSizeData = asmInOutSize(abstractInsnNode);
            if (asmInOutSizeData == null) throw new RuntimeException("当前工具无法处理的指令："+abstractInsnNode.getOpcode());
            int size1 = size;
            size -= asmInOutSizeData.out - asmInOutSizeData.in;
            if (skipDUP &&
                    ( abstractInsnNode.getOpcode() == Opcodes.DUP
                    || abstractInsnNode.getOpcode() == Opcodes.DUP_X1
                    || abstractInsnNode.getOpcode() == Opcodes.DUP_X2
                    || abstractInsnNode.getOpcode() == Opcodes.DUP2
                    || abstractInsnNode.getOpcode() == Opcodes.DUP2_X1
                    || abstractInsnNode.getOpcode() == Opcodes.DUP2_X2
                    )) {
                size += STATIC_ASM_IN_OUT_SIZE[abstractInsnNode.getOpcode()].in();
            }
            if (size1 - asmInOutSizeData.out < 1)break;
        }
        return abstractInsnNode;
    }
    /**
     * 获取是哪一个指令输入函数的这个指定参数
     * */
    public static AbstractInsnNode findMethodInAsm(int arg, MethodInsnNode methodInsnNode, boolean skipDUP) {
        int i = descInSize(methodInsnNode.desc);
        assert i >= arg;
        if (i == 0) {
            return methodInsnNode.getPrevious();
        }
        return previousStack(arg == -1 ? i : (i-arg), methodInsnNode, skipDUP);
    }
    public static record AsmInOutSizeData(int in, int out) {}

    //汇编码输入输出栈不是动态的的大小数据

    private static final AsmInOutSizeData asmInOutSizeData_i0_o0 = new AsmInOutSizeData(0, 0);
    private static final AsmInOutSizeData asmInOutSizeData_i0_o1 = new AsmInOutSizeData(0, 1);
    private static final AsmInOutSizeData asmInOutSizeData_i2_o1 = new AsmInOutSizeData(2, 1);
    private static final AsmInOutSizeData asmInOutSizeData_i1_o1 = new AsmInOutSizeData(1, 1);
    private static final AsmInOutSizeData asmInOutSizeData_i1_o0 = new AsmInOutSizeData(1, 0);
    private static final AsmInOutSizeData asmInOutSizeData_i2_o0 = new AsmInOutSizeData(2, 0);
    private static final AsmInOutSizeData asmInOutSizeData_i3_o0 = new AsmInOutSizeData(3, 0);
    private static final AsmInOutSizeData[] STATIC_ASM_IN_OUT_SIZE = new AsmInOutSizeData[256];
    static {
        STATIC_ASM_IN_OUT_SIZE[Opcodes.NOP] = asmInOutSizeData_i0_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ACONST_NULL] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ICONST_M1] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ICONST_0] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ICONST_1] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ICONST_2] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ICONST_3] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ICONST_4] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ICONST_5] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LCONST_0] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LCONST_1] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FCONST_0] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FCONST_1] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FCONST_2] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DCONST_0] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DCONST_1] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.BIPUSH] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.SIPUSH] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LDC] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ILOAD] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LLOAD] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FLOAD] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DLOAD] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ALOAD] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IALOAD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LALOAD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FALOAD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DALOAD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.AALOAD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.BALOAD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.CALOAD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.SALOAD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ISTORE] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LSTORE] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FSTORE] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DSTORE] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ASTORE] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IASTORE] = asmInOutSizeData_i3_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LASTORE] = asmInOutSizeData_i3_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FASTORE] = asmInOutSizeData_i3_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DASTORE] = asmInOutSizeData_i3_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.AASTORE] = asmInOutSizeData_i3_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.BASTORE] = asmInOutSizeData_i3_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.CASTORE] = asmInOutSizeData_i3_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.SASTORE] = asmInOutSizeData_i3_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.POP] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.POP2] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DUP] = new AsmInOutSizeData(1, 2);
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DUP_X1] = new AsmInOutSizeData(2, 3);
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DUP_X2] = new AsmInOutSizeData(3, 4);
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DUP2] = new AsmInOutSizeData(2, 4);
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DUP2_X1] = new AsmInOutSizeData(3, 5);
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DUP2_X2] = new AsmInOutSizeData(4, 6);
        STATIC_ASM_IN_OUT_SIZE[Opcodes.SWAP] = asmInOutSizeData_i0_o0;//new AsmInOutSizeData(2, 2);
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IADD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LADD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FADD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DADD] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ISUB] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LSUB] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FSUB] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DSUB] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IMUL] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LMUL] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FMUL] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DMUL] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IDIV] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LDIV] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FDIV] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DDIV] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IREM] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LREM] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FREM] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DREM] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.INEG] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LNEG] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FNEG] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DNEG] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ISHL] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LSHL] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ISHR] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LSHR] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IUSHR] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LUSHR] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IAND] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LAND] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IOR] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LOR] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IXOR] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LXOR] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IINC] = asmInOutSizeData_i0_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.I2L] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.I2F] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.I2D] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.L2I] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.L2F] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.L2D] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.F2I] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.F2L] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.F2D] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.D2I] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.D2L] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.D2F] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.I2B] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.I2C] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.I2S] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LCMP] = asmInOutSizeData_i2_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FCMPG] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DCMPL] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DCMPG] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IFEQ] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IFNE] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IFLT] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IFGE] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IFGT] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IFLE] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IF_ICMPEQ] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IF_ICMPNE] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IF_ICMPLT] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IF_ICMPGE] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IF_ICMPGT] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IF_ICMPLE] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IF_ACMPEQ] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IF_ACMPNE] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.GOTO] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.JSR] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.RET] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.TABLESWITCH] = null;//asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LOOKUPSWITCH] = null;//asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IRETURN] = null;//asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.LRETURN] = null;//asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.FRETURN] = null;//asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.DRETURN] = null;//asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ARETURN] = null;//asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.RETURN] = null;//asmInOutSizeData_i0_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.GETSTATIC] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.PUTSTATIC] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.GETFIELD] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.PUTFIELD] = asmInOutSizeData_i2_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.INVOKEVIRTUAL] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.INVOKESPECIAL] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.INVOKESTATIC] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.INVOKEINTERFACE] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.INVOKEDYNAMIC] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.NEW] = asmInOutSizeData_i0_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.NEWARRAY] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ANEWARRAY] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ARRAYLENGTH] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.ATHROW] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.CHECKCAST] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.INSTANCEOF] = asmInOutSizeData_i1_o1;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.MONITORENTER] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.MONITOREXIT] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.MULTIANEWARRAY] = null;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IFNULL] = asmInOutSizeData_i1_o0;
        STATIC_ASM_IN_OUT_SIZE[Opcodes.IFNONNULL] = asmInOutSizeData_i1_o0;

    }

    public static FieldInsnNode findField(String name, InsnList instructions) {
        AbstractInsnNode currentInsn = instructions.getLast();
        while (currentInsn != null) {
            if (currentInsn instanceof FieldInsnNode fieldInsnNode && fieldInsnNode.name.equals(name)) {
                return fieldInsnNode;
            }
            currentInsn = currentInsn.getPrevious();
        }
        return null;
    }
}
