package com.example.lenovopusher.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.lenovopusher.model.FriendBean;
import com.example.lenovopusher.R;

import java.util.ArrayList;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class FriendsListAdapter extends BaseSwipeAdapter{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<FriendBean> mData;

    public FriendsListAdapter(Context context, ArrayList<FriendBean> data) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mData=data;
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.listview_swipe;
    }

    /**
     * 重新计算ListView的每个item view的大小
     */
    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.friends_listview_item, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {

            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    /**
     * 为ListView的每个item项进行赋值
     */
    @Override
    public void fillValues(int position, View view) {
        TextView text = (TextView) view.findViewById(R.id.friends_name);
        //text.setText("绗� " + (position+1) + " 涓垪琛ㄩ」");
        text.setText(mData.get(position).getFriendName());
        text.setTextColor(Color.RED);
        text.setPadding(40, 0, 20, 0);
    }

    /**
     * 返回list的长度
     * */
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
}
