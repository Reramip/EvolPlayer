package pers.jyb.evolplayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

class Utils {

    private static final String[] ARGS={
            MediaStore.Audio.Media._ID,          // 0 Long
            MediaStore.Audio.Media.DISPLAY_NAME, // 1 String
            MediaStore.Audio.Media.ARTIST,       // 2 String
            MediaStore.Audio.Media.DURATION,     // 3 int
            MediaStore.Audio.Media.DATA          // 4 String
    };

    static List<Music> getMusicList(Context context) {
        List<Music> musicList=new ArrayList<>();
        Music music;
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                ARGS, null, null, null);
        if (cursor == null) {
            return null;
        }
        while(cursor.moveToNext()){
            music=new Music();
            music.setId(cursor.getLong(0));
            music.setName(cursor.getString(1));
            music.setArtist(cursor.getString(2));
            music.setDuration(cursor.getInt(3));
            music.setData(cursor.getString(4));
            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }
}
