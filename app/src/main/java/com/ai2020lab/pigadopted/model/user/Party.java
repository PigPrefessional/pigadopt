/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.model.user;

import java.io.Serializable;

/**
 * Created by Justin Z on 2016/4/17.
 * 502953057@qq.com
 */
public class Party implements Serializable{

	public int id;

	public String name;

	public RoleType roleType;

	@Override
	public String toString() {
		return "Party{" +
				"id=" + id +
				", name='" + name + '\'' +
				", roleType=" + roleType +
				'}';
	}
}
