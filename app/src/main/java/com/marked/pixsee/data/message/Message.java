package com.marked.pixsee.data.message
		;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.marked.pixsee.ui.chat.data.MessageConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tudor Pop on 04-Dec-15.
 */

public class Message implements Parcelable, MessageConstants, Comparable<Message> {

	public static final Creator<Message> CREATOR = new Creator<Message>() {
		@Override
		public Message createFromParcel(Parcel in) {
			return new Message(in);
		}

		@Override
		public Message[] newArray(int size) {
			return new Message[size];
		}
	};
	private String to;
	@SerializedName(value = "from", alternate = {"from_usr"})
	private String from;
	@SerializedName(value = "messageType", alternate = {"type"})
	private int messageType;
	private long date;
	private String id;
	/**
	 * Gets the payload data, which is immutable.
	 * IN DATA ADD ALL OTHER OPTIONS LIKE to/from/date/messageType
	 */
	private Map<String, String> data;

	protected Message(Builder builder) {
		data = Collections.unmodifiableMap(builder.data);

		messageType = builder.messageType;
		to = builder.to;
		from = builder.from;
		date = builder.date;
		id = builder.id;
	}

	private Message() {
		id = UUID.randomUUID().toString();
		data = new HashMap<>();
	}

	public Message(Parcel parcelIn) {
		this();
		List<String> keys = new ArrayList<>();
		List<String> values = new ArrayList<>();
		parcelIn.readStringList(keys);
		parcelIn.readStringList(values);
		for (int i = 0; i < keys.size(); i++)
			data.put(keys.get(i), values.get(i));

		messageType = parcelIn.readInt();
		to = parcelIn.readString();
		from = parcelIn.readString();
		date = parcelIn.readLong();
		id = parcelIn.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringList(new ArrayList<>(data.keySet()));
		dest.writeStringList(new ArrayList<>(data.values()));
		dest.writeInt(messageType);
		dest.writeString(to);
		dest.writeString(from);
		dest.writeLong(date);
		dest.writeString(id);
	}

	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	public long getDate() {
		return date;
	}

	@Override
	public int compareTo(@NonNull Message another) {
		return id.compareTo(another.getId());
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

	public static class Builder {
		private Map<String, String> data = new HashMap<>();
		// optional parameters
		private String to;
		private String from;
		private String id = UUID.randomUUID().toString();

		private long date = new Date().getTime();
		private int messageType = 0;

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
            /*data:{'text':'very long string'}*/
			data.put(MessageConstants.DATA_BODY, bundle.getString(MessageConstants.DATA_BODY));
			return this;
		}
		public Builder addData(Map<String,String> data){
			this.data.putAll(data);
			return this;
		}

		public Builder date(long date) {
			this.date = date;
			return this;
		}
		public Builder id(String id) {
			this.id = id;
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
		 * Sets the messageType property (default value is 0).
		 * MessageType is defined in MessageConstants.MessageType( ME_MESSAGE, YOU_MESSAGE )
		 * Todo should this be abstracted away with a Decorator ?
		 */
		public Builder messageType(int value) {
			messageType = value;
			return this;
		}

		public Message build() {
			return new Message(this);
		}
	}
}