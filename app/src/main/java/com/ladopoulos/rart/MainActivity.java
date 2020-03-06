package com.ladopoulos.rart;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        final ImageView favs = findViewById(R.id.painting);
        final String paintingImage = "https://images.metmuseum.org/CRDImages/ep/original/DT1567.jpg";

        Picasso.get().load(paintingImage)
                .noFade().into(favs);
    }
}
