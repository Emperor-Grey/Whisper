package com.firstcode.chatapplication.Model;

public class Users {
    private String Id;
    private String ImageUrl;
    private String Username;

    public Users() {
        //required for firebase
    }

    public Users(String id, String imageUrl, String username) {
        Id = id;
        ImageUrl = imageUrl;
        Username = username;
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
}
