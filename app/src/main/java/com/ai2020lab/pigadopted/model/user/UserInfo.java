package com.ai2020lab.pigadopted.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 用户信息实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class UserInfo implements Parcelable {
	/**
	 * 用户id
	 */
	@Expose
	@SerializedName("user_id")
	public String userID;
	/**
	 * 用户角色类型
	 */
	@Expose
	@SerializedName("role_type")
	public String roleType;
	/**
	 * 用户名
	 */
	@Expose
	@SerializedName("user_name")
	public String userName;
	/**
	 * 用户头像地址链接
	 */
	@Expose
	@SerializedName("user_portrait")
	public String userPortrait;


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.userID);
		dest.writeString(this.roleType);
		dest.writeString(this.userName);
		dest.writeString(this.userPortrait);
	}

	public UserInfo() {
	}

	protected UserInfo(Parcel in) {
		this.userID = in.readString();
		this.roleType = in.readString();
		this.userName = in.readString();
		this.userPortrait = in.readString();
	}

	public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
		public UserInfo createFromParcel(Parcel source) {
			return new UserInfo(source);
		}

		public UserInfo[] newArray(int size) {
			return new UserInfo[size];
		}
	};
}
