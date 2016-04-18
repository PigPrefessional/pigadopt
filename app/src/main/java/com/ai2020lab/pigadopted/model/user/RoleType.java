/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.model.user;

/**
 * 角色类型
 * Created by Justin Z on 2016/4/17.
 * 502953057@qq.com
 */
public enum RoleType {
	PROVIDER(1, "provider"),
	CUSTOMER(2, "customer");

	private int id;
	private String name;

	RoleType(int id, String name){
		this.id = id;
		this.name = name;
	}

	public int getId(){
		return id;
	}

	public static RoleType getRoleTypeById(int id) {
		for (RoleType type : values()) {
			if (type.id == id) {
				return type;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "RoleType{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
