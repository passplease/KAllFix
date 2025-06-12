package n1luik.KAllFix.forge;

import n1luik.KAllFix.DataCollectors;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;

/**
 * 大概率用不了
 * */
public class InitDataCollectorsEvent extends Event {
    public final DataCollectors dataCollectors = new DataCollectors();
}
