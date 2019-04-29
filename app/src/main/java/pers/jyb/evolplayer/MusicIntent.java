package pers.jyb.evolplayer;

import android.content.Context;
import android.content.Intent;

class MusicIntent {
    private static MusicIntent musicIntent;

    private Intent intent;

    private MusicIntent(Context context){
        intent=new Intent(context ,MusicService.class);
    }

    Intent getIntent(){
        return intent;
    }

    static MusicIntent get(Context context){
        if(musicIntent==null){
            musicIntent=new MusicIntent(context);
        }
        return musicIntent;
    }
}
