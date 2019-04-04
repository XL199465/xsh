package cn.ithcast.core.pojogroup;

import java.io.Serializable;

public class UserVo implements Serializable {
    private Integer userTotalCount;
    private Integer activityUserCount;
    private Integer unactivityUserCount;

    public Integer getUserTotalCount() {
        return userTotalCount;
    }

    public void setUserTotalCount(Integer userTotalCount) {
        this.userTotalCount = userTotalCount;
    }

    public Integer getActivityUserCount() {
        return activityUserCount;
    }

    public void setActivityUserCount(Integer activityUserCount) {
        this.activityUserCount = activityUserCount;
    }

    public Integer getUnactivityUserCount() {
        return unactivityUserCount;
    }

    public void setUnactivityUserCount(Integer unactivityUserCount) {
        this.unactivityUserCount = unactivityUserCount;
    }
}
