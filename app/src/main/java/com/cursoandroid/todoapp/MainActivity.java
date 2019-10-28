package com.cursoandroid.todoapp;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Button buttonAdd;
    private EditText editTextToDo;
    private ListView listViewToDo;
    private SQLiteDatabase sqLiteDatabase;

    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> items;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {

            editTextToDo = findViewById(R.id.text_id);
            buttonAdd = findViewById(R.id.button_add);
            listViewToDo = findViewById(R.id.list_view_id);


            sqLiteDatabase = openOrCreateDatabase("toDoApp", MODE_PRIVATE, null);

            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS task " +
                    "( " +
                    "task_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "task_name VARCHAR (255))");

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String typedText = editTextToDo.getText().toString();
                    saveTask(typedText);
                }
            });
            listViewToDo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removeTask(ids.get(position));

                    return false;
                }
            });

            recuperarTarefas();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void saveTask(String text) {
        try {
            if (editTextToDo.getText().toString().isEmpty()) {
                editTextToDo.setError("Type a text");
            } else {
                sqLiteDatabase.execSQL("" +
                        "INSERT INTO task (task_name) " +
                        "VALUES ('" + text + " ' ) ");
                Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                editTextToDo.setText("");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void recuperarTarefas() {
        try {
            //recupera as tarefas
            Cursor cursor = sqLiteDatabase.rawQuery("" +
                    "SELECT * " +
                    "FROM task " +
                    "ORDER BY task_id DESC", null);

            //recupera os ids
            int columnIndexId = cursor.getColumnIndex("task_id");
            int columnIndexTask = cursor.getColumnIndex("task_name");

            //criar adaptador

            items = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itemsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    items);
            listViewToDo.setAdapter(itemsAdapter);

            //lista as tarefas
            cursor.moveToFirst();

            //enquanto tiver elementos no cursos
            while (cursor != null) {
                Log.i("RESULTADO", "Tarefa: " + cursor.getString(columnIndexTask));
                Log.i("RESULTADO", "id: " + cursor.getString(columnIndexId));


                items.add(cursor.getString(columnIndexTask));
                ids.add(Integer.parseInt(cursor.getString(columnIndexId)));


                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeTask(Integer id) {
        try {
            sqLiteDatabase.execSQL("" +
                    "DELETE FROM task " +
                    "WHERE task_id=" + id);
            recuperarTarefas();
            Toast.makeText(MainActivity.this, "Task removed", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
