import os
import re

def update_transformer_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 添加ITransformer2导入
    if 'asm.n1luik.K_multi_threading.asm.util.ITransformer2' not in content:
        content = re.sub(r'^(import.*?)$', r'\1\nimport asm.n1luik.K_multi_threading.asm.util.ITransformer2;', content, count=1, flags=re.MULTILINE)
    
    # 将implements ITransformer<ClassNode>替换为extends ITransformer2
    content = re.sub(r'implements ITransformer<ClassNode>', 'extends ITransformer2', content)
    
    # 修改transform方法签名，移除ITransformerVotingContext参数
    content = re.sub(r'public\s+@NotNull\s+ClassNode\s+transform\s*\(\s*ClassNode\s+input\s*,\s*ITransformerVotingContext\s+context\s*\)', r'public @NotNull ClassNode transform(ClassNode input)', content)
    content = re.sub(r'public\s+ClassNode\s+transform\s*\(\s*ClassNode\s+input\s*,\s*ITransformerVotingContext\s+context\s*\)', r'public ClassNode transform(ClassNode input)', content)
    
    # 移除castVote方法
    content = re.sub(r'@Override\s+public\s+@NotNull\s+TransformerVoteResult\s+castVote\s*\(\s*ITransformerVotingContext\s+context\s*\)\s*\{[^}]*\}', '', content, flags=re.DOTALL)
    
    # 修改targets方法，返回Set<String>而不是Set<Target>
    # 处理简单的targets方法
    content = re.sub(r'@Override\s+public\s+@NotNull\s+Set<Target>\s+targets\s*\(\s*\)\s*\{\s*return\s+Set\.of\s*\(([^)]*)\)\s*;\s*\}', 
                     lambda m: '@Override\n    public @NotNull Set<String> targets() {\n        return Set.of(%s);\n    }' % 
                     re.sub(r'Target\.targetClass\s*\(\s*"([^"]*)"\s*\)', r'"\1"', m.group(1)), 
                     content)
    
    # 处理复杂的targets方法（包含多行逻辑）
    content = re.sub(r'@Override\s+public\s+@NotNull\s+Set<Target>\s+targets\s*\(\s*\)', r'@Override\n    public @NotNull Set<String> targets()', content)
    content = re.sub(r'Target\.targetClass\s*\(\s*"([^"]*)"\s*\)', r'"\1"', content)
    
    # 移除不再需要的导入
    content = re.sub(r'import\s+cpw\.mods\.modlauncher\.api\.ITransformerVotingContext\s*;\n', '', content)
    content = re.sub(r'import\s+cpw\.mods\.modlauncher\.api\.TransformerVoteResult\s*;\n', '', content)
    content = re.sub(r'import\s+cpw\.mods\.modlauncher\.api\.ITransformer\s*;\n', '', content)
    
    # 移除可能残留的Target导入
    content = re.sub(r'import\s+cpw\.mods\.modlauncher\.api\.ITransformer\$Target\s*;\n', '', content)
    content = re.sub(r'import\s+cpw\.mods\.modlauncher\.api\.Target\s*;\n', '', content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"Updated: {file_path}")

def main():
    # 遍历所有Java文件
    for root, dirs, files in os.walk('src/main/java'):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                # 只处理实现了ITransformer<ClassNode>的文件
                if 'implements ITransformer<ClassNode>' in content:
                    update_transformer_file(file_path)

if __name__ == '__main__':
    main()