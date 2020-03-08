package com.ladopoulos.rart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import com.ladopoulos.rart.BuildConfig;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.squareup.picasso.Target;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SharedPreferences myPrefs;
    boolean expanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        setContentView(R.layout.activity_main);

        File file = new File(Environment.getExternalStorageDirectory() + "/" + "paintingIDs.json");
        if (!file.exists()) {
            myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("NEXT", "0");
            editor.apply();
            PaintingIDs();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView paintingNameTV = findViewById(R.id.paintingName);
        final TextView artistNameTV = findViewById(R.id.artistName);
        TextView paintingYearTV = findViewById(R.id.paintingYear);
        TextView cultureTV = findViewById(R.id.culture);
        final TextView maxTV = findViewById(R.id.total);
        final TextView currentCountTV = findViewById(R.id.alreadyViewed);
        final ImageView paintingImageIV = findViewById(R.id.painting);
        final ImageView previousArrow = findViewById(R.id.previous);
        final ImageView nextArrow = findViewById(R.id.next);
        final ImageView infoImage = findViewById(R.id.info);
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        final String prefsPaintingImage = myPrefs.getString("IMAGE",null);
        final String prefsPaintingName = myPrefs.getString("TITLE",null);
        String prefsArtistName = myPrefs.getString("ARTISTNAME",null);
        String prefsPaintingYear = myPrefs.getString("DATE",null);
        String prefsCulture = myPrefs.getString("CULTURE",null);
        String allTimeTotal = myPrefs.getString("TOTAL",null);
        String currentCountSP = myPrefs.getString("CURRENT",null);
        final LinearLayout buttonsLayout = findViewById(R.id.linearLayout);
        final ScrollView infoMatrix = findViewById(R.id.scroll_View2);
        String currentVersionCode = Integer.toString(BuildConfig.VERSION_CODE);

        if (myPrefs.getBoolean("FIRST_RUN", true)) {
            // Do first run stuff here
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle("Instructions");
            builder1.setMessage(getString(R.string.instructions));
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder1.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    }
            );
            AlertDialog alert11 = builder1.create();
            alert11.show();
            myPrefs.edit().putBoolean("FIRST_RUN", false).apply();
        }

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String versionCode = myPrefs.getString("versionCode", "");
        Log.e("CURRENT VERSION CODE", currentVersionCode);
        Log.e("VERSION CODE", versionCode);
        try {
            if (!versionCode.matches(currentVersionCode)) {
                //Log.e("CURRENT VERSION CODE", "MPIKA");
                myPrefs.edit().putString("versionCode", currentVersionCode).apply();
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        //set icon
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        //set title
                        .setTitle("Updates")
                        //set message
                        .setMessage(getString(R.string.updates))
                        //set positive button
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what would happen when positive button is clicked
                            }
                        })
                        .show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (prefsPaintingImage == null && prefsPaintingName == null && prefsArtistName == null
                && prefsPaintingYear == null && prefsCulture == null && allTimeTotal == null && currentCountSP == null) {
            paintingNameTV.setText("-");
            artistNameTV.setText("-");
            paintingYearTV.setText("-");
            cultureTV.setText("-");
            maxTV.setText("-");
            currentCountTV.setText("-");
        } else {
            paintingNameTV.setText(prefsPaintingName);
            artistNameTV.setText(prefsArtistName);
            artistNameTV.setTextColor(Color.parseColor("#0000ff"));
            artistNameTV.setPaintFlags(artistNameTV.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            paintingYearTV.setText(prefsPaintingYear);
            cultureTV.setText(prefsCulture);
            maxTV.setText(allTimeTotal);
            currentCountTV.setText(currentCountSP);
            Picasso.get().load(prefsPaintingImage).noFade().into(paintingImageIV);
            // handle the value
        }

        previousArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String previousStringZero = myPrefs.getString("NEXT", null);
                assert previousStringZero != null;
                int nextIntZero = Integer.parseInt(previousStringZero);
                if(nextIntZero<=0){
                    Toast toast = Toast.makeText(MainActivity.this,"This is the beginning", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }else{
                try {
                    PreviousPaintingDetails();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                }
            }
        });
        nextArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    NextPaintingDetails();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        artistNameTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String prefsArtistNameClick = myPrefs.getString("ARTISTNAME",null);
                if (!prefsArtistNameClick.matches("")){
                String prefsArtistNameUnderscore = prefsArtistNameClick.replace(" ", "_");
                //Log.e("UNDERSOCRE", prefsArtistNameUnderscore);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/" + prefsArtistNameUnderscore));
                startActivity(browserIntent);
                }else{
                    Toast toast = Toast.makeText(MainActivity.this,"Artist Unknown", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });
        paintingImageIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Image Download");
                builder.setMessage("Are you sure you want to download this image?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, int which) {
                        final ProgressDialog imageDownload = new ProgressDialog(MainActivity.this);
                        imageDownload.setMessage("\tDownloading...");
                        imageDownload.setCancelable(false);
                        imageDownload.show();
                        String prefsPaintingImageClick = myPrefs.getString("IMAGE",null);
                        final String prefsPaintingNameClick = myPrefs.getString("TITLE",null);
                        Picasso.get()
                                .load(prefsPaintingImageClick)
                                .into(new Target() {
                                          @Override
                                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                              try {
                                                  String root = Environment.getExternalStorageDirectory().toString();
                                                  File myDir = new File(root + "/iART");

                                                  if (!myDir.exists()) {
                                                      myDir.mkdirs();
                                                  }

                                                  String name = prefsPaintingNameClick + ".jpg";
                                                  myDir = new File(myDir, name);
                                                  FileOutputStream out = new FileOutputStream(myDir);
                                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                  imageDownload.dismiss();
                                              } catch(Exception e){
                                                  // some action
                                              }
                                          }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        imageDownload.dismiss();
                                    }

                                          @Override
                                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                                          }
                                      }
                                );

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }
        });
        paintingImageIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!expanded) {
                    paintingImageIV.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    paintingImageIV.requestLayout();
                    buttonsLayout.setVisibility(View.GONE);
                    infoMatrix.setVisibility(View.GONE);
                    expanded = true;
                }else{
                    paintingImageIV.getLayoutParams().height = 0;
                    paintingImageIV.requestLayout();
                    buttonsLayout.setVisibility(View.VISIBLE);
                    infoMatrix.setVisibility(View.VISIBLE);
                    expanded = false;
                }
            }
        });
        infoImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("API Info");
                builder1.setMessage(getString(R.string.information)+getString(R.string.contactDeveloper)+getString(R.string.email));
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder1.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        }
                );
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    public void PaintingIDs(){
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

    public void FirstPaintingDetails() throws IOException, JSONException {
        final TextView paintingNameFTV = findViewById(R.id.paintingName);
        final TextView artistNameFTV = findViewById(R.id.artistName);
        final TextView paintingYearFTV = findViewById(R.id.paintingYear);
        final TextView cultureFTV = findViewById(R.id.culture);
        final TextView maxFTV = findViewById(R.id.total);
        final TextView currentCountFTV = findViewById(R.id.alreadyViewed);
        final ImageView paintingImageFIV = findViewById(R.id.painting);
        JSONArray jsonArray;
        final ProgressDialog pdDetails = new ProgressDialog(MainActivity.this);
        RequestQueue secondQueue = Volley.newRequestQueue(MainActivity.this);
        //this method will be running on UI thread
        pdDetails.setMessage("\tLoading...");
        pdDetails.setCancelable(false);
        pdDetails.show();


            InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/"+"paintingIDs.json");
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data, StandardCharsets.UTF_8);
            jsonArray = new JSONArray(json);
            int jsonobject = jsonArray.getInt(0);

        String urlPaintingDetails = "https://collectionapi.metmuseum.org/public/collection/v1/objects/" + jsonobject;

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
                                //Log.e("PAINTING DETAILS", response.toString());
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
                                        artistNameFTV.setTextColor(Color.parseColor("#0000ff"));
                                        artistNameFTV.setPaintFlags(artistNameFTV.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
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
                                String maxStr = myPrefs.getString("TOTAL",null);
                                maxFTV.setText(maxStr);
                                currentCountFTV.setText("1");
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

    public void NextPaintingDetails() throws IOException, JSONException {
        final TextView paintingNameFTV = findViewById(R.id.paintingName);
        final TextView artistNameFTV = findViewById(R.id.artistName);
        final TextView paintingYearFTV = findViewById(R.id.paintingYear);
        final TextView cultureFTV = findViewById(R.id.culture);
        final TextView maxFTV = findViewById(R.id.total);
        final TextView currentCountFTV = findViewById(R.id.alreadyViewed);
        final ImageView paintingImageFIV = findViewById(R.id.painting);
        JSONArray jsonArray;
        final ProgressDialog pdDetails = new ProgressDialog(MainActivity.this);
        RequestQueue secondQueue = Volley.newRequestQueue(MainActivity.this);
        //this method will be running on UI thread
        pdDetails.setMessage("\tLoading...");
        pdDetails.setCancelable(false);
        pdDetails.show();

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String nextString = myPrefs.getString("NEXT", null);
        assert nextString != null;
        final int nextInt = Integer.parseInt(nextString);

        InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/"+"paintingIDs.json");
        int size = is.available();
        byte[] data = new byte[size];
        is.read(data);
        is.close();
        String json = new String(data, StandardCharsets.UTF_8);
        jsonArray = new JSONArray(json);
        int jsonobject = jsonArray.getInt(nextInt+1);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("NEXT", String.valueOf(nextInt+1));
        editor.apply();

        String urlPaintingDetails = "https://collectionapi.metmuseum.org/public/collection/v1/objects/" + jsonobject;

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
                            //Log.e("PAINTING DETAILS", response.toString());
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
                                    paintingNameFTV.setTextColor(Color.parseColor("#000000"));
                                    paintingNameFTV.setText(response.getString("title"));
                                }

                                if(response.getString("artistDisplayName").matches("")){
                                    editor.putString("ARTISTNAME", "Not available");
                                    artistNameFTV.setTextColor(Color.parseColor("#ff0000"));
                                    artistNameFTV.setText(R.string.notAvailable);
                                }else{
                                    editor.putString("ARTISTNAME", (response.getString("artistDisplayName")));
                                    artistNameFTV.setText(response.getString("artistDisplayName"));
                                    artistNameFTV.setTextColor(Color.parseColor("#0000ff"));
                                    artistNameFTV.setPaintFlags(artistNameFTV.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                                }

                                if(response.getString("objectEndDate").matches("")){
                                    editor.putString("DATE", "Not available");
                                    paintingYearFTV.setTextColor(Color.parseColor("#ff0000"));
                                    paintingYearFTV.setText(R.string.notAvailable);
                                }else{
                                    editor.putString("DATE", (response.getString("objectEndDate")));
                                    paintingYearFTV.setTextColor(Color.parseColor("#000000"));
                                    paintingYearFTV.setText(response.getString("objectEndDate"));
                                }

                                if(response.getString("culture").matches("")){
                                    editor.putString("CULTURE", "Not available");
                                    cultureFTV.setTextColor(Color.parseColor("#ff0000"));
                                    cultureFTV.setText(R.string.notAvailable);
                                }else{
                                    editor.putString("CULTURE", (response.getString("culture")));
                                    cultureFTV.setTextColor(Color.parseColor("#000000"));
                                    cultureFTV.setText(response.getString("culture"));
                                }

                                editor.apply();

                                String primaryImage = response.getString("primaryImage");
                                Picasso.get().load(primaryImage).noFade().into(paintingImageFIV);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String maxStr = myPrefs.getString("TOTAL",null);
                            maxFTV.setText(maxStr);
                            currentCountFTV.setText(String.valueOf(nextInt+2));
                            myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.putString("CURRENT", String.valueOf(nextInt+2));
                            editor.apply();
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

    public void PreviousPaintingDetails() throws IOException, JSONException {
        final TextView paintingNameFTV = findViewById(R.id.paintingName);
        final TextView artistNameFTV = findViewById(R.id.artistName);
        final TextView paintingYearFTV = findViewById(R.id.paintingYear);
        final TextView cultureFTV = findViewById(R.id.culture);
        final TextView maxFTV = findViewById(R.id.total);
        final TextView currentCountFTV = findViewById(R.id.alreadyViewed);
        final ImageView paintingImageFIV = findViewById(R.id.painting);
        JSONArray jsonArray;
        final ProgressDialog pdDetails = new ProgressDialog(MainActivity.this);
        RequestQueue secondQueue = Volley.newRequestQueue(MainActivity.this);
        //this method will be running on UI thread
        pdDetails.setMessage("\tLoading...");
        pdDetails.setCancelable(false);
        pdDetails.show();

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String nextString = myPrefs.getString("NEXT", null);
        assert nextString != null;
        final int nextInt = Integer.parseInt(nextString);

        InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/"+"paintingIDs.json");
        int size = is.available();
        byte[] data = new byte[size];
        is.read(data);
        is.close();
        String json = new String(data, StandardCharsets.UTF_8);
        jsonArray = new JSONArray(json);
        int jsonobject = jsonArray.getInt(nextInt-1);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("NEXT", String.valueOf(nextInt-1));
        editor.apply();

        String urlPaintingDetails = "https://collectionapi.metmuseum.org/public/collection/v1/objects/" + jsonobject;

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
                            //Log.e("PAINTING DETAILS", response.toString());
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
                                    paintingNameFTV.setTextColor(Color.parseColor("#000000"));
                                    paintingNameFTV.setText(response.getString("title"));
                                }

                                if(response.getString("artistDisplayName").matches("")){
                                    editor.putString("ARTISTNAME", "Not available");
                                    artistNameFTV.setTextColor(Color.parseColor("#ff0000"));
                                    artistNameFTV.setText(R.string.notAvailable);
                                }else{
                                    editor.putString("ARTISTNAME", (response.getString("artistDisplayName")));
                                    artistNameFTV.setText(response.getString("artistDisplayName"));
                                    artistNameFTV.setTextColor(Color.parseColor("#0000ff"));
                                    artistNameFTV.setPaintFlags(artistNameFTV.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                                }

                                if(response.getString("objectEndDate").matches("")){
                                    editor.putString("DATE", "Not available");
                                    paintingYearFTV.setTextColor(Color.parseColor("#ff0000"));
                                    paintingYearFTV.setText(R.string.notAvailable);
                                }else{
                                    editor.putString("DATE", (response.getString("objectEndDate")));
                                    paintingYearFTV.setTextColor(Color.parseColor("#000000"));
                                    paintingYearFTV.setText(response.getString("objectEndDate"));
                                }

                                if(response.getString("culture").matches("")){
                                    editor.putString("CULTURE", "Not available");
                                    cultureFTV.setTextColor(Color.parseColor("#ff0000"));
                                    cultureFTV.setText(R.string.notAvailable);
                                }else{
                                    editor.putString("CULTURE", (response.getString("culture")));
                                    cultureFTV.setTextColor(Color.parseColor("#000000"));
                                    cultureFTV.setText(response.getString("culture"));
                                }

                                editor.apply();

                                String primaryImage = response.getString("primaryImage");
                                Picasso.get().load(primaryImage).noFade().into(paintingImageFIV);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String maxStr = myPrefs.getString("TOTAL",null);
                            maxFTV.setText(maxStr);
                            currentCountFTV.setText(String.valueOf(nextInt));
                            myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.putString("CURRENT", String.valueOf(nextInt));
                            editor.apply();
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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}