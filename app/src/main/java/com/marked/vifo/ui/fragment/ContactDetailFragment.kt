package com.marked.vifo.ui.fragment

import android.content.Context
import android.os.*
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.marked.vifo.R
import com.marked.vifo.extra.GCMConstants
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.extra.ServerConstants
import com.marked.vifo.gcm.service.GCMListenerService
import com.marked.vifo.helper.SpacesItemDecoration
import com.marked.vifo.model.Message
import com.marked.vifo.model.contact.Contact
import com.marked.vifo.model.database.DatabaseContract
import com.marked.vifo.model.database.database
import com.marked.vifo.ui.activity.ContactDetailActivity
import com.marked.vifo.ui.adapter.MessageAdapter
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_contact_detail.*
import kotlinx.android.synthetic.main.fragment_contact_detail.view.*
import org.jetbrains.anko.async
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.onTouch
import org.jetbrains.anko.support.v4.defaultSharedPreferences
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * A fragment representing a single Contact detail screen.
 * This fragment is either contained in a [ContactListActivity]
 * in two-pane mode (on tablets) or a [ContactDetailActivity]
 * on handsets.
 */
class ContactDetailFragment : Fragment(), GCMListenerService.Callbacks {
	private val mContext by lazy { activity }
	private val mThisUser by lazy { defaultSharedPreferences.getString(GCMConstants.USER_ID, null) }

	private val mMessagesDataset by lazy { ArrayList<Message>() }
	private val mMessageAdapter by lazy { MessageAdapter(mContext, mMessagesDataset) }

