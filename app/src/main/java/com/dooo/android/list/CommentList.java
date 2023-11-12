package com.dooo.android.list;

public class CommentList {
    private int userID;
    private String UserName;
    private String comment;

    public CommentList(int userID, String userName, String comment) {
        this.userID = userID;
        UserName = userName;
        this.comment = comment;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
