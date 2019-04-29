package pers.jyb.evolplayer;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListOfLists implements Serializable {
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

    static ListOfLists get(Context context){
        if(listOfLists==null){
            listOfLists=new ListOfLists(context);
        }
        return listOfLists;
    }

    static boolean set(Object obj){
        if(obj instanceof ListOfLists) {
            listOfLists = (ListOfLists) obj;
            return true;
        }
        return false;
    }

    void add(MusicList musicList){
        list.add(musicList);
    }

    public List<MusicList> getList(){
        return list;
    }

    int size(){
        return list.size();
    }
}
