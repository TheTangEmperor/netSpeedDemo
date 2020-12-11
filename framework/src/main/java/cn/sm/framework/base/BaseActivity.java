package cn.sm.framework.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import cn.sm.framework.util.AutoDensity;
import cn.sm.framework.util.VerificationUtils;

/**
 * Describe：所有Activity的基类
 */

public abstract class BaseActivity extends FragmentActivity {


    protected final int PERMISSON_REQUESTCODE = 0;
    protected View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        setOrientation();
        initImmersionBar();
        setContentView(getLayoutId());
        initView();
    }


    /**
     * 沉浸栏颜色
     */
    protected void initImmersionBar() {
        ImmersionBar.with(this).transparentStatusBar().init();
    }

    /**
     *
     * 由于是个人封装,此方法需要写在onCreate()中的setContentView()方法前面,切换方向的效果才会生效
     */
    public void setOrientation() {
        AutoDensity.setDefault(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //***************************************空页面方法*************************************
    protected void showToast(String text) {
        if (VerificationUtils.strNullOrEmpty(text)) {
            return;
        }
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }




    /**
     * 是否需要ActionBar
     * TODO 暂时用此方法 后续优化
     */
    protected boolean isActionBar() {
        return false;
    }


    protected abstract int getLayoutId();

    protected abstract void initView();


    protected void applyPermissions(){
        ActivityCompat.requestPermissions(this, NeedPermissions.permissions, PERMISSON_REQUESTCODE);
    }

    /**
     * 重写 getResource 方法，防止系统字体影响
     */
    @Override
    public Resources getResources() {
        //禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

}
