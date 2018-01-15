package com.ego.im4bmob.event;


import com.ego.im4bmob.mvp.bean.Comment;


public class DeleteCommentEvent {

    Comment comment;

    public DeleteCommentEvent(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
