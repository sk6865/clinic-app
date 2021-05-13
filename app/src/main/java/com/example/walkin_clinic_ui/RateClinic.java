package com.example.walkin_clinic_ui;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class RateClinic extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Spinner ratingSpin;
    private String username;
    private String clinicID;

    private ListView listComments;

    private List<Comment> clinicComments;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_clinic);

        Intent intent = getIntent();
        clinicID = intent.getStringExtra("ID");

        ratingSpin = findViewById(R.id.ratingSpinner);
        ArrayAdapter<CharSequence> ratingAdapter = ArrayAdapter.createFromResource(this,R.array.ratings,android.R.layout.simple_spinner_item);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpin.setAdapter(ratingAdapter);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("clinics").child(clinicID);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String dBname = dataSnapshot.child("name").getValue(String.class);
                TextView texty = findViewById(R.id.tv_description);
                texty.setText("Comments for " + dBname);
/*
                String dBaddress = dataSnapshot.child("address").getValue(String.class);
                String dBinsurance = dataSnapshot.child("insuranceType").getValue(String.class);
                String dBname = dataSnapshot.child("name").getValue(String.class);
                Schedule dBschedule = dataSnapshot.child("schedule").getValue(Schedule.class);
                String dBphoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);

                ArrayList<Service> dBservices = new ArrayList<Service>();

                for(DataSnapshot postSnapshot: dataSnapshot.child("services").getChildren()){
                    Service service = postSnapshot.getValue(Service.class);
                    dBservices.add(service);
                }
*/
                clinicComments = new ArrayList<Comment>();

                for(DataSnapshot postSnapshot: dataSnapshot.child("comments").getChildren()){
                    Comment comment = postSnapshot.getValue(Comment.class);
                    clinicComments.add(comment);
                }

/*
                WalkinClinic clinic = new WalkinClinic(dBname,dBinsurance, dBphoneNumber, dBaddress);
                clinic.setSchedule(dBschedule);
                clinic.setServices(dBservices);
                clinic.setComments((ArrayList)clinicComments);
*/

                listComments = findViewById(R.id.listViewComments);

                CommentList commentAdapter = new CommentList(RateClinic.this, clinicComments);
                listComments.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Ahoy", "onCancelled", databaseError.toException());
            }
        });

    } //closes onCreate method

    @Override
    public void onStart()
    {
        super.onStart();
    }

    public void onSubmit(View view)
    {
        /* Appends username to comment */
        FirebaseUser user = mAuth.getCurrentUser();
        username = user.getDisplayName();

        /* Must leave at least a comment and/or rating */
        boolean noRating = false;
        boolean noComment = false;

        String commentText = ((EditText)findViewById(R.id.fieldComment)).getText().toString();

        if(commentText == null)
        {
            commentText = ""; // Default comment message.
            noComment = true;
        }

        String rating = ((Spinner) findViewById(R.id.ratingSpinner)).getSelectedItem().toString();

        if(!rating.equals("No rating")) // No rating by default.
        {
            rating += " out of 5";
            noRating = true;
        }

        if(noRating && noComment)
            Toast.makeText(RateClinic.this, "Must leave a rating and/or a comment", Toast.LENGTH_LONG).show();

        else
        {
            /* Gets current system time and appends it to comment. */
            DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());

            Comment newComment = new Comment(username, rating, commentText, date); // Write this to db once the other methods are updated.

            clinicComments.add(0, newComment);

            mDatabase.child("comments").setValue(clinicComments);
        }

        //finish(); // Ends the cuurent activity since we've made our comment.
    }

} //closes RateClinic class
