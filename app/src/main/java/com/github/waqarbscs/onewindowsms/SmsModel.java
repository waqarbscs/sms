package com.github.waqarbscs.onewindowsms;

import android.os.Parcel;
import android.os.Parcelable;

public class SmsModel  implements Parcelable{

    public static final String ERROR_UNKNOWN = "UNKNOWN";
    public static final String ERROR_GENERIC = "GENERIC";
    public static final String ERROR_NO_SERVICE = "NO_SERVICE";
    public static final String ERROR_NULL_PDU = "NULL_PDU";
    public static final String ERROR_RADIO_OFF = "RADIO_OFF";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_FAILED = "FAILED";

    private long timestampCreated;
    private long timestampScheduled;
    private String recipientNumber;
    private String recipientName;
    private String message;
    private String status = STATUS_PENDING;

    private String result = "";

    public SmsModel() {

        timestampCreated = 0;
        timestampScheduled = 0;

        recipientNumber = "NA";
        recipientName = "NA";
        message = "NA";
        status = "NA";

    }
    public void setInitialValues() {

        timestampCreated = 0;
        timestampScheduled = 0;

        recipientNumber = "NA";
        recipientName = "NA";
        message = "NA";
        status = "NA";

    }
    protected SmsModel(Parcel in) {
        timestampCreated = in.readLong();
        timestampScheduled = in.readLong();
        recipientNumber = in.readString();
        recipientName = in.readString();
        message = in.readString();
        status = in.readString();

    }

    public static final Parcelable.Creator<SmsModel> CREATOR = new Parcelable.Creator<SmsModel>() {
        @Override
        public SmsModel createFromParcel(Parcel in) {
            return new SmsModel(in);
        }

        @Override
        public SmsModel[] newArray(int size) {
            return new SmsModel[size];
        }
    };


    public int getId() {
        return (int) (getTimestampCreated() / 1000);
    }

    public Long getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(long timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public Long getTimestampScheduled() {
        return timestampScheduled;
    }

    public void setTimestampScheduled(long timestampScheduled) {
        this.timestampScheduled = timestampScheduled;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(String recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(timestampCreated);
        dest.writeLong(timestampScheduled);

        dest.writeString(recipientNumber);
        dest.writeString(recipientName);
        dest.writeString(message);
        dest.writeString(status);


    }
}
