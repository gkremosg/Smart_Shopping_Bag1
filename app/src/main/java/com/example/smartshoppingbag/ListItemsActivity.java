package com.example.smartshoppingbag;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartshoppingbag.Connection.ConnectionClass;

public class ListItemsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView dateText;
    private TextView listNameText;
    String listname;
    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listitems);
        listNameText = findViewById(R.id.listNameText);
        dateText = findViewById(R.id.dateText);

        findViewById(R.id.showCalender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //Load listname from MainActivity
        Intent intent = getIntent();
        String listID = intent.getStringExtra("message_key");

        try{
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
            if(con == null){

            } else {

                //find listname from dbo.userlist
                String sql = "SELECT listname FROM userlist WHERE listID = " + listID ;
                ResultSet rs1 = con.createStatement().executeQuery(sql);
                while (rs1.next()) {
                    listname = rs1.getString("listname");
                }

            }

        } catch (Exception e){

            //status.setText("Error");
        }

        listNameText.setText("Selected list " + listname);

    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Integer addmonth = month + 1;
        String date = dayOfMonth + "/" + addmonth + "/" + year;
        dateText.setText(date);
    }

    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String server){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + server+"/" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(connectionURL);
        }catch (Exception e){
            Log.e("SQL Connection Error : ", e.getMessage());
        }

        return connection;
    }
}

