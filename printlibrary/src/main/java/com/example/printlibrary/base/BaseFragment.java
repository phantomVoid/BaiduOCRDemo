package com.example.printlibrary.base;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by jc on 2017/11/10.
 */

abstract public class BaseFragment extends Fragment implements View.OnClickListener{

    private View view;

    public void init(View view){
        this.view = view;
        initView();
        initData();
    }

    public View findViewById(int id){
        return view.findViewById(id);
    }

    abstract public void initView();
    abstract public void initData();
}
