package com.ego.im4bmob.ui;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ego.im4bmob.R;
import com.ego.im4bmob.base.BaseActivity;
import com.ego.im4bmob.base.ParentWithNaviActivity;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.model.UserModel;
import com.ego.im4bmob.mvp.bean.Installation;
import com.ego.im4bmob.util.BmobUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import rx.functions.Action1;

public class LoginByCodeActivity extends ParentWithNaviActivity {

    @Bind(R.id.v_top)
    View mVTop;
    @Bind(R.id.tv_left)
    ImageView mTvLeft;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    @Bind(R.id.et_login)
    EditText mLoginPhone;
    @Bind(R.id.et_login_sms)
    EditText mLoginSms;
    @Bind(R.id.login_send_smsCode)
    Button mSendSms;
    @Bind(R.id.btn_login_ok)
    Button mLoginOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_code);
        ButterKnife.bind(this);
        initNaviView();
    }

    @Override
    protected String title() {
        return "登陆";
    }


    @OnClick({R.id.login_send_smsCode, R.id.btn_login_ok})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_send_smsCode:
                UserModel.getInstance().sendSms(mLoginPhone.getText().toString());
                break;
            case  R.id.btn_login_ok:
                    UserModel.getInstance().loginByCode(mLoginPhone.getText().toString(), mLoginSms.getText().toString(), new LogInListener() {
                        @Override
                        public void done(Object o, BmobException e) {
                            modifyInstallationUser((User)o);
                            Explode explode = new Explode();
                            explode.setDuration(500);

                            getWindow().setExitTransition(explode);
                            getWindow().setEnterTransition(explode);
                            ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginByCodeActivity.this);
                            Intent i2 = new Intent(LoginByCodeActivity.this, MainActivity.class);
                            startActivity(i2, oc2.toBundle());
                            LoginByCodeActivity.this.finish();
                        }

                        @Override
                        public void done(Object o, Object o2) {

                        }
                    });

                break;
        }
    }

    private void modifyInstallationUser(final User user) {
        BmobQuery<Installation> bmobQuery = new BmobQuery<>();
        final String id = BmobInstallationManager.getInstallationId();
        bmobQuery.addWhereEqualTo("installationId", id);
        bmobQuery.findObjectsObservable(Installation.class)
                .subscribe(new Action1<List<Installation>>() {
                    @Override
                    public void call(List<Installation> installations) {

                        if (installations.size() > 0) {
                            Installation installation = installations.get(0);
                            installation.setUser(user);
                            installation.updateObservable()
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {
                                            BmobUtils.toast(LoginByCodeActivity.this, "登陆成功！");
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Logger.e("更新设备用户信息失败：" + throwable.getMessage());
                                        }
                                    });

                        } else {
                            Logger.e("后台不存在此设备Id的数据，请确认此设备Id是否正确！\n" + id);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e("查询设备数据失败：" + throwable.getMessage());
                    }
                });
    }
}
