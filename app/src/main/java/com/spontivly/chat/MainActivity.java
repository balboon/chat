package com.spontivly.chat;

import android.content.Intent;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.spontivly.chat.models.SpontivlyUser;
import com.spontivly.chat.services.DatabaseService;
import com.spontivly.chat.services.VolleyController;

public class MainActivity extends AppCompatActivity {
    private Button user1Btn;
    private Button user2Btn;
    private static DatabaseService dbService;
    private SpontivlyUser user1, user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init database services
//        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
//        Network network = new BasicNetwork(new HurlStack());
//        RequestQueue netRequests = new RequestQueue(cache, network);
//        netRequests.start();

        dbService = new DatabaseService();
        // dbService.netRequests = netRequests;
        dbService.netRequests = VolleyController.getInstance(this.getApplicationContext()).getRequestQueue();

//        dbService.getUserInfo(25, new DatabaseService.GetUserInfoCallback() {
//            @Override
//            public void callback(SpontivlyUser user) {
//                user1 = user;
//            }
//        });
//        dbService.getUserInfo(24, new DatabaseService.GetUserInfoCallback() {
//            @Override
//            public void callback(SpontivlyUser user) {
//                user2 = user;
//            }
//        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Spontivly - MainActivity");
        setSupportActionBar(toolbar);

        user1Btn = findViewById(R.id.user1);
        user1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(25);
            }
        });

        user2Btn = findViewById(R.id.user2);
        user2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(24);
            }
        });
    }

    public void login(int userId) {
//        final Intent intent = new Intent(this, EventActivity.class);
//
//        if (userId == 1) {
//            intent.putExtra("User", user1);
//        }
//        else if (userId == 2) {
//            intent.putExtra("User", user2);
//        }
//
//        startActivity(intent);

        dbService.getUserInfo(userId, new DatabaseService.GetUserInfoCallback() {
            @Override
            public void callback(SpontivlyUser user) {
                final Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("User", user);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });
    }
}
