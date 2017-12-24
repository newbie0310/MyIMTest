package com.ego.im4bmob.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ego.im4bmob.R;
import com.ego.im4bmob.bean.Setting;
import java.util.List;

/**
 * Created by zaXie on 2017/12/23.
 */

public class SettingAdapter extends BaseAdapter {
    private List<Setting> mSettingList;
    private Context mContext;

    public SettingAdapter(List<Setting> mSettingList, Context mContext) {
        this.mSettingList = mSettingList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mSettingList.size();
    }

    @Override
    public Object getItem(int i) {
        return mSettingList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view1;
        if (view == null){
            view1 = LayoutInflater.from(mContext).inflate(R.layout.item_set_rl,viewGroup,false);
            viewHolder = new ViewHolder();
            viewHolder.mSetImg = view1.findViewById(R.id.rl_set_img);
            viewHolder.mSetInfo = view1.findViewById(R.id.rl_set_tv);
            view1.setTag(viewHolder);
        }else {
            view1 = view;
            viewHolder = (ViewHolder) view1.getTag();
        }
        viewHolder.mSetImg.setImageResource(mSettingList.get(i).getSetImgId());
        viewHolder.mSetInfo.setText(mSettingList.get(i).getSettingInfo());
        return view1;
    }

    class ViewHolder{
        ImageView mSetImg;
        TextView mSetInfo;
    }
}
