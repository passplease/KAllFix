package n1luik.KAllFix.data.ae;

import appeng.api.stacks.AEKey;

public record PreferredStorageForBuf(AEKey lastCheckedKey, boolean lastCheckResult, long lastCheckTime){}
