package com.ego.im4bmob.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ego.im4bmob.R;
import com.ego.im4bmob.bean.UserBaseInfo;
import com.ego.im4bmob.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailAdater extends BaseAdapter {

    private List<UserBaseInfo> userBaseInfoList;
    private Context mContext;

    public UserDetailAdater(List<UserBaseInfo> userBaseInfoList,Context context) {
        this.userBaseInfoList = userBaseInfoList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return userBaseInfoList.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if (i == 0){
            return 0;
        }else {
            return userBaseInfoList.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view1;
        if (i == 0){
            view1 = inflater.inflate(R.layout.item_user_base_info_avator,null);
            TextView set_Info_avator = view1.findViewById(R.id.set_info_avator);
            CircleImageView avator = view1.findViewById(R.id.set_avator);
            set_Info_avator.setText("头像");
            if (UserModel.getInstance().getCurrentUser().getAvatar() != null)
                Glide.with(mContext).load(UserModel.getInstance().getCurrentUser().getAvatar().getFileUrl()).into(avator);
            else Glide.with(mContext).load(R.mipmap.icon_message_press).into(avator);
        }else {
            view1 = inflater.inflate(R.layout.item_user_base_info,null);
            TextView setInfo = view1.findViewById(R.id.user_base_info);
            TextView setDetail = view1.findViewById(R.id.user_base_info_detail);
            setInfo.setText(userBaseInfoList.get(i-1).getSetName());
            setDetail.setText(userBaseInfoList.get(i-1).getSetDatile());
        }
        return view1;
    }


    public  void notifyList(List<UserBaseInfo> userBaseInfos){
        userBaseInfoList = userBaseInfos;
        notifyDataSetChanged();
    }
}
