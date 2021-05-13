package com.example.walkin_clinic_ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AccountList extends ArrayAdapter<Account> {
    private Activity context;
    List<Account> accounts;

    public AccountList(Activity context, List<Account> accounts) {
        super(context, R.layout.layout_account_list, accounts);
        this.context = context;
        this.accounts = accounts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_account_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewAccountListUsername);
        TextView textViewRole = (TextView) listViewItem.findViewById(R.id.textViewAccountListType);

        Account account = accounts.get(position);
        textViewName.setText(account.getEmail());
        textViewRole.setText(account.getType());
        return listViewItem;
    }
}
