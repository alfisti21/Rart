package com.ladopoulos.rart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        setContentView(R.layout.activity_main);

        File file = new File(Environment.getExternalStorageDirectory() + "/" + "paintingIDs.json");
        if (!file.exists()) {
            PaintingIDs();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView paintingNameTV = findViewById(R.id.paintingName);
        TextView artistNameTV = findViewById(R.id.artistName);
        TextView paintingYearTV = findViewById(R.id.paintingYear);
        TextView cultureTV = findViewById(R.id.culture);
        ImageView paintingImageIV = findViewById(R.id.painting);
        ImageView previousArrow = findViewById(R.id.previous);
        ImageView nextArrow = findViewById(R.id.next);
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String prefsPaintingImage = myPrefs.getString("IMAGE",null);
        String prefsPaintingName = myPrefs.getString("TITLE",null);
        String prefsArtistName = myPrefs.getString("ARTISTNAME",null);
        String prefsPaintingYear = myPrefs.getString("DATE",null);
        String prefsCulture = myPrefs.getString("CULTURE",null);

        if (prefsPaintingImage == null && prefsPaintingName == null && prefsArtistName == null && prefsPaintingYear == null && prefsCulture == null) {
            paintingNameTV.setText("-");
            artistNameTV.setText("-");
            paintingYearTV.setText("-");
            cultureTV.setText("-");
        } else {
            paintingNameTV.setText(prefsPaintingName);
            artistNameTV.setText(prefsArtistName);
            paintingYearTV.setText(prefsPaintingYear);
            cultureTV.setText(prefsCulture);
            Picasso.get().load(prefsPaintingImage).noFade().into(paintingImageIV);
            // handle the value
        }

        previousArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        nextArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

    public void  PaintingIDs(){
        final ProgressDialog pdIDs = new ProgressDialog(MainActivity.this);
        RequestQueue firstQueue = Volley.newRequestQueue(MainActivity.this);

            //this method will be running on UI thread
            pdIDs.setMessage("\tFetching the Art of the World...");
            pdIDs.setCancelable(false);
            pdIDs.show();

            //this method will be running on background thread so don't update UI from here
            //do your long running http tasks here,you don't want to pass argument and u can access the parent class' variable url over here
            String urlPaintingsWithPictures = "https://collectionapi.metmuseum.org/public/collection/v1/search?hasImages=true&q=\"\"";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, urlPaintingsWithPictures, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e("RESPONSE",response.toString());
                            if(response.length()==0){
                                Toast toast = Toast.makeText(MainActivity.this,"0 Response", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            } else {
                                try {
                                    int total = (int) response.get("total");
                                    JSONArray quote = response.getJSONArray("objectIDs");
                                    mCreateAndSaveFile("paintingIDs.json", quote.toString());
                                    myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = myPrefs.edit();
                                    editor.putString("TOTAL", String.valueOf(total));
                                    editor.apply();
                                    pdIDs.dismiss();
                                } catch (JSONException e) {
                                    Log.e("ERROR", e.toString());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyError errorCode = (VolleyError) error.getCause();
                            Toast toast = Toast.makeText(MainActivity.this,errorCode.toString(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            firstQueue.add(jsonObjectRequest);
    }

    public void FirstPaintingDetails(){
        final TextView paintingNameFTV = findViewById(R.id.paintingName);
        final TextView artistNameFTV = findViewById(R.id.artistName);
        final TextView paintingYearFTV = findViewById(R.id.paintingYear);
        final TextView cultureFTV = findViewById(R.id.culture);
        final ImageView paintingImageFIV = findViewById(R.id.painting);
        JSONArray jsonArray;
        final ProgressDialog pdDetails = new ProgressDialog(MainActivity.this);
        RequestQueue secondQueue = Volley.newRequestQueue(MainActivity.this);
        //this method will be running on UI thread
        pdDetails.setMessage("\tLoading...");
        pdDetails.setCancelable(false);
        pdDetails.show();

        try {
            InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/"+"paintingIDs.json");
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data, StandardCharsets.UTF_8);
            jsonArray = new JSONArray(json);
            JSONObject jsonobject = jsonArray.getJSONObject(0);
            Log.e("JSONOBJECT", jsonobject.toString());

        } catch (JSONException | IOException je) {
            je.printStackTrace();
        }

        String urlPaintingDetails = "https://collectionapi.metmuseum.org/public/collection/v1/objects/266133";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlPaintingDetails, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e("RESPONSE",response.toString());
                            if(response.length()==0){
                                Toast toast = Toast.makeText(MainActivity.this,"0 Response", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            } else {
                                Log.e("PAINTING DETAILS", response.toString());
                                try {
                                    myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = myPrefs.edit();
                                    editor.putString("IMAGE", response.getString("primaryImage"));
                                    if(response.getString("title").matches("")){
                                        editor.putString("TITLE", "Not available");
                                        paintingNameFTV.setTextColor(Color.parseColor("#ff0000"));
                                        paintingNameFTV.setText(R.string.notAvailable);
                                    }else{
                                        editor.putString("TITLE", response.getString("title"));
                                        paintingNameFTV.setText(response.getString("title"));
                                    }

                                    if(response.getString("artistDisplayName").matches("")){
                                        editor.putString("ARTISTNAME", "Not available");
                                        artistNameFTV.setTextColor(Color.parseColor("#ff0000"));
                                        artistNameFTV.setText(R.string.notAvailable);
                                    }else{
                                        editor.putString("ARTISTNAME", (response.getString("artistDisplayName")));
                                        artistNameFTV.setText(response.getString("artistDisplayName"));
                                    }

                                    if(response.getString("objectEndDate").matches("")){
                                        editor.putString("DATE", "Not available");
                                        paintingYearFTV.setTextColor(Color.parseColor("#ff0000"));
                                        paintingYearFTV.setText(R.string.notAvailable);
                                    }else{
                                        editor.putString("DATE", (response.getString("objectEndDate")));
                                        paintingYearFTV.setText(response.getString("objectEndDate"));
                                    }

                                    if(response.getString("culture").matches("")){
                                        editor.putString("CULTURE", "Not available");
                                        cultureFTV.setTextColor(Color.parseColor("#ff0000"));
                                        cultureFTV.setText(R.string.notAvailable);
                                    }else{
                                        editor.putString("CULTURE", (response.getString("culture")));
                                        cultureFTV.setText(response.getString("culture"));
                                    }

                                    editor.apply();

                                    String primaryImage = response.getString("primaryImage");
                                    Picasso.get().load(primaryImage).noFade().into(paintingImageFIV);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                pdDetails.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyError errorCode = (VolleyError) error.getCause();
                            Toast toast = Toast.makeText(MainActivity.this,errorCode.toString(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            secondQueue.add(jsonObjectRequest);
    }

    public void mCreateAndSaveFile(String params, String mJsonResponse) {
        try {
            FileWriter file = new FileWriter(Environment.getExternalStorageDirectory() + "/" + params);
            file.write(mJsonResponse);
            file.flush();
            file.close();
            FirstPaintingDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
