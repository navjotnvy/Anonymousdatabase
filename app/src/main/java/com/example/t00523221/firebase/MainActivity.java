package com.example.t00523221.firebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* for anonymous token authentication*/
// In database on browser. Go To Authentication --> Signed In methods --> Anonymous --> Enable --> Save
public class MainActivity extends AppCompatActivity {
    EditText stdid_key, stdname_value;
    private Button add_record, clear_all;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String mykey,myvalue;
    ListView listview;
    String TAG="";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Boolean access_granted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clear_all = (Button) findViewById(R.id.clear_all);
        stdid_key = (EditText)findViewById(R.id.stdid_key);
        stdname_value = (EditText)findViewById(R.id.stdname_value);
        add_record = (Button)findViewById(R.id.add_record);
        listview = (ListView) findViewById(R.id.listview01);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    access_granted = true;
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // Write a message to the database
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("RootElement");
                    // Read from the database
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            //String value = dataSnapshot.getValue(String.class);
                            //Log.d(TAG, "Value is: " + value);
                            ArrayList<String> Userlist = new ArrayList<String>();
                            // Result will be holded Here
                         /*   for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                String std_id = dsp.child("student_id").getValue().toString();
                                String std_name = dsp.child("student_name").getValue().toString();

                                Userlist.add(" === " + std_id + " || " + std_name + " === "); //add result into array list
                            }*/
                            Log.d("TAG", "-***-" + String.valueOf(Userlist));

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                                    android.R.layout.simple_list_item_1, android.R.id.text1, Userlist);
                            listview.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    access_granted = false;
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    mAuth.signInAnonymously()
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInAnonymously", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    // ...
                                }
                            });
                }
                // ...
            }
        };
        //default value is added here in list but not in db
       /* ArrayList<String> Userlist = new ArrayList<String>();
        Userlist.add("Test 01");
        Userlist.add("Test 02");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, Userlist);
        listview.setAdapter(adapter);*/

        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("RootElement"); //creates a root element in db

        add_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stdid_key.getText().toString().isEmpty()) mykey = "Default";
                else mykey = stdid_key.getText().toString();

                if (stdname_value.getText().toString().isEmpty()) myvalue = "Default";
                else myvalue = stdname_value.getText().toString();
// Default is given when there is no input in the box
                myRef.setValue(myvalue); // deletes all the ids in database, ID with {\"student_id\":\"bn\",\"student_name\":\"234\"} in one line
                //myRef.push().setValue(myvalue); // Random key value genetrated "key" : Studentname and in new line studentID : with "student_id\":\"T00\",\"student_name\":\"Hello\"
                //myRef.child(mykey).setValue(myvalue); //key the one already generated and value pair \"student_id\":\"T00\",\"student_name\":\"Hello\"
                myRef.child(mykey).setValue(writeJSON(mykey,myvalue).toString()); //For printing an array list in field. used Json function for it
                JSONObject studentRecord = writeJSON(mykey,myvalue); //Json next functionJSONObject studentRecord = writeJSON(mykey,myvalue);
                Map<String, Object> classList= new Gson().fromJson(studentRecord.toString(), new TypeToken<HashMap<String, Object>>(){}.getType());
                //add Gson lib in project structure to do so. from the project structure button-> app -> dependencies -> + -> search 'Gson' without quotes and add 'com.google.code.gson:gson:2.8.0'
                //myRef.push().setValue(classList);// random key generated when adding, then key value pair is generated then this(value in database is {\"student_id\":\"T00\",\"student_name\":\"Hello\"}")
                //myRef.child("record_0").setValue(classList);// record_0 is added student_id and student_name is given under it but it just keeps updating itself.
                //it doesn't make any difference if there is any random key or not to read the records back
            }
        });

        clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Cleared!",Toast.LENGTH_SHORT).show();
                myRef.removeValue();

            }
        });



    }
    /*
    public JSONArray writeJSON() {
        JSONObject student1 = new JSONObject();
        JSONObject student2 = new JSONObject();
        try {
            student1.put("student_id", "T1234");
            student1.put("student_name", "Alex");
            student1.put("student_email", "a@tru.ca");

            student2.put("student_id", "T2345");
            student2.put("student_name", "John");
            student2.put("student_email", "b@tru.ca");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONArray StudentList = new JSONArray();
        StudentList.put(student1);
        StudentList.put(student2);
        return StudentList;
    }*/
    public JSONObject writeJSON(String stid, String stdname) {
        JSONObject studentInfo = new JSONObject();

        try {
            studentInfo.put("student_id", stid);
            studentInfo.put("student_name", stdname);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return studentInfo;
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    }

