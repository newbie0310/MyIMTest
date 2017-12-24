package com.ego.im4bmob.ui.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ego.im4bmob.R;
import com.ego.im4bmob.adapter.SettingAdapter;
import com.ego.im4bmob.base.ParentWithNaviFragment;
import com.ego.im4bmob.bean.Setting;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.event.RefreshEvent;
import com.ego.im4bmob.model.UserModel;
import com.ego.im4bmob.mvp.bean.Installation;
import com.ego.im4bmob.ui.LogActivity;
import com.ego.im4bmob.ui.SetUserInfoActivity;
import com.ego.im4bmob.ui.image_selector.MultiImageSelector;
import com.ego.im4bmob.util.BmobUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Action1;

import static android.app.Activity.RESULT_OK;

/**
 * 设置
 *
 * @author :smile
 * @project:SetFragment
 * @date :2016-01-25-18:23
 */
public class SetFragment extends ParentWithNaviFragment {

    @Bind(R.id.v_top)
    View mVTop;
    @Bind(R.id.tv_left)
    ImageView mTvLeft;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_right)
    TextView mTvRight;

    private List<Setting> mSettingList = new ArrayList<>();

    private void initSettingData(){
        Setting userInfo = new Setting();
        userInfo.setSetImgId(R.drawable.setting_user_img);
        userInfo.setSettingInfo("个人数据");
        mSettingList.add(userInfo);
        Setting dataInfo = new Setting();
        dataInfo.setSetImgId(R.drawable.data_info);
        dataInfo.setSettingInfo("详细信息设置");
        mSettingList.add(dataInfo);
    }

    @Override
    protected String title() {
        return "设置";
    }

    public static SetFragment newInstance() {
        SetFragment fragment = new SetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_set, container, false);
        initNaviView();
        initSettingData();

        final ListView mSetLv = rootView.findViewById(R.id.lv_set);
        SettingAdapter adapter = new SettingAdapter(mSettingList,getActivity());
        mSetLv.setAdapter(adapter);
        mSetLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        startActivity(new Intent(getActivity(), SetUserInfoActivity.class));
                        break;
                    case 1:
                        Toast.makeText(getActivity(),"你点击了第2个条目",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
