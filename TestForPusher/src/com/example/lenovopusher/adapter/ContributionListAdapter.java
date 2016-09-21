package com.example.lenovopusher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lenovopusher.model.ContributionListItemBean;
import com.example.lenovopusher.R;

import java.util.List;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class ContributionListAdapter extends BaseAdapter {

    private List<ContributionListItemBean> mData;
    private LayoutInflater mInflater;

    public ContributionListAdapter(Context context, List<ContributionListItemBean> data){
        this.mData=data;
        mInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.contributionlist_item_layout,null);
            holder.ranking=(TextView)convertView.findViewById(R.id.ranking);
            holder.contribution=(TextView)convertView.findViewById(R.id.contribution);
            holder.user_name=(TextView)convertView.findViewById(R.id.user_name);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        if(mData.size()>position){
            ContributionListItemBean bean=mData.get(position);
            holder.ranking.setText("No."+bean.getRanking());
            holder.user_name.setText(bean.getUserName());
            holder.contribution.setText("¹±Ï×:"+bean.getContributeSum());
        }
        return convertView;
    }

    public final class ViewHolder{
        public TextView ranking;
        public TextView user_name;
        public TextView contribution;
    }
}

