package cn.sm.framework.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import cn.sm.framework.net.download.DownLoadInfo;
import cn.sm.framework.net.download.DownLoadState;
import cn.sm.framework.net.download.HttpDownloadManager;
import okhttp3.ResponseBody;

/**
 * Created by shiminli
 */

public class AppTools {



    /**提示框
     *
     * @param tipText
     * @param mContext
     */
    public static void showTipDialog(String tipText, Activity mContext, DialogInterface.OnClickListener confirm, DialogInterface.OnClickListener cancel){
        if (mContext == null){
            JLog.d("showTipDialog:  mContext is null");
            return ;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        TextView mMsg = new TextView(mContext);
        mMsg.setText(tipText);
        mMsg.setPadding(30,0,30,0);
//        mMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        mMsg.setTextSize(18);
        builder.setView(mMsg);
        builder.setCancelable(false);
        if (confirm != null){
            builder.setPositiveButton("确定", confirm);
        }
        if (cancel != null){
            builder.setNegativeButton("取消", cancel);
        }
        Dialog tipDialog = builder.create();
        tipDialog.setCanceledOnTouchOutside(false);
        tipDialog.show();
    }

    /**
     * 安装应用程序的方法
     */
    public static void onInstall(String path, Context context, String packageName) {
        if (context == null){
            JLog.e("context is null");
            Toast.makeText(context, "context is null", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(path);
        if (file.exists()) {
//            给文件设置可读可写权限
            try {
                String[] command1 = {"chmod", "777", file.getAbsolutePath()};
                ProcessBuilder builder = new ProcessBuilder(command1);
                builder.start();

            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean success = false;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File apkFile = new File(path);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            先尝试使用最原始的方式打开
            try{
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                context.startActivity(intent);
                success = true;
            }catch (Exception e){
//                e.printStackTrace();
                System.out.println("第一种安装方式调用失败");
            }
            if (!success){
                //          再尝试使用 最新的打开方式打开
                try{
                    //  7.0以上需要使用content:// 的路径
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = FileProvider.getUriForFile(context, packageName + ".fileprovider", apkFile);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    context.startActivity(intent);
                }catch (Exception e){
//                e.printStackTrace();
                    System.out.println("第二种安装方式调用失败");
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(context, "安装文件不存在", Toast.LENGTH_SHORT).show();
        }
    }


    public static Notification.Builder createNotication(@NonNull Context applicationContext, @NonNull NotificationManager manager, String title, boolean soundOnce, int iconId){
        Notification.Builder notification = new Notification
                .Builder(applicationContext)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(iconId)   //必须添加（Android 8.0）
                .setLargeIcon(BitmapFactory.decodeResource(applicationContext.getResources(), iconId))
                .setDefaults(Notification.DEFAULT_ALL)
                .setOnlyAlertOnce(soundOnce)
                .setChannelId(applicationContext.getPackageName()) //必须添加（Android 8.0） 【唯一标识】
                .setSound(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(applicationContext.getPackageName(), title, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        return notification;

    }

    /**
     * 写入文件
     * @param file
     * @param info
     * @throws IOException
     */
    public  static  void writeCache(ResponseBody responseBody, File file, DownLoadInfo info) throws Exception {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        long allLength;
        if (info.getCountLength()==0){
            allLength=responseBody.contentLength();
        }else{
            allLength=info.getCountLength();
        }
        /**
         * FileChannel 优势：
         多线程并发读写，并发性；
         IO读写性能提高（OS负责），也可引做共享内存，减少IO操作，提升并发性；
         应用crash，保证这部分内容还能写的进去文件。在我们调用channel.write(bytebuffer)之后，
         具体何时写入磁盘、bytebuffer中内容暂存于哪里（os cache）等相关一系列问题，就交由OS本身负责了
         */
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        if (info.getReadLength() == 0){
            randomAccessFile.setLength(allLength);
        }
        randomAccessFile.seek(info.getReadLength());
        FileChannel channelOut = randomAccessFile.getChannel();
        System.out.println(channelOut.size());
        MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, info.getReadLength(),allLength-info.getReadLength());
        byte[] buffer = new byte[1024*4];
        int len;
        long record = 0;
        info.setStateInte(DownLoadState.DOWN.getState());
        HttpDownloadManager.getInstance().notifyDownloadStateChanged(info);
        InputStream inputStream = responseBody.byteStream();
        long beforeTime = System.currentTimeMillis();
        long secondCount = 0;
        while ((len = inputStream.read(buffer)) != -1) {
//            System.out.println("has: " + mappedBuffer.hasRemaining() + " rema: " + mappedBuffer.remaining() + " len: " + len);
            if (mappedBuffer.remaining() >= len){
                mappedBuffer.put(buffer, 0, len);
            }

//            randomAccessFile.write(buffer, 0, len);
//            计算已经下载的长度
            record +=len;
            long readlength = record;
            int percentage1 = info.getPercentage();
            if (info.getCountLength() > responseBody.contentLength()){
                readlength = info.getCountLength() - responseBody.contentLength() + record;
            }else {
                info.setCountLength(responseBody.contentLength());
            }
            info.setReadLength(readlength);

//            计算每秒的下载量并进行回调
            long currentTime = System.currentTimeMillis();
            if (currentTime - beforeTime > 1000){
                beforeTime = currentTime;
                info.setSecondCount(secondCount);
                secondCount = 0;
                HttpDownloadManager.getInstance().notifyDownloadProgressed(info);
            }else {
                secondCount += len;
            }

//            状态改变后停止写入
            if (info.getStateInte() != DownLoadState.DOWN.getState()){
                System.out.println("状态改变，暂停写入");
                break;
            }
        }
        inputStream.close();
        responseBody.close();
        if (channelOut != null) {
            channelOut.close();
        }
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }

}
