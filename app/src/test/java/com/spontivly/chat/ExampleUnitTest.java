package com.spontivly.chat;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.spontivly.chat.models.AppModel;
import com.spontivly.chat.models.SpontivlyEventChatMessage;
import com.spontivly.chat.services.DatabaseService;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testPostMessage() {
        Cache cache = new DiskBasedCache(new File(""), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue netRequests = new RequestQueue(cache, network);
        netRequests.start();

        DatabaseService dbService = new DatabaseService();
        dbService.netRequests = netRequests;

        SpontivlyEventChatMessage msg = new SpontivlyEventChatMessage();
        msg.eventId = 313;
        msg.posterId = 24;
        msg.postedMessage = "I'm the greatest";
        msg.createdAt = 21234567;

        dbService.postEventChatMessage(msg, new DatabaseService.UpdateEventChatCallback() {
            @Override
            public void callback(int messageId) {

            }
        });
    }


}