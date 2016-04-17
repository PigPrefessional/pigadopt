/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.common;

import android.content.Context;

import com.ai2020lab.aiutils.storage.PreferencesUtils;
import com.ai2020lab.pigadopted.model.user.RoleType;

/**
 * SharedPreferences工具类
 * Created by Justin Z on 2016/4/17.
 * 502953057@qq.com
 */
public class PrefManager {

	public final static String ROLE_TYPE = "role_type";
	public final static String PARTY_ID_PROVIDER = "party_id_provider";
	public final static String PARTY_ID_CUSTOMER = "party_id_customer";

	public final static int PARTY_ID_DEFAULT_CUSTOMER = 2;
	public final static int PARTY_ID_DEFAULT_PROVIDER = 1;

	/**
	 * 设置当前用户角色类型
	 * @param context Context
	 * @param roleType RoleType
	 */
	public static void setRoleType(Context context, RoleType roleType) {
		PreferencesUtils.setInt(context, ROLE_TYPE, roleType.getId());
	}

	/**
	 * 获取当前用户角色类型
	 * @param context Context
	 * @return RoleType 没有找到返回null
	 */
	public static RoleType getRoleType(Context context) {
		return RoleType.getRoleTypeById(PreferencesUtils.getInt(context, ROLE_TYPE, -1));
	}

	/**
	 * 保存卖家PartyID
	 * @param context Context
	 * @param partyID int
	 */
	public static void setPartyIDForProvider(Context context, int partyID){
		PreferencesUtils.setInt(context, PARTY_ID_PROVIDER, partyID);
	}

	/**
	 * 获取卖家PartyID
	 * @param context Context
	 * @return 返回卖家PartyID
	 */
	public static int getPartyIDForProvider(Context context){
		return PreferencesUtils.getInt(context, PARTY_ID_PROVIDER, PARTY_ID_DEFAULT_PROVIDER);
	}

	/**
	 * 保存买家PartyID
	 * @param context Context
	 * @param partyID int
	 */
	public static void setPartyIDForCustomer(Context context, int partyID){
		PreferencesUtils.setInt(context, PARTY_ID_CUSTOMER, partyID);
	}

	/**
	 * 获取买家PartyID
	 * @param context Context
	 * @return 返回买家PartyID
	 */
	public static int getPartyIDForCustomer(Context context){
		return PreferencesUtils.getInt(context, PARTY_ID_CUSTOMER, PARTY_ID_DEFAULT_CUSTOMER);
	}




}
