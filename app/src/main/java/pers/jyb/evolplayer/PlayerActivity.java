package pers.jyb.evolplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import static pers.jyb.evolplayer.MainActivity.musicList;

public class PlayerActivity extends AppCompatActivity {
    private final int LIST_LOOP=0;
    private final int SINGLE_LOOP=1;
    private final int RANDOM_PLAY=2;

    private Communication communication;
    private MusicIntent musicIntent;
    MusicService musicService;
    boolean isBound=false;

    private CurrentReceiver currentReceiver;
    private CompleteReceiver completeReceiver;
    private ModeReceiver modeReceiver;

    private int position;
    private int positionList;
    private int mode=LIST_LOOP;
    private Music music;
    private List<Music> list;

    private TextView nameTextView;
    private TextView artistTextView;
    private ImageView backImageView;
    private ImageView playImageView;
    private ImageView nextImageView;
    private ImageView prevImageView;
    private ImageView modeImageView;
    private ImageView addImageView;
    private SeekBar playSeekBar;
    private TextView nowTextView;
    private TextView durationTextView;

    private ListOfLists listOfLists;
    private MusicList listAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        listOfLists=ListOfLists.get(getApplicationContext());
        final Intent intent = getIntent();
        positionList=intent.getIntExtra("POSITION",0);
        position=intent.getIntExtra("CLICK_MUSIC",0);

        communication=Communication.get();

        list=musicList.getList();
        music=list.get(position);
        nowTextView= findViewById(R.id.text_view_duration_now);
        nowTextView.setText(timeFormat(0));
        durationTextView= findViewById(R.id.text_view_duration_all);
        durationTextView.setText(timeFormat(music.getDuration()));
        nameTextView= findViewById(R.id.text_view_player_music);
        nameTextView.setText(music.getName());
        artistTextView= findViewById(R.id.text_view_player_artist);
        artistTextView.setText(music.getArtist());
        backImageView= findViewById(R.id.image_view_player_back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playImageView= findViewById(R.id.image_view_player_play);
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

        nextImageView= findViewById(R.id.image_view_player_next);
        nextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        prevImageView= findViewById(R.id.image_view_player_prev);
        prevImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        modeImageView= findViewById(R.id.image_view_player_mode);
        modeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode();
            }
        });

        addImageView= findViewById(R.id.image_view_player_add);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(PlayerActivity.this, ListAddActivity.class);
                startActivityForResult(addIntent,0);
            }
        });
        nowTextView.setText(timeFormat(0));
        durationTextView.setText(timeFormat(music.getDuration()));

        playSeekBar= findViewById(R.id.seek_bar_play);
        playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int process = seekBar.getProgress();
                musicService.mediaPlayer.seekTo(process);
            }

        });

        if(!communication.isServiceStarted()) {
            musicIntent = MusicIntent.get(getApplicationContext());
            Intent serviceIntent = musicIntent.getIntent();
            serviceIntent.putExtra("POSITION_LIST", positionList);
            serviceIntent.putExtra("POSITION", position);
            startService(serviceIntent);
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
           communication.setServiceStarted(true);
        }else{
            musicIntent = MusicIntent.get(getApplicationContext());
            Intent serviceIntent = musicIntent.getIntent();
            bindService(serviceIntent,musicConnection,Context.BIND_AUTO_CREATE);
            isBound=true;
        }
        playImageView.setImageDrawable(getResources().getDrawable(R.drawable.pause));


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isBound){
            Intent serviceIntent = musicIntent.getIntent();
            bindService(serviceIntent,musicConnection,Context.BIND_AUTO_CREATE);
            isBound=true;
        }
        Intent openPlayerIntent=new Intent();
        if(communication.isServiceStarted()){
            openPlayerIntent.putExtra("POSITION_LIST_OPEN",positionList);
            openPlayerIntent.putExtra("POSITION_OPEN",position);
        }
        openPlayerIntent.setAction("OPEN_PLAYER");
        sendBroadcast(openPlayerIntent);

        currentReceiver=new CurrentReceiver();
        completeReceiver=new CompleteReceiver();
        modeReceiver=new ModeReceiver();
        IntentFilter currentIntentFilter=new IntentFilter();
        currentIntentFilter.addAction(Intent.ACTION_VIEW);
        IntentFilter completeIntentFilter=new IntentFilter();
        completeIntentFilter.addAction("COMPLETE");
        IntentFilter modeIntentFilter=new IntentFilter();
        modeIntentFilter.addAction("MODE");
        registerReceiver(currentReceiver, currentIntentFilter);
        registerReceiver(completeReceiver, completeIntentFilter);
        registerReceiver(modeReceiver, modeIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(currentReceiver);
        unregisterReceiver(completeReceiver);
        unregisterReceiver(modeReceiver);
        unbindService(musicConnection);
        isBound=false;
        super.onPause();
    }

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

    private void playNext(){
        musicService.playNext();
    }

    private void playPrev(){
        musicService.playPrev();
    }


    private static String timeFormat(int time){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
        return sdf.format(time);
    }

    private class CurrentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int currentDuration = intent.getIntExtra("CURRENT_DURATION",0);
            int totalDuration=intent.getIntExtra("TOTAL_DURATION",0);
            nowTextView.setText(timeFormat(currentDuration));
            durationTextView.setText(timeFormat(totalDuration));
            playSeekBar.setMax(totalDuration);
            playSeekBar.setProgress(currentDuration);
        }
    }

    private class CompleteReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            position=intent.getIntExtra("POSITION",0);
            music=list.get(position);
            updateUI();
            playImageView.setImageDrawable(getResources().getDrawable(R.drawable.pause));
        }
    }

    private class ModeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            mode=intent.getIntExtra("MODE",0);
            switch(mode){
                case LIST_LOOP:
                    modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.list_loop));
                    break;
                case SINGLE_LOOP:
                    modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.single_loop));
                    break;
                case RANDOM_PLAY:
                    modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.random_play));
                    break;
                default:
                    break;
            }
        }
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
