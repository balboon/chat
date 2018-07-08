package com.spontivly.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spontivly.chat.models.SpontivlyEvent;
import com.spontivly.chat.services.DatabaseService;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    TextView textView;
    private DatabaseReference mDatabase;
    public static RequestQueue netRequests;
    public static DatabaseService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = (TextView)findViewById(R.id.tv_chat_data);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue netRequests = new RequestQueue(cache, network);
        netRequests.start();

        dbService = new DatabaseService();
        dbService.netRequests = netRequests;

//        SpontivlyEventChatMessage msg = new SpontivlyEventChatMessage();
//        msg.eventId = 313;
//        msg.posterId = 24;
//        msg.postedMessage = "I'm the greatest ever GOAT";
//        msg.createdAt = 21234567;
//
//        textView.append(msg.toString());

//        dbService.postEventChatMessage(msg, new DatabaseService.UpdateEventChatCallback() {
//            @Override
//            public void callback(int messageId) {
//                textView.append("Message posted: " + messageId);
//            }
//        });

//        dbService.getEventChatMessages(313, 12346579, new DatabaseService.GetEventChatMessagesCallback() {
//            @Override
//            public void callback(ArrayList<SpontivlyEventChatMessage> response) {
//                if (!response.isEmpty()) {
//                    for (SpontivlyEventChatMessage msg : response) {
//                        textView.append("\n" + msg.toString());
//                    }
//                }
//            }
//        });

//        dbService.getEventChat(313, new DatabaseService.GetEventChatCallback() {
//            @Override
//            public void callback(SpontivlyEventChat eventChat) {
//                SpontivlyEventChatMessage last = eventChat.lastPostedMessage;
//                if (last != null) {
//                    textView.append("Last chat message:\n" + last.toString() + "\n" +
//                        "Number of chat messages: " + eventChat.chatMessages.size());
//                }
//                else {
//                    textView.append("No chat messages");
//                }
//            }
//        });

//        dbService.getUserInfo(25, new DatabaseService.GetUserInfoCallback() {
//            @Override
//            public void callback(SpontivlyUser user) {
//                textView.append(user.userId + "\n" +
//                user.firstName + " " + user.lastName + "\n" +
//                user.email);
//            }
//        });

        dbService.getChatEvents(new DatabaseService.GetActiveCallback() {
            @Override
            public void callback(ArrayList<SpontivlyEvent> response) {
                for (SpontivlyEvent event : response) {
                    textView.append(event.eventId + ": " + event.title + "\n\n");
                }
            }
        });

    }

}
