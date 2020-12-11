package cn.sm.framework.db;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import androidx.annotation.NonNull;

public class SearchDbUtil {



    public void save(@NonNull SearchBean info){
        DaoSession writeSession = DbManager.getInstance().getWriteSession();
        long l = writeSession.getSearchBeanDao().insertOrReplace(info);
        System.out.println("column："+l);
    }

    public void delete(@NonNull SearchBean info){
        DaoSession writeSession = DbManager.getInstance().getWriteSession();
        QueryBuilder<SearchBean> queryBuilder = writeSession.getSearchBeanDao().queryBuilder();
        DeleteQuery<SearchBean> deleteQuery = queryBuilder.where(SearchBeanDao.Properties.Id.eq(info.getId())).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        writeSession.clear();
    }

    public List<SearchBean> queryAll(){
        DaoSession readSession = DbManager.getInstance().getReadSession();
        QueryBuilder<SearchBean> queryBuilder = readSession.getSearchBeanDao().queryBuilder();
//        倒序查询
        return queryBuilder.orderDesc(SearchBeanDao.Properties.Time).list();
    }

    public void update(@NonNull SearchBean info){
        save(info);
    }


}
