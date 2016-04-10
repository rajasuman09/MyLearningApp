package com.example.MyLearningApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

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
        String image_file_name = extras.getString("EXTRA_FILENAME");

        if(image_file_name.endsWith("ekadasi")){
            image_file_name = "ekadasi";
        }

       // String fnm = "cat"; //  this is image file name
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        int imgId = getResources().getIdentifier(PACKAGE_NAME+":drawable/"+image_file_name , null, null);
       // System.out.println("IMG ID :: "+imgId);
       // System.out.println("PACKAGE_NAME :: "+PACKAGE_NAME);
//    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),imgId);
        iv_event_image.setImageBitmap(BitmapFactory.decodeResource(getResources(),imgId));

        // displaying selected product name
        //iv_event_image.setImageResource(R.drawable.srisitaram);
        tv_event_title.setText(event_title);
        tv_event_desc.setText(event_desc);

    }


}
