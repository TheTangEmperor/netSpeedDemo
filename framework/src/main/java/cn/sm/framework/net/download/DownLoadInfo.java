package cn.sm.framework.net.download;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * apk下载请求数据基础类
 */
@Entity
public class DownLoadInfo {

    private String iconUrl;
    private String versionName;
    private String versionCode;
    private String versionDesc;
    private String packageName;
    @Unique
    private Long id;
    /**存储位置*/
    private String savePath;
    /**文件总长度*/
    private long countLength = 0;
    /**下载长度*/
    private long readLength = 0;
    /**超时设置*/
    private  int connectonTime=6;
    /**
    state状态数据库保存
    * */
    private int stateInte;
    /**
     url
    */
    private String url;
    @Transient
    private String appType;
    @Transient
    private String appId;
    @Transient
    private long secondCount;
    private String appName;


    @Generated(hash = 187912929)
    public DownLoadInfo(String iconUrl, String versionName, String versionCode,
            String versionDesc, String packageName, Long id, String savePath,
            long countLength, long readLength, int connectonTime, int stateInte,
            String url, String appName) {
        this.iconUrl = iconUrl;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.versionDesc = versionDesc;
        this.packageName = packageName;
        this.id = id;
        this.savePath = savePath;
        this.countLength = countLength;
        this.readLength = readLength;
        this.connectonTime = connectonTime;
        this.stateInte = stateInte;
        this.url = url;
        this.appName = appName;
    }


    @Generated(hash = 1743687477)
    public DownLoadInfo() {
    }


    @Override
    public String toString() {
        return "DownLoadInfo{" +
                "iconUrl='" + iconUrl + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionDesc='" + versionDesc + '\'' +
                ", packageName='" + packageName + '\'' +
                ", id=" + id +
                ", savePath='" + savePath + '\'' +
                ", countLength=" + countLength +
                ", readLength=" + readLength +
                ", connectonTime=" + connectonTime +
                ", stateInte=" + stateInte +
                ", url='" + url + '\'' +
                ", appType='" + appType + '\'' +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public int getPercentage(){
//        float value = 1.0f * readLength / countLength;
        return (int) (1.0f * readLength / countLength * 100);
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public long getCountLength() {
        return countLength;
    }

    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public int getConnectonTime() {
        return connectonTime;
    }

    public void setConnectonTime(int connectonTime) {
        this.connectonTime = connectonTime;
    }

    public int getStateInte() {
        return stateInte;
    }

    public void setStateInte(int stateInte) {
        this.stateInte = stateInte;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getSecondCount() {
        return secondCount;
    }

    public void setSecondCount(long secondCount) {
        this.secondCount = secondCount;
    }
}
