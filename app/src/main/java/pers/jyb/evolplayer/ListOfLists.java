package pers.jyb.evolplayer;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import pers.jyb.evolplayer.Utils;

public class ListOfLists {
    private static ListOfLists listOfLists;
    private List<MusicList> list;
    private ListOfLists(Context context){
        list=new ArrayList<>();
        MusicList listAllMusic=new MusicList("所有音乐");
        listAllMusic.setList(Utils.getMusicList(context));
        MusicList listHistory=new MusicList("播放历史",1);
        list.add(0,listAllMusic);
        list.add(1,listHistory);
    }

    public static ListOfLists get(Context context){
        if(listOfLists==null){
            listOfLists=new ListOfLists(context);
        }
        return listOfLists;
    }

    public void add(int position, MusicList musicList){
        list.add(position,musicList);
    }

    public void add(MusicList musicList){
        list.add(musicList);
    }

    public void remove(int position){
        list.remove(position);
    }

    public List<MusicList> getList(){
        return list;
    }

    public int size(){
        return list.size();
    }

    public void save(){

    }

    public void load(){

    }
}
