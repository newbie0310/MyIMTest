package com.ego.im4bmob.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ego.im4bmob.R;
import com.ego.im4bmob.event.FinishEvent;
import com.ego.im4bmob.model.UserModel;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.b.V;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.ego.im4bmob.Config.APP_KEY;

public class RegActivity extends AppCompatActivity {


    @Bind(R.id.et_username)
    EditText mEtUsername;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.et_repeatpassword)
    EditText mEtRepeatpassword;
    @Bind(R.id.bt_go)
    Button mBtGo;
    @Bind(R.id.cv_add)
    CardView mmCvAdd;
    @Bind(R.id.fab)
    ImageView mFab;
    @Bind(R.id.reg_ll_send_sms)
    LinearLayout mRegSendSms;
    @Bind(R.id.reg_ll_ok)
    LinearLayout mRegOk;
    @Bind(R.id.reg_bt_go)
    Button mRegBtnGo;

    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.et_sms)
    EditText mSmsCode;
    @Bind(R.id.reg_send_smsCode)
    Button mSendSmsCode;

    private boolean SmsIsTrue = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegActivity.this.finish();
            }
        });
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                mmCvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(mmCvAdd, mmCvAdd.getWidth() / 2, 0, mFab.getWidth() / 2, mmCvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mmCvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    //退出动画
//    public void animateRevealClose() {
//        Animator mAnimator = ViewAnimationUtils.createCircularReveal(mmCvAdd, mmCvAdd.getWidth() / 2, 0, mmCvAdd.getHeight(), mFab.getWidth() / 2);
//        mAnimator.setDuration(500);
//        mAnimator.setInterpolator(new AccelerateInterpolator());
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mmCvAdd.setVisibility(View.INVISIBLE);
//                super.onAnimationEnd(animation);
//                mFab.setImageResource(R.drawable.plus);
//                RegActivity.super.onBackPressed();
//            }
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                super.onAnimationStart(animation);
//            }
//        });
//        mAnimator.start();
//    }

//    @Override
//    public void onBackPressed() {
//        animateRevealClose();
//    }


    @OnClick({R.id.bt_go,R.id.reg_bt_go,R.id.reg_send_smsCode})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.bt_go:
                if(SmsIsTrue){
                    UserModel.getInstance().registerByPhone(mEtPhone.getText().toString(),mEtUsername.getText().toString(), mEtPassword.getText().toString(),
                            mEtRepeatpassword.getText().toString(), mSmsCode.getText().toString(), new LogInListener() {
                                @Override
                                public void done(Object o, BmobException e) {
                                    if (e == null) {
                                        EventBus.getDefault().post(new FinishEvent());
                                        startActivity(new Intent(RegActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Logger.e(e.getMessage() + "(" + e.getErrorCode() + ")");
                                        Toast.makeText(RegActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
//                else {
//                    UserModel.getInstance().register(mEtUsername.getText().toString(), mEtPassword.getText().toString(), mEtRepeatpassword.getText().toString(), new LogInListener() {
//                        @Override
//                        public void done(Object o, BmobException e) {
//                            if (e == null) {
//                                EventBus.getDefault().post(new FinishEvent());
//                                startActivity(new Intent(RegActivity.this, MainActivity.class));
//                                finish();
//                            } else {
//                                Logger.e(e.getMessage() + "(" + e.getErrorCode() + ")");
//                                Toast.makeText(RegActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
                break;
            case R.id.reg_bt_go:
                if (checkSmsCode()){
                    mRegSendSms.setVisibility(View.GONE);
                    mRegOk.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.reg_send_smsCode:
                BmobSMS.requestSMSCode(mEtPhone.getText().toString(), "重置密码模板", new QueryListener<Integer>() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                        if (e == null){
                            Toast.makeText(RegActivity.this,"验证码已发送，请注意查收" ,Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(RegActivity.this,"失败：" + e,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }

    }

    /**
     * 验证短信
     */
    private boolean checkSmsCode(){
        BmobSMS.verifySmsCode(mEtPhone.getText().toString(),mSmsCode.getText().toString(), new UpdateListener(){

            @Override
            public void done(BmobException e) {
                if (e == null){
                    SmsIsTrue = true;
                }else {
                    SmsIsTrue = false;
                    Log.i("checkSmsCode",e + "");
                    Toast.makeText(RegActivity.this,"请输入正确的验证码" + e,Toast.LENGTH_SHORT).show();
                }
            }
        });
        return SmsIsTrue;
    }
}
