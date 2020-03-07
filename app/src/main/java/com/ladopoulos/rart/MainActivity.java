package com.ladopoulos.rart;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new AsyncCaller().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        final ImageView favs = findViewById(R.id.painting);
        final String paintingImage = "https://images.metmuseum.org/CRDImages/ad/original/DT5361.jpg";

        Picasso.get().load(paintingImage)
                .noFade().into(favs);
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        RequestQueue firstQueue = Volley.newRequestQueue(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tAccessing the Art of the World...");
            pdLoading.setCancelable(false);
            pdLoading.show();
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
                            Log.e("RESPONSE",response.toString());
                            if(response.length()==0){
                                Toast toast = Toast.makeText(MainActivity.this,"0 Response", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            } else {
                                try {
                                    int total = (int) response.get("total");
                                    Log.e("TOTAL", String.valueOf(total));
                                    JSONArray quote = response.getJSONArray("objectIDs");
                                    Log.e("OBJECTID", quote.toString());
                                    pdLoading.dismiss();
                                    //String quoteLine = quote.getString("body");
                                    //String author = quote.getString("author");
                                    //JSONArray tagsTemp = quote.getJSONArray("tags");
                                    //String tags = (tagsTemp.toString()).replaceAll("\\[|]|\"", "");
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
}
