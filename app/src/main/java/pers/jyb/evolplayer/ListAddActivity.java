package pers.jyb.evolplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

public class ListAddActivity extends AppCompatActivity {
    private ListOfLists listOfLists;
    private List<MusicList> list;
    private List<MusicList> subList;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent = getIntent();
        listOfLists=ListOfLists.get(getApplicationContext());
        list=listOfLists.getList();
        subList=list.subList(2,list.size());
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_add);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AddListsAdapter(R.layout.list_item_add, subList);
        ((AddListsAdapter) adapter).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent();
                intent.putExtra("ADD_LIST",position);
                setResult(1, intent);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
        Toolbar addToolbar = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(addToolbar);
    }

    private View getView(int viewId) {
        return LayoutInflater.from(this).inflate(viewId, new RelativeLayout(this));
    }

}
