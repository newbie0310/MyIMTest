package com.ego.im4bmob.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ego.im4bmob.ui.RegActivity;
import com.ego.im4bmob.ui.SetUserInfoActivity;
import com.orhanobut.logger.Logger;

import java.util.List;

import com.ego.im4bmob.bean.Friend;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.model.i.QueryUserListener;
import com.ego.im4bmob.model.i.UpdateCacheListener;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * @author :smile
 * @project:UserModel
 */
public class UserModel extends BaseModel {

    public static boolean isTrue = true;

    private static UserModel ourInstance = new UserModel();

    public static UserModel getInstance() {
        return ourInstance;
    }

    private UserModel() {
    }

    /**
     * TODO 用户管理：2.1.1、使用用户名注册
     *
     * @param username
     * @param password
     * @param pwdagain
     * @param listener
     */
    public void register(String username, String password, String pwdagain, final LogInListener listener) {
        if (TextUtils.isEmpty(username)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写用户名"));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写密码"));
            return;
        }
        if (TextUtils.isEmpty(pwdagain)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写确认密码"));
            return;
        }
        if (!password.equals(pwdagain)) {
            listener.done(null, new BmobException(CODE_NULL, "两次输入的密码不一致，请重新输入"));
            return;
        }
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    listener.done(null, null);
                } else {
                    listener.done(null, e);
                }
            }
        });
    }

    /**
     * TODO 用户管理：2.2.2使用短信验证码注册
     * //     * @param phoneNumber
     *
     * @param username
     * @param password
     * @param pwdagain
     */
    public void registerByPhone(String phoneNumber, String username, String password, String pwdagain, String smsCode, final LogInListener listener) {
        if (TextUtils.isEmpty(username)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写用户名"));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写密码"));
            return;
        }
        if (TextUtils.isEmpty(pwdagain)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写确认密码"));
            return;
        }
        if (!password.equals(pwdagain)) {
            listener.done(null, new BmobException(CODE_NULL, "两次输入的密码不一致，请重新输入"));
            return;
        }
        final User user = new User();
        user.setMobilePhoneNumberVerified(true);
        user.setMobilePhoneNumber(phoneNumber);
        user.setUsername(username);
        user.setPassword(password);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    listener.done(null, null);
                } else {
                    listener.done(null, e);
                }
            }
        });
    }


    /**
     * TODO 用户管理：2.2、使用登录
     *
     * @param username
     * @param password
     * @param listener
     */
    public void login(String username, String password, final LogInListener listener) {
        if (TextUtils.isEmpty(username)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写用户名"));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写密码"));
            return;
        }
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    listener.done(getCurrentUser(), null);
                } else {
                    listener.done(user, e);
                }
            }
        });
    }

    public void loginByCode(String phone, String code, final LogInListener listener) {
        if (TextUtils.isEmpty(phone)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写用户名"));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            listener.done(null, new BmobException(CODE_NULL, "请填写密码"));
            return;
        }
        final User user = new User();
        user.loginBySMSCode(phone, code, new LogInListener<Object>() {
            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    listener.done(getCurrentUser(), null);
                } else {
                    listener.done(user, e);
                }
            }
        });
    }


    /**
     * TODO  用户管理：2.3、退出登录
     */
    public void logout() {
        BmobUser.logOut();
    }

    /**
     * TODO 用户管理：2.4、获取当前用户
     *
     * @return
     */
    public User getCurrentUser() {
        return BmobUser.getCurrentUser(User.class);
    }


    /**
     * TODO 用户管理：2.5、查询用户
     *
     * @param username
     * @param limit
     * @param listener
     */
    public void queryUsers(String username, final int limit, final FindListener<User> listener) {
        BmobQuery<User> query = new BmobQuery<>();
        //去掉当前用户
        try {
            BmobUser user = BmobUser.getCurrentUser();
            query.addWhereNotEqualTo("username", user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        query.addWhereEqualTo("username", username);
        query.setLimit(limit);
        query.order("-createdAt");
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        listener.done(list, e);
                    } else {
                        listener.done(list, new BmobException(CODE_NULL, "查无此人"));
                    }
                } else {
                    listener.done(list, e);
                }
            }
        });
    }

    /**
     * TODO 用户管理：2.6、查询指定用户信息
     *
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(
                new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {

                            if (list != null && list.size() > 0) {
                                listener.done(list.get(0), null);
                            } else {
                                listener.done(null, new BmobException(000, "查无此人"));
                            }
                        } else {
                            listener.done(null, e);
                        }
                    }
                });
    }

    /**
     * 更新用户资料和会话资料
     *
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event, final UpdateCacheListener listener) {
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo info = event.getFromUserInfo();
        final BmobIMMessage msg = event.getMessage();
        String username = info.getName();
        String avatar = info.getAvatar();
        String title = conversation.getConversationTitle();
        String icon = conversation.getConversationIcon();
        //SDK内部将新会话的会话标题用objectId表示，因此需要比对用户名和私聊会话标题，后续会根据会话类型进行判断
        if (!username.equals(title) || (avatar != null && !avatar.equals(icon))) {
            UserModel.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if (e == null) {
                        String name = s.getUsername();
                        String avatar = s.getAvatar() == null ? null : s.getAvatar().getFileUrl();
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        info.setName(name);
                        info.setAvatar(avatar);
                        //TODO 用户管理：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().updateUserInfo(info);
                        //TODO 会话：4.7、更新会话资料-如果消息是暂态消息，则不更新会话资料
                        if (!msg.isTransient()) {
                            BmobIM.getInstance().updateConversation(conversation);
                        }
                    } else {
                        Logger.e(e);
                    }
                    listener.done(null);
                }
            });
        } else {
            listener.done(null);
        }
    }


    //TODO 好友管理：9.12、添加好友
    public void agreeAddFriend(User friend, SaveListener<String> listener) {
        Friend f = new Friend();
        User user = BmobUser.getCurrentUser(User.class);
        f.setUser(user);
        f.setFriendUser(friend);
        f.save(listener);
    }

    /**
     * 查询好友
     *
     * @param listener
     */
    //TODO 好友管理：9.2、查询好友
    public void queryFriends(final FindListener<Friend> listener) {
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        listener.done(list, e);
                    } else {
                        listener.done(list, new BmobException(0, "暂无联系人"));
                    }
                } else {
                    listener.done(list, e);
                }
            }
        });
    }

    /**
     * 删除好友
     *
     * @param f
     * @param listener
     */
    //TODO 好友管理：9.3、删除好友
    public void deleteFriend(Friend f, UpdateListener listener) {
        Friend friend = new Friend();
        friend.delete(f.getObjectId(), listener);
    }

    /**
     * TODO 信息编辑:10.1 修改用户名
     *
     * @param newName
     */
    public void editUserName(String newName) {
        Log.i("editUserName", newName);
        User user = new User();
        user.setUsername(newName);
        BmobUser bmobUser = BmobUser.getCurrentUser();
        user.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "更新成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "更新用户名失败！" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * TODO 信息编辑:10.2 发送验证码
     *
     * @param phone
     */
    public void sendSms(String phone) {
        BmobSMS.requestSMSCode(phone, "MySmsCode", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "失败：" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * TODO 信息编辑：10.3 验证短信验证码
     *
     * @param phone
     * @param code
     * @return
     */
    public boolean checkSms(String phone, String code) {

        BmobSMS.verifySmsCode(phone.toString(), code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    isTrue = true;
                    Log.i("btn_change_ok", "验证码正确");
                } else {
                    isTrue = false;
                    Log.i("checkSmsCode", e + "");
                    Toast.makeText(getContext(), "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return isTrue;
    }

    /**
     * 修改绑定手机
     *
     * @param newPhone
     */
    public void changePhoneNumber(String newPhone) {
        User user = new User();
        user.setMobilePhoneNumberVerified(true);
        user.setMobilePhoneNumber(newPhone);
        BmobUser bmobUser = BmobUser.getCurrentUser();
        user.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "更新成功！", Toast.LENGTH_SHORT).show();
                    isTrue = true;
                } else {
                    Toast.makeText(getContext(), "更新手机号码失败！" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 修改密码
     *
     * @param newPassword
     */
    public void changePassword(String newPassword) {
        User user = new User();
        user.setPassword(newPassword);
        BmobUser bmobUser = BmobUser.getCurrentUser();
        user.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "更新成功！", Toast.LENGTH_SHORT).show();
                    isTrue = true;
                } else {
                    isTrue = false;
                    Toast.makeText(getContext(), "更新密码失败！" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 设置性别
     * @param sex
     */
    public void setSex(String sex, int age,final UpdateListener listener){
        User user = new User();
        user.setSex(sex);
        user.setAge(age);
        BmobUser bmobUser = BmobUser.getCurrentUser();
        user.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    listener.done(e);
                    Toast.makeText(getContext(), "设置成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "设置失败！" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
