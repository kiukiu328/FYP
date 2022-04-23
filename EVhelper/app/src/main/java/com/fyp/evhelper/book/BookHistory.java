package com.fyp.evhelper.book;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.evhelper.MainActivity;
import com.fyp.evhelper.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BookHistory extends AppCompatActivity {

    //Create variables
    private ListView historyLV;
    ImageView ivBack;
    ArrayList<String> historyArrayList;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_history);

        //initializing variables
        historyLV = findViewById(R.id.idLVCourses);
        ivBack = findViewById(R.id.ivBack);

        // initializing our array list
        historyArrayList = new ArrayList<String>();

        // calling a method to get data from
        // Firebase and set data to list view
        initializeListView();

        //Create a dialog
        //to show detailed booking history
        historyLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Take the first two digits
                //from the title of list item
                String selectedItem = (String) parent.getItemAtPosition(position);

                selectedItem = selectedItem.substring(0,selectedItem.lastIndexOf('-'));
                selectedItem = selectedItem.replace(" ", "");

                //Toast.makeText(getApplicationContext(), "*" + selectedItem + "*", Toast.LENGTH_SHORT).show();


                //connect to the database
                DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference().child("Booking").child("Client01").child(selectedItem);

                clientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //initiate the variables and get value from database
                        String bookdate = (String) snapshot.child("book_date").getValue();
                        String carPark = (String) snapshot.child("car_park").getValue();
                        String entrance = (String) snapshot.child("entrance_time").getValue();
                        String exit = (String) snapshot.child("exit_time").getValue();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookHistory.this);
                        // set title
                        alertDialogBuilder.setTitle("Message");
                        alertDialogBuilder.setCancelable(true);
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Book Date : " + bookdate +
                                        "\nCar Park : " + carPark +
                                        "\nEntrance Time : " + entrance +
                                        "\nExit Time : " + exit)
                                .setCancelable(true)
                                .setPositiveButton( "OK",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        try {

                                        } catch (Exception e) {
                                            //Exception
                                        }
                                    }
                                })
                            
                                .setNegativeButton("Delete History", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        clientRef.removeValue();
                                        Intent intent = new Intent(getApplicationContext(), BookHistory.class);

                                        startActivity(intent);
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });


            }
        });

        //go back to main activity by clicking the return image
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
            }
        });

    }

    //initiate the items from list
    private void initializeListView() {
        // creating a new array adapter for our list view.
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, historyArrayList);

        //connect to database
        reference = FirebaseDatabase.getInstance().getReference().child("Booking").child("Client01");

        // add child event listener to get the child of database.
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                // this method is called when new child is added to
                // the database and after adding new child
                // the item is added inside the array list and
                // notifying the adapter that the data in adapter is changed.
                String listTitle = snapshot.getKey();
                listTitle += " - " + snapshot.child("car_park").getValue() + " (" + snapshot.child("book_date").getValue() + ")";
                historyArrayList.add(listTitle);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                // this method is called when the new child is added.
                // when the new child is added to the list that will be
                // notifying the adapter that data has changed.
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                // below method is called when a child is removed from the database.
                // inside this method the child will be removed from the array list
                // by comparing with it's value.
                // after removing the data, the adapter will be notifying that the
                // data has been changed.
                String listTitle = snapshot.getKey();
                listTitle += " - " + snapshot.child("car_park").getValue();
                historyArrayList.remove(listTitle);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                // this method is called when we move our
                // child in our database.
                // in our code we are note moving any child.
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // this method is called when we get any
                // error from Firebase with error.
            }
        });
        //setting an adapter to our list view.
        historyLV.setAdapter(adapter);
    }
}
