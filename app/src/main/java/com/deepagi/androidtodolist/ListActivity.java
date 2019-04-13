package com.deepagi.androidtodolist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView listView;
    private String JSON_STRING;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.lv_task);
        listView.setOnItemClickListener(this);
        getJSON();

        buttonAdd = (Button) findViewById(R.id.btnCreate);
        buttonAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent createActivity = new Intent(getApplicationContext(), CreateActivity.class);
                startActivity(createActivity);

            }
        });
    }


    private void showTask(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(App.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(App.TAG_ID);
                String name = jo.getString(App.TAG_NAME);

                HashMap<String,String> tasks = new HashMap<>();
                tasks.put(App.TAG_ID,id);
                tasks.put(App.TAG_NAME,name);
                list.add(tasks);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                ListActivity.this, list, R.layout.list_item,
                new String[]{App.TAG_NAME},
                new int[]{R.id.name});

        listView.setAdapter(adapter);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ListActivity.this,"Fetching Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showTask();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(App.URL_GET_ALL);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, UpdateActivity.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String taskId = map.get(App.TAG_ID).toString();
        intent.putExtra(App.ID,taskId);
        startActivity(intent);
    }
}
