package com.example.printlibrary.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jc on 2017/10/29.
 */

abstract public class BaseActivity extends AppCompatActivity{

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void init(){
        initView();
        initData();
    }

    abstract public void initView();

    abstract public void initData();

    /*PermissionResultListener permissionResultListener;

    public interface PermissionResultListener {
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    public void setPermissionResultListener(PermissionResultListener permissionResultListener) {
        this.permissionResultListener = permissionResultListener;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionResultListener != null) {
            permissionResultListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/
}
