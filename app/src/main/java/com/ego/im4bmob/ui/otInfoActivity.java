package com.ego.im4bmob.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class otInfoActivity extends ParentWithNaviActivity {

    @Bind(R.id.ot_list)
    ListView detailList;
    @Bind(R.id.tv_ot_content)
    TextView contnet;
    List<UserBaseInfo> userBaseInfoLists = new ArrayList<>();
    private UserDetailAdater adater;
    User user;
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ot_info);
        initNaviView();
        ButterKnife.bind(this);
        user = (User)getBundle().getSerializable("u");

        initData();
        adater = new UserDetailAdater(userBaseInfoLists,this);
        detailList.setAdapter(adater);
    }

    private void initData(){
        BmobQuery<User> query = new BmobQuery<User>();
        String name = user.getUsername();
        UserBaseInfo userName = new UserBaseInfo();
        userName.setSetName("用户名");
        userName.setSetDatile(name);
        userBaseInfoLists.add(userName);


        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user1, BmobException e) {
                String sex =user1.getSex();
                UserBaseInfo userSex = new UserBaseInfo();;
                userSex.setSetName("性别");
                userSex.setSetDatile(sex);
                userBaseInfoLists.add(userSex);
                adater.notifyDataSetChanged();
            }
        });



        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user1, BmobException e) {
                String age = user1.getAge() + "";
                UserBaseInfo userAge = new UserBaseInfo();
                userAge.setSetName("年龄");
                userAge.setSetDatile(age);
                userBaseInfoLists.add(userAge);
                adater.notifyDataSetChanged();
            }
        });
        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user1, BmobException e) {
                String phone = user1.getMobilePhoneNumber() ;
                UserBaseInfo otP = new UserBaseInfo();
                otP.setSetName("手机");
                otP.setSetDatile(phone);
                userBaseInfoLists.add(otP);
                adater.notifyDataSetChanged();
            }
        });


        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user1, BmobException e) {
                String sign = user1.getSign();
                contnet.setText(sign);
            }
        });



    }

    @Override
    protected String title() {
        return "查看资料";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private String querySign(String id){

        return sign;
    }
}
