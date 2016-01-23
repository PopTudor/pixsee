package com.marked.vifo.model

import android.os.Bundle
import com.marked.vifo.extra.MessageConstants
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.*

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
data class Message private constructor(val builder: Message.Builder) : Serializable, MessageConstants {
	/**
	 * Gets the collapse key.
	 */
	val collapseKey: String?
	/**
	 * Gets the delayWhileIdle flag.
	 */
	val isDelayWhileIdle: Boolean?
	/**
	 * Gets the time to live (in seconds).
	 */
	val timeToLive: Int?
	/**
	 * Gets the payload data, which is immutable.
	 */
	val data: Map<String, String>
	/**
	 * Gets the notification params, which are immutable.
	 */
	val notificationParams: Map<String, String>
	/**
	 * Gets the dryRun flag.
	 */
	val isDryRun: Boolean?
	/**
	 * Gets the restricted package name.
	 */
	val restrictedPackageName: String?
	val to: String?
	val from: String?
	/**
	 * Gets the message type.
	 */
	val messageType: Int

	init {
		collapseKey = builder.collapseKey
		isDelayWhileIdle = builder.delayWhileIdle
		data = Collections.unmodifiableMap(builder.data)
		notificationParams = Collections.unmodifiableMap(builder.notificationParams)
		timeToLive = builder.timeToLive
		isDryRun = builder.dryRun
		restrictedPackageName = builder.restrictedPackageName
		messageType = builder.viewType
		to = builder.to
		from = builder.from
	}


	fun toJSON(): JSONObject {
		val jsonObject = JSONObject()
		jsonObject.put(MessageConstants.COLLAPSE_OPTION, collapseKey)
		jsonObject.put(MessageConstants.DELAY_WHILE_IDLE_OPTION, isDelayWhileIdle)
		jsonObject.put(MessageConstants.TIME_TO_LIVE_OPTION, timeToLive)
		jsonObject.put(MessageConstants.DRY_RUN_OPTION, isDryRun)
		jsonObject.put(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, restrictedPackageName)
		jsonObject.put(MessageConstants.MESSAGE_TYPE, messageType)
		jsonObject.put(MessageConstants.DATA_PAYLOAD, mapToJSON(data))
		jsonObject.put(MessageConstants.TO_TARGETS, to)
		jsonObject.put(MessageConstants.FROM_TARGETS, from)

		return jsonObject
	}

	@Throws(JSONException::class)
	private fun mapToJSON(map: Map<String, String>): JSONObject {
		val `object` = JSONObject()
		if (!map.isEmpty())
			for (entry in map.entries)
				`object`.put(entry.key, entry.value)
		return `object`
	}

	fun toBundle(): Bundle {
		val bundle = Bundle()
		if (data.containsKey(MessageConstants.COLLAPSE_OPTION))
			bundle.putString(MessageConstants.COLLAPSE_OPTION, data[MessageConstants.COLLAPSE_OPTION])
		if (data.containsKey(MessageConstants.TIME_TO_LIVE_OPTION))
			bundle.putString(MessageConstants.TIME_TO_LIVE_OPTION, data[MessageConstants.TIME_TO_LIVE_OPTION])
		if (data.containsKey(MessageConstants.DELAY_WHILE_IDLE_OPTION))
			bundle.putString(MessageConstants.DELAY_WHILE_IDLE_OPTION, data[MessageConstants.DELAY_WHILE_IDLE_OPTION])
		if (data.containsKey(MessageConstants.DRY_RUN_OPTION))
			bundle.putString(MessageConstants.DRY_RUN_OPTION, data[MessageConstants.DRY_RUN_OPTION])
		if (data.containsKey(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION))
			bundle.putString(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, data[MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION])
		if (data.containsKey(MessageConstants.DATA_BODY))
			bundle.putString(MessageConstants.DATA_BODY, data[MessageConstants.DATA_BODY])
		if (data.containsKey(MessageConstants.TO_TARGETS))
			bundle.putString(MessageConstants.TO_TARGETS, data[MessageConstants.TO_TARGETS])
		if (data.containsKey(MessageConstants.FROM_TARGETS))
			bundle.putString(MessageConstants.FROM_TARGETS, data[MessageConstants.FROM_TARGETS])
		if (data.containsKey(MessageConstants.DATA_ROOM))
			bundle.putString(MessageConstants.DATA_ROOM, data[MessageConstants.DATA_ROOM])

		return bundle
	}

	override fun toString(): String {
		val builder = StringBuilder("Message(")
		if (collapseKey != null) {
			builder.append(MessageConstants.COLLAPSE_OPTION + "=").append(collapseKey).append(", ")
		}
		if (timeToLive != null) {
			builder.append(MessageConstants.TIME_TO_LIVE_OPTION + "=").append(timeToLive).append(", ")
		}
		if (isDelayWhileIdle != null) {
			builder.append(MessageConstants.DELAY_WHILE_IDLE_OPTION + "=").append(isDelayWhileIdle).append(", ")
		}
		if (isDryRun != null) {
			builder.append(MessageConstants.DRY_RUN_OPTION + ":").append(isDryRun).append(", ")
		}
		if (restrictedPackageName != null) {
			builder.append(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION + "=").append(restrictedPackageName).append(", ")
		}
		if (to != null) {
			builder.append(MessageConstants.TO_TARGETS + "=").append(to).append(", ")
		}
		if (from != null) {
			builder.append(MessageConstants.FROM_TARGETS + "=").append(from).append(", ")
		}
		appendMap(builder, "data", data)
		appendMap(builder, "notificationParams", notificationParams)
		// Remove trailing ", "
		if (builder[builder.length - 1] == ' ') {
			builder.delete(builder.length - 2, builder.length)
		}
		builder.append(")")
		return builder.toString()
	}

