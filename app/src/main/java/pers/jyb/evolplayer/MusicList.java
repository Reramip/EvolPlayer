package pers.jyb.evolplayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MusicList {
    private String name;
    private List<Music> list;

    public MusicList() {
        name="New List";
        list=new ArrayList<>();
    }

    public MusicList(String name){
        this.name=name;
        list=new ArrayList<>();
    }

    public MusicList(String name, int type){
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Music> getList() {
        return list;
    }

    public void setList(List<Music> musicList){
        list=musicList;
    }

    public void add(Music music){
        list.add(music);
    }

    public void add(int index, Music music){
        list.add(index,music);
    }

    public void remove(Music music){
        list.remove(music);
    }

    public String getNumString(){
        return " "+list.size()+" 首音乐";
    }

    public boolean contains(Music music){
        return list.contains(music);
    }

    public void addFirst(Music music){
        if(list instanceof LinkedList) {
            ((LinkedList<Music>) list).addFirst(music);
        }
    }
}
