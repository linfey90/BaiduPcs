package baidupcs.response.api;

import baidupcs.response.file.QuotaResponse;

/**
 * 用户空间配额信息。
 * 
 * @author blove
 */
public class Quota {
    private long quota;
    private long used;

    public static Quota fromResponse(QuotaResponse res) {
        Quota quota = new Quota();
        quota.quota = res.getQuota();
        quota.used = res.getUsed();
        return quota;
    }

    /**
     * 空间配额，单位为字节。
     * 
     * @return
     */
    public long getQuota() {
        return quota;
    }

    /**
     * 已使用空间大小，单位为字节。
     * 
     * @return
     */
    public long getUsed() {
        return used;
    }

    @Override
    public String toString() {
        return quota + ":" + used;
    }

}