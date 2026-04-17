package n1luik.KAllFix.Imixin.mod.ae;

import appeng.api.storage.MEStorage;

public interface NetworkStoragePipe {
    default MEStorage KAllFix$pipe() {
        return null;
        //try {
        //    return (MEStorage)getClass().getDeclaredField("storage").get(this);
        //} catch (NoSuchFieldException e) {
        //    throw new RuntimeException(e);
        //} catch (IllegalAccessException e) {
        //    throw new RuntimeException(e);
        //}
    }
}
