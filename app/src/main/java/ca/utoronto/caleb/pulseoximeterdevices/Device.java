package ca.utoronto.caleb.pulseoximeterdevices;

import android.hardware.usb.UsbDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Device implements Parcelable {

    private UsbDevice mDevice;
    private String mType;

    private String name, desc, userDesc;

    static final class FINGERTIP_DEVICE {
        static final String USB_NAME = "USBUART";
        static final String DESCRIPTION = "Fingertip Reader";
    }

    static final class FLORA_DEVICE {
        static final String USB_NAME = "Flora";
        static final String DESCRIPTION = "Adafruit Flora Board";
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mDevice, flags);
        dest.writeStringArray(new String[]{
                mType, name, desc, userDesc
        });
    }

    public Device(Parcel in) {
        mDevice = in.readParcelable(UsbDevice.class.getClassLoader());
        String[] saved = in.createStringArray();
        mType = saved[0];
        name = saved[1];
        desc = saved[2];
        userDesc = saved[3];
    }

    public Device(UsbDevice device) {
        mDevice = device;
        mType = device.getProductName();
        if (isValid()) {
            Log.d(MainActivity.TAG, "Valid device detected");
            switch (mType) {
                case FINGERTIP_DEVICE.USB_NAME:
                    name = FINGERTIP_DEVICE.USB_NAME;
                    desc = FINGERTIP_DEVICE.DESCRIPTION;
                    break;
                case FLORA_DEVICE.USB_NAME:
                    name = FLORA_DEVICE.USB_NAME;
                    desc = FLORA_DEVICE.DESCRIPTION;
                    break;
            }
        } else {
            Log.d(MainActivity.TAG, "Invalid Device Detected");
            Log.d(MainActivity.TAG, device.toString());
        }
    }

    public boolean isValid() {
        return (mType.equals(FINGERTIP_DEVICE.USB_NAME))
                || (mType.equals(FLORA_DEVICE.USB_NAME));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }

    public String getUserDescription() {
        return userDesc;
    }

    public void setUserDescription(String desc) {
        this.userDesc = desc;
    }

    public boolean isType(String type) {
        return type.equals(mType);
    }

    public UsbDevice getUsb() {
        return mDevice;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", desc);
        data.put("user_description", userDesc);
        return data;
    }
}