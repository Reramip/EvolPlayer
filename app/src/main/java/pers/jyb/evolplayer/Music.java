package pers.jyb.evolplayer;

import android.os.Parcel;

import java.util.Objects;

public class Music{
    private Long id;
    private String name;
    private String artist;
    private String album;
    private int duration;
    private String data;

    Music(){

    }

    protected Music(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        artist = in.readString();
        album = in.readString();
        duration = in.readInt();
        data = in.readString();
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

    public String getAlbum() {
        return album;
    }

    void setAlbum(String album) {
        this.album = album;
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
                Objects.equals(album, music.album) &&
                Objects.equals(data, music.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, artist, album, duration, data);
    }
}
