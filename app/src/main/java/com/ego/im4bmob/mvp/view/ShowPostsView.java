package com.ego.im4bmob.mvp.view;


import java.util.List;

import com.ego.im4bmob.mvp.bean.Post;


public interface ShowPostsView extends BmobView {
    void showPosts(List<Post> posts);
}
