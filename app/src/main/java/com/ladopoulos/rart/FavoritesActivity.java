package com.ladopoulos.rart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    private RecyclerView.Adapter mAdapter;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        mydb = new DBHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recycler);

        // data to populate the RecyclerView with
        ArrayList<String> paintingNames = mydb.getAllPaintings();

        // set up the RecyclerView
        LinearLayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                lm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new MyAdapter(this, paintingNames);
        ((MyAdapter) mAdapter).setClickListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + mAdapter.getItemId(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

}
