package com.example.adefault.messagingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class chatroom extends AppCompatActivity {

    ArrayList<User> arrayOfUsers = new ArrayList<User>();
    ArrayList<String> chat = new ArrayList<String>();
    ArrayList<String> recieverchatlist = new ArrayList<String>();
    ArrayList<String> recieverkeylist = new ArrayList<String>();
    UsersAdapter adapter1;
    User newUser;
    EditText text;
    ListView l;
    String name;
    private FirebaseAuth mAuth;
    HashMap<String,String> map = new HashMap<String, String>();
    HashMap<String,String> map1 = new HashMap<String, String>();
    String chatkey;
    String userkey;


    int flag;
    String[] array;
    String[] array1;
    int marker;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.menu_2,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.clearchat:
                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString())
                        .child("chats").child(chatkey).removeValue();
                FirebaseDatabase.getInstance().getReference().child("users").child(userkey)
                        .child("chats").child(chatkey).removeValue();
                chat = new ArrayList<String>();
                adapter1.clear();
                adapter1.notifyDataSetChanged();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        flag = 0;
        mAuth= FirebaseAuth.getInstance();
        marker = 0;
        text = (EditText) findViewById(R.id.editText3);
        l = (ListView) findViewById(R.id.chatlist);

        adapter1 = new UsersAdapter(this, arrayOfUsers);

        l.setAdapter(adapter1);
        name = getIntent().getStringExtra("name");
        array1 = name.split("@");
        setTitle(array1[0]);
        map.put("name",name);
        map1.put("name",mAuth.getCurrentUser().getEmail().toString());
        userkey=getIntent().getStringExtra("key");

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        chat = new ArrayList<String>();
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString())
                .child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("name").getValue().toString().equals(name))
                {
                    chatkey=dataSnapshot.getKey().toString();
                    try {
                        chat = (ArrayList<String>) ObjectSerializer.deserialize(dataSnapshot.child("array").getValue().toString());

                        adapter1.clear();
                        for(int i=0;i<chat.size();i++)
                        {
                            newUser = new User(chat.get(i));
                            adapter1.add(newUser);

                        }
                        adapter1.notifyDataSetChanged();
                        if(flag==0)
                        {
                            l.setSelection(l.getCount()-1);
                            flag=1;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();


                    }
                }

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.child("name").getValue().toString().equals(name))
                {
                    chatkey=dataSnapshot.getKey().toString();
                    try {
                        chat = (ArrayList<String>) ObjectSerializer.deserialize(dataSnapshot.child("array").getValue().toString());

                        adapter1.clear();
                        for(int i=0;i<chat.size();i++)
                        {
                            newUser = new User(chat.get(i));
                            adapter1.add(newUser);
                        }
                        adapter1.notifyDataSetChanged();
                        if(flag==0)
                        {
                            l.setSelection(l.getCount()-1);
                            flag=1;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

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


//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        if(chatkey==null)
        {
            chatkey= UUID.randomUUID().toString();
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

    public class User {
        public String name;

        public User(String name) {
            this.name = name;
        }
    }

    public class UsersAdapter extends ArrayAdapter<User> {
        public UsersAdapter(Context context, ArrayList<User> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            User user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);

            // Populate the data into the template view using the data object
            tvName.setText(user.name);

            // Return the completed view to render on screen
            return convertView;
        }
    }



    public void send(View view)
    {
        if(!text.getText().toString().isEmpty()) {
            array = mAuth.getCurrentUser().getEmail().toString().split("@");
            chat.add(array[0] + " : " + text.getText().toString());
            newUser = new User(array[0] + " : " + text.getText().toString());
            arrayOfUsers.add(newUser);
            adapter1.notifyDataSetChanged();
            try {
                String chat1 = ObjectSerializer.serialize(chat);
                map.put("array", chat1);
                map1.put("array", chat1);
                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString())
                        .child("chats").child(chatkey).setValue(map);
                FirebaseDatabase.getInstance().getReference().child("users").child(userkey)
                        .child("chats").child(chatkey).setValue(map1);




            } catch (Exception e) {
                e.printStackTrace();

            }
            text.setText("");
            FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(dataSnapshot.getKey()==userkey)
                    {
                        try {
                            recieverchatlist = (ArrayList<String>)ObjectSerializer.deserialize(dataSnapshot.child("chatlist").getValue().toString());
                            recieverkeylist = (ArrayList<String>)ObjectSerializer.deserialize(dataSnapshot.child("chatkey").getValue().toString());
                            if(dataSnapshot.child("status").getValue().toString().equals("offline"))
                            {
                                FirebaseDatabase.getInstance().getReference().child("notifications").child(userkey).push()
                                        .setValue(array[0]);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

            if(!recieverchatlist.contains(array[0]))
            {
                try {
                    recieverchatlist.add(array[0]);
                    recieverkeylist.add(mAuth.getCurrentUser().getUid().toString());
                    String recieverchatliststring = ObjectSerializer.serialize(recieverchatlist);
                    String  recieverkeyliststring = ObjectSerializer.serialize(recieverkeylist);
                    FirebaseDatabase.getInstance().getReference().child("users").child(userkey)
                            .child("chatlist").removeValue();
                    FirebaseDatabase.getInstance().getReference().child("users").child(userkey)
                            .child("chatkey").removeValue();
                    FirebaseDatabase.getInstance().getReference().child("users").child(userkey)
                            .child("chatlist").setValue(recieverchatliststring);
                    FirebaseDatabase.getInstance().getReference().child("users").child(userkey)
                            .child("chatkey").setValue(recieverkeyliststring);
                }
                catch(Exception e){
                }
            }
        }
        else
        {
            Toast.makeText(this, "Enter a Message", Toast.LENGTH_SHORT).show();
        }
    }

    public void search()
    {



    }

    public void onBackPressed()
    {
        marker = 1;
        Intent intent = new Intent(this,chatlist.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }

}
