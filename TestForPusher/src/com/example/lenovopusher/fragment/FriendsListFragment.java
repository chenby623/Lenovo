package com.example.lenovopusher.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimajia.swipe.util.Attributes;
import com.example.lenovopusher.adapter.FriendsListAdapter;
import com.example.lenovopusher.model.FriendBean;
import com.example.lenovopusher.utils.Config;
import com.example.lenovopusher.utils.MyConnection;
import com.example.vlctest.MainActivity;
import com.example.lenovopusher.R;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class FriendsListFragment extends Fragment {
    private static String TAG="FriendsListFragment";
    private ListView mListView;
    private FriendsListAdapter mAdapter;
    private ArrayList<FriendBean> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.friends_listview,null);
        mListView=(ListView)view.findViewById(R.id.listview);
        data=new ArrayList<FriendBean>();
//        data.add("好友一");
//        data.add("好友二");
//        data.add("好友三");
        data.add(new FriendBean("好友一"));


        //找到所有的好友，并设置监听函数
        ArrayList<RosterEntry> rosterEntryList= MyConnection.getInstance().getAllFriends();
        data=new ArrayList<FriendBean>();

        for(RosterEntry rosterEntry:rosterEntryList){
            if(rosterEntry.getName()!=null) {
                FriendBean newFriend = new FriendBean(rosterEntry.getName().toString());
                data.add(newFriend);
            }
        }

        mAdapter=new FriendsListAdapter(getActivity().getApplicationContext(),data);
        mListView.setAdapter(mAdapter);
        mAdapter.setMode(Attributes.Mode.Single);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.putExtra("friendName",data.get(position).getFriendName());
                Log.e(TAG,"friendName is"+data.get(position).getFriendName());
                Config.roomName=data.get(position).getFriendName();
                startActivity(intent);
            }
        });
        return view;
    }
}
