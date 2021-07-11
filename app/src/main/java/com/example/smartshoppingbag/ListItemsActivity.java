package com.example.smartshoppingbag;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartshoppingbag.Connection.ConnectionClass;

public class ListItemsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView dateText;
    private TextView listNameText;
    String listname;
    String listdate;
    Connection con;
    Button savebtn;
    ImageButton insertbtn;
    EditText editTextListItem;
    Spinner itemCategorySpinner_2;
    Spinner costBeforeCommaSpinner_2;
    Spinner costAfterCommaSpinner_2;
    TextView status_listitems;
    EditText enterListName;

    private ArrayList<ListItemsActivity_RecyclerView> itemListArray; //Item List Array
    private MyListItemsAdapter myListItemsAdapter; //Array Adapter
    private RecyclerView recyclerViewMyListItems; //RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean success = false; // boolean

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listitems);
        listNameText = findViewById(R.id.listNameText);
        dateText = findViewById(R.id.dateText);
        savebtn = (Button)findViewById(R.id.saveChangesMyListItemsButton);
        insertbtn = (ImageButton)findViewById(R.id.insertItemButton);
        editTextListItem = (EditText)findViewById(R.id.editTextListItem2);
        itemCategorySpinner_2 = (Spinner)findViewById(R.id.itemCategory2);
        costBeforeCommaSpinner_2 = (Spinner)findViewById(R.id.cost_before_comma2);
        costAfterCommaSpinner_2 = (Spinner)findViewById(R.id.cost_after_comma2);
        status_listitems = (TextView)findViewById(R.id.status_listitems);
        enterListName = (EditText)findViewById(R.id.enterListName);
        FillSpinner();

        //Load str (email txt) from LoginActivity and intent to MainActivity
        /*String str = getIntent().getStringExtra("message_key_email");
        Toast.makeText(ListItemsActivity.this,"email:"+str,Toast.LENGTH_LONG).show();
        new Intent(ListItemsActivity.this, MainActivity.class).putExtra("message_key_email", str);*/
        //


        findViewById(R.id.showCalender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //Load listname from MainActivity
        final String listID = getIntent().getStringExtra("message_keyID");
        //itemListArray and Recyclerview Declaration for list Items
        itemListArray = new ArrayList<ListItemsActivity_RecyclerView>();
        recyclerViewMyListItems = (RecyclerView) findViewById(R.id.recyclerViewMyListItems);
        recyclerViewMyListItems.setHasFixedSize(true);
        //use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyListItems.setLayoutManager(mLayoutManager);

        // Calling AsyncTask/SyncData for Items List recyclerview
        //SyncData_ListItems orderData_ListItems = new SyncData_ListItems();
        //orderData_ListItems.execute("");

        try{
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
            if(con == null){

            } else {

                //find listname from dbo.userlist
                String sql = "SELECT listname, listdate FROM userlist WHERE listID = " + listID ;
                ResultSet rs1 = con.createStatement().executeQuery(sql);
                while (rs1.next()) {
                    listname = rs1.getString("listname");
                    listdate = rs1.getString("listdate");
                    //write Listname in enterListName (Edit Text)
                    enterListName.setText(listname);
                    dateText.setText(listdate);

                }

            }

        } catch (Exception e){

            //status.setText("Error");
        }

        listNameText.setText("Selected list " + listname);

        //edit text change text on sql, when user enters/corrects his text
        /*enterListName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String listNameAfter = enterListName.getText().toString();
                listNameText.setText("Entered list name: " + listNameAfter);

            }
        });*/


        String listNameAfter = enterListName.getText().toString();
        listNameText.setText("Entered list name: " + listNameAfter);

        enterListName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String listNameAfter = enterListName.getText().toString();
                    listNameText.setText("Entered list name: " + listNameAfter);
                    if(listname != listNameAfter){
                        try {
                            con = connectionClass(ConnectionClass.un.toString(), ConnectionClass.pass.toString(), ConnectionClass.db.toString(), ConnectionClass.server.toString());
                            if (con == null) {

                            } else {
                                String query = "UPDATE userlist SET listname ='" + listNameAfter + "' WHERE listID =" + listID ;
                                PreparedStatement stmt = con.prepareStatement(query);
                                ResultSet rs = stmt.executeQuery();
                                }

                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            /*e.printStackTrace();
                            status_listitems.setText("Listname renamed to" + listNameAfter);*/
                        }

                    }
                }
            }
        });
    }

    public void FillSpinner() {
        try {
            con = connectionClass(ConnectionClass.un.toString(), ConnectionClass.pass.toString(), ConnectionClass.db.toString(), ConnectionClass.server.toString());
            if (con == null) {

            } else {
                String query = "SELECT itemcategoryname FROM itemcategories";
                PreparedStatement stmt=con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                ArrayList<String> data = new ArrayList<String>();

                while (rs.next()){
                    String itemcategoryname = rs.getString( "itemcategoryname");
                    data.add(itemcategoryname);
                }

                ArrayAdapter array = new ArrayAdapter(this, android.R.layout.simple_list_item_1,data);
                itemCategorySpinner_2.setAdapter(array);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            status_listitems.setText("Query unsuccessfull");
        }
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
        String date = year + "-" + addmonth + "-" + dayOfMonth;
        dateText.setText(date);
        //Update Listdate based on ListID
        final String listID = getIntent().getStringExtra("message_keyID");
        try{
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.server.toString());
            if(con == null){

            } else {

                //update listdate in dbo.userlist
                String query = "UPDATE userlist SET listdate = '" + dateText.getText() + "' WHERE listID =" + listID ;
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                }

        } catch (Exception e){
            System.out.println(e.getMessage());
            //status.setText("Error");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class MyListItemsAdapter extends RecyclerView.Adapter<MyListItemsAdapter.ViewHolder> {
        private List<ListItemsActivity_RecyclerView> values_mylistitems;
        public Context context_listItems;
        private int checkedPosition_RecyclerView = -1;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            // public textView and layout
            public EditText editTextListItem;
            public Spinner itemCategory;
            public Spinner cost_before_comma;
            public TextView cost_comma;
            public Spinner cost_after_comma;
            public ImageView img_remove_listitem;
            public View layout;
            //public ImageView mylist_popup_options;

            public ViewHolder(View view)
            {
                super(view);
                layout = view;

                editTextListItem = (EditText) view.findViewById(R.id.editTextListItem);
                itemCategory = (Spinner) view.findViewById(R.id.itemCategory);
                cost_before_comma = (Spinner) view.findViewById(R.id.cost_before_comma);
                cost_comma = (TextView) view.findViewById(R.id.cost_comma);
                cost_after_comma = (Spinner) view.findViewById(R.id.cost_after_comma);
                img_remove_listitem = (ImageView) view.findViewById(R.id.img_remove_listitem);
                
            }
        }

        //Start AsyncTask/SyncData to Load Data to recyclerViewMyLists-----------------------------
        private class SyncData_ListItems extends AsyncTask<String, String, String>
        {
            String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
            ProgressDialog progress;

            //Starts the progress dailog
            @Override
            protected void onPreExecute()
            {
                progress = ProgressDialog.show(ListItemsActivity.this, "Synchronising",
                        "MyItemList is Loading...", true);
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
                    /*
                        //Load str (email txt) from LoginActivity
                        Intent in = getIntent();
                        String str = in.getStringExtra("message_key");


                        //Find userID from intented str (email of the logged-in-user)
                        String sql = "SELECT userID FROM register WHERE email = '" + str +"'";
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
                                    status.setText("Second itemArrayMylists Query successfull");

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
                        */

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

            //Load items of itemListArray to recyclerViewMyListItems in ListItemsActivity by using MyListItemsAdapter
            @Override
            protected void onPostExecute(String msg)
            {
                progress.dismiss();
                Toast.makeText(ListItemsActivity.this, msg + "", Toast.LENGTH_LONG).show();
                if (success == false)
                {
                }
                else {
                    try
                    {
                        myListItemsAdapter = new MyListItemsAdapter(itemListArray , ListItemsActivity.this);
                        recyclerViewMyListItems.setAdapter(myListItemsAdapter);
                    } catch (Exception ex)
                    {

                    }
                }
            }
        }
        //End AsyncTask/SyncData to Load Data to recyclerViewMyLists-------------------------------

        // Constructor
        public MyListItemsAdapter(List<ListItemsActivity_RecyclerView> myDataset_myItems,Context context_listItems)
        {
            values_mylistitems = myDataset_myItems;
            this.context_listItems = context_listItems;
        }

        // Create new views (invoked by the layout manager) and inflates
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_activity_recycler_view, parent, false);
            return new ViewHolder(view);
        }

        // Binding items to the view
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ListItemsActivity_RecyclerView listItemsActivity_RecyclerView = values_mylistitems.get(position);
            holder.editTextListItem.setText(listItemsActivity_RecyclerView.getEditTextListItem());
            //holder.itemCategory.setText(listItemsActivity_RecyclerView.getItemCategory());
            //holder.cost_before_comma.setText(listItemsActivity_RecyclerView.getCostBeforeComma());
            //holder.cost_after_comma.setText(listItemsActivity_RecyclerView.getCostAfterComma());

            /*holder.mylist_popup_options.setOnClickListener(new View.OnClickListener(){
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

                                    intentMain_edit.putExtra("message_key", listID);
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
                                            MainActivity.SyncData_MyLists orderData = new MainActivity.SyncData_MyLists();
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
            });*/

            /*
            //highlights only the selected position
            if(checkedPosition==position){
                holder.layout.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.layout.setBackgroundColor(Color.WHITE);
            }
            */
        }

        // get item count returns the list item count
        @Override
        public int getItemCount() {
            return values_mylistitems == null ? 0 : values_mylistitems.size();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////


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



