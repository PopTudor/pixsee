package com.marked.pixsee.friends.cards;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.commons.SpaceItemDecorator;
import com.marked.pixsee.friends.cards.data.Message;
import com.marked.pixsee.friends.friends.data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardFragment extends Fragment implements CardContract.View {
	private static final String ARG_FRIEND = "param1";
	private RecyclerView mCardRecyclerView;
	private CardAdapter mCardAdapter;

	private User mFriend;

	private OnFragmentInteractionListener mListener;
	private CardContract.Presenter mPresenter;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param friend Friend's messages to show on the screen
	 * @return A new instance of fragment MessagesFragment.
	 */
	public static CardFragment newInstance(User friend) {
		CardFragment fragment = new CardFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_FRIEND, friend);
		fragment.setArguments(args);
		return fragment;
	}

	public CardFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mFriend = getArguments().getParcelable(ARG_FRIEND);
		}
		mCardAdapter = new CardAdapter(new ArrayList<Message>(), mPresenter);
		if (mPresenter != null) {

			mPresenter.loadMore(10, mFriend.getUserID());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
		mCardRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardRecyclerView);
		mCardRecyclerView.setAdapter(mCardAdapter);
		mCardRecyclerView.addItemDecoration(new SpaceItemDecorator(getActivity(),R.dimen.item_spacing_chat));
		mCardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		mPresenter.start();
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					                           + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void setPresenter(CardContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void showCards(List<Message> cards) {
		mCardAdapter.setDataset(cards);
		mCardAdapter.notifyDataSetChanged();
	}

	@Override
	public void showNoCards() {

	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}
}
