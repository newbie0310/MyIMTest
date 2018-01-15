package com.ego.im4bmob.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ego.im4bmob.R;
import com.ego.im4bmob.adapter.UserDetailAdater;
import com.ego.im4bmob.base.ParentWithNaviActivity;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.bean.UserBaseInfo;
import com.ego.im4bmob.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UserBaseInfoActivity extends ParentWithNaviActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.detail_list)
    ListView detailList;
    List<UserBaseInfo> userBaseInfoLists = new ArrayList<>();
    private UserDetailAdater adater;

    User userOt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_base_info);
        ButterKnife.bind(this);
        initNaviView();


        initData();
        adater = new UserDetailAdater(userBaseInfoLists,this);
        detailList.setAdapter(adater);
        detailList.setOnItemClickListener(this);
    }

    @Override
    protected String title() {
        return "基本信息";
    }

    private void initData(){
        userBaseInfoLists.clear();
        String name = UserModel.getInstance().getCurrentUser().getUsername();
        UserBaseInfo userName = new UserBaseInfo();
        userName.setSetName("用户名");
        userName.setSetDatile(name);
        userBaseInfoLists.add(userName);
        String sex = UserModel.getInstance().getCurrentUser().getSex();
        UserBaseInfo userSex = new UserBaseInfo();
        userSex.setSetName("性别");
        userSex.setSetDatile(sex);
        userBaseInfoLists.add(userSex);
        String age = UserModel.getInstance().getCurrentUser().getAge() + "";
        UserBaseInfo userAge = new UserBaseInfo();
        userAge.setSetName("年龄");
        userAge.setSetDatile(age);
        userBaseInfoLists.add(userAge);
        String phone = UserModel.getInstance().getCurrentUser().getMobilePhoneNumber();
        UserBaseInfo userPhone = new UserBaseInfo();
        userPhone.setSetName("手机");
        userPhone.setSetDatile(phone);
        userBaseInfoLists.add(userPhone);
        String sign = UserModel.getInstance().getCurrentUser().getSign();
        UserBaseInfo userSign = new UserBaseInfo();
        userSign.setSetName("个性签名");
        userSign.setSetDatile(sign);
        userBaseInfoLists.add(userSign);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        if (i == 0){

        }else if (userBaseInfoLists.get(i -1).getSetName().equals("性别")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(UserBaseInfoActivity.this);
            //builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("选择性别");
            //    指定下拉列表的显示数据
            final String[] sex = {"男", "女"};
            //    设置一个下拉的列表选择项
            builder.setItems(sex, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    int age = UserModel.getInstance().getCurrentUser().getAge();
                    UserModel.getInstance().setSex(sex[which],age, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            initData();
                            adater.notifyList(userBaseInfoLists);
                        }
                    });
                }
            });
            builder.show();

        }else if (userBaseInfoLists.get(i -1).getSetName().equals("个性签名")){
            startActivity(new Intent(UserBaseInfoActivity.this,UserSignActivity.class));
            UserBaseInfoActivity.this.finish();
        }else if (userBaseInfoLists.get(i -1).getSetName().equals("年龄")){
            startActivity(new Intent(UserBaseInfoActivity.this,UserAgeActivity.class));
            UserBaseInfoActivity.this.finish();
        }
    }
}
