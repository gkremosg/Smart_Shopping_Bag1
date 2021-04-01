package com.example.smartshoppingbag;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.smartshoppingbag.Connection.ConnectionClass;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.snackbar.Snackbar;
//import androidx.appcompat.widget.Toolbar;


//Start Code for Main Activity----------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {
    //Start MainActivity Declarations------------------------------------------------------------
    Button createNewListbtn;
    TextView Welcometext;
    EditText email;
    Button addmemberbtn;
    TextView status;
    Connection con;
    Integer userID;
    Integer memberID;
    String memberemail;
    String useremail;
    private ArrayList<MainActivity_UsermembersListItems> itemArrayList; //List items Array
    private MyAppAdapter myAppAdapter; //Array Adapter
    private RecyclerView recyclerViewMyMembers; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean success = false; // boolean
    private ConnectionClass connectionClass; //Connection Class Variable
    //End MainActivity Declarations--------------------------------------------------------------

    //Start onCreate MainActivity ---------------------------------------------------------------
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

        //Load str (email txt) from LoginActivity
        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");
        Welcometext.setText("Welcome " + str);

        //itemArrayList and Recyclerview Declaration
        itemArrayList = new ArrayList<MainActivity_UsermembersListItems>();
        recyclerViewMyMembers = (RecyclerView) findViewById(R.id.recyclerViewMyMembers);
        recyclerViewMyMembers.setHasFixedSize(true);
        //use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyMembers.setLayoutManager(mLayoutManager);

        // Connection Class Initialization
        connectionClass = new ConnectionClass();

        // Calling AsyncTask/SyncData
        SyncData orderData = new SyncData();
        orderData.execute("");

        //Action onClick BUTTON Add Member
        addmemberbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MainActivity.checkmember().execute("");
                // Calling AsyncTask/SyncData (Refresh recyclerViewMyMembers)
                itemArrayList = new ArrayList<MainActivity_UsermembersListItems>();
                SyncData orderData = new SyncData();
                orderData.execute("");
            }
        });
    }
    //End onCreate MainActivity -----------------------------------------------------------------

    //Start AsyncTask/SyncData to Load Data to recyclerViewMyMembers-----------------------------
    private class SyncData extends AsyncTask<String, String, String>
    {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
        ProgressDialog progress;

        //Starts the progress dailog
        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Synchronising",
                    "Member-List is Loading...", true);
        }

        //Connect to the database, write query and add items to itemArrayList of MainActivity_UsermembersListItems
        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
                if(con == null)
                {
                    success = false;
                }
                else {
                    //Load str (email txt) from LoginActivity
                    Intent in = getIntent();
                    String str = in.getStringExtra("message_key");

                    //Find userID from intented str (email of the logged-in-user)
                    String sql = "SELECT userID FROM register WHERE email = '" + str +"'";
                    ResultSet rs2 = con.createStatement().executeQuery(sql);
                    while (rs2.next()) {
                        userID = rs2.getInt("userID");
                    }

                    //Find emails from joined members of the logged-in-user
                    sql = "SELECT email FROM register AS r INNER JOIN usermember AS um ON r.userID = um.memberID WHERE um.userID = "+ userID ;
                    ResultSet rs = con.createStatement().executeQuery(sql);

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
                        msg = "No Members found!";
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

        //Load items of itemArrayList to recyclerViewMyMembers in MainActivity by using MyAppAdapter
        @Override
        protected void onPostExecute(String msg)
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
    //End AsyncTask/SyncData to Load Data to recyclerViewMyMembers-------------------------------

    //Start RecyclerView.Adapter: MyAppAdapter---------------------------------------------------
    public class MyAppAdapter extends RecyclerView.Adapter<MyAppAdapter.ViewHolder> {
        private List<MainActivity_UsermembersListItems> values;
        public Context context;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public textView and layout
            public TextView textView;
            public View layout;
            ///public Button btndelete_member;

            public ViewHolder(View view)
            {
                super(view);
                layout = view;
                textView = (TextView) view.findViewById(R.id.textView);

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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_usermembers_listitems, parent, false);
            return new ViewHolder(view);
        }

        // Binding items to the view
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            final MainActivity_UsermembersListItems mainactivity_usermemberslistitems = values.get(position);
            //holder.textName.setText(mainactivity_usermemberslistitems.getName());
            holder.textView.setText(mainactivity_usermemberslistitems.getName());
            holder.layout.setBackgroundColor(mainactivity_usermemberslistitems.isSelected() ? Color.CYAN : Color.WHITE);
            holder.textView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    mainactivity_usermemberslistitems.setSelected(!mainactivity_usermemberslistitems.isSelected());
                    holder.layout.setBackgroundColor(mainactivity_usermemberslistitems.isSelected() ? Color.CYAN : Color.WHITE);
                }
            });
        }

        // get item count returns the list item count
        @Override
        public int getItemCount() {
            return values == null ? 0 : values.size();
        }

    }
    //End RecyclerView.Adapter: MyAppAdapter-----------------------------------------------------

    //Start checkmeber class---------------------------------------------------------------------
    public class checkmember extends AsyncTask<String, String , String> {

        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            status.setText("Sending Data to Database");
        }

        //Connect to DB, check if entered email (member to add) exists and find userID and username from logged-in-user,
        //If member-user connection does not already exist, then insert new member-user connection in usermember table
        @Override
        public String doInBackground(String... strings) {
            try{
                con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
                if(con == null){
                    z = "Check Your Internet Connection";
                }
                else{

                    String sql = "SELECT userID, email FROM register WHERE email = '" + email.getText() +"'";
                    ResultSet rs = con.createStatement().executeQuery(sql);
                    while (rs.next()) {
                        memberID = rs.getInt("userID");
                        memberemail = rs.getString("email");
                    }
                    //Load str (email txt) from LoginActivity
                    Intent in = getIntent();
                    String str = in.getStringExtra("message_key");

                    sql = "SELECT userID, email FROM register WHERE email = '" + str +"'";
                    ResultSet rs2 = con.createStatement().executeQuery(sql);
                    while (rs2.next()) {
                        userID = rs2.getInt("userID");
                        useremail = rs2.getString("email");
                    }
                    //check if member-user connection does not already exist
                    sql = "SELECT usermemberID FROM usermember WHERE userID = " + userID + " AND " + "memberID = "+ memberID ;
                    ResultSet rs3 = con.createStatement().executeQuery(sql);
                    if (!rs3.next()) {
                        //write new member-user connection into DB
                        sql = "INSERT INTO usermember (userID, memberID) VALUES (" + userID + ", " + memberID + ")";
                        ResultSet rs4 = con.createStatement().executeQuery(sql);
                        status.setText(useremail + " has added " + memberemail + " as member");
                    } else {
                        status.setText("Member "+ memberemail +" is already in your list");
                    }
                }

            }catch (Exception e){
                isSuccess = false;
                z = e.getMessage();
            }

            return z;
        }

        //Show Text to user
        @Override
        protected void onPostExecute(String s) {
            /*if(usermemberentry = false){
                status.setText("Member is already in your list");

            }
            else{
                status.setText(useremail + " has added " + memberemail + " as member");
            }*/
        }
    }
    //End checkmember class-----------------------------------------------------------------------

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
//End Code for Main Activity------------------------------------------------------------------------