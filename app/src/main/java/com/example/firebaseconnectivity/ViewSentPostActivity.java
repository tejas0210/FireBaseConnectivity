package com.example.firebaseconnectivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewSentPostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView postView;
    private ArrayList<String> usernames;
    private ArrayAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private TextView txtDes;
    private ImageView post;
    private ArrayList<DataSnapshot> dataSnapshots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sent_post);

        firebaseAuth = FirebaseAuth.getInstance();

        postView = findViewById(R.id.postView);
        usernames = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,usernames);
        postView.setAdapter(adapter);

        dataSnapshots = new ArrayList<>();

        postView.setOnItemClickListener(this);
        postView.setOnItemLongClickListener(this);

        txtDes = findViewById(R.id.txtDes);
        post = findViewById(R.id.post);

        FirebaseDatabase.getInstance().getReference().child("my_users").child(firebaseAuth.getUid()).child("received posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                dataSnapshots.add(snapshot);
                String fromWhomUsername = (String) snapshot.child("fromWhom").getValue();
                usernames.add(fromWhomUsername);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot snapshot1:dataSnapshots){
                    if(snapshot1.getKey().equals(snapshot.getKey())){
                        dataSnapshots.remove(i);
                        usernames.remove(i);
                    }
                    i++;
                }
                adapter.notifyDataSetChanged();
                post.setImageResource(R.drawable.seeposts);
                txtDes.setText("");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DataSnapshot myDataSnapshot = dataSnapshots.get(i);
        String downloadLink =(String) myDataSnapshot.child("imageLink").getValue();

        Picasso.get().load(downloadLink).into(post);
        txtDes.setText((String)myDataSnapshot.child("Des").getValue());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                }
                else{
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        FirebaseStorage.getInstance().getReference()
                                .child("my_images")
                                .child((String) dataSnapshots.get(i).child("imageIdentifier").getValue())
                                .delete();

                        FirebaseDatabase.getInstance().getReference()
                                .child("my_users")
                                .child(firebaseAuth.getCurrentUser().getUid())
                                .child("received posts")
                                .child(dataSnapshots.get(i).getKey())
                                .removeValue();

                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return false;
    }
}