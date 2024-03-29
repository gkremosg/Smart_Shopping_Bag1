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
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import java.io.Serializable;
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


//1.Start Code for Main Activity----------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {

    //1.1.Start MainActivity Declarations------------------------------------------------------------
    Button createNewListbtn;
    Button showmycostsbtn;
    TextView Welcometext;
    EditText emailmembertoadd;
    Button addmemberbtn;
    TextView status;
    Connection con;
    Integer userID;
    Integer memberID;
    String memberemail;
    String useremail;
    String listID;

    public ArrayList<MainActivity_UsermembersListItems> itemArrayList; //List items Array
    private ArrayList<MainActivity_UserLists> itemArrayMylists; //List items Array
    private MyAppAdapter myAppAdapter; //Array Adapter
    private MyAppAdapter_Userlist myAppAdapter_userlist; //Array Adapter
    private RecyclerView recyclerViewMyMembers; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean success = false; // boolean
    private RecyclerView recyclerViewMyLists; //RecyclerView
    private ConnectionClass connectionClass; //Connection Class Variable
    //End MainActivity Declarations--------------------------------------------------------------

    //1.2.Start onCreate MainActivity ---------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        showmycostsbtn = (Button)findViewById(R.id.showmycostsbtn);
        addmemberbtn = (Button)findViewById(R.id.addmemberbtn);
        createNewListbtn = (Button)findViewById(R.id.createNewListbtn);
        Welcometext = (TextView)findViewById(R.id.Welcometext);
        emailmembertoadd = (EditText)findViewById(R.id.AddNewMember);
        status = (TextView)findViewById(R.id.status);

        //Load globaluseremail (set from LoginActivity)
        GlobalClass globalClass = (GlobalClass) getApplicationContext();
        Welcometext.setText("Welcome " + globalClass.getGlobaluseremail());

        //itemArrayList and Recyclerview Declaration for member list
        itemArrayList = new ArrayList<MainActivity_UsermembersListItems>();
        recyclerViewMyMembers = (RecyclerView) findViewById(R.id.recyclerViewMyMembers);
        recyclerViewMyMembers.setHasFixedSize(true);
        //use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyMembers.setLayoutManager(mLayoutManager);

        //itemArrayList and Recyclerview Declaration for Mylists list
        itemArrayMylists = new ArrayList<MainActivity_UserLists>();
        recyclerViewMyLists = (RecyclerView) findViewById(R.id.recyclerViewMyLists);
        recyclerViewMyLists.setHasFixedSize(true);

        //use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyLists.setLayoutManager(mLayoutManager);

        // Connection Class Initialization
        connectionClass = new ConnectionClass();

        // Calling AsyncTask/SyncData for Mymembers recyclerview
        SyncData_MyMembers orderData = new SyncData_MyMembers();
        orderData.execute("");

        // Calling AsyncTask/SyncData for MyLists recyclerview
        SyncData_MyLists orderData_MyLists = new SyncData_MyLists();
        orderData_MyLists.execute("");

        //Action onClick BUTTON Add Member
        addmemberbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MainActivity.checkmember().execute("");
                // Calling AsyncTask/SyncData_MyMembers (Refresh recyclerViewMyMembers)
                itemArrayList = new ArrayList<MainActivity_UsermembersListItems>();
                SyncData_MyMembers orderData = new SyncData_MyMembers();
                orderData.execute("");
            }
        });

        createNewListbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListItemsActivity.class);
                startActivity(intent);

            }
        });
    }
    //End onCreate MainActivity -----------------------------------------------------------------

    //1.3.1.Start AsyncTask/SyncData to Load Data to recyclerViewMyLists-----------------------------
    private class SyncData_MyLists extends AsyncTask<String, String, String>
    {
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
        ProgressDialog progress;

        //Starts the progress dailog
        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Synchronising",
                    "MyList is Loading...", true);
        }

        //Connect to the database, write query and add items to itemArrayList of MainActivity_UserLists
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

                    //Load globaluseremail (set from LoginActivity)
                    GlobalClass globalClass = (GlobalClass) getApplicationContext();

                    //Find userID from globaluseremail (email of the logged-in-user)
                    String sql = "SELECT userID FROM register WHERE email = '" + globalClass.getGlobaluseremail() +"'";
                    ResultSet rs2 = con.createStatement().executeQuery(sql);
                    while (rs2.next()) {
                        userID = rs2.getInt("userID");
                    }

                    //Find userlist information of the logged-in-user
                    sql = "SELECT listname, listID, listdate FROM userlist WHERE userID = "+ userID + " ORDER BY listdate";
                    ResultSet rs = con.createStatement().executeQuery(sql);

                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                itemArrayMylists.add(new MainActivity_UserLists(rs.getString("listname"), rs.getString("listdate"), rs.getString("listID")));
                                //status.setText("Second itemArrayMylists Query successfull");

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                status.setText("Second itemArrayMylists Query unsuccessfull for "+userID);
                            }
                        }
                        msg = "Found";
                        success = true;
                    } else {
                        msg = "No Lists found!";
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
                    myAppAdapter_userlist = new MyAppAdapter_Userlist(itemArrayMylists , MainActivity.this);
                    recyclerViewMyLists.setAdapter(myAppAdapter_userlist);
                } catch (Exception ex)
                {

                }
            }
        }
    }
    //End AsyncTask/SyncData to Load Data to recyclerViewMyLists-------------------------------


    //1.3.2.Start AsyncTask/SyncData_MyMembers to Load Data to recyclerViewMyMembers-----------------------------
    private class SyncData_MyMembers extends AsyncTask<String, String, String>
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
                    //Load globaluseremail (set from LoginActivity)
                    GlobalClass globalClass = (GlobalClass) getApplicationContext();

                    //Find userID from intented str (email of the logged-in-user)
                    String sql = "SELECT userID FROM register WHERE email = '" + globalClass.getGlobaluseremail() +"'";
                    ResultSet rs2 = con.createStatement().executeQuery(sql);
                    while (rs2.next()) {
                        userID = rs2.getInt("userID");
                    }

                    //Find emails from joined members of the logged-in-user
                    sql = "SELECT email FROM register AS r INNER JOIN usermember AS um ON r.userID = um.memberID WHERE um.userID = "+ userID + " ORDER BY r.email";
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

    //1.4.1.Start RecyclerView.Adapter: MyAppAdapter_UserList---------------------------------------------------
    public class MyAppAdapter_Userlist extends RecyclerView.Adapter<MyAppAdapter_Userlist.ViewHolder> {
        private List<MainActivity_UserLists> values_mylists;
        public Context context;
        private int checkedPosition = -1;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public textView and layout
            public TextView textListName;
            public TextView textListDate;
            public TextView textListID;
            public View layout;
            public ImageView mylist_popup_options;

            public ViewHolder(View view)
            {
                super(view);
                layout = view;

                textListName = (TextView) view.findViewById(R.id.textListName);
                textListDate = (TextView) view.findViewById(R.id.textListDate);
                textListID = (TextView) view.findViewById(R.id.textListID);
                textListID.setVisibility(View.GONE);
                mylist_popup_options = itemView.findViewById(R.id.img_options);
            }
        }

        // Constructor
        public MyAppAdapter_Userlist(List<MainActivity_UserLists> myDataset_myLists,Context context)
        {
            values_mylists = myDataset_myLists;
            this.context = context;
        }

        // Create new views (invoked by the layout manager) and inflates
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_userlists, parent, false);
            return new ViewHolder(view);
        }

        // Binding items to the view
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final MainActivity_UserLists mainActivity_userLists = values_mylists.get(position);
            holder.textListName.setText(mainActivity_userLists.getListName());
            holder.textListDate.setText(mainActivity_userLists.getListDate());
            holder.textListID.setText(mainActivity_userLists.getListID());

            holder.mylist_popup_options.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    checkedPosition = position;
                    status.setText("Checked Position is  "+ checkedPosition);
                    notifyDataSetChanged();

                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.getMenuInflater().inflate(R.menu.mylist_popup, popupMenu.getMenu());
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId())
                            {
                                case R.id.edit:

                                    listID = values_mylists.get(position).getListID();
                                    //Toast.makeText(context, listname, Toast.LENGTH_SHORT).show();
                                    Intent intentMain_edit = new Intent(MainActivity.this, ListItemsActivity.class);
                                    intentMain_edit.putExtra("message_keyID", listID);
                                    GlobalClass globalClass = (GlobalClass) getApplicationContext();
                                    globalClass.setGloballistID(listID);
                                    startActivity(intentMain_edit);

                                    break;

                                case R.id.delete:

                                    listID = values_mylists.get(position).getListID();
                                    //Toast.makeText(context, listname, Toast.LENGTH_SHORT).show();

                                    Toast.makeText(context, "Delete clicked", Toast.LENGTH_SHORT).show();

                                    //Remove list from recycleview
                                    values_mylists.remove(position);
                                    notifyDataSetChanged();

                                    //Remove list from DB
                                    try{
                                        con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
                                        if(con == null){

                                        } else {

                                            //Delete list entry from dbo.userlist
                                            String sql = "DELETE FROM userlist WHERE listID = " + listID ;
                                            ResultSet rs1 = con.createStatement().executeQuery(sql);

                                            //Refresh List List
                                            itemArrayMylists = new ArrayList<MainActivity_UserLists>();
                                            SyncData_MyLists orderData = new SyncData_MyLists();
                                            orderData.execute("");

                                            //break;
                                        }

                                } catch (Exception e){
                                    status.setText("Error");
                                }

                                status.setText("List "+ mainActivity_userLists.getListName() +" deleted");
                            }

                            return false;
                        }
                    });
                }
            });

            //highlights only the selected position
            if(checkedPosition==position){
                holder.layout.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.layout.setBackgroundColor(Color.WHITE);
            }
        }

        // get item count returns the list item count
        @Override
        public int getItemCount() {
            return values_mylists == null ? 0 : values_mylists.size();
        }
    }
    //End RecyclerView.Adapter_Userlist: MyAppAdapter_Userlist-----------------------------------------------------

    //1.4.2.Start RecyclerView.Adapter: MyAppAdapter---------------------------------------------------
    public class MyAppAdapter extends RecyclerView.Adapter<MyAppAdapter.ViewHolder> {
        private List<MainActivity_UsermembersListItems> values;
        public Context context;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public textView and layout
            public TextView textView;
            public View layout;
            public ImageView removeImg;

            public ViewHolder(View view)
            {
                super(view);
                layout = view;
                textView = (TextView) view.findViewById(R.id.textView);
                removeImg = itemView.findViewById(R.id.img_remove);
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
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final MainActivity_UsermembersListItems mainactivity_usermemberslistitems = values.get(position);
            holder.textView.setText(mainactivity_usermemberslistitems.getName());
            holder.layout.setBackgroundColor(mainactivity_usermemberslistitems.isSelected() ? Color.LTGRAY : Color.WHITE);
            holder.textView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    mainactivity_usermemberslistitems.setSelected(!mainactivity_usermemberslistitems.isSelected());
                    holder.layout.setBackgroundColor(mainactivity_usermemberslistitems.isSelected() ? Color.LTGRAY : Color.WHITE);
                }
            });

            holder.removeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Remove member from recycleview
                    values.remove(position);
                    notifyDataSetChanged();
                    //Remove member (user-member connection entry of dbo.usermember) from DB
                    try{
                        con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
                        if(con == null){

                        }
                        else{
                            //Find memberID from dbo.register
                            String sql = "SELECT userID FROM register WHERE email = '" + mainactivity_usermemberslistitems.getName() +"'";
                            ResultSet rs = con.createStatement().executeQuery(sql);
                            while (rs.next()) {
                                memberID = rs.getInt("userID");
                            }
                            //Load globaluseremail (set from LoginActivity)
                            GlobalClass globalClass = (GlobalClass) getApplicationContext();

                            sql = "SELECT userID FROM register WHERE email = '" + globalClass.getGlobaluseremail() +"'";
                            ResultSet rs2 = con.createStatement().executeQuery(sql);
                            while (rs2.next()) {
                                userID = rs2.getInt("userID");
                            }
                            //Delete user-member connection entry from dbo.usermember
                            sql = "DELETE FROM usermember WHERE userID = " + userID + " AND " + "memberID = "+ memberID ;
                            ResultSet rs3 = con.createStatement().executeQuery(sql);

                            //Refresh Member-List
                            itemArrayList = new ArrayList<MainActivity_UsermembersListItems>();
                            SyncData_MyMembers orderData = new SyncData_MyMembers();
                            orderData.execute("");
                        }

                    }catch (Exception e){
                        status.setText("Error");
                    }
                    status.setText("Member "+ mainactivity_usermemberslistitems.getName() +" deleted");
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

    //1.5.Start Selection_Userlist class
    public class Selection_Userlist implements Serializable {

        private boolean isChecked = false;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
    //End Selection_Userlist class


    //1.6.Start checkmeber class---------------------------------------------------------------------
    public class checkmember extends AsyncTask<String, String , String> {

        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            status.setText("Sending Data to Database");
        }

        //Connect to DB, check if entered email (member to add) exists and find userID and username from logged-in-user,
        //If member-user connection does not already exist, then insert new member-user connection in usermember table
        @SuppressLint("WrongThread")
        @Override
        public String doInBackground(String... strings) {
            try{
                con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
                if(con == null){
                    z = "Check Your Internet Connection";
                }
                else{

                    String sql = "SELECT userID, email FROM register WHERE email = '" + emailmembertoadd.getText() +"'";
                    ResultSet rs = con.createStatement().executeQuery(sql);
                    while (rs.next()) {
                        memberID = rs.getInt("userID");
                        memberemail = rs.getString("email");
                    }
                    //Load globaluseremail (set from LoginActivity)
                    GlobalClass globalClass = (GlobalClass) getApplicationContext();

                    sql = "SELECT userID, email FROM register WHERE email = '" + globalClass.getGlobaluseremail() +"'";
                    ResultSet rs2 = con.createStatement().executeQuery(sql);
                    while (rs2.next()) {
                        userID = rs2.getInt("userID");
                        useremail = rs2.getString("email");
                    }
                    //check if member-user connection does not already exist
                    sql = "SELECT usermemberID FROM usermember WHERE userID = " + userID + " AND " + "memberID = "+ memberID ;
                    ResultSet rs3 = con.createStatement().executeQuery(sql);
                    if (!rs3.next()&&!(memberID==null)) {
                        //write new member-user connection into DB
                        sql = "INSERT INTO usermember (userID, memberID) VALUES (" + userID + ", " + memberID + ")";
                        ResultSet rs4 = con.createStatement().executeQuery(sql);
                        status.setText(useremail + " has added " + memberemail + " as member");
                    } else {
                        status.setText("Member "+ memberemail +" is already in your list or doesn't exist in the application");
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
            //emailmembertoadd = null;
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