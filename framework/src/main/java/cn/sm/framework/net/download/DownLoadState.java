package cn.sm.framework.net.download;

/**
 * 下载状态
 */

public enum DownLoadState {

    /**
     * 默认状态
     */
    NORMAL(0),

    /**
     * 下载中
     */
    DOWN(1),
    /**
     * 暂停
     */
    PAUSE(2),
    /**
     * 停止
     */
    STOP(3),
    /**
     * 出错
     */
    ERROR(4),
    /**
     * 完成
     */
    FINISH(5),
    /**
     * 可打开
     */
    OPEN(6),
    /**
     * 预约
     */
    APPOINTMENT(7),
    /**
     * 未购买
     */
    CHARGE(8),
    /**
     * 未购买
     */
    UPDATE(9),
    /**
     * 卸载
     */
    UNINSTALL(10);


    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    DownLoadState(int state) {
        this.state = state;
    }
}
