package com.example.smartshoppingbag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smartshoppingbag.Connection.ConnectionClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class AddNewMember extends AppCompatActivity {

    EditText email;
    Button addmemberbtn;
    TextView status;
    Connection con;
    Statement stmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);

        email = (EditText)findViewById(R.id.AddNewMember);
        addmemberbtn = (Button)findViewById(R.id.addmemberbtn);
        status = (TextView)findViewById(R.id.status);

        addmemberbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddNewMember.checkmember().execute("");
            }
        });
    }

    public class checkmember extends AsyncTask<String, String , String> {

        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            status.setText("Sending Data to Database");
        }

        @Override
        protected void onPostExecute(String s) {
            status.setText("New Member was added");
            email.setText("");

        }

        @Override
        public String doInBackground(String... strings) {
            try{
                con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
                if(con == null){
                    z = "Check Your Internet Connection";
                }
                else{

                    Intent intent_email = getIntent();
                    String str = intent_email.getStringExtra("email");

                    String sql = "SELECT userID FROM register WHERE email = '" + email.getText() +"'";
                    stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    sql = "SELECT userID FROM register WHERE email = '" + str +"'";
                    stmt = con.createStatement();
                    ResultSet rs2 = stmt.executeQuery(sql);

                    sql = "INSERT INTO dbo.usermember (memberID, userID) VALUES (" + rs.getString(0) +"," +rs2.getString(0) + ")";
                    stmt = con.createStatement();
                    stmt.executeQuery(sql);
                }

            }catch (Exception e){
                isSuccess = false;
                z = e.getMessage();
            }

            return z;
        }
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