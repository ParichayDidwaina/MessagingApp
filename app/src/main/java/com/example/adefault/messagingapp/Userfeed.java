package com.example.adefault.messagingapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Userfeed extends AppCompatActivity {
    ListView l;
    ArrayList<String> a;

    ArrayList<String> chatlist1 = new ArrayList<String>();
    ArrayList<String> chatkey = new ArrayList<String>();
    ArrayList<String> keys;
    private FirebaseAuth mAuth;
    String[] array;
    String chatliststring;
    String chatkeystring;
    int marker;

    Customadapter adap1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userfeed);

        adap1 = new Customadapter();
        marker = 0;
        setTitle("User List");
        mAuth= FirebaseAuth.getInstance();
        a = new ArrayList<String>();
        keys = new ArrayList<String>();
        l = (ListView) findViewById(R.id.list);

        l.setAdapter(adap1);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.child("username").getValue().toString().equals(mAuth.getCurrentUser().getEmail())) {
                    array = dataSnapshot.child("username").getValue().toString().split("@");
                    a.add(array[0]);
                    adap1.notifyDataSetChanged();

                    keys.add(dataSnapshot.getKey().toString());
                }
                else
                {
                    try {
                        chatlist1 =(ArrayList<String>) ObjectSerializer.deserialize(dataSnapshot.child("chatlist").getValue().toString());
                        chatkey = (ArrayList<String>) ObjectSerializer.deserialize(dataSnapshot.child("chatkey").getValue().toString());
                    } catch (Exception e) {
                        chatlist1=new ArrayList<String>();
                        chatkey = new ArrayList<String>();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {
                marker = 1;
                if(!chatlist1.contains(a.get(i))) {
                    chatlist1.add(a.get(i));
                    chatkey.add(keys.get(i));
                }

                try {
                    chatliststring = ObjectSerializer.serialize(chatlist1);
                    chatkeystring = ObjectSerializer.serialize(chatkey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("chatlist")
                        .setValue(chatliststring);
                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("chatkey")
                        .setValue(chatkeystring);
                Intent intent = new Intent(Userfeed.this,chatlist.class);
                startActivity(intent);

            }
        });
    }

    public class Customadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return a.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.custom_userfeed, null);

            TextView t2 = (TextView) view.findViewById(R.id.textView2);

            t2.setText(a.get(i));

            return view;
        }
    }

    public void onStart()
    {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("status")
                .setValue("online");
    }

    public void onStop()
    {
        super.onStop();
        if(marker!=1) {
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("status")
                    .setValue("offline");
        }
    }
    public void onBackPressed()
    {
        marker = 1;
        Intent i = new Intent(this,chatlist.class);
        startActivity(i);
    }

}
