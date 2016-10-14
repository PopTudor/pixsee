package com.marked.pixsee.features.chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.commons.SpaceItemDecorator;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.di.Injectable;
import com.marked.pixsee.features.chat.custom.ChatAdapter;
import com.marked.pixsee.features.chat.data.Message;
import com.marked.pixsee.features.fullscreenImage.ImageFullscreenActivity;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import tyrantgit.explosionfield.ExplosionField;

/**
 * A fragment representing a single Contact detail screen.
 * This fragment is either contained in a [ContactListActivity]
 * in two-pane mode (on tablets) or a [ChatDetailActivity]
 * on handsets.
 */
public class ChatFragment extends Fragment implements ChatContract.View, ChatAdapter.ChatInteraction, Injectable {
	/**
	 * Check if the user is using the app
	 *
	 * @return if the app is in foreground or not
	 */
	public static boolean isInForeground = false;
	private ChatAdapter mChatAdapter;
	private LinearLayoutManager mLinearLayoutManager;
	private ExplosionField mExplosionField;

	private CardView mPictureTakenContainer;
	private EditText messageEditText;
	private RecyclerView messagesRecyclerView;
	@Inject
	ChatContract.Presenter mPresenter;

	public static ChatFragment newInstance(Parcelable parcelable) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(ChatActivity.EXTRA_CONTACT, parcelable);
		ChatFragment fragment = new ChatFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * Add message to dataset and notify the adapter of the change
	 *
	 * @param message the message to add
	 */
	@Override
	public void addMessage(Message message) {
		mChatAdapter.getDataset().add(message);
		mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount()-1);
		messagesRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChatAdapter = new ChatAdapter(this);
		mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
		mPictureTakenContainer = (CardView) rootView.findViewById(R.id.pictureTakenContainer);
		messagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.messagesRecyclerView);
		messagesRecyclerView.setLayoutManager(mLinearLayoutManager);
		messagesRecyclerView.addItemDecoration(new SpaceItemDecorator(15));
		messagesRecyclerView.setAdapter(mChatAdapter);
		messageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
		messageEditText.addTextChangedListener(new ChatTextWatcher());
		rootView.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = messageEditText.getText().toString();
				if (!text.isEmpty()){
					messageEditText.setText("");
					mPresenter.sendMessage(text);
				} else if(!messageEditText.isEnabled()) {
					mPresenter.sendImage();
				} else {
					mPresenter.onCameraClick();
				}
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			rootView.findViewById(R.id.sendButton)
					.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.transparent)));
		}

		((FloatingActionButton)rootView.findViewById(R.id.sendButton))
				.getDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.teal), PorterDuff.Mode.SRC_ATOP);

		rootView.findViewById(R.id.clearImageButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.clearImageButton();
			}
		});
		return rootView;
	}

	@Override
	public void imageSent(Message message) {

	}

	@Override
	public void onResume() {
		super.onResume();
		isInForeground = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		isInForeground = false;
		mPresenter.detach();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		injectComponent();
		checkFragmentInteraction(context);
		mPresenter.attach();
		mExplosionField = ExplosionField.attach2Window(getActivity());
	}

	private void checkFragmentInteraction(Context context) {
		try {
			ChatFragmentInteraction listener = (ChatFragmentInteraction) context;
			mPresenter.setChatInteraction(listener);
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void pop() {
		if (mChatAdapter.getDataset().size()<=0)
			return;
		mChatAdapter.getDataset().remove(mChatAdapter.getDataset().size() - 1);
		mChatAdapter.notifyItemRemoved(mChatAdapter.getDataset().size());
	}

	@Override
	public void showMessages(List<Message> cards) {
		mChatAdapter.getDataset().clear();
		mChatAdapter.getDataset().addAll(cards);
		messagesRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
		mChatAdapter.notifyDataSetChanged();
	}

	@Override
	public void showEmptyMessages() {

	}

	@Override
	public void setPresenter(ChatContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void chatClicked(View view, Message message,int position) {
		mPresenter.chatClicked(message,position);
		view.setClickable(false);
		mExplosionField.explode(view);
	}

	@Override
	public void imageClicked(View view, Uri uri) {
		mPresenter.chatImageClicked(uri);
	}

	@Override
	public void showImageFullscreen(Uri uri) {
		Log.i("TAG", uri.toString());
		Intent intent = new Intent(getActivity(), ImageFullscreenActivity.class);
		intent.putExtra("URI", uri);
		getActivity().startActivity(intent);
	}

	@Override
	public void remove(Message message,int position) {
		mChatAdapter.getDataset().remove(position);
		mChatAdapter.notifyItemRemoved(position);
	}

	@Override
	public void showImage(File file) {
		switchButonImage(R.drawable.ic_send_24dp);

		getView().findViewById(R.id.messageEditText).setEnabled(false);
		getView().findViewById(R.id.pictureTakenContainer).setVisibility(View.VISIBLE);
		((SimpleDraweeView) getView().findViewById(R.id.picture)).setImageURI(Uri.fromFile(file));
	}

	private void switchButonImage(@DrawableRes int resId) {
		Drawable drawable = ContextCompat.getDrawable(getActivity(), resId);
		drawable.setColorFilter(ContextCompat.getColor(getContext(),R.color.teal), PorterDuff.Mode.SRC_ATOP);
		((FloatingActionButton) getView().findViewById(R.id.sendButton)).setImageDrawable(drawable);
	}

	@Override
	public void showPictureContainer(boolean show) {
		switchButonImage(R.drawable.ic_camera_24dp);

		getView().findViewById(R.id.messageEditText).setEnabled(true);
		getView().findViewById(R.id.pictureTakenContainer).setVisibility(View.GONE);
		getView().findViewById(R.id.picture).destroyDrawingCache();
	}

	public void pictureTaken(File file) {
		mPresenter.pictureTaken(file);
	}

	@Override
	public void injectComponent() {
		User thatUser = getArguments().getParcelable(ChatActivity.EXTRA_CONTACT);
// as long as ChatComponent != null, objects annotated with @FragmentScope are in memory
		DaggerChatComponent.builder().activityComponent(((ChatActivity) getActivity()).getActivityComponent())
				.chatModule(new ChatModule(this, thatUser))
				.build()
				.inject(this);
	}

	interface ChatFragmentInteraction{
		void onCameraClick();
	}

	private class ChatTextWatcher implements android.text.TextWatcher {
		boolean mTyping = false;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (!mTyping && count > 0) mTyping = true;
			if (mTyping && count == 0) mTyping = false;

			mPresenter.onTyping(mTyping);
			if (s.length() > 0)
				switchButonImage(R.drawable.ic_send_24dp);
			else if (s.length() == 0 && mPictureTakenContainer.getVisibility() == View.GONE)
				switchButonImage(R.drawable.ic_camera_24dp);
		}

		@Override
		public void afterTextChanged(Editable s) {
		}

	}
}
