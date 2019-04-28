package pers.jyb.evolplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static pers.jyb.evolplayer.MainActivity.musicList;


public class MusicService extends Service {
    private final int LIST_LOOP=0;
    private final int SINGLE_LOOP=1;
    private final int RANDOM_PLAY=2;

    private int positionPrev;
    private int mode=LIST_LOOP;

    public int position=-1;
    public int positionList=-1;
    public ListOfLists listOfLists;
    public MusicList listHistory;
    public Music music;
    public List<Music> list;
    public int musicNumber;
    public MediaPlayer mediaPlayer = new MediaPlayer();

    private final IBinder binder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        listOfLists=ListOfLists.get(getApplicationContext());
        listHistory=listOfLists.getList().get(1);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }
        positionList=intent.getIntExtra("POSITION_LIST",-1);
        position=intent.getIntExtra("POSITION",-1);
        setMusic();
        if(music!=null){
            setMediaPlayer();
            mediaPlayer.start();
            updateHistory();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void setMusic() {
        list=musicList.getList();
        musicNumber=list.size();
        if(position!=-1) {
            music = list.get(position);
        }
    }

    public void stopMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }


    public void setMediaPlayer(){
        if(music!=null) {
            Uri uri = Uri.parse(music.getData());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switch (mode) {
                        case SINGLE_LOOP:
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                            break;
                        default:
                            playNext();
                            break;
                    }
                }
            });
        }
    }

    public int randomChangeMusic(int now){
        Random random=new Random();
        int result=now;
        if(musicNumber!=1) {
            while (result == now) {
                result = random.nextInt(list.size());
            }
        }
        return result;
    }

    public void changeMode(int mode){
        this.mode=mode;
    }

    public void updateHistory(){
        if(listHistory.contains(music)) {
            listHistory.remove(music);
        }
        listHistory.addFirst(music);
    }

    public void playNext(){
        mediaPlayer.reset();
        mediaPlayer=new MediaPlayer();
        positionPrev=position;
        switch(mode){
            case RANDOM_PLAY:
                position=randomChangeMusic(position);
                break;
            default:
                position=(position+1)%list.size();
                break;
        }
        music=list.get(position);
        setMediaPlayer();
        mediaPlayer.start();
        updateHistory();
    }

    public void playPrev(){
        mediaPlayer.reset();
        mediaPlayer=new MediaPlayer();
        switch(mode){
            case RANDOM_PLAY:
                position=randomChangeMusic(position);
                break;
            default:
                position=position-1;
                if(position<0){
                    position=list.size()-1;
                }
                break;
        }
        music=list.get(position);
        setMediaPlayer();
        mediaPlayer.start();
        updateHistory();
    }

    public int getPosition() {
        return position;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
