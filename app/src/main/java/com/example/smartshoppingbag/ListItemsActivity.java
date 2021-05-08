package com.example.smartshoppingbag;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;

public class ListItemsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView dateText;
    private TextView listNameText;

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
        String str = intent.getStringExtra("message_key");
        listNameText.setText("Selected list " + str);

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


}