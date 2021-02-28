package com.example.smartshoppingbag;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


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
import android.widget.Toast;

import com.example.smartshoppingbag.Connection.ConnectionClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class MainActivity extends AppCompatActivity {
    Button createNewListbtn;

    TextView Welcometext;
    EditText email;
    //String email;
    Button addmemberbtn;
    TextView status;
    Connection con;
    Statement stmt;
    Integer userID;
    Integer memberID;
    String membername;
    String username;


    //email = (EditText)findViewById(R.id.AddNewMember);

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        addmemberbtn = (Button)findViewById(R.id.addmemberbtn);
        createNewListbtn = (Button)findViewById(R.id.createNewListbtn);
        Welcometext = (TextView)findViewById(R.id.Welcometext);

        email = (EditText)findViewById(R.id.AddNewMember);
        addmemberbtn = (Button)findViewById(R.id.addmemberbtn);
        status = (TextView)findViewById(R.id.status);

        addmemberbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MainActivity.checkmember().execute("");
            }
        });

        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");
        Welcometext.setText("Welcome " + str);
    }

    public class checkmember extends AsyncTask<String, String , String> {

        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            status.setText("Sending Data to Database");
        }

        @Override
        public String doInBackground(String... strings) {
            try{
                con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
                if(con == null){
                    z = "Check Your Internet Connection";
                }
                else{
                    //String test_email = email.toString();
                    //Intent intent_email = getIntent();
                    //String str = intent_email.getStringExtra("email");
                   // String sql = "SELECT userID FROM register WHERE email = '" + "gkremosg@gmail.com" +"'";
                    String sql = "SELECT userID, email FROM register WHERE email = '" + email.getText() +"'";
                    stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        memberID = rs.getInt("userID");
                        membername = rs.getString("email");
                    }

                    //Toast.makeText(AddNewMember.this, "Email was selected", Toast.LENGTH_LONG).show();
                    Intent in = getIntent();
                    String str = in.getStringExtra("message_key");

                   sql = "SELECT userID, email FROM register WHERE email = '" + str +"'";
                   stmt = con.createStatement();
                   ResultSet rs2 = stmt.executeQuery(sql);
                    while (rs2.next()) {
                        userID = rs2.getInt("userID");
                        username = rs2.getString("email");
                    }

                   sql = "INSERT INTO usermember (userID, memberID) VALUES (" + userID +"," + memberID + ")";
                    stmt = con.createStatement();
                    ResultSet rs3 = stmt.executeQuery(sql);
                }

            }catch (Exception e){
                isSuccess = false;
                z = e.getMessage();
            }

            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            status.setText("User " + username + " has member " + membername + " added");
           // email.setText("");

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
