package com.marked.vifo.ui.adapter;

import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.marked.vifo.R
import com.marked.vifo.model.Contact
import com.marked.vifo.ui.activity.ContactDetailActivity
import com.marked.vifo.ui.activity.ContactListActivity
import com.marked.vifo.ui.adapter.viewholders.ContactHolder

/**
 * Created by Tudor Pop on 30-Nov-15.
 */
public class ContactsAdapter(context: Context, dataSet: kotlin.List<Contact>) : RecyclerView.Adapter<ContactHolder>() {
    private val context = context;
    private val dataSet = dataSet;

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        // Create a new view.
        val v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout_item, parent, false);
        val holder = ContactHolder(v, context);
        return holder;
    }
    public override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contact = dataSet.get(position);
        holder.bindContact(contact);
        holder.itemView.setOnClickListener { view ->
            // itemView is a public member that points to the root element of the contactHolder (in the layout)
            // TODO: 03-Dec-15 send contact detail from here to ContactDetailActivity
            // send the clicked contact to detail activity
            val intent = Intent(context, ContactDetailActivity::class.java)
            intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contact);
            context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(context as ContactListActivity).toBundle()); // RIGHT !!!
        };
    }

    // Return the size of your dataset (invoked by the layout manager)
    public override fun getItemCount(): Int {
        return dataSet.size;
    }

}
