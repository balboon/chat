package com.spontivly.chat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.spontivly.chat.models.SpontivlyEventChat;
import com.spontivly.chat.models.SpontivlyEventChatMessage;
import com.spontivly.chat.models.SpontivlyUser;
import com.spontivly.chat.services.DatabaseService;
import com.spontivly.chat.services.VolleyController;

import java.util.Date;

public class MessageActivity extends AppCompatActivity {

    private DatabaseService dbService;
    private SpontivlyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Init database services
//        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
//        Network network = new BasicNetwork(new HurlStack());
//        RequestQueue netRequests = new RequestQueue(cache, network);
//        netRequests.start();

        dbService = new DatabaseService();
        dbService.netRequests = VolleyController.getInstance(this.getApplicationContext()).getRequestQueue();

        Intent intent = getIntent();
        int mImageView = intent.getIntExtra("imageID", 0);
        int eventId = intent.getIntExtra("eventId", 0);
        String eventTitle = intent.getStringExtra("eventTitle");
        this.user = (SpontivlyUser) intent.getSerializableExtra("User");


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(eventTitle);
        toolbar.setSubtitle("Peter, Al");
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(mImageView);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Check if we're running Android 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
        } else { // Builds below API 21
            // Implement this feature without material design
        }

        dbService.getEventChat(eventId, new DatabaseService.GetEventChatCallback() {
            @Override
            public void callback(SpontivlyEventChat eventChat) {
                if (eventChat.lastPostedMessage != null)
                    Log.i("Spontivly", eventChat.lastPostedMessage.toString());
                // Load event chat
                for (SpontivlyEventChatMessage msg : eventChat.chatMessages) {
                    Date postDate = new Date(msg.createdAt * 1000);
                    // Check if msg was written by user and place accordingly
                    if (msg.posterId == user.userId) {
                        // Place on right side with time only
                        Log.i("Spontivly", "Me: " + postDate.toString() + "," + msg.postedMessage);
                    } else {
                        // Place on left side with poster name and time
                        Log.i("Spontivly", msg.posterFirstName + " "  +
                                msg.posterLastName + ": " + postDate.toString() + "," + msg.postedMessage);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.item1:
                Toast.makeText(this, "Item1 Selected!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "Item2 Selected!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                Toast.makeText(this, "Item3 Selected!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
