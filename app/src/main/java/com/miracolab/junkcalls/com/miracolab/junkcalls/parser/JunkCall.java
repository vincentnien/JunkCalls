package com.miracolab.junkcalls.com.miracolab.junkcalls.parser;

/**
 * Created by vincent on 2015/12/8.
 */
public class JunkCall {

    enum CallType {
        NORMAL,
        JUNK,
    };

    private CallType mCallType;
    private String mPhoneCall;
    private String mDescription;

    private JunkCall(CallType type, String phone, String description) {
        mCallType = type;
        mPhoneCall = phone;
        mDescription = description;
    }

    public static JunkCall create(CallType type, String phone, String description) {
        return new JunkCall(type, phone, description);
    }

    public static JunkCall create(String phone, String description) {
        return new JunkCall(CallType.NORMAL, phone, description);
    }

    public String phoneNumber() {
        return mPhoneCall;
    }

    public String description() {
        return mDescription;
    }
}
