package com.example.walkin_clinic_ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ClinicList extends ArrayAdapter<WalkinClinic> {

    private Activity context;
    List<WalkinClinic> clinics;

    public ClinicList(Activity context, List<WalkinClinic> clinics) {
        super(context, R.layout.layout_clinic_list, clinics);
        this.context = context;
        this.clinics = clinics;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_clinic_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.tv_commentHeader);
        TextView textViewRole = (TextView) listViewItem.findViewById(R.id.tv_commentFooter);

        WalkinClinic clinic = clinics.get(position);
        textViewName.setText(clinic.getName());
        textViewRole.setText(clinic.getAddress());
        return listViewItem;
    }
}
