package com.example.ekene.blogzone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DBManager {
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c){
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insertList(String name, String desc, byte[] image, boolean hasLocation, boolean hasTimestamp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put("list_name", name);
        contentValue.put("description", desc);
        contentValue.put("image", image);
        contentValue.put("hasLocation", hasLocation);
        contentValue.put("hasTimestamp", hasTimestamp);
        database.insert(DatabaseHelper.TABLE_LIST, null, contentValue);
    }

    public ArrayList<Lists> fetchLists() {
        String[] columns = new String[] {"_id","list_name","description","image","hasLocation","hasTimestamp"};
        ArrayList<Lists> lists =new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_LIST,columns,null,null,null,null,null);
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String desc = cursor.getString(2);
                byte[] image = cursor.getBlob(3);
                boolean hasLocation = cursor.getInt(4)>0;
                boolean hasTimestamp = cursor.getInt(5)>0;
                lists.add(new Lists(id,title,desc,image,hasLocation,hasTimestamp));
                Log.e("location enabled: ",""+lists.get(lists.size()-1).isHasLocation());
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return lists;
    }

    public void deleteList(int list_id) {
        database.delete(DatabaseHelper.TABLE_TASK,"list_id="+list_id,null);
        database.delete(DatabaseHelper.TABLE_LIST,"_id="+list_id,null);
    }

    public void insertTask(String name, String desc, byte[] image, double lat, double lng, int list_id, String timestamp) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("task_name",name);
        contentValues.put("description",desc);
        contentValues.put("image",image);
        contentValues.put("lat",lat);
        contentValues.put("lng",lng);
        contentValues.put("list_id",list_id);
        contentValues.put("timestamp",timestamp);
        database.insert(DatabaseHelper.TABLE_TASK,null,contentValues);
    }

    public ArrayList<Tasks> fetchTasks(int list_id) {
        String[] columns = new String[] {"_id","task_name","description","image","lat","lng","list_id","timestamp"};
        ArrayList<Tasks> tasks =new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_TASK,columns,"list_id=?",new String[] {String.valueOf(list_id)},null,null,"_id",null);
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String desc = cursor.getString(2);
                byte[] image = cursor.getBlob(3);
                double lat = cursor.getDouble(4);
                double lng = cursor.getDouble(5);
                String timestamp = cursor.getString(7);
                tasks.add(new Tasks(id,title,desc,timestamp,image,lat,lng));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    public void deleteTask(int task_id) {
        database.delete(DatabaseHelper.TABLE_TASK,"_id="+task_id,null);
    }

    public Tasks fetchSingleTask(int task_id) {
        String[] columns = new String[] {"_id","task_name","description","image","lat","lng","list_id","timestamp"};
        Tasks task = new Tasks();
        Cursor cursor = database.query(DatabaseHelper.TABLE_TASK,columns,"_id=?",new String[] {String.valueOf(task_id)},null,null,"_id",null);
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            byte[] image = cursor.getBlob(3);
            double lat = cursor.getDouble(4);
            double lng = cursor.getDouble(5);
            String timestamp = cursor.getString(7);
            task = new Tasks(id,title,desc,timestamp,image,lat,lng);
        }
        cursor.close();
        return task;
    }
}
