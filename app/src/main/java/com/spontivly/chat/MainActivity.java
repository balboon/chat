package com.spontivly.chat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private Button user3Btn;
    private static DatabaseService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbService = new DatabaseService();
        dbService.netRequests = VolleyController.getInstance(this.getApplicationContext()).getRequestQueue();

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

        user2Btn = findViewById(R.id.user3);
        user2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(50);
            }
        });

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.i("Internet", isConnected + "");
    }

    public void login(int userId) {

        dbService.getUserInfo(userId, new DatabaseService.GetUserInfoCallback() {
            @Override
            public void callback(SpontivlyUser user) {
                final Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("User", user);
                Log.i("MainActivity", user.toString());
                MainActivity.this.startActivity(intent);
                finish();
            }
        });
    }
}
