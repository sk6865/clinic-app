package com.example.walkin_clinic_ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class        DeleteAccount extends AppCompatActivity {

    ListView listViewAccounts;
    DatabaseReference databaseAccounts;

    List<Account> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        databaseAccounts = FirebaseDatabase.getInstance().getReference("users");


        listViewAccounts = findViewById(R.id.listViewAccounts);

        accounts = new ArrayList<>();

        listViewAccounts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Account account = accounts.get(i);

                if (!(account.getType().equals("admin"))) {
                    showUpdateAccountDialog(account.getUsername(),account.getEmail(), account.getType(), account.getId());
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "Admin account cannot be deleted", Toast.LENGTH_LONG).show();
                    return true;
                }//closes if else to block user from deleting admin

//                showUpdateAccountDialog(account.getUsername(),account.getEmail(), account.getType(), account.getId());
//                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseAccounts.addValueEventListener(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot){

                accounts.clear();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String accType = postSnapshot.child("type").getValue(String.class);
                    System.out.println(accType);
                    if(accType != null){
                        if(accType.equals("admin")) {
                            Account account = postSnapshot.getValue(Account.class);
                            accounts.add(account);
                        }
                    }
                }

                for(DataSnapshot postSnapshot: dataSnapshot.child("employees").getChildren()){
                    Account account = postSnapshot.getValue(Account.class);
                    accounts.add(account);
                }

                for(DataSnapshot postSnapshot: dataSnapshot.child("patients").getChildren()){
                    Account account = postSnapshot.getValue(Account.class);
                    accounts.add(account);
                }

                AccountList accountsAdapter = new AccountList(DeleteAccount.this, accounts);
                listViewAccounts.setAdapter(accountsAdapter);
            }

            public void onCancelled(DatabaseError databaseError){

            }
        });
    }


    public void onClickCancel(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    private void showUpdateAccountDialog(final String username, final String email, final String type, final String id) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_account_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonCancel = (Button) dialogView.findViewById(R.id.button_cancel_account);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.button_delete__account);



        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equals("employee"))
                    databaseAccounts.child("employees").child(id).removeValue();

                else if(type.equals("patient"))
                    databaseAccounts.child("patients").child(id).removeValue();

                /*Would ideally confirm this with a query. */
                Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_LONG).show();

                b.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });
    }

}

