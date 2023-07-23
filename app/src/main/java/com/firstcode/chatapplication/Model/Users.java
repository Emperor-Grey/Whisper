package com.firstcode.chatapplication.Model;

public class Users {
    private String Id;
    private String ImageUrl;
    private String Username;
    private String Status;

    public Users() {
        //required for firebase
    }

    public Users(String id, String imageUrl, String username, String status) {
        Id = id;
        ImageUrl = imageUrl;
        Username = username;
        Status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
