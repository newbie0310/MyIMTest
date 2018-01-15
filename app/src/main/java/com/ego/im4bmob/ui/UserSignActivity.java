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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UserSignActivity extends ParentWithNaviActivity {

    @Bind(R.id.edt_sign_content)
    EditText mSignContent;
    @Bind(R.id.btn_sign)
    Button mSgin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign);
        ButterKnife.bind(this);
        initNaviView();
    }

    @OnClick(R.id.btn_sign)
    public void Click(View view){
        User user = new User();
        user.setSign(mSignContent.getText().toString());
        BmobUser bmobUser = BmobUser.getCurrentUser();
        user.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(UserSignActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserSignActivity.this,UserBaseInfoActivity.class));
                    UserSignActivity.this.finish();
                } else {
                    Toast.makeText(UserSignActivity.this, "设置失败！" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected String title() {
        return "个性签名";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
