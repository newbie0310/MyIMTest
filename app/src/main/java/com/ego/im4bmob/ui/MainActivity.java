package com.ego.im4bmob.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ego.im4bmob.model.UserModel;
import com.ego.im4bmob.mvp.bean.Installation;
import com.ego.im4bmob.ui.image_selector.MultiImageSelector;
import com.ego.im4bmob.util.BmobUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.polaric.colorful.ColorPickerDialog;
import org.polaric.colorful.Colorful;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.ego.im4bmob.R;
import com.ego.im4bmob.base.BaseActivity;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.db.NewFriendManager;
import com.ego.im4bmob.event.RefreshEvent;
import com.ego.im4bmob.ui.fragment.ContactFragment;
import com.ego.im4bmob.ui.fragment.ConversationFragment;
import com.ego.im4bmob.ui.fragment.DiscoverFragment;
import com.ego.im4bmob.ui.fragment.SetFragment;
import com.ego.im4bmob.util.IMMLeaks;

import java.io.File;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Action1;

/**
 * @author :smile
 * @project:MainActivity
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.btn_conversation)
    TextView btn_conversation;
    @Bind(R.id.btn_set)
    TextView btn_set;
    @Bind(R.id.btn_contact)
    TextView btn_contact;

    @Bind(R.id.iv_conversation_tips)
    ImageView iv_conversation_tips;
    @Bind(R.id.iv_contact_tips)
    ImageView iv_contact_tips;
    @Bind(R.id.btn_discover)
    TextView mBtnDiscover;
    @Bind(R.id.main_bottom)
    LinearLayout mMainBottom;
    @Bind(R.id.line)
    LinearLayout mLine;
    @Bind(R.id.fragment_container)
    RelativeLayout mFragmentContainer;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    private TextView[] mTabs;
    private ConversationFragment conversationFragment;
    private SetFragment setFragment;
    private DiscoverFragment mDiscoverFragment;
    ContactFragment contactFragment;
    private int index;
    private int currentTabIndex;
    private View view;
    private CircleImageView mCivAvatar;
    private CheckBox mUserNameEdit;
    private EditText mTvUsername;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final User user = BmobUser.getCurrentUser(User.class);
        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
        if (!TextUtils.isEmpty(user.getObjectId()) &&
                BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        EventBus.getDefault().post(new RefreshEvent());
                        //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getUsername(), user.getAvatar()==null?null:user.getAvatar().getFileUrl()));
                    } else {
                        toast(e.getMessage());
                    }
                }
            });
            //TODO 连接：3.3、监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    toast(status.getMsg());
                    Logger.i(BmobIM.getInstance().getCurrentStatus().getMsg());
                }
            });
        }
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());

        mNavigationView.setCheckedItem(R.id.setting_img);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString()){
                    case "基本数据":
                        startActivity(new Intent(MainActivity.this,UserBaseInfoActivity.class));
                        break;
                    case "修改手机":
                        startActivity(new Intent(MainActivity.this, ChangePhoneActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case "修改密码":
                        startActivity(new Intent(MainActivity.this, ChangPwActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case "更改主题":
                        showDialog();
                        drawerLayout.closeDrawers();
                        break;
                    case "退出登陆":
                        modifyInstallationUser();
                        break;
                }
                return true;
            }
        });


    }


    @Override
    public void onClick(View view1) {
        switch (view1.getId()){
            case R.id.civ_avatar:
                select();
                break;
            case R.id.tv_username:
                break;
            case R.id.ck_user_name:
                checkBoxClick();
                break;
        }
    }

    private void checkBoxClick(){
        mUserNameEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            private InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(INPUT_METHOD_SERVICE);
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mUserNameEdit.isChecked()){
                    mTvUsername.setFocusable(true);
                    mTvUsername.setFocusableInTouchMode(true);
                    mTvUsername.requestFocus();
                    imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
                }else {
                    mTvUsername.setFocusable(false);
                    mTvUsername.setFocusableInTouchMode(false);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    if (User.getCurrentUser().getUsername().equals(mTvUsername.getText().toString())){
                        Toast.makeText(MainActivity.this,"未做任何修改",Toast.LENGTH_SHORT).show();
                    }else {
                        UserModel.getInstance().editUserName(mTvUsername.getText().toString());
                    }
                }
            }
        });
    }

    private void showDialog(){
        myDialog dialog = new myDialog(MainActivity.this);
        dialog.setOnColorSelectedListener(new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(Colorful.ThemeColor themeColor) {
                Colorful.config(MainActivity.this)
                        .primaryColor(themeColor)
                        .accentColor(themeColor)
                        .translucent(false)
                        .dark(false)
                        .apply();
            }
        });
        dialog.show();
    }



    @Override
    protected void initView() {
        super.initView();

        initTab();

        view = mNavigationView.inflateHeaderView(R.layout.nav_header);
        mCivAvatar = view.findViewById(R.id.civ_avatar);
        mCivAvatar.setOnClickListener(this);
        mUserNameEdit = view.findViewById(R.id.ck_user_name);
        mUserNameEdit.setOnClickListener(this);
        mTvUsername = view.findViewById(R.id.tv_username);
        String username = UserModel.getInstance().getCurrentUser().getUsername();
        mTvUsername.setText(TextUtils.isEmpty(username) ? "" : username);
        if (UserModel.getInstance().getCurrentUser().getAvatar() != null)
            Glide.with(this).load(UserModel.getInstance().getCurrentUser().getAvatar().getFileUrl()).into(mCivAvatar);
        else Glide.with(this).load(R.mipmap.icon_message_press).into(mCivAvatar);
    }

    private void initTab() {
        mTabs = new TextView[4];
        mTabs[0] = btn_conversation;
        mTabs[1] = btn_contact;
        mTabs[2] = mBtnDiscover;
        mTabs[3] = btn_set;

        conversationFragment = new ConversationFragment();
        setFragment = new SetFragment();
        mDiscoverFragment = new DiscoverFragment();
        contactFragment = new ContactFragment();

        onTabSelect(btn_conversation);
    }

    public void onTabSelect(View view) {

        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                switchFragment(mCurrentFragment, conversationFragment);
                break;
            case R.id.btn_contact:
                index = 1;
                switchFragment(mCurrentFragment, contactFragment);
                break;
            case R.id.btn_discover:
                index = 2;
                switchFragment(mCurrentFragment, mDiscoverFragment);
                break;
            case R.id.btn_set:
                index = 3;
                switchFragment(mCurrentFragment, setFragment);
                break;
        }
        onTabIndex(index);
    }

    private void onTabIndex(int index) {
        mTabs[currentTabIndex].setSelected(false);
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    private Fragment mCurrentFragment = null;

    /**
     * 切换fragment，避免切换一直new fragment对象
     *
     * @param from
     * @param to
     */
    public void switchFragment(Fragment from, Fragment to) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment == null) {
            transaction.add(R.id.fragment_container, to).commit();
            mCurrentFragment = to;
            return;
        }
        if (mCurrentFragment != to) {
            mCurrentFragment = to;
            if (!to.isAdded()) {
                if (from.isAdded()) {
                    transaction.hide(from).add(R.id.fragment_container, to).commit();
                } else {
                    transaction.add(R.id.fragment_container, to).commit();
                }
            } else {
                transaction.hide(from).show(to).commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次进来应用都检查会话和好友请求的情况
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.3、通知有在线消息接收
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.4、通知有离线消息接收
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.5、通知有自定义消息接收
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        checkRedPoint();
    }

    /**
     *
     */
    private void checkRedPoint() {
        //TODO 会话：4.4、获取全部会话的未读消息数量
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            iv_conversation_tips.setVisibility(View.VISIBLE);
        } else {
            iv_conversation_tips.setVisibility(View.GONE);
        }
        //TODO 好友管理：是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
            iv_contact_tips.setVisibility(View.VISIBLE);
        } else {
            iv_contact_tips.setVisibility(View.GONE);
        }
    }


    class myDialog extends ColorPickerDialog{

        public myDialog(Context context) {
            super(context);
        }
        @Override
        public void onItemClick(Colorful.ThemeColor color) {
            super.onItemClick(color);
            Intent intent = getIntent();
            overridePendingTransition(0,0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0,0);
            startActivity(intent);
            Toast.makeText(getContext(),"主题切换成功！",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 修改设备表的用户信息：先查询设备表中的数据，再修改数据中用户信息
     */
    private void modifyInstallationUser() {
        BmobQuery<Installation> bmobQuery = new BmobQuery<>();
        final String id = BmobInstallationManager.getInstallationId();
        bmobQuery.addWhereEqualTo("installationId", id);
        bmobQuery.findObjectsObservable(Installation.class)
                .subscribe(new Action1<List<Installation>>() {
                    @Override
                    public void call(List<Installation> installations) {

                        if (installations.size() > 0) {
                            Installation installation = installations.get(0);
                            User user = new User();
                            installation.setUser(user);
                            user.setObjectId("");
                            installation.updateObservable()
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {
                                            BmobUtils.toast(MainActivity.this,"退出成功！");
                                            /**
                                             * TODO 更新成功之后再退出
                                             */
                                            //TODO 连接：3.2、退出登录需要断开与IM服务器的连接
                                            BmobIM.getInstance().disConnect();
                                            BmobUser.logOut();
                                            startActivity(new Intent(MainActivity.this, LogActivity.class));
                                            MainActivity.this.finish();
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
    public void select() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            MultiImageSelector.create()
                    .showCamera(true) // show camera or not. true by default
                    .count(1) // max select image size, 9 by default. used width #.multi()
                    .multi() // multi mode, default mode;
                    .start(this, REQUEST_CODE_SELECT_IMAGE);
        } else {
            Toast.makeText(MainActivity.this,"你需要申请权限",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission_group.CAMERA},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }


    }



    //TODO 权限申请

    public static final int REQUEST_CODE_SELECT_IMAGE = 0;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_CAMERA = 3;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                final List<String> paths = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                for (String s : paths) {
                    Log.e("path", s);
                }

                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("正在上传头像……");
                progressDialog.show();
                Glide.with(MainActivity.this).load(paths.get(0)).into(mCivAvatar);
                final BmobFile bmobFile = new BmobFile(new File(paths.get(0)));

                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Logger.i("上传成功");

                            progressDialog.dismiss();

                            Toast.makeText(MainActivity.this, "上传头像成功", Toast.LENGTH_SHORT).show();
                            final User user = BmobUser.getCurrentUser(User.class);
                            if (user == null)
                                return;
                            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setTitle("正在更新头像……");
                            progressDialog.show();
                            user.setAvatar(bmobFile);
                            user.updateObservable().subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "更新头像成功", Toast.LENGTH_SHORT).show();
                                    Logger.d("更新头像成功");
                                    progressDialog.dismiss();
                                    EventBus.getDefault().post(new RefreshEvent());
                                    //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                                    BmobIM.getInstance().
                                            updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                                    user.getUsername(), user.getAvatar()==null?null:user.getAvatar().getFileUrl()));
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    progressDialog.dismiss();
                                    Logger.e("更新头像失败：" + throwable.getMessage());
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "上传头像失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Logger.e("上传失败：" + e.getMessage());
                        }

                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您已经同意了读取外置存储器权限", Toast.LENGTH_SHORT).show();

                    MultiImageSelector.create()
                            .showCamera(true) // show camera or not. true by default
                            .count(1) // max select image size, 9 by default. used width #.multi()
                            .multi() // multi mode, default mode;
                            .start(this, REQUEST_CODE_SELECT_IMAGE);

                } else {
                    Toast.makeText(this, "您已经拒绝了读取外置存储器权限", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您已经同意了写入外置存储器权限", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "您已经拒绝了写入外置存储器权限", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您已经同意了照相机权限", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "您已经拒绝了照相机权限", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

}
