package com.ego.im4bmob.bean;

import java.io.File;

import com.ego.im4bmob.db.NewFriend;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author :smile
 * @project:User
 */
public class User extends BmobUser {

    private BmobFile avatar;
    private int age;
    private String sex;
    private String sign;

    public User() {
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public User(NewFriend friend) {
        setObjectId(friend.getUid());
        setUsername(friend.getName());
        setAvatar(new BmobFile(new File(friend.getAvatar())));
    }


    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }
}
