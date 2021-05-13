package com.example.walkin_clinic_ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CommentList extends ArrayAdapter<Comment> {
    private Activity context;
    List<Comment> comments;

    public CommentList(Activity context, List<Comment> _comments) {
        super(context, R.layout.layout_comment_list, _comments);
        this.context = context;
        this.comments = _comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_comment_list, null, true);

        TextView commentHeader = (TextView) listViewItem.findViewById(R.id.tv_commentHeader);
        TextView commentFooter = (TextView) listViewItem.findViewById(R.id.tv_commentFooter);

        Comment comment = comments.get(position);
        commentHeader.setText(comment.getHeader());
        commentFooter.setText(comment.getBody());
        return listViewItem;
    }
}
