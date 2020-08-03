package com.dev_app.managebugdet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.dev_app.managebugdet.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    //Firebase...
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    //Global Variable..
    private String title;
    private String description;
    private String budget;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllData").child(uid);


        floatingActionButton = findViewById(R.id.fab_add);

        //Recycler View....
        recyclerView = findViewById(R.id.recyclerId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });
    }

        public void addData() {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View myView = inflater.inflate(R.layout.inputlayout, null);
            alert.setView(myView);
            final AlertDialog dialog = alert.create();

            final EditText mTitle = myView.findViewById(R.id.title);
            final EditText mDesc = myView.findViewById(R.id.description);
            final EditText mBudget = myView.findViewById(R.id.budget);
            Button btnSave = myView.findViewById(R.id.btnSave);
            //dialog.show();
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String title = mTitle.getText().toString().trim();
                        String description = mDesc.getText().toString().trim();
                        String budget = mBudget.getText().toString().trim();
                        String date = DateFormat.getDateInstance().format(new Date());
                        String id = databaseReference.push().getKey();

                        Data data = new Data(title, description, budget, id, date);

                        if (TextUtils.isEmpty(title)) {
                            mTitle.setError("Required field..");
                        }
                        if (TextUtils.isEmpty(description)) {
                            mDesc.setError("Required field..");
                        }

                        if (TextUtils.isEmpty(budget)) {
                            mBudget.setError("Required field..");
                        } else {
                            databaseReference.child(id).setValue(data);
                            Toast.makeText(HomeActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
        }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(databaseReference,Data.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Data,MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int i, @NonNull final Data model) {
                   viewHolder.setTitle(model.getTitle());
                   viewHolder.setDescription(model.getDescription());
                   viewHolder.setBudget(model.getBudget());
                   viewHolder.setDate(model.getDate());

                   viewHolder.view.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {

                           post_key = getRef(i).getKey();
                           title = model.getTitle();
                           description = model.getDescription();
                           budget = model.getBudget();
                           upDateData();
                       }
                   });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dataitem, parent, false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    /* THE UPDATE METHOD*/
    private void upDateData() {
        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.updatelayout,null);
        dialogAlert.setView(myView);
        final AlertDialog dialog = dialogAlert.create();

        final EditText updateTitle = myView.findViewById(R.id.title_update);
        final EditText updateDesc = myView.findViewById(R.id.description_update);
        final EditText updateBudget = myView.findViewById(R.id.budget_update);
        Button  btnUpdate = myView.findViewById(R.id.btn_update);
        Button btnDelete = myView.findViewById(R.id.btn_delete);

            //We need to set our data server inside the Edit text...
            updateTitle.setText(title);
            updateTitle.setSelection(title.length());

            updateDesc.setText(description);
            updateDesc.setSelection(description.length());

            updateBudget.setText(budget);
            updateBudget.setSelection(budget.length());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = updateTitle.getText().toString().trim();
                description = updateDesc.getText().toString().trim();
                budget = updateBudget.getText().toString().trim();
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(title, description, budget, post_key, date);

                if (TextUtils.isEmpty(title)) {
                    updateTitle.setError("Required field..");
                }
                if (TextUtils.isEmpty(description)){
                    updateDesc.setError("Required field..");
                }
                if (TextUtils.isEmpty(budget)) {
                    updateBudget.setError("Required field..");
                } else {
                    databaseReference.child(post_key).setValue(data);
                    dialog.dismiss();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(post_key).removeValue();
                Toast.makeText(HomeActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Recycler View Holder...
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            view = (itemView);
        }
        //Add method to retrieve from firebase Database......
        public void setTitle(String title) {
            TextView mTitle = view.findViewById(R.id.title_item);
            mTitle.setText(title);
        }

        public void setDescription(String description) {
            TextView mDesc = view.findViewById(R.id.desc_item);
            mDesc.setText(description);
        }

        public void setBudget(String description) {
            TextView mBudget = view.findViewById(R.id.budget_item);
            mBudget.setText(description);
        }

        public void setDate(String date) {
            TextView mDate = view.findViewById(R.id.date);
            mDate.setText(date);
        }


    }

    //Menu for logout

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logoutmenu,menu);
        getMenuInflater().inflate(R.menu.optionsmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }

        switch (item.getItemId()){
            case R.id.contact_us:
                Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show();

                case R.id.share:
                Toast.makeText(this, "Share With Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.rate:
                Toast.makeText(this, "Rate App", Toast.LENGTH_SHORT).show();
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }
}
