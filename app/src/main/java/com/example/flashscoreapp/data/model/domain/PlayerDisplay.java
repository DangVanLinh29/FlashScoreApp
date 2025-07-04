package com.example.flashscoreapp.data.model.domain;

public class PlayerDisplay {
    private final int id;
    private final String name;
    private final Integer number;
    private final String position;
    private final String photoUrl;

    public PlayerDisplay(int id, String name, Integer number, String position, String photoUrl) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.position = position;
        this.photoUrl = photoUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Integer getNumber() { return number; }
    public String getPosition() { return position; }
    public String getPhotoUrl() { return photoUrl; }
}