	private val mLinearLayoutManager by lazy { LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) }
	private val mSocket by lazy { IO.socket(ServerConstants.SERVER) }

	private val mThatUser by lazy { arguments.getParcelable<Contact>(ContactDetailActivity.EXTRA_CONTACT) }
	private var mCallback: ContactDetailFragmentInteraction? = null

	private val onMessage by lazy { onNewMessage() }
	private val onTyping by lazy { onTyping() }

	@Throws(JSONException::class)
	fun sendMessage(messageText: String) {
		val message = Message.Builder().addData(MessageConstants.DATA_BODY, messageText).from(mThisUser).to(mThatUser.id).build()
		//		doGcmSendUpstreamMessage(message);
		val jsonObject = message.toJSON()

		mSocket.emit(ON_NEW_MESSAGE, jsonObject)
		addMessage(message)

	}

	/**
	 * This is used to receive messages from socket.io

	 * @param from
	 * @param data
	 */
	override fun receiveMessage(from: String, data: Bundle) {
		val message = Message.Builder().addData(data).viewType(MessageConstants.MessageType.YOU).build()
		addMessage(message)
	}

	/** Add message to dataset and notify the adapter of the change
	 * @param message the message to add
	 */
	private fun addMessage(message: Message) {
		mMessagesDataset.add(message)
		mMessageAdapter.notifyItemInserted(mMessagesDataset.size - 1)
		messagesRecyclerView.scrollToPosition(mMessagesDataset.size - 1)
		async() {
			mContext.database.use {
				insert(DatabaseContract.Message.TABLE_NAME, null, message.toContentValues())
			}
		}
	}

	/**
	 * Remove message from dataset and notify the adapter
	 */
	private fun removeMessage() {
		if (mMessagesDataset.isEmpty())
			return
		mMessagesDataset.removeAt(mMessagesDataset.size - 1)
		mMessageAdapter.notifyItemRemoved(mMessagesDataset.size)
		messagesRecyclerView.scrollToPosition(mMessagesDataset.size)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		GCMListenerService.setCallbacks(this)

		mSocket.on(ON_NEW_MESSAGE, onMessage)
		mSocket.on(ON_TYPING, onTyping)

		mSocket.connect()

		async() {
			mContext.database.use {
				select(DatabaseContract.Message.TABLE_NAME, DatabaseContract.Message.COLUMN_DATA_BODY, DatabaseContract.Message.COLUMN_TYPE, DatabaseContract.Message.COLUMN_DATE)
						.exec {
							parseList(rowParser { t1: String, t2: Int, t3: Long ->
								val message = Message.Builder().addData(MessageConstants.DATA_BODY, t1).viewType(t2).date(t3).build()
								mMessagesDataset.add(message)
							})
							mMessageAdapter.notifyItemInserted(mMessagesDataset.size - 1)
							messagesRecyclerView.scrollToPosition(mMessagesDataset.size - 1)
						}
			}
		}

		mSocket.emit(ON_NEW_ROOM, JSONObject("{from:%s,to:%s}".format(mThisUser, mThatUser.id)))

	}

	override fun onDestroy() {
		super.onDestroy()
		mSocket.off(ON_NEW_MESSAGE, onMessage)
		mSocket.off(ON_TYPING, onTyping)
		mSocket.disconnect()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false)

		//		mLinearLayoutManager.reverseLayout = true
		rootView.messagesRecyclerView.apply {
			layoutManager = mLinearLayoutManager
			addItemDecoration(SpacesItemDecoration(15))
			adapter = mMessageAdapter
			onTouch { view, motionEvent ->
				val currentFocus = (mContext as AppCompatActivity).currentFocus
				val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
				false
			}
		}
		return rootView
	}

	/**
	 * Send a message to the server using GCM

	 * @param message The message to send
	 */
	private fun doGcmSendUpstreamMessage(message: Message) {
		val gcm = GoogleCloudMessaging.getInstance(mContext)
		val senderId = getString(R.string.gcm_defaultSenderId)
		val msgId = UUID.randomUUID().toString()
		val token = mThatUser.token
		val data = message.toBundle()
		data.putString(GCMConstants.TOKEN, token)

		if (msgId.isEmpty())
			return

		object : AsyncTask<Void, Void, String>() {
			override fun doInBackground(vararg params: Void): String? {
				try {
					gcm.send(senderId + GCMConstants.SERVER_UPSTREAM_ADRESS, msgId, data)
					return null
				} catch (ex: IOException) {
					return "Error sending upstream message:" + ex.message
				}

			}

			override fun onPostExecute(result: String?) {
				if (result != null) {
					Toast.makeText(mContext, "send message failed: " + result, Toast.LENGTH_LONG).show()
				}
			}
		}.execute(null, null, null)
	}

	override fun onPause() {
		super.onPause()
		isInForeground = false
	}

	override fun onResume() {
		super.onResume()
		isInForeground = true
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		try {
			mCallback = context as ContactDetailFragmentInteraction?
		} catch (e: ClassCastException) {
			throw ClassCastException(context!!.toString() + " must implement " +
					ContactDetailFragmentInteraction::class.java)
		}

	}

	fun onTyping(typing: Boolean) {
		try {
			mSocket.emit(ON_TYPING, JSONObject("{from:%s,to:%s,typing:%s}".format(mThisUser, mThatUser.id, typing)))
		} catch (e: JSONException) {
			e.printStackTrace()
		}

	}

	fun onNewMessage(): Emitter.Listener {
		val onMessage = Emitter.Listener { args ->
			Handler(Looper.getMainLooper()).post {
				val json = JSONObject(args[0].toString())
				val data = json.getJSONObject(MessageConstants.DATA_PAYLOAD)

				val body = data.getString(MessageConstants.DATA_BODY)
				val type = json.getInt(MessageConstants.MESSAGE_TYPE) // MessageConstants.MessageType.ME
				val from = json.getString(MessageConstants.FROM)

				val bundle = Bundle()
				bundle.putInt(MessageConstants.MESSAGE_TYPE, type)
				bundle.putString(MessageConstants.DATA_BODY, body)

				receiveMessage(from, bundle)
			}
		}

		return onMessage
	}

	fun onTyping(): Emitter.Listener {
		val onTyping = Emitter.Listener { args ->
			Handler(Looper.getMainLooper()).post(Runnable {
				val typing = args[0] as Boolean
				if (typing) {
					if (!mMessagesDataset.isEmpty() && mMessagesDataset.last().messageType == MessageConstants.MessageType.TYPING)
						return@Runnable
					val message = Message.Builder().viewType(MessageConstants.MessageType.TYPING).build()
					addMessage(message)
				} else if (!mMessagesDataset.isEmpty())
				// !typing
					removeMessage()
			})
		}
		return onTyping
	}

	interface ContactDetailFragmentInteraction

	companion object Static {
		const val ON_NEW_MESSAGE = "onMessage"
		const val ON_NEW_ROOM = "onRoom"
		const val ON_TYPING = "onTyping"
		/**
		 * keep track if the user is interacting with the app. If not, disconnect the socket
		 */
		/**
		 * Check if the user is using the app

		 * @return if the app is in foreground or not
		 */
		var isInForeground: Boolean = false
			private set

		@JvmStatic
		fun newInstance(parcelable: Parcelable): ContactDetailFragment {
			val bundle = Bundle()
			bundle.putParcelable(ContactDetailActivity.EXTRA_CONTACT, parcelable)
			val fragment = ContactDetailFragment()
			fragment.arguments = bundle
			return fragment
		}
	}

}
