package com.example.ekene.blogzone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {
    private DBManager dbManager;
    private Menu titleBar;
    private RecyclerView recyclerView;
    private ArrayList<Tasks> myTasks;
    private TasksAdapter adapter;
    private int list_id;
    private String list_name;
    private boolean hasLocation,hasTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Bundle bundle = getIntent().getExtras();
        list_id = bundle.getInt("list_id");
        hasLocation = bundle.getBoolean("hasLocation");
        hasTimestamp = bundle.getBoolean("hasTimestamp");
        list_name = bundle.getString("list_name");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(list_name);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        dbManager = new DBManager(this);
        dbManager.open();
        myTasks = new ArrayList<>();
        myTasks = dbManager.fetchTasks(list_id);
        adapter = new TasksAdapter(myTasks,getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_task, menu);
        titleBar = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent newIntent;
            if (hasLocation)
                newIntent = new Intent(TaskActivity.this, NewTaskActivity.class);
            else
                newIntent = new Intent(TaskActivity.this, NewTaskActivity2.class);
            newIntent.putExtra("list_id",list_id);
            newIntent.putExtra("hasTimestamp",hasTimestamp);
            startActivityForResult(newIntent,3);
        }
        else if (id == R.id.action_del) {
            dbManager.deleteList(list_id);
            setResult(2);
            Toast.makeText(this, "List Deleted..", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == 4) {
            myTasks = dbManager.fetchTasks(list_id);
            adapter = new TasksAdapter(myTasks,getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
    }
}
