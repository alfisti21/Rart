package com.ladopoulos.rart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
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
import java.io.FileWriter;
import java.io.IOException;
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
            new AsyncCallerPaintingIDs().execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final ImageView favs = findViewById(R.id.painting);
        /*myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String totalString = myPrefs.getString("TOTAL", null);
        Log.e("totalString", totalString);*/
        final String paintingImage = "https://images.metmuseum.org/CRDImages/ad/original/DT5361.jpg";
        Picasso.get().load(paintingImage).noFade().into(favs);
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncCallerPaintingIDs extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdIDs = new ProgressDialog(MainActivity.this);
        RequestQueue firstQueue = Volley.newRequestQueue(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdIDs.setMessage("\tFetching the Art of the World...");
            pdIDs.setCancelable(false);
            pdIDs.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            //this method will be running on background thread so don't update UI from here
            //do your long running http tasks here,you don't want to pass argument and u can access the parent class' variable url over here
            String urlRandomQuote = "https://collectionapi.metmuseum.org/public/collection/v1/search?hasImages=true&q=\"\"";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, urlRandomQuote, null, new Response.Listener<JSONObject>() {
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


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //this method will be running on UI thread
            //pdLoading.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncCallerPaintingDetails extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdDetails = new ProgressDialog(MainActivity.this);
        RequestQueue secondQueue = Volley.newRequestQueue(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdDetails.setMessage("\tLoading...");
            pdDetails.setCancelable(false);
            pdDetails.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            //this method will be running on background thread so don't update UI from here
            //do your long running http tasks here,you don't want to pass argument and u can access the parent class' variable url over here
            String urlRandomQuote = "https://collectionapi.metmuseum.org/public/collection/v1/search?hasImages=true&q=\"\"";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, urlRandomQuote, null, new Response.Listener<JSONObject>() {
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
                                    pdDetails.dismiss();
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
            secondQueue.add(jsonObjectRequest);


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //this method will be running on UI thread
            //pdLoading.dismiss();
        }
    }

    public void mCreateAndSaveFile(String params, String mJsonResponse) {
        try {
            FileWriter file = new FileWriter(Environment.getExternalStorageDirectory() + "/" + params);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
