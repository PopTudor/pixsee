package com.marked.pixsee.chat.data
		;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tudor Pop on 04-Dec-15.
 */

public class Message implements MessageConstants,Comparable<Message> {

	protected Message(Builder builder) {
		data = Collections.unmodifiableMap(builder.data);
		notificationParams = Collections.unmodifiableMap(builder.notificationParams);

		collapseKey = builder.collapseKey;
		delayWhileIdle = builder.delayWhileIdle;
		timeToLive = builder.timeToLive;
		restrictedPackageName = builder.restrictedPackageName;
		messageType = builder.messageType;
		to = builder.to;
		from = builder.from;
		date = builder.date;
		id = builder.id;
	}

	private Message() {
		id = UUID.randomUUID().toString();
	}

	/**
	 * Gets the payload data, which is immutable.
	 * IN DATA ADD ALL OTHER OPTIONS LIKE to/from/date/messageType
	 */
	Map<String, String> data;

	/**
	 * Gets the notification params, which are immutable.
	 */
	private Map<String, String> notificationParams;
	private String restrictedPackageName;
	private String collapseKey;
	private Boolean delayWhileIdle;
	private Integer timeToLive;

	private String to;
	@SerializedName(value = "from", alternate = {"from_usr"})
	private String from;
	@SerializedName(value = "messageType",alternate = {"type"})
	private Integer messageType;

