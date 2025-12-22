import os
import re

def fix_targets_method(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 修复targets方法返回Set<Target>为Set<String>
    content = re.sub(r'public\s+@NotNull\s+Set<Target>\s+targets\s*\(\s*\)', r'public @NotNull Set<String> targets()', content)
    content = re.sub(r'public\s+Set<Target>\s+targets\s*\(\s*\)', r'public Set<String> targets()', content)
    
    # 修复targets方法中的Target.targetClass调用
    content = re.sub(r'Set<Target>\s+out\s*=\s+new\s+HashSet<Target>\s*\(\s*\)', r'Set<String> out = new HashSet<String>()', content)
    content = re.sub(r'Target\.targetClass\s*\(\s*("[^"]*")\s*\)', r'\1', content)
    
    # 修复toArray(Target[]::new)为toArray(String[]::new)
    content = re.sub(r'toArray\s*\(\s*Target\[\]::new\s*\)', r'toArray(String[]::new)', content)
    
    # 修复stream().map(Target::targetClass)为直接返回
    content = re.sub(r'stream\(\)\.map\s*\(\s*Target::targetClass\s*\)', r'stream()', content)
    
    # 修复stream().map(AsmTarget::className).map(Target::targetClass)为stream().map(AsmTarget::className)
    content = re.sub(r'stream\(\)\.map\s*\(\s*([^)]+)::className\s*\)\.map\s*\(\s*Target::targetClass\s*\)', r'stream().map(\1::className)', content)
    
    # 修复getClassName()调用，因为现在targets()返回的是String而不是Target
    content = re.sub(r'targets\(\)\.stream\(\)\.anyMatch\s*\(\s*t->t\.getClassName\(\)\.equals\s*\(([^)]+)\)\s*\)', r'targets().contains(\1)', content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"Fixed targets method in: {file_path}")

def fix_transformer_addition(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 修复List<ITransformer>的类型不兼容问题
    content = re.sub(r'List<ITransformer>\s+transformers\s*=\s+new\s+ArrayList<>\s*\(\s*List\.of\s*\(([^)]*)\)\s*\)\s*;', r'List<?> transformers = new ArrayList<>(List.of(\1));', content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"Fixed transformer addition in: {file_path}")

def main():
    # 遍历所有Java文件
    for root, dirs, files in os.walk('src/main/java'):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                # 修复targets方法相关错误
                if 'targets()' in content:
                    fix_targets_method(file_path)
                
                # 修复transformer添加相关错误
                if 'List<ITransformer>' in content and 'new ArrayList<>(List.of(' in content:
                    fix_transformer_addition(file_path)

if __name__ == '__main__':
    main()