package com.example.MyLearningApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Raja.Chirala on 05/04/2016.
 */
public class FestivalDescription extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.festival_description);

        ImageView iv_event_image = (ImageView) findViewById(R.id.event_image);
        TextView tv_event_title = (TextView) findViewById(R.id.event_title);
        TextView tv_event_desc = (TextView) findViewById(R.id.event_desc);

        Bundle extras = getIntent().getExtras();
        String event_title = extras.getString("EXTRA_EVENTNAME");
        String event_desc = extras.getString("EXTRA_EVENTDESC");

        // displaying selected product name
        iv_event_image.setImageResource(R.drawable.srisitaram);
        tv_event_title.setText(event_title);
        tv_event_desc.setText(event_desc);

    }


}
