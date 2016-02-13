package com.marked.vifo.model

import android.content.ContentValues
import android.os.Bundle
import com.marked.vifo.extra.MessageConstants
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Tudor Pop on 04-Dec-15.
 */

data class Message private constructor(val builder: Message.Builder) : MessageConstants {
    constructor() : this(Builder())

    /**
     * Gets the payload data, which is immutable.
     * IN DATA ADD ALL OTHER OPTIONS LIKE to/from/date/messageType
     */
    var data: Map<String, String>
    /**
     * Gets the notification params, which are immutable.
     */
    var notificationParams: Map<String, String>
    var restrictedPackageName: String?
    var collapseKey: String?
    var isDelayWhileIdle: Boolean?
    var timeToLive: Int?

    var to: String?
    var from: String?
    var messageType: Int

    var date: Long
    var id: UUID = UUID.randomUUID()

    init {
        data = Collections.unmodifiableMap(builder.data)
        notificationParams = Collections.unmodifiableMap(builder.notificationParams)

        collapseKey = builder.collapseKey
        isDelayWhileIdle = builder.delayWhileIdle
        timeToLive = builder.timeToLive
        restrictedPackageName = builder.restrictedPackageName
        messageType = builder.messageType
        to = builder.to
        from = builder.from
        date = builder.date
    }



    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(MessageConstants.COLLAPSE_OPTION, collapseKey)
        jsonObject.put(MessageConstants.DELAY_WHILE_IDLE_OPTION, isDelayWhileIdle)
        jsonObject.put(MessageConstants.TIME_TO_LIVE_OPTION, timeToLive)
        jsonObject.put(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, restrictedPackageName)
        jsonObject.put(MessageConstants.MESSAGE_TYPE, messageType)
        jsonObject.put(MessageConstants.DATA_PAYLOAD, mapToJSON(data))
        jsonObject.put(MessageConstants.TO, to)
        jsonObject.put(MessageConstants.FROM, from)
        jsonObject.put(MessageConstants.CREATION_DATE, date)

        return jsonObject
    }

    @Throws(JSONException::class)
    private fun mapToJSON(map: Map<String, String>): JSONObject {
        val result = JSONObject()
        for ((key, value) in map)
            result.put(key, value)
        return result
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        if (!collapseKey.isNullOrBlank())
            bundle.putString(MessageConstants.COLLAPSE_OPTION, collapseKey)
        if (timeToLive != null)
            bundle.putInt(MessageConstants.TIME_TO_LIVE_OPTION, timeToLive as Int)
        if (isDelayWhileIdle == true)
            bundle.putBoolean(MessageConstants.DELAY_WHILE_IDLE_OPTION, isDelayWhileIdle as Boolean)
        if (!restrictedPackageName.isNullOrBlank())
            bundle.putString(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, restrictedPackageName)
        if (data.containsKey(MessageConstants.DATA_BODY))
            bundle.putString(MessageConstants.DATA_BODY, data[MessageConstants.DATA_BODY])
        if (!to.isNullOrBlank())
            bundle.putString(MessageConstants.TO, to)
        if (!from.isNullOrBlank())
            bundle.putString(MessageConstants.FROM, from)
        bundle.putLong(MessageConstants.CREATION_DATE, date)

        return bundle
    }

    fun toContentValues(): ContentValues {
        val values = ContentValues()
        //		if (!collapseKey.isNullOrBlank())
        //			values.put(MessageConstants.COLLAPSE_OPTION, collapseKey)
        //		if (timeToLive != null)
        //			values.put(MessageConstants.TIME_TO_LIVE_OPTION, timeToLive)
        //		if (isDelayWhileIdle==true)
        //			values.put(MessageConstants.DELAY_WHILE_IDLE_OPTION, isDelayWhileIdle)
        //		if (!restrictedPackageName.isNullOrBlank())
        //			values.put(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, restrictedPackageName)
        if (data.containsKey(MessageConstants.DATA_BODY))
            values.put(MessageConstants.DATA_BODY, data[MessageConstants.DATA_BODY])
        if (!to.isNullOrBlank())
            values.put("_" + MessageConstants.TO, to)
        //		if (!source.isNullOrBlank())
        //			values.put(MessageConstants.SOURCE, source)
        values.put(MessageConstants.MESSAGE_TYPE, messageType)
        values.put(MessageConstants.CREATION_DATE, date)

        return values
    }

    override fun toString(): String {
        val builder = StringBuilder("Message(")
        if (collapseKey != null) {
            builder.append(MessageConstants.COLLAPSE_OPTION + "=").append(collapseKey).append(", ")
        }
        if (timeToLive != null) {
            builder.append(MessageConstants.TIME_TO_LIVE_OPTION + "=").append(timeToLive as Int).append(", ")
        }
        if (isDelayWhileIdle != null) {
            builder.append(MessageConstants.DELAY_WHILE_IDLE_OPTION + "=").append(isDelayWhileIdle as Boolean).append(", ")
        }
        if (restrictedPackageName != null) {
            builder.append(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION + "=").append(restrictedPackageName).append(", ")
        }
        if (to != null) {
            builder.append(MessageConstants.TO + "=").append(to).append(", ")
        }
        if (from != null) {
            builder.append(MessageConstants.FROM + "=").append(from).append(", ")
        }
        appendMap(builder, "data", data)
        appendMap(builder, "notificationParams", notificationParams)
        // Remove trailing ", "
        if (builder.last() == ' ') {
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
        var restrictedPackageName: String? = null
        var to: String? = null
        var from: String? = null
        var room: String? = null

        var date: Long = Date().time
        var messageType: Int = 0

        init {
            data = LinkedHashMap<String, String>()
            notificationParams = LinkedHashMap<String, String>()
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
            if (restrictedPackageName != null)
                data.put(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, bundle.getString(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION))

            /*data:{'text':'very long string'}*/
            data.put(MessageConstants.DATA_BODY, bundle.getString(MessageConstants.DATA_BODY))
            return this
        }

        fun date(date: Long): Builder {
            this.date = date
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
         * MessageType is defined in MessageConstants.MessageType( ME_MESSAGE, YOU_MESSAGE )
         */
        fun messageType(value: Int): Builder {
            messageType = value
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
fun message(init: Message.() -> Unit): Message {
    val message = Message()
    message.init()
    return message
}