	private String date;
	private String id;

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(MessageConstants.COLLAPSE_OPTION, collapseKey);
			jsonObject.put(MessageConstants.DELAY_WHILE_IDLE_OPTION, delayWhileIdle);
			jsonObject.put(MessageConstants.TIME_TO_LIVE_OPTION, timeToLive);
			jsonObject.put(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, restrictedPackageName);
			jsonObject.put(MessageConstants.MESSAGE_TYPE, messageType);
			jsonObject.put(MessageConstants.DATA_PAYLOAD, mapToJSON(data));
			jsonObject.put(MessageConstants.TO, to);
			jsonObject.put(MessageConstants.FROM, from);
			jsonObject.put(MessageConstants.CREATION_DATE, date);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	private JSONObject mapToJSON(Map<String, String> map) {
		JSONObject result = new JSONObject();

		try {
			for (Map.Entry<String, String> it : map.entrySet()) {
				result.put(it.getKey(), it.getValue());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		if (collapseKey != null && !collapseKey.isEmpty())
			bundle.putString(MessageConstants.COLLAPSE_OPTION, collapseKey);
		if (timeToLive != null)
			bundle.putInt(MessageConstants.TIME_TO_LIVE_OPTION, timeToLive);
		if (delayWhileIdle == true)
			bundle.putBoolean(MessageConstants.DELAY_WHILE_IDLE_OPTION, delayWhileIdle);
		if (restrictedPackageName != null && !restrictedPackageName.isEmpty())
			bundle.putString(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, restrictedPackageName);
		if (data.containsKey(MessageConstants.DATA_BODY))
			bundle.putString(MessageConstants.DATA_BODY, data.get(MessageConstants.DATA_BODY));
		if (to != null && !to.isEmpty())
			bundle.putString(MessageConstants.TO, to);
		if (from != null && !from.isEmpty())
			bundle.putString(MessageConstants.FROM, from);
		bundle.putString(MessageConstants.CREATION_DATE, date);

		return bundle;
	}

	public Map<String, String> getNotificationParams() {
		return notificationParams;
	}

	public String getRestrictedPackageName() {
		return restrictedPackageName;
	}

	public String getCollapseKey() {
		return collapseKey;
	}

	public Boolean getDelayWhileIdle() {
		return delayWhileIdle;
	}

	public Integer getTimeToLive() {
		return timeToLive;
	}

	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	public String getDate() {
		return date;
	}

//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder("Message(");
//		if (collapseKey != null) {
//			builder.append(MessageConstants.COLLAPSE_OPTION + "=").append(collapseKey).append(", ");
//		}
//		if (timeToLive != null) {
//			builder.append(MessageConstants.TIME_TO_LIVE_OPTION + "=").append(timeToLive).append(", ");
//		}
//		if (delayWhileIdle != null) {
//			builder.append(MessageConstants.DELAY_WHILE_IDLE_OPTION + "=").append(delayWhileIdle).append(", ");
//		}
//		if (restrictedPackageName != null) {
//			builder.append(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION + "=").append(restrictedPackageName).append(", ");
//		}
//		if (to != null) {
//			builder.append(MessageConstants.TO + "=").append(to).append(", ");
//		}
//		if (from != null) {
//			builder.append(MessageConstants.FROM + "=").append(from).append(", ");
//		}
//		appendMap(builder, "data", data);
//		appendMap(builder, "notificationParams", notificationParams);
//		// Remove trailing ", "
//		if (builder.charAt(builder.length() - 1) == ' ') {
//			builder.delete(builder.length() - 2, builder.length());
//		}
//		builder.append(")");
//		return builder.toString();
//	}

	private void appendMap(StringBuilder builder, String name, Map<String, String> map) {
		if (!map.isEmpty()) {
			builder.append(name).append("= {");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				builder.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
			}
			// Remove trailing ","
			builder.delete(builder.length() - 1, builder.length());
			builder.append("}, ");
		}
	}

	@Override
	public int compareTo(@NonNull Message another) {
		return id.compareTo(another.getId());
	}

	public static class Builder {
		private LinkedHashMap<String, String> data;
		private LinkedHashMap<String, String> notificationParams;
		// optional parameters
		private String collapseKey;
		private boolean delayWhileIdle;
		private Integer timeToLive;
		private String restrictedPackageName;
		private String to;
		private String from;
		private String room;
		private String id = UUID.randomUUID().toString();

		private String date = String.valueOf(new Date().getTime());
		private int messageType = 0;

		{
			data = new LinkedHashMap<>();
			notificationParams = new LinkedHashMap<>();
		}

		/**
		 * Adds a key/value pair to the payload data.
		 */
		public Builder addData(String key, String value) {
			data.put(key, value);
			return this;
		}

		/**
		 * Adds a bundle to the payload data.
		 */
		public Builder addData(Bundle bundle) {
			if (collapseKey != null)
				data.put(MessageConstants.COLLAPSE_OPTION, bundle.getString(MessageConstants.COLLAPSE_OPTION));
			if (timeToLive != null)
				data.put(MessageConstants.TIME_TO_LIVE_OPTION, bundle.getString(MessageConstants.TIME_TO_LIVE_OPTION));
			if (delayWhileIdle)
				data.put(MessageConstants.DELAY_WHILE_IDLE_OPTION, bundle.getString(MessageConstants.DELAY_WHILE_IDLE_OPTION));
			if (restrictedPackageName != null)
				data.put(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, bundle.getString(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION));

            /*data:{'text':'very long string'}*/
			data.put(MessageConstants.DATA_BODY, bundle.getString(MessageConstants.DATA_BODY));
			return this;
		}
		public Builder addData(Map<String,String> data){
			this.data.putAll(data);
			return this;
		}

		public Builder date(String date) {
			this.date = date;
			return this;
		}
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		/**
		 * Sets the collapseKey property.
		 */
		public Builder collapseKey(String value) {
			collapseKey = value;
			return this;
		}

		/**
		 * Sets the target where to send the message
		 *
		 * @param target the contact where to send the message
		 */
		public Builder to(String target) {
			to = target;
			return this;
		}

		/**
		 * Set the source of this message. The current user that is using the app
		 *
		 * @param from this user
		 */
		public Builder from(String from) {
			this.from = from;
			return this;
		}

		/**
		 * The room where to send the message
		 *
		 * @param room
		 */
		public Builder room(String room) {
			this.room = room;
			return this;
		}

		/**
		 * Sets the delayWhileIdle property (default value is false).
		 */
		public Builder delayWhileIdle(boolean value) {
			delayWhileIdle = value;
			return this;
		}

		/**
		 * Sets the messageType property (default value is 0).
		 * MessageType is defined in MessageConstants.MessageType( ME_MESSAGE, YOU_MESSAGE )
		 * Todo should this be abstracted away with a Decorator ?
		 */
		public Builder messageType(int value) {
			messageType = value;
			return this;
		}


		/**
		 * Sets the time to live, in seconds.
		 */
		public Builder timeToLive(int value) {
			timeToLive = value;
			return this;
		}

		/**
		 * Sets the restrictedPackageName property.
		 */
		public Builder restrictedPackageName(String value) {
			restrictedPackageName = value;
			return this;
		}

		/**
		 * Sets the notification icon.
		 */
		public Builder notificationIcon(String value) {
			notificationParams.put(MessageConstants.NOTIFICATION_ICON, value);
			return this;
		}

		/**
		 * Sets the notification title text.
		 */
		public Builder notificationTitle(String value) {
			notificationParams.put(MessageConstants.NOTIFICATION_TITLE, value);
			return this;
		}

		/**
		 * Sets the notification body text.
		 */
		public Builder notificationBody(String value) {
			notificationParams.put(MessageConstants.NOTIFICATION_BODY, value);
			return this;
		}

		/**
		 * Sets the notification click action.
		 */
		public Builder notificationClickAction(String value) {
			notificationParams.put(MessageConstants.NOTIFICATION_ACTION_CLICK, value);
			return this;
		}

		/**
		 * Sets the notification sound.
		 */
		public Builder notificationSound(String value) {
			notificationParams.put("sound", value);
			return this;
		}

		/**
		 * Sets the notification tag.
		 */
		public Builder notificationTag(String value) {
			notificationParams.put("tag", value);
			return this;
		}

		/**
		 * Sets the notification color.
		 */
		public Builder notificationColor(String value) {
			notificationParams.put("color", value);
			return this;
		}

		public Message build() {
			return new Message(this);
		}
	}

	public String getId() {
		return id;
	}

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public Map<String, String> getData() {
		return data;
	}
}

//	fun message(init:Message.()->Unit):Message{
//			                           val message=Message()
//			                           message.init()
//			                           return message
//			                           }
