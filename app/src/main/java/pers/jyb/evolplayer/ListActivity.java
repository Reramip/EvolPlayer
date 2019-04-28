package pers.jyb.evolplayer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;

import java.util.List;

import static pers.jyb.evolplayer.MainActivity.musicList;


public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private int positionList;
    private ListOfLists listOfLists;
    private Music music;

    @SuppressLint("Assert")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listOfLists=ListOfLists.get(getApplicationContext());
        Intent intent = getIntent();
        positionList=intent.getIntExtra("CLICK_POSITION",-1);
        assert positionList!=-1;
        musicList= listOfLists.getList().get(positionList);
        recyclerView= findViewById(R.id.recycler_view_musics);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MusicsAdapter(R.layout.list_item_music,musicList.getList());
        ((MusicsAdapter) adapter).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intentPlayer = new Intent(ListActivity.this, PlayerActivity.class);
                intentPlayer.putExtra("POSITION",positionList);
                intentPlayer.putExtra("CLICK_MUSIC",position);
                startActivity(intentPlayer);
            }
        });
        recyclerView.setAdapter(adapter);
        ((MusicsAdapter) adapter).setEmptyView(getView(R.layout.empty_music_list));
        Toolbar listToolbar = findViewById(R.id.toolbar_list);
        listToolbar.setTitle(musicList.getName());
        setSupportActionBar(listToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        listToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private View getView(int viewId) {
        return LayoutInflater.from(this).inflate(viewId, new RelativeLayout(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if(positionList!=0&&positionList!=1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("删除此列表？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            listOfLists.getList().remove(positionList);
                            finish();
                        }
                    });
                    builder.show();
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(this, "此列表不可删除", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_settings:
                if(positionList!=0&&positionList!=1) {
                    Intent intentDrag = new Intent(ListActivity.this, ItemDragActivity.class);
                    intentDrag.putExtra("POSITION",positionList);
                    startActivity(intentDrag);
                }else{
                    Toast.makeText(this, "此列表不可修改", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
