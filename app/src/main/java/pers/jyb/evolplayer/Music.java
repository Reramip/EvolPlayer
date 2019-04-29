package pers.jyb.evolplayer;

import java.io.Serializable;
import java.util.Objects;

public class Music implements Serializable {
    private Long id;
    private String name;
    private String artist;
    private int duration;
    private String data;

    Music(){

    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getArtist() {
        return artist;
    }

    void setArtist(String artist) {
        this.artist = artist;
    }

    int getDuration() {
        return duration;
    }

    void setDuration(int duration) {
        this.duration = duration;
    }

    String getData() {
        return data;
    }

    void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Music)) return false;
        Music music = (Music) o;
        return duration == music.duration &&
                Objects.equals(id, music.id) &&
                Objects.equals(name, music.name) &&
                Objects.equals(artist, music.artist) &&
                Objects.equals(data, music.data);
    }
}
