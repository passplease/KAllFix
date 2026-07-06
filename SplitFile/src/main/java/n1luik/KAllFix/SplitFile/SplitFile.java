package n1luik.KAllFix.SplitFile;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class SplitFile {
    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var classLoader = new URLClassLoader(new URL[]{new File(args[0]).toURI().toURL()});
        classLoader.loadClass("asm.n1luik.K_multi_threading.asm.GenFile").getMethod("run").invoke(null);
    }
}
