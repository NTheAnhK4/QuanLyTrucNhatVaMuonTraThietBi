package com.example.quanlytrucnhatvamuontrathietbi;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import Data.DataUtil;
import Data.User;

public class TAExampleDataUlti extends AppCompatActivity {

    ListView taDemoListView;
    TextView txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_taexample_data_ulti);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWidget();
        try{
            ArrayList<String> arr = new ArrayList<>();
            //here
            ArrayList<User> users = DataUtil.getInstance(this).users.getAll();
            for (User user: users) {
                arr.add(user.toString());
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                    this,android.R.layout.simple_list_item_1,arr);
            taDemoListView.setAdapter(adapter);
        }
        catch(Exception e){
            txtView.setText(e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }
    private void getWidget(){
        taDemoListView = findViewById(R.id.taDemoListView);
        txtView = findViewById(R.id.taDemoTxt);
    }

}