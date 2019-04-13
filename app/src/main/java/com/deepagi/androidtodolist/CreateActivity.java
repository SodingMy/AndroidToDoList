package com.deepagi.androidtodolist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener{

    //Defining views
    private EditText editTextName;
    private EditText editTextDesription;

    private Button buttonAdd;
    private Button buttonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //Initializing views
        editTextName = (EditText) findViewById(R.id.txtName);
        editTextDesription = (EditText) findViewById(R.id.txtDescription);

        buttonAdd = (Button) findViewById(R.id.btnCreate);
        buttonView = (Button) findViewById(R.id.btnBack);

        //Setting listeners to button
        buttonAdd.setOnClickListener(this);
        buttonView.setOnClickListener(this);
    }


    //Adding a task
    private void addTask(){

        final String name = editTextName.getText().toString().trim();
        final String description = editTextDesription.getText().toString().trim();

        class AddTask extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CreateActivity.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(CreateActivity.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(App.KEY_NAME,name);
                params.put(App.KEY_DESCRIPTION,description);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(App.URL_CREATE, params);
                return res;
            }
        }

        AddTask ae = new AddTask();
        ae.execute();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonAdd){
            addTask();
            startActivity(new Intent(CreateActivity.this,ListActivity.class));
        }

        if(v == buttonView){
            startActivity(new Intent(this,ListActivity.class));
        }
    }
}
