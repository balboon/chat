package com.spontivly.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseService dbService;
    private SpontivlyUser user;
    private ArrayList<MessageItem> msgList;
    private SpontivlyEventChatMessage postMsg;
    private int eventId;
    private String membersSubtitle;
    private String currentDateString = "MMMM, dd, yyyy";
    private String lastUser = "not a user";
    private int addDateFlag, addUserFlag = 0;


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

        msgList = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        buildToolbar(toolbar);
        buildRecyclerView(eventId);
    }

    public void buildToolbar(final Toolbar toolbar) {
        Intent intent = getIntent();
        int mImageView = intent.getIntExtra("imageID", 0);
        eventId = intent.getIntExtra("eventId", 0);
        String eventTitle = intent.getStringExtra("eventTitle");
        this.user = (SpontivlyUser) intent.getSerializableExtra("User");
        membersSubtitle = "";
        dbService.getUsersAtEvent(eventId, new DatabaseService.GetUsersCallback() {
            @Override
            public void callback(ArrayList<SpontivlyUser> response) {
                for (int i = 0; i < response.size(); i++) {
                    if (i < response.size() - 1) {
                        if (response.get(i).firstName.toLowerCase().equals(user.firstName.toLowerCase())) {
                            membersSubtitle = new StringBuilder().append(membersSubtitle).append("Me").append(", ").toString();
                        } else {
                            membersSubtitle = new StringBuilder().append(membersSubtitle).append(response.get(i).firstName).append(", ").toString();
                        }
                    } else {
                        if (response.get(i).firstName.toLowerCase().equals(user.firstName.toLowerCase())) {
                            membersSubtitle = new StringBuilder().append(membersSubtitle).append("Me").toString();
                        } else {
                            membersSubtitle = new StringBuilder().append(membersSubtitle).append(response.get(i).firstName).toString();
                        }
                    }
                }
                if (membersSubtitle.equals("")) {
                    toolbar.setSubtitle("No members...");
                } else {
                    toolbar.setSubtitle(membersSubtitle);
                }
            }
        });
        toolbar.setTitle(eventTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(mImageView);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void buildRecyclerView(int eventId) {
        mRecyclerView = findViewById(R.id.messageList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new MessageAdapter(msgList, user);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        dbService.getEventChat(eventId, new DatabaseService.GetEventChatCallback() {
            @Override
            public void callback(SpontivlyEventChat eventChat) {
                if (eventChat.lastPostedMessage != null)
                    Log.i("Spontivly", eventChat.lastPostedMessage.toString());
                // Load event chat
                for (SpontivlyEventChatMessage msg : eventChat.chatMessages) {
                    addDateFlag = addDate(msg.createdAt);
                    if (addDateFlag == 1) {
                        msgList.add(new MessageItem(0, "", "", msg.createdAt));
                    }
                    addUserFlag = addUserName(msg.posterFirstName + " " + msg.posterLastName);
                    if (addUserFlag == 1) {
                        msgList.add(new MessageItem(msg.posterId, msg.posterFirstName + " " + msg.posterLastName, msg.postedMessage, msg.createdAt));
                    } else {
                        msgList.add(new MessageItem(msg.posterId, " ", msg.postedMessage, msg.createdAt));
                    }
                    lastUser = msg.posterFirstName + " " + msg.posterLastName;
                    currentDateString = convertDate(msg.createdAt);
                }
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(msgList.size()-1);
            }

        });
    }

    public void sendMessage(final View view) {
        final EditText editText = findViewById(R.id.editText);
        postMsg = new SpontivlyEventChatMessage();
        postMsg.eventId = eventId;
        postMsg.posterId = user.userId;
        postMsg.posterFirstName = user.firstName;
        postMsg.posterLastName = user.lastName;
        postMsg.postedMessage = editText.getText().toString();
        postMsg.createdAt = System.currentTimeMillis();
        addDateFlag = addDate(postMsg.createdAt);
        Log.i("Spontivly", "clicked button");
        if (editText.getText().toString().length() > 0) {
            dbService.postEventChatMessage(postMsg, new DatabaseService.UpdateEventChatCallback() {
                @Override
                public void callback(int messageId) {
                    Log.i("Spontivly", postMsg.toString());
                    editText.getText().clear();

                    if (addDateFlag == 1) {
                        msgList.add(new MessageItem(0, "", "", postMsg.createdAt));
                    }
                    msgList.add(new MessageItem(postMsg.posterId, postMsg.posterFirstName + " " + postMsg.posterLastName, postMsg.postedMessage, postMsg.createdAt));
                    lastUser = postMsg.posterFirstName + " " + postMsg.posterLastName;
                    currentDateString = convertDate(postMsg.createdAt);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(msgList.size()-1);

                    // Hide keyboard
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                    Log.i("Spontivly", "Adapter notified!");
                }

            });
        }
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

    public String convertDate(long date) {
        String dateString = new SimpleDateFormat("MMMM dd, yyyy").format(date);
        return dateString;
    }

    public int addDate(long date) {
        if (currentDateString.equals(convertDate(date)))
            return 0;
        return 1;
    }

    public int addUserName(String user) {
        if (lastUser.equals(user))
            return 0;
        return 1;
    }
}
