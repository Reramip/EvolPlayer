package pers.jyb.evolplayer;

import android.content.Context;
import android.content.Intent;

public class MusicIntent {
    private static MusicIntent musicIntent;

    private Intent intent;

    private MusicIntent(Context context){
        intent=new Intent(context ,MusicService.class);
    }

    public Intent getIntent(){
        return intent;
    }

    public static MusicIntent get(Context context){
        if(musicIntent==null){
            musicIntent=new MusicIntent(context);
        }
        return musicIntent;
    }
}
