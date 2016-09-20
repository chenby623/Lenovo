package com.example.lenovopusher.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.vlctest.MainActivity;
import com.example.vlctest.R;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class PersonalPageFragment extends Fragment implements View.OnClickListener {
    private LinearLayout my_live_layout;
    private LinearLayout group_live_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.personal_page_fragment,null);

        my_live_layout=(LinearLayout)view.findViewById(R.id.my_live_linearlayout);
        group_live_layout=(LinearLayout)view.findViewById(R.id.group_live_linearlayout);

        my_live_layout.setOnClickListener(this);
        group_live_layout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_live_linearlayout:{
                //Intent intent=new Intent(getActivity(), PersonalLiveActivity.class);
                Intent intent=new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.group_live_linearlayout:{
                break;
            }
            default:
                break;
        }
    }
}
