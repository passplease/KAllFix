package n1luik.KAllFix.forge;

import n1luik.KAllFix.DataCollectors;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;

/**
 * 大概率用不了
 * */
@Deprecated
public class InitDataCollectorsEvent extends Event { // TODO 没有调用
    public final DataCollectors dataCollectors = new DataCollectors();
}
