package com.marked.vifo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.vifo.R;
import com.marked.vifo.activity.ContactDetailActivity;
import com.marked.vifo.adapter.viewholders.ContactHolder;
import com.marked.vifo.model.Contact;

import java.util.List;

/**
 * Created by Tudor Pop on 30-Nov-15.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactHolder> {
    private List<Contact> mDataSet;
	private Context mContext;

	public ContactsAdapter(Context context, List<Contact> dataSet) {
        mDataSet = dataSet;
		mContext = context;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout_item, parent, false);
	    ContactHolder holder = new ContactHolder(v, mContext);
	    return holder;
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, final int position) {
        Contact contact = mDataSet.get(position);
        holder.bindContact(contact);
	    holder.itemView.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) { // itemView is a public member that points to the root element of the contactHolder (in the layout)
			    // TODO: 03-Dec-15 send contact detail from here to ContactDetailActivity
			    Intent intent = new Intent(mContext, ContactDetailActivity.class);
			    mContext.startActivity(new Intent(mContext, ContactDetailActivity.class));
		    }
	    });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
