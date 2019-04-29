package pers.jyb.evolplayer;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class ListsAdapter extends BaseQuickAdapter<MusicList, BaseViewHolder> {
    ListsAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, MusicList item) {
        viewHolder.setText(R.id.name_text_view, item.getName());
        viewHolder.setText(R.id.number_text_view,item.getNumString());
    }


}
