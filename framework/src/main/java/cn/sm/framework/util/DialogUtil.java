package cn.sm.framework.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.TextView;

public class DialogUtil {


    /**提示框
     *
     * @param tipText
     * @param mContext
     */
    public static void showTipDialog(String tipText, Context mContext, DialogInterface.OnClickListener confirm, DialogInterface.OnClickListener cancel){
        if (mContext == null){
            System.out.println("showTipDialog:  mContext is null");
            return ;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        TextView mMsg = new TextView(mContext);
        mMsg.setText(tipText);
        mMsg.setGravity(Gravity.CENTER_HORIZONTAL);
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
}
