package com.marked.vifo.ui.fragment.messaging

import android.content.Context
import android.os.*
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.marked.vifo.model.message.Message
import com.marked.vifo.model.contact.Contact
import com.marked.vifo.model.database.DatabaseContract
import com.marked.vifo.model.database.database
import com.marked.vifo.model.message.MessageDataset
import com.marked.vifo.model.message.message
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
import org.jetbrains.anko.onClick
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
	private val mThatUser by lazy { arguments.getParcelable<Contact>(ContactDetailActivity.EXTRA_CONTACT) }

	private val mMessagesInstance by lazy { MessageDataset.getInstance(mContext) }
	private val mMessageAdapter by lazy { MessageAdapter(mContext, mMessagesInstance) }

	private val mLinearLayoutManager by lazy { LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) }

	private val mSocket by lazy { IO.socket(ServerConstants.SERVER) }
	private var mCallback: ContactDetailFragmentInteraction? = null

	private val onMessage by lazy { onNewMessage() }
	private val onTyping by lazy { onTyping() }


	@Throws(JSONException::class)
	fun sendMessage(messageText: String, messageType: Int) {
		val message = Message.Builder().addData(MessageConstants.DATA_BODY, messageText).messageType(messageType).from(mThisUser).to(mThatUser.id).build()
		//		doGcmSendUpstreamMessage(message);
		val jsonObject = message.toJSON()

		mSocket.emit(ON_NEW_MESSAGE, jsonObject)
		message.messageType = reverseMessageType(messageType) /* after the message is sent with message type 1 (to appear on the left for the other user) we want to appear on the right for this user*/
		addMessage(message)

	}

	fun reverseMessageType(int: Int):Int{
		when(int){
			MessageConstants.MessageType.YOU_MESSAGE -> return MessageConstants.MessageType.ME_MESSAGE
			MessageConstants.MessageType.YOU_IMAGE -> return MessageConstants.MessageType.ME_IMAGE
			MessageConstants.MessageType.ME_MESSAGE -> return MessageConstants.MessageType.YOU_MESSAGE
			MessageConstants.MessageType.ME_IMAGE -> return MessageConstants.MessageType.YOU_IMAGE
			else -> return MessageConstants.MessageType.TYPING
		}
	}

	/**
	 * This is used to receive messages from socket.io

	 * @param from
	 * @param data
	 */
	override fun receiveMessage(from: String, data: Bundle) {
		val messageType: Int = data.getInt("type", MessageConstants.MessageType.YOU_MESSAGE)
		val message = Message.Builder().addData(data).messageType(messageType).from(mThatUser.id).to(mThatUser.id).build()
		addMessage(message)
	}

	/** Add message to dataset and notify the adapter of the change
	 * @param message the message to add
	 */
	private fun addMessage(message: Message) {
		mMessagesInstance.add(message)
		mMessageAdapter.notifyItemInserted(mMessagesInstance.size - 1)
		messagesRecyclerView.scrollToPosition(mMessagesInstance.size - 1)
	}

	/**
	 * Remove message from dataset and notify the adapter
	 */
	private fun removeMessage() {
		if (mMessagesInstance.isEmpty())
			return
		mMessagesInstance.removeAt(mMessagesInstance.size - 1)
		mMessageAdapter.notifyItemRemoved(mMessagesInstance.size)
		messagesRecyclerView.scrollToPosition(mMessagesInstance.size)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		GCMListenerService.setCallbacks(this)

		mSocket.on(ON_NEW_MESSAGE, onMessage)
		mSocket.on(ON_TYPING, onTyping)

		mSocket.connect()

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
		rootView.messagesRecyclerView.apply {
			layoutManager = mLinearLayoutManager
			addItemDecoration(SpacesItemDecoration(15))
			adapter = mMessageAdapter
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
		mMessagesInstance.clear()
	}

	override fun onResume() {
		super.onResume()
		isInForeground = true
		mMessagesInstance.loadMore(mThatUser)
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
		mSocket.emit(ON_TYPING, JSONObject("{from:%s,to:%s,typing:%s}".format(mThisUser, mThatUser.id, typing)))
	}

	fun onNewMessage(): Emitter.Listener {
		val onMessage = Emitter.Listener { args ->
			Handler(Looper.getMainLooper()).post {
				val json = JSONObject(args[0].toString())
				val data = json.getJSONObject(MessageConstants.DATA_PAYLOAD)

				val body = data.getString(MessageConstants.DATA_BODY)
				val type = json.getInt(MessageConstants.MESSAGE_TYPE) // MessageConstants.MessageType.ME_MESSAGE
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
					if (!mMessagesInstance.isEmpty() && mMessagesInstance.last().messageType == MessageConstants.MessageType.TYPING)
						return@Runnable
					val message = Message.Builder().messageType(MessageConstants.MessageType.TYPING).build()
					addMessage(message)
				} else if (!mMessagesInstance.isEmpty())
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
