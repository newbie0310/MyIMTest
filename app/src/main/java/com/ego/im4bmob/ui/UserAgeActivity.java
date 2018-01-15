package com.ego.im4bmob.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ego.im4bmob.R;
import com.ego.im4bmob.base.ParentWithNaviActivity;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.model.UserModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UserAgeActivity extends ParentWithNaviActivity implements View.OnClickListener{

    EditText mSignContent;
    Button mSgin;
    EditText et_age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_age);
        mSignContent = findViewById(R.id.edt_age_content);
        mSgin = findViewById(R.id.btn_age);
        et_age = findViewById(R.id.edt_age_content);
        mSgin.setOnClickListener(this);
        initNaviView();
    }

    @Override
    public void onClick(View view){
        int age = 0;
        if (et_age.getText().toString() == null){
            Toast.makeText(UserAgeActivity.this,"请输入数据",Toast.LENGTH_SHORT).show();
        }else {
            age = Integer.parseInt(mSignContent.getText().toString());
        }
        if (age > 100 || age <= 0){
            Toast.makeText(UserAgeActivity.this,"请输入0-100以内的数据",Toast.LENGTH_SHORT).show();
        }else {
            User user = new User();
            user.setAge(age);
            BmobUser bmobUser = BmobUser.getCurrentUser();
            user.update(bmobUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(UserAgeActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserAgeActivity.this,UserBaseInfoActivity.class));
                        UserAgeActivity.this.finish();
                    } else {
                        Toast.makeText(UserAgeActivity.this, "设置失败！" + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected String title() {
        return "修改年龄";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


}