	private fun appendMap(builder: StringBuilder, name: String, map: Map<String, String>) {
		if (!map.isEmpty()) {
			builder.append(name).append("= {")
			for (entry in map.entries) {
				builder.append(entry.key).append("=").append(entry.value).append(",")
			}
			// Remove trailing ","
			builder.delete(builder.length - 1, builder.length)
			builder.append("}, ")
		}
	}

	class Builder {

		val data: MutableMap<String, String>
		val notificationParams: MutableMap<String, String>

		// optional parameters
		var collapseKey: String? = null
		var delayWhileIdle: Boolean? = null
		var timeToLive: Int? = null
		var dryRun: Boolean? = null
		var restrictedPackageName: String? = null
		var to: String? = null
		var from: String? = null
		var room: String? = null


		var viewType: Int = 0

		init {
			this.data = LinkedHashMap<String, String>()
			this.notificationParams = LinkedHashMap<String, String>()
		}

		/**
		 * Adds a key/value pair to the payload data.
		 */
		fun addData(key: String, value: String): Builder {
			data.put(key, value)
			return this
		}

		/**
		 * Adds a bundle to the payload data.
		 */
		fun addData(bundle: Bundle): Builder {
			if (collapseKey != null)
				data.put(MessageConstants.COLLAPSE_OPTION, bundle.getString(MessageConstants.COLLAPSE_OPTION))
			if (timeToLive != null)
				data.put(MessageConstants.TIME_TO_LIVE_OPTION, bundle.getString(MessageConstants.TIME_TO_LIVE_OPTION))
			if (delayWhileIdle != null)
				data.put(MessageConstants.DELAY_WHILE_IDLE_OPTION, bundle.getString(MessageConstants.DELAY_WHILE_IDLE_OPTION))
			if (dryRun != null)
				data.put(MessageConstants.DRY_RUN_OPTION, bundle.getString(MessageConstants.DRY_RUN_OPTION))
			if (restrictedPackageName != null)
				data.put(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, bundle.getString(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION))

			/*data:{'text':'very long string'}*/
			data.put(MessageConstants.DATA_BODY, bundle.getString(MessageConstants.DATA_BODY))
			return this
		}

		/**
		 * Sets the collapseKey property.
		 */
		fun collapseKey(value: String): Builder {
			collapseKey = value
			return this
		}

		/**
		 * Sets the target where to send the message
		 * @param target the contact where to send the message
		 */
		fun to(target: String): Builder {
			to = target
			return this
		}

		/**
		 * Set the source of this message. The current user that is using the app
		 * @param from this user
		 */
		fun from(from: String): Builder {
			this.from = from
			return this
		}

		/**
		 * The room where to send the message
		 * @param room
		 */
		fun room(room: String): Builder {
			this.room = room
			return this
		}

		/**
		 * Sets the delayWhileIdle property (default value is false).
		 */
		fun delayWhileIdle(value: Boolean): Builder {
			delayWhileIdle = value
			return this
		}

		/**
		 * Sets the messageType property (default value is 0).
		 * MessageType is defined in MessageConstants.MessageType( ME, YOU )
		 */
		fun viewType(value: Int): Builder {
			viewType = value
			return this
		}


		/**
		 * Sets the time to live, in seconds.
		 */
		fun timeToLive(value: Int): Builder {
			timeToLive = value
			return this
		}


		/**
		 * Sets the dryRun property (default value is false).
		 */
		fun dryRun(value: Boolean): Builder {
			dryRun = value
			return this
		}

		/**
		 * Sets the restrictedPackageName property.
		 */
		fun restrictedPackageName(value: String): Builder {
			restrictedPackageName = value
			return this
		}

		/**
		 * Sets the notification icon.
		 */
		fun notificationIcon(value: String): Builder {
			notificationParams.put(MessageConstants.NOTIFICATION_ICON, value)
			return this
		}

		/**
		 * Sets the notification title text.
		 */
		fun notificationTitle(value: String): Builder {
			notificationParams.put(MessageConstants.NOTIFICATION_TITLE, value)
			return this
		}

		/**
		 * Sets the notification body text.
		 */
		fun notificationBody(value: String): Builder {
			notificationParams.put(MessageConstants.NOTIFICATION_BODY, value)
			return this
		}

		/**
		 * Sets the notification click action.
		 */
		fun notificationClickAction(value: String): Builder {
			notificationParams.put(MessageConstants.NOTIFICATION_ACTION_CLICK, value)
			return this
		}

		/**
		 * Sets the notification sound.
		 */
		fun notificationSound(value: String): Builder {
			notificationParams.put("sound", value)
			return this
		}

		/**
		 * Sets the notification tag.
		 */
		fun notificationTag(value: String): Builder {
			notificationParams.put("tag", value)
			return this
		}

		/**
		 * Sets the notification color.
		 */
		fun notificationColor(value: String): Builder {
			notificationParams.put("color", value)
			return this
		}

		fun build(): Message {
			return Message(this)
		}
	}

}
