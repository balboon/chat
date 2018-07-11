package com.spontivly.chat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.spontivly.chat.models.SpontivlyEvent;
import com.spontivly.chat.models.SpontivlyUser;
import com.spontivly.chat.services.DatabaseService;
import com.spontivly.chat.services.VolleyController;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ChatItem> mChatList;
    private FloatingActionButton insertBtn;
    private DatabaseService dbService;
    private SpontivlyUser user;
    private ArrayList<SpontivlyEvent> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dbService = new DatabaseService();
        dbService.netRequests = VolleyController.getInstance(this.getApplicationContext()).getRequestQueue();

        Intent intent = getIntent();
        user = (SpontivlyUser) intent.getSerializableExtra("User");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Events for " + user.firstName + " " + user.lastName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mChatList = new ArrayList<>();
        createChatList();
        buildRecyclerView();

        insertBtn = findViewById(R.id.button_insert);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Insert Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // Check if we're running Android 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
        } else { // Builds below API 21
            // Implement this feature without material design
        }
    }

    public void createChatList() {
        dbService.getChatEvents(new DatabaseService.GetActiveCallback() {
            @Override
            public void callback(ArrayList<SpontivlyEvent> response) {
                events = response;
//                Log.i("Spontively", events.size() + "");
//                for (int i=0; i<response.size(); i++) {
//                    SpontivlyEvent e = response.get(i);
//                    if ((i%3) == 0)
//                        mChatList.add(new ChatItem(R.drawable.ic_android, e.title, "Members..."));
//                    else if ((i%3) == 1)
//                        mChatList.add(new ChatItem(R.drawable.ic_local_dining, e.title, "Members..."));
//                    else if ((i%3) == 2)
//                        mChatList.add(new ChatItem(R.drawable.ic_local_car_wash, e.title, "Members..."));
//                }
                for (SpontivlyEvent e : response) {
                    Log.i("Spontively", e.title);
                    mChatList.add(new ChatItem(R.drawable.ic_android, e.title, "Members..."));
                }
                mAdapter.notifyDataSetChanged();

            }
        });
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ChatAdapter(mChatList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openMessage(position);
            }
        });
    }

    public void openMessage(int position) {
        Intent intent = new Intent(this, MessageActivity.class);
        int image = mAdapter.getImage(position);
        intent.putExtra("imageID", image);
        intent.putExtra("eventId", events.get(position).eventId);
        intent.putExtra("eventTitle", events.get(position).title);
        intent.putExtra("User", this.user);

        startActivity(intent);
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
