package pers.jyb.evolplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {
    public static MusicList musicList;

    private static final String TAG = "MainActivity";
    private static final int MY_REQUEST_CODE = 1713;
    private ListOfLists listOfLists;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_REQUEST_CODE);
        }

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_lists);

        load();
        listOfLists=ListOfLists.get(getApplicationContext());

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListsAdapter(R.layout.list_item_list, listOfLists.getList());
        ((ListsAdapter) adapter).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("CLICK_POSITION",position);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onResume: "+ listOfLists.size());
    }

    @Override
    protected void onPause() {
        save();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        MusicIntent musicIntent=MusicIntent.get(getApplicationContext());
        stopService(musicIntent.getIntent());
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {//nameNewList 弹窗输入
            final EditText inputServer = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("输入歌单名称")
                    .setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String nameNewList = inputServer.getText().toString();
                    listOfLists.add(new MusicList(nameNewList));
                }
            });
            builder.show();
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        adapter.notifyDataSetChanged();
    }

    private void save(){
        try {
            FileOutputStream fos=openFileOutput("save_list", MODE_PRIVATE);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(listOfLists);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean load(){
        try {
            FileInputStream fis=openFileInput("save_list");
            ObjectInputStream ois=new ObjectInputStream(fis);
            boolean flag=ListOfLists.set(ois.readObject());
            ois.close();
            fis.close();
            return flag;
        } catch (IOException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

