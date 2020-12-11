package cn.sm.framework.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

@Entity
public class SearchBean {
    @Unique
    private Long id;
    private String keyWord;
    private Date time;
    @Generated(hash = 748336689)
    public SearchBean(Long id, String keyWord, Date time) {
        this.id = id;
        this.keyWord = keyWord;
        this.time = time;
    }
    @Generated(hash = 562045751)
    public SearchBean() {
    }
    public String getKeyWord() {
        return this.keyWord;
    }
    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
    public Date getTime() {
        return this.time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SearchBean{" +
                "id=" + id +
                ", keyWord='" + keyWord + '\'' +
                ", time=" + time +
                '}';
    }
}
