package com.deepagi.androidtodolist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName;
    private EditText editTextDescription;

    private Button buttonUpdate;
    private Button buttonDelete;
    private Button buttonView;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent intent = getIntent();

        id = getIntent().getExtras().getString(App.ID);

        editTextName = (EditText) findViewById(R.id.txtName);
        editTextDescription = (EditText) findViewById(R.id.txtDescription);

        buttonUpdate = (Button) findViewById(R.id.btnUpdate);
        buttonDelete = (Button) findViewById(R.id.btnDelete);
        buttonView = (Button) findViewById(R.id.btnBack);

        buttonUpdate.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonView.setOnClickListener(this);

        getTask();
    }

    private void getTask(){
        class GetTask extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UpdateActivity.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showTask(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(App.URL_GET_DETAIL,id);
                return s;
            }
        }
        GetTask gt = new GetTask();
        gt.execute();
    }

    private void showTask(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(App.TAG_JSON_DETAIL);
            JSONObject c = result.getJSONObject(0);
            String name = c.getString(App.TAG_NAME);
            String description = c.getString(App.TAG_DESCRIPTION);

            editTextName.setText(name);
            editTextDescription.setText(description);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updateTask(){
        final String name = editTextName.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();

        class UpdateTask extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UpdateActivity.this,"Updating...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(UpdateActivity.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(App.KEY_ID,id);
                hashMap.put(App.KEY_NAME,name);
                hashMap.put(App.KEY_DESCRIPTION,description);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(App.URL_UPDATE,hashMap);

                return s;
            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }

    private void deleteTask(){
        class DeleteTask extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UpdateActivity.this, "Updating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(UpdateActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(App.URL_DELETE, id);
                return s;
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();
    }

    private void confirmDeleteTask(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to delete this task?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteTask();
                        startActivity(new Intent(UpdateActivity.this,ListActivity.class));
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonUpdate){
            updateTask();
        }

        if(v == buttonDelete){
            confirmDeleteTask();
        }

        if(v == buttonView){
            startActivity(new Intent(this,ListActivity.class));
        }
    }
}

