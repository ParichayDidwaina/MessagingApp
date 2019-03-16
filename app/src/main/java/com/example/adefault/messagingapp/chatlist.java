package com.example.adefault.messagingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class chatlist extends AppCompatActivity {

    ListView l;
    ArrayList<String> chatarray = new ArrayList<String>();
    ArrayList<String> chatkey = new ArrayList<String>();

    private FirebaseAuth mAuth;
    String key="";
    String useruid="";

    int marker;

    Customadapter adap;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        marker = 0;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_1,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.logout:
                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("status")
                        .setValue("offline");
                mAuth.signOut();


                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.newchat:
                marker = 1;
                Intent intent1 = new Intent(this,Userfeed.class);
                startActivity(intent1);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        adap= new Customadapter();

        setTitle("Your Chats");

        key = "";
        mAuth= FirebaseAuth.getInstance();

        l = (ListView) findViewById(R.id.chatuserlist);
        l.setAdapter(adap);

        //-------------------------------------------------------------------------------

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {

                    if (dataSnapshot.child("username").getValue().toString().equals(mAuth.getCurrentUser().getEmail().toString())) {
                        try {
                            chatarray = (ArrayList<String>) ObjectSerializer.deserialize(dataSnapshot.child("chatlist").getValue().toString());
                            chatkey = (ArrayList<String>) ObjectSerializer.deserialize(dataSnapshot.child("chatkey").getValue().toString());
                            adap.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println("--------------------------------");
                }
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                try {

                    if (dataSnapshot.child("username").getValue().toString().equals(mAuth.getCurrentUser().getEmail().toString())) {
                        try {
                            chatarray = (ArrayList<String>) ObjectSerializer.deserialize(dataSnapshot.child("chatlist").getValue().toString());
                            chatkey = (ArrayList<String>) ObjectSerializer.deserialize(dataSnapshot.child("chatkey").getValue().toString());
                            adap.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println("--------------------------------");
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

        //-------------------------------------------------------------------------------


        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(chatlist.this,chatroom.class);
                intent.putExtra("key",chatkey.get(i));
                intent.putExtra("name",chatarray.get(i)+"@gmail.com");
                marker = 1;
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);

            }
        });

        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                useruid = chatkey.get(i);

                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString())
                        .child("chats").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try{
                        if(dataSnapshot.child("name").getValue().toString().equals(chatarray.get(i)+"@gmail.com"))
                        {
                            try {
                                key = dataSnapshot.getKey().toString();
                            }
                            catch (Exception e)
                            {}
                        }}
                        catch (Exception e)
                        {}
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

                dialog(i);
                return true;
            }
        });
    }

    public class Customadapter extends BaseAdapter{
        @Override
        public int getCount() {
            return chatarray.size();
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

            view = getLayoutInflater().inflate(R.layout.custom_chatlist,null);

            TextView t2 = (TextView)view.findViewById(R.id.textView2);

            t2.setText(chatarray.get(i));

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
        try {
            if (marker != 1) {
                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("status")
                        .setValue("offline");
            }
        }
        catch (Exception e)
        {}
    }

    public void dialog(final int index)
    {
        new AlertDialog.Builder(this)
                .setTitle("DELETE")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Delete this Chat?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        chatarray.remove(index);
                        chatkey.remove(index);
                        adap.notifyDataSetChanged();

                        try {
                            String chatliststring = ObjectSerializer.serialize(chatarray);
                            String chatkeystring = ObjectSerializer.serialize(chatkey);
                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("chatlist")
                                    .setValue(chatliststring);
                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("chatkey")
                                    .setValue(chatliststring);
                        }
                        catch (Exception e)
                        {}
                        if(!key.isEmpty())
                        {
                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString())
                                    .child("chats").child(key).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("users").child(useruid)
                                    .child("chats").child(key).removeValue();
                        }
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    public void refresh()
    {

    }

    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
