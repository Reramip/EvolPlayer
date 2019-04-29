package pers.jyb.evolplayer;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class MusicsAdapter extends BaseQuickAdapter<Music, BaseViewHolder> {
    MusicsAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Music item) {
        viewHolder.setText(R.id.name_music_text_view, item.getName());
        viewHolder.setText(R.id.artist_text_view,item.getArtist());
    }

}