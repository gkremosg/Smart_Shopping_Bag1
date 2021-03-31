package com.example.smartshoppingbag;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    private ArrayList<MainActivity_UsermembersListItems> itemArrayList; //List items Array
    private MyAppAdapter myAppAdapter; //Array Adapter
    private RecyclerView recyclerViewMyMembers; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean success = false; // boolean
    private ConnectionClass connectionClass; //Connection Class Variable


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



        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");
        Welcometext.setText("Welcome " + str);

        recyclerViewMyMembers = (RecyclerView) findViewById(R.id.recyclerViewMyMembers); //Recylcerview Declaration
        recyclerViewMyMembers.setHasFixedSize(true);
// use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyMembers.setLayoutManager(mLayoutManager);

        connectionClass = new ConnectionClass(); // Connection Class Initialization
        itemArrayList = new ArrayList<MainActivity_UsermembersListItems>(); // Arraylist Initialization

        // Calling Async Task
        SyncData orderData = new SyncData();
        orderData.execute("");

        addmemberbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MainActivity.checkmember().execute("");
                // Calling Async Task
                SyncData orderData = new SyncData();
                orderData.execute("");
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Async Task has three overrided methods,
    private class SyncData extends AsyncTask<String, String, String>
    {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(MainActivity.this, "Synchronising",
                    "RecyclerView Loading! Please Wait...", true);
        }

        @Override
        protected String doInBackground(String... strings) // Connect to the database, write query and add items to array list
        {
            try
            {
                con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
                if(con == null)
                {
                    success = false;
                }
                else {
// Change below query according to your own database.
                    Intent in = getIntent();
                    String str = in.getStringExtra("message_key");

                    String sql = "SELECT userID FROM register WHERE email = '" + str +"'";
                    Statement stmt = con.createStatement();
                    ResultSet rs2 = stmt.executeQuery(sql);
                    while (rs2.next()) {
                        userID = rs2.getInt("userID");
                    }
                    //status.setText("User "+ userID);
                    sql = "SELECT email FROM register AS r INNER JOIN usermember AS um ON r.userID = um.memberID WHERE um.userID = "+ userID ;
                    stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                itemArrayList.add(new MainActivity_UsermembersListItems(rs.getString("email")));
                                //status.setText("Second Querry successfull");

                            } catch (Exception ex) {
                                ex.printStackTrace();
                               // status.setText("Second Querry unsuccessfull for"+email);
                            }
                        }
                        msg = "Found";
                        success = true;
                    } else {
                        msg = "No Data found!";
                        success = false;
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my listview
        {
            progress.dismiss();
            Toast.makeText(MainActivity.this, msg + "", Toast.LENGTH_LONG).show();
            if (success == false)
            {
            }
            else {
                try
                {
                    myAppAdapter = new MyAppAdapter(itemArrayList , MainActivity.this);
                    recyclerViewMyMembers.setAdapter(myAppAdapter);
                } catch (Exception ex)
                {

                }

            }
        }
    }

    public class MyAppAdapter extends RecyclerView.Adapter<MyAppAdapter.ViewHolder> {
        private List<MainActivity_UsermembersListItems> values;
        public Context context;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public image title and image url
            public TextView textName;
            public View layout;

            public ViewHolder(View v)
            {
                super(v);
                layout = v;
                textName = (TextView) v.findViewById(R.id.textName);
            }
        }

        // Constructor
        public MyAppAdapter(List<MainActivity_UsermembersListItems> myDataset,Context context)
        {
            values = myDataset;
            this.context = context;
        }

        // Create new views (invoked by the layout manager) and inflates
        @Override
        public MyAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
// create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.main_activity_usermembers_listitems, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Binding items to the view
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final MainActivity_UsermembersListItems classListItems = values.get(position);
            holder.textName.setText(classListItems.getName());

        }

        // get item count returns the list item count
        @Override
        public int getItemCount() {
            return values.size();
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
