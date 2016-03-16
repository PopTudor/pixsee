package com.marked.pixsee.friends

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.google.android.gms.appinvite.AppInviteInvitation
import com.marked.pixsee.R
import com.marked.pixsee.face.UserDefinedTargets.ImageTargets
import com.marked.pixsee.frienddetail.ChatDetailActivity
import com.marked.pixsee.friends.data.Friend
import com.marked.pixsee.friends.data.FriendRepository
import com.marked.pixsee.utility.toast
import kotlinx.android.synthetic.main.fragment_contact_list.view.*

/**
 * A list fragment representing a list of Contacts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a [ChatDetailFragment].
 *
 *
 * Activities containing this fragment MUST implement the [Callbacks]
 * interface.
 */
class FriendFragment : Fragment(), FriendsContract.View {
    private val mRepository by lazy { FriendRepository.getInstance(activity.baseContext) }
    private val mContactsAdapter by lazy { FriendsAdapter(activity, mRepository, friendItemInteraction) }
    private val mLayoutManager by lazy { LinearLayoutManager(activity) }

    //	lateinit private var mFabMenu: FloatingActionMenu

    private val mPresenter: FriendPresenter by lazy { FriendPresenter(mRepository, this) }
    private val friendItemInteraction by lazy {
        object : FriendItemListener {
            override fun onFriendClick(friend: Friend) {
                mPresenter.openFriendDetailUI(friend)
            }
        }
    }

    override fun onFriendsLoaded(from: Int, to: Int) {
        mContactsAdapter.notifyItemRangeChanged(from, to)
    }

    override fun showFriendDetailUI(friend: Friend) {
        val intent = Intent(activity, ChatDetailActivity:: class.java)
        intent.putExtra(ChatDetailActivity.EXTRA_CONTACT, friend);
        activity.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //		attachListeners();
        //		mSocket.on("hi", onNewRoom);
        //		mSocket.emit("room",new JSONObject());
        //		mSocket.connect();
        //
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mPresenter.loadFriends(true)
    }

    private val REQUEST_INVITE: Int = 11

    override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_contact_list, container, false)
        //		mFabMenu = rootView.fabMenu
        rootView.contactRecyclerView.apply {
            this.adapter = mContactsAdapter
            this.layoutManager = mLayoutManager
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    if (dy > 0 && recyclerView?.layoutManager is LinearLayoutManager) {
                        val s = mLayoutManager.childCount
                        val x = mLayoutManager.findFirstVisibleItemPosition()
                        if (x + s >= recyclerView?.layoutManager?.itemCount as Int) {
                            mPresenter.loadFriends(50)
                        }
                    }
                }
            })
        }
        rootView.fab.setOnClickListener {
            /* Google AppInvites */
            val intent = AppInviteInvitation.IntentBuilder("Invite more friends")
                    .setMessage("Check out this cool app !")
                    //						.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                    //						.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                    //						.setCallToActionText(getString(R.string.invitation_cta))
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);
        }

        rootView.vuforiaCamera.setOnClickListener {
            val intent = Intent(activity, ImageTargets::class.java)
            intent.putExtra("ACTIVITY_TO_LAUNCH", "app.ImageTargets.ImageTargets")
            startActivity(intent)
        }
        /*
        createCustomAnimation()
        mFabMenu.setClosedOnTouchOutside(true)
        mFabMenu.setOnMenuButtonClickListener {
            if (mFabMenu.isOpened) {

            }
            mFabMenu.toggle(true)
        }
*/
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == Activity.RESULT_OK) {
                toast("Hurray \ud83d\ude04")
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                if (data != null) {
                    val ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    toast("got invitations " + ids.size)
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                toast("Ooh \ud83d\ude14")
            }
        }
    }
    /* // uncomment this when we are going to add floating action menu
        private fun createCustomAnimation() {
            val set = AnimatorSet();

            val scaleOutX = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
            val scaleOutY = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

            val scaleInX = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
            val scaleInY = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

            scaleOutX.setDuration(50);
            scaleOutY.setDuration(50);

            scaleInX.setDuration(150);
            scaleInY.setDuration(150);


            scaleInX.addListener(object : AnimatorListenerAdapter() {
                override
                fun onAnimationStart(animation: Animator) {
                    if (mFabMenu.isOpened)
                        mFabMenu.getMenuIconView().setImageResource(R.drawable.ic_group_add_24dp);
                    else
                        mFabMenu.getMenuIconView().setImageResource(R.drawable.ic_email_24dp);
                }
            });

            set.play(scaleOutX).with(scaleOutY);
            set.play(scaleInX).with(scaleInY).after(scaleOutX);
            set.setInterpolator(OvershootInterpolator(2.0f));

            mFabMenu.setIconToggleAnimatorSet(set);
        }

        */
    /**
     * Closes the Floating Action Button
     * @return true if it was closed successfully, false otherwise
     * */
    /*
        fun closeFAM(): Boolean {
            if (mFabMenu.isOpened) {
                mFabMenu.close(true)
                return true
            } else
                return false
        }

        override fun onStop() {
            super.onStop()
            mFabMenu.close(false)
        }*/

    override
    fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_contacts_activity, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDetach() {
        super.onDetach()
        // Reset the active callbacks interface to the dummy implementation.
        //		dettachListeners();
    }

    //	public void attachListeners(){
    //		for (Contact contact:mContacts.getContacts())
    //			mSocket.on(contact.getId(), onNewRoom);
    //	}
    //	public void dettachListeners(){
    //		for (Contact contact:mContacts.getContacts())
    //			mSocket.off(contact.getId(), onNewRoom);
    //	}

    companion object {

        fun newInstance(): FriendFragment {
            return FriendFragment()
        }
    }

    interface FriendItemListener {
        fun onFriendClick(friend: Friend)
    }
}//
//	private Socket mSocket;
//	{
//		try {
//			mSocket = IO.socket(GCMConstants.SERVER);
//		} catch (URISyntaxException e) {
//		}
//	}
//
//	public static String room;
//
//	private Emitter.Listener onNewRoom = new Emitter.Listener() {
//		@Override
//		public void call(final Object... args) {
//			new Handler(Looper.getMainLooper()).post(new Runnable() {
//				@Override
//				public void run() {
////					room = (String) args[0];
//
//
//					// add the message to view
//					//					addMessage(username, message);
//					Toast.makeText(getActivity(), "newRoom TOAAST ", Toast.LENGTH_SHORT).show();
//					//					room = (String) args[0];
//				}
//
//			});
//		}
//	};
/**
 * A dummy implementation of the [Callbacks] interface that does
 * nothing. Used only when this fragment is not attached to an activity.
 */
