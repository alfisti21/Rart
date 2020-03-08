package com.ladopoulos.rart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class MoreInfoActivity extends AppCompatActivity {
    SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView mediumTV = findViewById(R.id.medium);
        TextView departmentTV = findViewById(R.id.department);
        TextView periodTV = findViewById(R.id.period);
        TextView creditLineTV = findViewById(R.id.credit);
        TextView categoryTV = findViewById(R.id.category);
        TextView bioTV = findViewById(R.id.bio);
        TextView dimensionsTV = findViewById(R.id.dimensions);

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String prefsMedium = myPrefs.getString("MEDIUM","");
        String prefsDepartment = myPrefs.getString("DEPARTMENT","");
        String prefsPeriod = myPrefs.getString("PERIOD","");
        String prefsCreditLine = myPrefs.getString("CREDITS","");
        String prefsCategory = myPrefs.getString("CATEGORY","");
        String prefsBio = myPrefs.getString("BIO","");
        String prefsDimensions = myPrefs.getString("DIMENSIONS","");

        if(prefsMedium.matches("")){
            mediumTV.setTextColor(Color.parseColor("#ff0000"));
            mediumTV.setText(R.string.notAvailable);
        }else{
            mediumTV.setText(prefsMedium);
        }

        if(prefsDepartment.matches("")){
            departmentTV.setTextColor(Color.parseColor("#ff0000"));
            departmentTV.setText(R.string.notAvailable);
        }else{
            departmentTV.setText(prefsDepartment);
        }

        if(prefsPeriod.matches("")){
            periodTV.setTextColor(Color.parseColor("#ff0000"));
            periodTV.setText(R.string.notAvailable);
        }else{
            periodTV.setText(prefsPeriod);
        }

        if(prefsCreditLine.matches("")){
            creditLineTV.setTextColor(Color.parseColor("#ff0000"));
            creditLineTV.setText(R.string.notAvailable);
        }else{
            creditLineTV.setText(prefsCreditLine);
        }

        if(prefsCategory.matches("")){
            categoryTV.setTextColor(Color.parseColor("#ff0000"));
            categoryTV.setText(R.string.notAvailable);
        }else{
            categoryTV.setText(prefsCategory);
        }

        if(prefsBio.matches("")){
            bioTV.setTextColor(Color.parseColor("#ff0000"));
            bioTV.setText(R.string.notAvailable);
        }else{
            bioTV.setText(prefsBio);
        }

        if(prefsDimensions.matches("")){
            dimensionsTV.setTextColor(Color.parseColor("#ff0000"));
            dimensionsTV.setText(R.string.notAvailable);
        }else{
            dimensionsTV.setText(prefsDimensions);
        }
    }
}
