/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.common;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.pigadopted.model.order.PigPart;
import com.ai2020lab.pigadopted.model.pig.PigCategory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 全局数据管理类<p>
 * 猪品种列表数据，猪部位列表数据在应用初始化的时候就下载并放入内存
 * Created by Justin Z on 2016/3/19.
 * 502953057@qq.com
 */
public class DataManager {

	private final static String TAG = DataManager.class.getSimpleName();
	/**
	 * 猪品种列表
	 */
	private ArrayList<PigCategory> pigCategories;

	private HashMap<String, PigCategory> pigCategoriesMap;
	/**
	 * 猪部位列表
	 */
	private ArrayList<PigPart> pigParts;

	private DataManager() {
	}

	private static class SingletonHolder {
		public final static DataManager INSTANCE = new DataManager();
	}

	/**
	 * 获取本类单例
	 *
	 * @return DataManager
	 */
	public static DataManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 在应用初始化的时候调用
	 */
	public void init() {
		if (pigCategories == null) {
			pigCategories = new ArrayList<>();
		}
		if (pigCategoriesMap == null) {
			pigCategoriesMap = new HashMap<>();
		}
		if (pigParts == null) {
			pigParts = new ArrayList<>();
		}
		pigParts.clear();
		pigCategoriesMap.clear();
		// 初始化数据
		initPigCategories();
	}

	/**
	 * 返回全部猪品种
	 *
	 * @return
	 */
	public ArrayList<PigCategory> getAllPigCategories() {
		return pigCategories;
	}

	/**
	 * 根据品种ID查找品种实体数据
	 *
	 * @param pigCategoryID String
	 * @return 没有的时候返回null
	 */
	public PigCategory getPigCategory(String pigCategoryID) {
		if (pigCategoriesMap == null) {
			LogUtils.i(TAG, "还没有初始化数据");
			return null;
		}
		return pigCategoriesMap.get(pigCategoryID);
	}

	// TODO:实际通过网络获取数据，可以设置策略，当数据没有更新的时候不请求网络
	private void initPigCategories() {
		pigCategories.clear();
		pigCategories.addAll(getPigCategories());
		// 将数据放入HashMap,便于快速检索
		for (PigCategory pigCategory : pigCategories) {
			pigCategoriesMap.put(pigCategory.categoryID,
					pigCategory);
		}

	}


	// TODO:构造猪品种列表基础数据
	private ArrayList<PigCategory> getPigCategories() {
		ArrayList<PigCategory> pigCategories = new ArrayList<>();
		PigCategory pigCategory;
		pigCategory = new PigCategory();
		pigCategory.categoryID = "1";
		pigCategory.categoryName = "长白猪";
		pigCategories.add(pigCategory);
		pigCategory = new PigCategory();
		pigCategory.categoryID = "2";
		pigCategory.categoryName = "荣昌猪";
		pigCategories.add(pigCategory);
		pigCategory = new PigCategory();
		pigCategory.categoryID = "3";
		pigCategory.categoryName = "内江猪";
		pigCategories.add(pigCategory);
		pigCategory = new PigCategory();
		pigCategory.categoryID = "4";
		pigCategory.categoryName = "香猪";
		pigCategories.add(pigCategory);
		pigCategory = new PigCategory();
		pigCategory.categoryID = "5";
		pigCategory.categoryName = "藏猪";
		pigCategories.add(pigCategory);
		pigCategory.categoryID = "6";
		pigCategory.categoryName = "太湖猪";
		pigCategories.add(pigCategory);
		pigCategory.categoryID = "7";
		pigCategory.categoryName = "大白猪";
		pigCategories.add(pigCategory);
		pigCategory.categoryID = "8";
		pigCategory.categoryName = "金华猪";
		pigCategories.add(pigCategory);
		pigCategory.categoryID = "9";
		pigCategory.categoryName = "陆川猪";
		pigCategories.add(pigCategory);
		pigCategory.categoryID = "10";
		pigCategory.categoryName = "民猪";
		pigCategories.add(pigCategory);
		return pigCategories;
	}


}
