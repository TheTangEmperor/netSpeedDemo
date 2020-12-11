package cn.sm.framework.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cn.sm.framework.R;

/**
 * Describe：所有Fragment的基类
 */

public abstract class BaseFragment extends Fragment {

    private ViewStub emptyView;
    protected View rootView;
    protected Context mContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_base, container, false);
        ((ViewGroup) rootView.findViewById(R.id.fl_content)).addView(inflater.inflate(getLayoutId(), null));

        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    //***************************************空页面方法*************************************
    protected void showEmptyView(String text) {

    }



    protected void showErrorMsg(String text) {
        if (mContext != null && text != null){
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
        }
    }


    protected abstract int getLayoutId();

    protected abstract void initView();
    protected abstract void initData();

}
