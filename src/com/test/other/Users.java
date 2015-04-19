package com.test.other;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @fileName NearByPeople.java
 * @description 附近个人实体类
 * @author _Hill3
 */
public class Users extends Entity implements Parcelable {

    /** 用户常量 **/

    // 共有
    public static final String AVATAR = "avatar";
    public static final String BIRTHDAY = "birthday";
    public static final Parcelable.Creator<Users> CREATOR = new Parcelable.Creator<Users>() {

        @Override
        public Users createFromParcel(Parcel source) {
            Users user = new Users();
            user.setAvatar(source.readInt());
            user.setNickname(source.readString());
            user.setIMEI(source.readString());
            user.setDevice(source.readString());
            user.setIpaddress(source.readString());
            user.setLogintime(source.readString());
            user.setMsgCount(source.readInt());
            return user;
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };
    public static final String DEVICE = "Device";
    public static final String ENTITY_PEOPLE = "entity_people";
    public static final String GENDER = "Gender";
    // 个人
    public static final String ID = "ID";
    public static final String IMEI = "IMEI";
    public static final String IPADDRESS = "Ipaddress";

    public static final String ISCLIENT = "isClient";
    public static final String LOGINTIME = "LoginTime";
    public static final String NICKNAME = "Nickname";
    public static final String ORDER = "order";

    public static final String SERVERIPADDRESS = "serverIPaddress";
    public static Parcelable.Creator<Users> getCreator() {
        return CREATOR;
    }
    private int mAvatar;
    private String mDevice;
    private String mGender;
    private int mGenderBgId;
    private int mGenderId;

    private String mIMEI;
    private String mIpaddress;
    private String mLogintime;
    private String mNickname;
    
    private int msgCount;

    private int order;


    public Users() {
        this.msgCount = 0;
        order = -1;
    }



    public Users(int avatar, String nickname, String gender, String IMEI,
            String device, String ip, String logintime) {
        this.mAvatar = avatar;
        this.mNickname = nickname;
        this.mIMEI = IMEI;
        this.mDevice = device;
        this.mIpaddress = ip;
        this.mLogintime = logintime;
        this.order = -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @JSONField(name = Users.AVATAR)
    public int getAvatar() {
        return this.mAvatar;
    }

    @JSONField(name = Users.DEVICE)
    public String getDevice() {
        return this.mDevice;
    }


    @JSONField(name = Users.GENDER)
    public String getGender() {
        return this.mGender;
    }

    @JSONField(serialize = false)
    public int getGenderBgId() {
        return this.mGenderBgId;
    }

    /** 个人变量 get set **/

    @JSONField(serialize = false)
    public int getGenderId() {
        return this.mGenderId;
    }


    @JSONField(name = Users.IMEI)
    public String getIMEI() {
        return this.mIMEI;
    }

    @JSONField(name = Users.IPADDRESS)
    public String getIpaddress() {
        return this.mIpaddress;
    }

    @JSONField(name = Users.LOGINTIME)
    public String getLogintime() {
        return this.mLogintime;
    }

    @JSONField(serialize = false)
    public int getMsgCount() {
        return this.msgCount;
    }


    @JSONField(name = Users.NICKNAME)
    public String getNickname() {
        return this.mNickname;
    }

    @JSONField(serialize = false)
    public int getOrder() {
        return this.order;
    }

    public void setAvatar(int paramAvatar) {
        this.mAvatar = paramAvatar;
    }

    public void setDevice(String paramDevice) {
        this.mDevice = paramDevice;
    }

    public void setGenderBgId(int paramGenderBgId) {
        this.mGenderBgId = paramGenderBgId;
    }

    public void setGenderId(int paramGenderId) {
        this.mGenderId = paramGenderId;
    }

    public void setIMEI(String paramIMEI) {
        this.mIMEI = paramIMEI;
    }

    public void setIpaddress(String paramIpaddress) {
        this.mIpaddress = paramIpaddress;
    }

    public void setLogintime(String paramLogintime) {
        this.mLogintime = paramLogintime;
    }
    
    public void setMsgCount(int paramMsgCount) {
        this.msgCount = paramMsgCount;
    }

    public void setNickname(String paramNickname) {
        this.mNickname = paramNickname;
    }

    public void setOrder(int paramOrder) {
        this.order = paramOrder;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mAvatar);
        dest.writeString(mNickname);
        dest.writeString(mGender);
        dest.writeString(mIMEI);
        dest.writeString(mDevice);
        dest.writeString(mIpaddress);
        dest.writeString(mLogintime);
        dest.writeInt(msgCount);
    }

}
