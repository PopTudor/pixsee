package com.marked.pixsee.views.adapter;

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.marked.pixsee.R
import com.marked.pixsee.model.friend.Friend
import com.marked.pixsee.activity.chat.ChatDetailActivity
import com.marked.pixsee.views.adapter.viewholders.ContactHolder
import org.jetbrains.anko.onClick
import java.util.*

/**
 * Created by Tudor Pop on 30-Nov-15.
 */
class ContactsAdapter(private val context: Context, private var dataSet: ArrayList<Friend>) : RecyclerView.Adapter<ContactHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        // Create a new view.
		val v = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false);
		val holder = ContactHolder(v, context);
        return holder;
    }

	override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contact = dataSet.get(position);
		holder.apply {
			bindContact(contact)
			itemView.onClick { view ->
				// itemView is a public member that points to the root element of the contactHolder (in the layout)
				// TODO: 03-Dec-15 send contact detail from here to ContactDetailActivity
				// send the clicked contact to detail activity
				val intent = Intent(context, ChatDetailActivity::class.java)
				intent.putExtra(ChatDetailActivity.EXTRA_CONTACT, contact);
                context.startActivity(intent)
//				context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(context as ContactListActivity).toBundle()); // RIGHT !!!
			}
		};
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataSet.size;
    }

}
