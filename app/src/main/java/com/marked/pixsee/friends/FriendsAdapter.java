package com.marked.pixsee.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.data.friend.Friend;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.facebook.common.internal.Preconditions.checkNotNull;


/**
 * Created by Tudor Pop on 30-Nov-15.
 */
class FriendsAdapter extends RecyclerView.Adapter<FriendHolder> {
    private Context context;
    private ArrayList<Friend> mDataSet;
    private FriendFragment.FriendItemListener friendItemListener;

    FriendsAdapter(@NotNull Context context,@NotNull ArrayList<Friend> dataSet,@NotNull FriendFragment.FriendItemListener friendItemListener) {
        this.context = checkNotNull(context);
        this.mDataSet = checkNotNull(dataSet);
        this.friendItemListener = checkNotNull(friendItemListener);
    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new FriendHolder(v, context);
    }

    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {
        final Friend friend = mDataSet.get(position);
        holder.bindContact(friend);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // itemView is a public member that points to the root element of the contactHolder (in the layout)
                // TODO: 03-Dec-15 send contact detail from here to ContactDetailActivity
                // send the clicked contact to detail activity
                friendItemListener.onFriendClick(friend);
            }
        });
    }

    // Return the size of your mDataSet (invoked by the layout manager)

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
