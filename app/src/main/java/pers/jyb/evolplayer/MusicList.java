package pers.jyb.evolplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MusicList implements Serializable {
    private String name;
    private List<Music> list;

    public MusicList() {
        name="New List";
        list=new ArrayList<>();
    }

    MusicList(String name){
        this.name=name;
        list=new ArrayList<>();
    }

    MusicList(String name, int type){
        this.name=name;
        switch(type){
            case 0:
                list=new ArrayList<>();
                break;
            case 1:
                list=new LinkedList<>();
                break;
            default:break;
        }
    }

    String getName() {
        return name;
    }

    public List<Music> getList() {
        return list;
    }

    public void setList(List<Music> musicList){
        list=musicList;
    }

    void add(Music music){
        list.add(music);
    }

    void remove(Music music){
        list.remove(music);
    }

    String getNumString(){
        return " "+list.size()+" 首音乐";
    }

    boolean contains(Music music){
        return list.contains(music);
    }

    void addFirst(Music music){
        if(list instanceof LinkedList) {
            ((LinkedList<Music>) list).addFirst(music);
        }
    }
}
