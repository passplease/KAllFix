package asm.n1luik.K_multi_threading.asm.JavaAgent;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@AllArgsConstructor
public abstract class AgentAPI {
    public final String name;
    /**
     * 这个方法会在运行时被调用，返回一个Transformer2的列表
     */
    public abstract List<ITransformer2> transformers();
    /**
     * 这只能在agent模式使用，是否启用全局修改接口
     */
    public boolean isTransformerAll(){
        return false;
    }
    public ClassNode transformerAll(ClassNode node){
        return node;
    }
}
