package cn.sm.framework.db;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import androidx.annotation.NonNull;
import cn.sm.framework.net.download.DownLoadInfo;
import cn.sm.framework.net.download.DownLoadInfoDao;

public class DownloadDbUtil {


    public void save(@NonNull DownLoadInfo info){
        DownLoadInfoDao downLoadInfoDao = DbManager.getInstance().getWriteSession().getDownLoadInfoDao();
        long l = downLoadInfoDao.insertOrReplace(info);
        System.out.println("column: " + l);
    }

    public void delete(@NonNull DownLoadInfo info){
        DaoSession writeSession = DbManager.getInstance().getWriteSession();
        QueryBuilder<DownLoadInfo> queryBuilder = writeSession.getDownLoadInfoDao().queryBuilder();
        DeleteQuery<DownLoadInfo> deleteQuery = queryBuilder.where(DownLoadInfoDao.Properties.Id.eq(info.getId())).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        writeSession.clear();
    }

    public void update(@NonNull DownLoadInfo info){
        save(info);
    }

    public List<DownLoadInfo> queryAll(){
        DaoSession readSession = DbManager.getInstance().getReadSession();
        QueryBuilder<DownLoadInfo> queryBuilder = readSession.getDownLoadInfoDao().queryBuilder();
        return queryBuilder.list();
    }

    public DownLoadInfo queryById(Long id){
        DaoSession readSession = DbManager.getInstance().getReadSession();
        QueryBuilder<DownLoadInfo> queryBuilder = readSession.getDownLoadInfoDao().queryBuilder();
        queryBuilder.where(DownLoadInfoDao.Properties.Id.eq(id));
        List<DownLoadInfo> list = queryBuilder.list();
        return list.isEmpty() ? null : list.get(0);
    }

}
