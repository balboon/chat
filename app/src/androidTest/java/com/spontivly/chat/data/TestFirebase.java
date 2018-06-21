package com.spontivly.chat.data;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spontivly.chat.models.Users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class TestFirebase {

    private DatabaseReference mDatabase;
    private FirebaseDatabase database;

    @Before
    public void before() {
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        System.out.println(mDatabase.toString());
    }

    @Test
    public void testGetData() {

        Map<String,Integer> map = new HashMap<>();
        map.put("Event 1", 1);
        Users testnode = new Users("Al", map);
        System.out.println(testnode.toString());
        final List<Users> usernodes = new ArrayList<>();
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    Users user = userSnapShot.getValue(Users.class);
                    System.out.println(user.toString());
                    usernodes.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        mDatabase.child("Users").child("user0003").setValue(testnode);
        // Print out nodes

    }
}
