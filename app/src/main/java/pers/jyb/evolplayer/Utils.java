package pers.jyb.evolplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String[] ARGS={
            MediaStore.Audio.Media._ID,          // 0 Long
            MediaStore.Audio.Media.DISPLAY_NAME, // 1 String
            MediaStore.Audio.Media.ARTIST,       // 2 String
            MediaStore.Audio.Media.ALBUM,        // 3 String
            MediaStore.Audio.Media.DURATION,     // 4 int
            MediaStore.Audio.Media.DATA          // 5 String
    };

    public static List<Music> getMusicList(Context context) {
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
            music.setAlbum(cursor.getString(3));
            music.setDuration(cursor.getInt(4));
            music.setData(cursor.getString(5));
            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }
}
