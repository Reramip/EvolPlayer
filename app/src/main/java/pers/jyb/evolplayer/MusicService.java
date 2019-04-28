package pers.jyb.evolplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private OpenPlayerReceiver openPlayerReceiver;
    private Communication communication;

    int mode;
    int position=-1;
    int positionList=-1;
    ListOfLists listOfLists;
    MusicList listHistory;
    Music music;
    List<Music> list;
    int musicNumber;
    MediaPlayer mediaPlayer = new MediaPlayer();

    private final IBinder binder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        communication=Communication.get();

        listOfLists=ListOfLists.get(getApplicationContext());
        listHistory=listOfLists.getList().get(1);

        mode=LIST_LOOP;

        openPlayerReceiver=new OpenPlayerReceiver();
        IntentFilter openPLayerFilter=new IntentFilter();
        openPLayerFilter.addAction("OPEN_PLAYER");
        registerReceiver(openPlayerReceiver,openPLayerFilter);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(openPlayerReceiver);
        stopMediaPlayer();
        communication.setServiceStarted(false);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
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

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent currentIntent = new Intent();
                currentIntent.setAction(Intent.ACTION_VIEW);
                currentIntent.putExtra("CURRENT_DURATION",mediaPlayer.getCurrentPosition());
                currentIntent.putExtra("TOTAL_DURATION",mediaPlayer.getDuration());
                sendBroadcast(currentIntent);
            }
        }, 0, 1000);

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
        mediaPlayer.reset();
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
                    if (mode == SINGLE_LOOP) {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    } else {
                        playNext();
                        Intent completeIntent=new Intent();
                        completeIntent.putExtra("POSITION",position);
                        completeIntent.setAction("COMPLETE");
                        sendBroadcast(completeIntent);
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
        if (mode == RANDOM_PLAY) {
            position = randomChangeMusic(position);
        } else {
            position = (position + 1) % list.size();
        }
        music=list.get(position);
        setMediaPlayer();
        mediaPlayer.start();
        updateHistory();
    }

    public void playPrev(){
        mediaPlayer.reset();
        mediaPlayer=new MediaPlayer();
        if (mode == RANDOM_PLAY) {
            position = randomChangeMusic(position);
        } else {
            position = position - 1;
            if (position < 0) {
                position = list.size() - 1;
            }
        }
        music=list.get(position);
        setMediaPlayer();
        mediaPlayer.start();
        updateHistory();
    }

    public int getPosition() {
        return position;
    }

    private class OpenPlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int positionOpen=intent.getIntExtra("POSITION_OPEN",0);
            int positionListOpen=intent.getIntExtra("POSITION_LIST_OPEN",0);
            if(positionListOpen!=positionList||positionOpen!=position){
                positionList=positionListOpen;
                position=positionOpen;
                setMusic();
                if(music!=null){
                    setMediaPlayer();
                    mediaPlayer.start();
                    updateHistory();
                }
            }
            Intent playerIntent=new Intent();
            playerIntent.putExtra("MODE",mode);
            playerIntent.setAction("MODE");
            sendBroadcast(playerIntent);
        }
    }

    class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
