package cn.sm.framework.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

import cn.sm.framework.net.download.DownLoadInfoDao;

public class GreendaoOpenHelper extends DaoMaster.OpenHelper {
    public GreendaoOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        /**

         * 1、第一次创建数据库的时候，这个方法不会走

         * 2、清除数据后再次运行(相当于第一次创建)这个方法不会走

         * 3、数据库已经存在，而且版本升高的时候，这个方法才会调用
         */
        System.out.println("oldVersion: " + oldVersion + "  newVersion: " + newVersion);
        if (oldVersion < newVersion) {
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }

                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            }, SearchBeanDao.class, DownLoadInfoDao.class);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("onDowngrade  oldVersion: " + oldVersion + "  newVersion: " + newVersion);
        StandardDatabase database = new StandardDatabase(db);
        DaoMaster.dropAllTables(database, true);
        DaoMaster.createAllTables(database, false);
    }

}
