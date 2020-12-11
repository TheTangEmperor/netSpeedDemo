package cn.sm.framework.db;

import cn.sm.framework.WorkApp;

public class DbManager {

    private static DbManager manager = new DbManager();
    private GreendaoOpenHelper openHelper;
    private DownloadDbUtil downloadDb;
    private SearchDbUtil searchDb;
    public static final String db_name = "bjrcb_gm";

    public static DbManager getInstance() {
        return manager;
    }

    private DbManager() {
        openHelper = new GreendaoOpenHelper(WorkApp.getApp(), db_name);
        downloadDb = new DownloadDbUtil();
        searchDb = new SearchDbUtil();
    }


    /**
     * 获取可写数据库
     */
    public DaoSession getWriteSession(){
        return new DaoMaster(openHelper.getWritableDatabase()).newSession();
    }
    /**
     * 获取可读数据库
     */
    public DaoSession getReadSession(){
        return new DaoMaster(openHelper.getReadableDatabase()).newSession();
    }

    public DownloadDbUtil getDownloadDb(){
        return downloadDb;
    }

    public SearchDbUtil getSearchDb(){
        return searchDb;
    }

}
