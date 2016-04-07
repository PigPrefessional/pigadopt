package com.ai2020lab.pigadopted.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Rocky on 16/4/7.
 */
public class BaseFragment extends Fragment {

    private final static String TAG_DIALOG_LOADING = "tag_dialog_ai_loading";

    private LoadingDialog loadingDialog;

    public void showLoading(String content) {
        loadingDialog = LoadingDialog.newInstance(content);
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_LOADING);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (fragment != null)
            ft.remove(fragment);
        ft.addToBackStack(null);// 加入回退栈

    }

    /**
     * 关闭进度条对话框
     */
    public void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
