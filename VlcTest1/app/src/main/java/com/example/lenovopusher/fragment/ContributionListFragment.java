package com.example.lenovopusher.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.lenovopusher.adapter.ContributionListAdapter;
import com.example.lenovopusher.model.ContributionListItemBean;
import com.example.vlctest.R;

import java.util.ArrayList;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class ContributionListFragment extends Fragment {
    ContributionListAdapter listAdapter;
    ListView listView;
    ArrayList<ContributionListItemBean> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mData=new ArrayList<ContributionListItemBean>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.contribution_list_fragment,container,false);
        listView=(ListView)view.findViewById(R.id.contribution_listview);
        listAdapter=new ContributionListAdapter(getActivity().getApplicationContext(),mData);
        listView.setAdapter(listAdapter);
        return view;
    }
}

