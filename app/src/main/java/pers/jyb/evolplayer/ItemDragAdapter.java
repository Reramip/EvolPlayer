package pers.jyb.evolplayer;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class ItemDragAdapter extends BaseItemDraggableAdapter<Music, BaseViewHolder> {
    ItemDragAdapter(List<Music> music) {
        super(R.layout.item_draggable_view, music);
    }

    @Override
    protected void convert(BaseViewHolder helper, Music item) {
        helper.setText(R.id.name_music_text_view_draggable, item.getName());
        helper.setText(R.id.artist_text_view_draggable,item.getArtist());
    }

}