package com.ego.im4bmob.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ego.im4bmob.R;
import com.ego.im4bmob.base.ParentWithNaviActivity;
import com.ego.im4bmob.model.UserModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePhoneActivity extends ParentWithNaviActivity {

    @Bind(R.id.v_top)
    View mVTop;
    @Bind(R.id.tv_left)
    ImageView mTvLeft;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    @Bind(R.id.et_change_phone)
    EditText mChangePhone;
    @Bind(R.id.et_change_sms)
    EditText mChangeSms;
    @Bind(R.id.et_change_new_phone)
    EditText mNewPhone;
    @Bind(R.id.change_send_smsCode)
    Button mSendSms;
    @Bind(R.id.btn_change_ok)
    Button mChangeOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        ButterKnife.bind(this);
        initNaviView();
    }

    @OnClick({R.id.change_send_smsCode, R.id.btn_change_ok})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_send_smsCode:
                UserModel.getInstance().sendSms(mChangePhone.getText().toString());
                break;
            case R.id.btn_change_ok:
                Toast.makeText(this,"正在更新手机号码，请稍后...",Toast.LENGTH_SHORT).show();
                UserModel.getInstance().checkSms(mChangePhone.getText().toString(),mChangeSms.getText().toString());
                if (UserModel.isTrue){
                    UserModel.getInstance().changePhoneNumber(mNewPhone.getText().toString());
                    this.finish();
                }
                break;
        }
    }

    @Override
    protected String title() {
        return "修改手机";
    }
}
