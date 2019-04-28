package pers.jyb.evolplayer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static pers.jyb.evolplayer.MainActivity.musicList;

public class PlayerActivity extends AppCompatActivity {
    private final int LIST_LOOP=0;
    private final int SINGLE_LOOP=1;
    private final int RANDOM_PLAY=2;

    private MusicIntent musicIntent;
    MusicService musicService;
    boolean isBound=false;

    private int positionPrev;
    private int position;
    private int positionList;
    private int mode=LIST_LOOP;
    private static Music music;
    private List<Music> list;
    private int musicNumber;

    private TextView nameTextView;
    private TextView artistTextView;
    private ImageView backImageView;
    private ImageView playImageView;
    private ImageView nextImageView;
    private ImageView prevImageView;
    private ImageView modeImageView;
    private ImageView addImageView;
    private ImageView masterImageView;
    private SeekBar playSeekBar;
    private TextView nowTextView;
    private TextView durationTextView;

    private ListOfLists listOfLists;
    private MusicList listHistory;
    private MusicList listAdded;

    @SuppressLint("Assert")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        listOfLists=ListOfLists.get(getApplicationContext());
        listHistory=listOfLists.getList().get(1);
        final Intent intent = getIntent();
        positionList=intent.getIntExtra("POSITION",-1);
        assert positionList!=-1;
        position=intent.getIntExtra("CLICK_MUSIC",-1);
        assert position!=-1;


        list=musicList.getList();
        musicNumber=list.size();
        music=list.get(position);
        nowTextView=(TextView)findViewById(R.id.text_view_duration_now);
        nowTextView.setText(timeFormat(0));
        durationTextView=(TextView)findViewById(R.id.text_view_duration_all);
        durationTextView.setText(timeFormat(music.getDuration()));
        nameTextView=(TextView) findViewById(R.id.text_view_player_music);
        nameTextView.setText(music.getName());
        artistTextView=(TextView)findViewById(R.id.text_view_player_artist);
        artistTextView.setText(music.getArtist());
        backImageView=(ImageView)findViewById(R.id.image_view_player_back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playImageView=(ImageView)findViewById(R.id.image_view_player_play);
        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!musicService.mediaPlayer.isPlaying()) {
                    musicService.mediaPlayer.start();
                    playImageView.setImageDrawable(getResources().getDrawable(R.drawable.pause));

                }else{
                    musicService.mediaPlayer.pause();
                    playImageView.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }

            }
        });

        nextImageView=(ImageView)findViewById(R.id.image_view_player_next);
        nextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        prevImageView=(ImageView)findViewById(R.id.image_view_player_prev);
        prevImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        modeImageView=(ImageView)findViewById(R.id.image_view_player_mode);
        modeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode();
            }
        });

        masterImageView=(ImageView)findViewById(R.id.image_view_player_master);
        masterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: effects
            }
        });

        addImageView=(ImageView)findViewById(R.id.image_view_player_add);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(PlayerActivity.this, ListAddActivity.class);
                startActivityForResult(addIntent,0);
            }
        });
        nowTextView.setText(timeFormat(0));
        durationTextView.setText(timeFormat(music.getDuration()));

        playSeekBar=(SeekBar)findViewById(R.id.seek_bar_play);
        playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.mediaPlayer.seekTo(playSeekBar.getProgress());
            }

        });

        musicIntent=MusicIntent.get(getApplicationContext());
        Intent serviceIntent=musicIntent.getIntent();
        serviceIntent.putExtra("POSITION_LIST",positionList);
        serviceIntent.putExtra("POSITION",position);
        startService(serviceIntent);
        bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        playImageView.setImageDrawable(getResources().getDrawable(R.drawable.pause));


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }

    @SuppressLint("Assert")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            assert data != null;
            int addListPosition=data.getIntExtra("ADD_LIST",-1)+2;
            if(addListPosition==-1){
                return;
            }
            listAdded=listOfLists.getList().get(addListPosition);
            if(!listAdded.contains(music)) {
                listAdded.add(music);
            }else{
                Toast.makeText(this, "已在列表中", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(){
        nameTextView.setText(music.getName());
        artistTextView.setText(music.getArtist());
        if(musicService.mediaPlayer.isPlaying()) {
            playImageView.setImageDrawable(getResources().getDrawable(R.drawable.pause));
        }else{
            playImageView.setImageDrawable(getResources().getDrawable(R.drawable.play));
        }
    }

    private void changeMode(){
        switch(mode){
            case LIST_LOOP:
                mode=SINGLE_LOOP;
                musicService.changeMode(SINGLE_LOOP);
                modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.single_loop));
                break;
            case SINGLE_LOOP:
                mode=RANDOM_PLAY;
                musicService.changeMode(RANDOM_PLAY);
                modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.random_play));
                break;
            case RANDOM_PLAY:
                mode=LIST_LOOP;
                musicService.changeMode(LIST_LOOP);
                modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.list_loop));
                break;
            default:
                break;
        }
    }

    public void playNext(){
        musicService.playNext();
        music=musicService.music;
        updateUI();
    }

    public void playPrev(){
        musicService.playPrev();
        music=musicService.music;
        updateUI();
    }

    private static String timeFormat(long time){
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
        return sdf.format(time);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            isBound = false;
        }
    };
}
