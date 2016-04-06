/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.common;

import android.content.Context;

import com.ai2020lab.aiutils.common.JsonUtils;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.model.order.PigPart;
import com.ai2020lab.pigadopted.model.pig.CategoriesListRequest;
import com.ai2020lab.pigadopted.model.pig.CategoriesListResponse;
import com.ai2020lab.pigadopted.model.pig.PigCategory;
import com.ai2020lab.pigadopted.model.pig.PigPartsListRequest;
import com.ai2020lab.pigadopted.model.pig.PigPartsListResponse;
import com.ai2020lab.pigadopted.model.user.BuyerInfoByPartyIDResponse;
import com.ai2020lab.pigadopted.model.user.SellerInfoByPartyIDResponse;
import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * 全局数据管理类<p>
 * 猪品种列表数据，猪部位列表数据在应用初始化的时候就下载并放入内存
 * TODO:还没有网络请求的响应事件监听
 * Created by Justin Z on 2016/3/19.
 * 502953057@qq.com
 */
public class DataManager {

	private final static String TAG = DataManager.class.getSimpleName();

	private Context context;
	/**
	 * 猪品种列表
	 */
	private ArrayList<PigCategory> pigCategories;

	private HashMap<Integer, PigCategory> pigCategoriesMap;
	/**
	 * 猪部位列表
	 */
	private ArrayList<PigPart> pigParts;

	private HashMap<String, PigPart> pigPartsMap;

	private UserInfo buyerInfo;

	private UserInfo sellerInfo;

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
	public void init(Context context) {
		this.context = context;
		initData();
		openDiskCaches(context);
		// 初始化猪品种数据
		initPigCategories();
		// 初始化猪部位数据
		initPigParts();
	}

	private void initData() {
		if (pigCategories == null) {
			pigCategories = new ArrayList<>();
		}
		if (pigCategoriesMap == null) {
			pigCategoriesMap = new HashMap<>();
		}
		if (pigParts == null) {
			pigParts = new ArrayList<>();
		}
		if (pigPartsMap == null) {
			pigPartsMap = new HashMap<>();
		}
	}

	/**
	 * 打开磁盘缓存,在应用退出的时候需要关闭磁盘缓存
	 */
	private void openDiskCaches(Context context) {
		// 打开买家用户信息磁盘缓存
		DiskCacheManager.getInstance().openCache(context, DiskCacheMaxSize.BUYER_INFO,
				DiskCachePath.BUYER_INFO);
		// 打开卖家用户信息磁盘缓存
		DiskCacheManager.getInstance().openCache(context, DiskCacheMaxSize.SELLER_INFO,
				DiskCachePath.SELLER_INFO);
		// 打开猪品种的磁盘缓存
		DiskCacheManager.getInstance().openCache(context, DiskCacheMaxSize.PIG_CATEGORIES_LIST,
				DiskCachePath.PIG_CATEGORIES_LIST);
		// 打开猪部位的磁盘缓存
		DiskCacheManager.getInstance().openCache(context, DiskCacheMaxSize.PIG_BODY_PARTS_LIST,
				DiskCachePath.PIG_BODY_PARTS_LIST);
	}

	/**
	 * 返回全部猪品种列表
	 *
	 * @return 返回全部猪品种列表
	 */
	public ArrayList<PigCategory> getPigCategories() {
		if (pigCategories == null || pigCategories.size() == 0) {
			initPigCategories();
		}
		return pigCategories;
	}

	/**
	 * 根据品种ID查找品种实体数据
	 *
	 * @param pigCategoryID String
	 * @return 没有的时候返回null
	 */
	public PigCategory getPigCategory(String pigCategoryID) {
		if (pigCategories == null || pigCategories.size() == 0) {
			initPigCategories();
		}
		return pigCategoriesMap.get(pigCategoryID);
	}

	/**
	 * 返回猪全部的部位列表
	 *
	 * @return 返回猪全部的部位列表
	 */
	public ArrayList<PigPart> getPigParts() {
		if (pigParts == null || pigParts.size() == 0) {
			initPigParts();
		}
		return pigParts;
	}

	/**
	 * 根据部位code查找部位实体数据
	 *
	 * @param pigPartCode 部位code
	 * @return 返回部位实体数据
	 */
	public PigPart getPigPart(String pigPartCode) {
		if (pigParts == null || pigParts.size() == 0) {
			initPigParts();
		}
		return pigPartsMap.get(pigPartCode);
	}

	public void setSellerInfo(SellerInfoByPartyIDResponse jsonObj) {
		DiskCacheManager.getInstance().putString(DiskCachePath.SELLER_INFO,
				DiskCachePath.SELLER_INFO, JsonUtils.getInstance()
						.serializeToJson(jsonObj.data));
		initSellerInfo();
	}

	public void setBuyerInfo(BuyerInfoByPartyIDResponse jsonObj) {
		DiskCacheManager.getInstance().putString(DiskCachePath.BUYER_INFO,
				DiskCachePath.BUYER_INFO, JsonUtils.getInstance()
						.serializeToJson(jsonObj.data));
		initBuyerInfo();
	}

	/**
	 * 获取买家用户信息
	 *
	 * @return 返回买家用户信息
	 */
	public UserInfo getBuyerInfo() {
		if (buyerInfo == null) {
			initBuyerInfo();
		}
		return buyerInfo;
	}

	/**
	 * 获取卖家用户信息
	 *
	 * @return 返回卖家用户信息
	 */
	public UserInfo getSellerInfo() {
		if (sellerInfo == null) {
			initSellerInfo();
		}
		return sellerInfo;
	}

	private void initSellerInfo() {
		String sellerInfoJson = DiskCacheManager.getInstance()
				.getString(DiskCachePath.SELLER_INFO, DiskCachePath.SELLER_INFO);
		this.sellerInfo = JsonUtils.getInstance()
				.deserializeToObj(sellerInfoJson, SellerInfoByPartyIDResponse.SellerInfoResult.class);
	}

	private void initBuyerInfo() {
		String buyerInfoJson = DiskCacheManager.getInstance()
				.getString(DiskCachePath.BUYER_INFO, DiskCachePath.BUYER_INFO);
		this.buyerInfo = JsonUtils.getInstance()
				.deserializeToObj(buyerInfoJson, BuyerInfoByPartyIDResponse.BuyerInfoResult.class);
	}


	/**
	 * 从磁盘中获取品种数据并放于内存缓存中
	 */
	private void initPigCategories() {
		// 从磁盘读取猪品种缓存
		String categoriesJson = DiskCacheManager.getInstance()
				.getString(DiskCachePath.PIG_CATEGORIES_LIST, DiskCachePath.PIG_CATEGORIES_LIST);
		CategoriesListResponse.CategoriesListResult categories = JsonUtils.getInstance()
				.deserializeToObj(categoriesJson, CategoriesListResponse.CategoriesListResult.class);
		if (categories != null) {
			pigCategories.clear();
			pigCategories.addAll(categories.pigCategories);
			// 将数据放入HashMap,便于快速检索
			for (PigCategory pigCategory : pigCategories) {
				pigCategoriesMap.put(pigCategory.categoryID, pigCategory);
			}
		}
		// 如果磁盘上没有数据则从网络上获取
		else {
			queryPigCategoriesList();
		}
	}

	/**
	 * 从磁盘中获取猪部位数据并放于内存缓存中
	 */
	private void initPigParts() {
		// 从磁盘读取猪部位缓存
		String categoriesJson = DiskCacheManager.getInstance()
				.getString(DiskCachePath.PIG_BODY_PARTS_LIST, DiskCachePath.PIG_BODY_PARTS_LIST);
		PigPartsListResponse.PigPartsListResult pigPartObj = JsonUtils.getInstance()
				.deserializeToObj(categoriesJson, PigPartsListResponse.PigPartsListResult.class);
		if (pigPartObj != null) {
			pigParts.clear();
			pigParts.addAll(pigPartObj.pigParts);
			// 将数据放入HashMap,便于快速检索
			for (PigPart pigPart : pigParts) {
				pigPartsMap.put(pigPart.partCode, pigPart);
			}
		}
		// 如果磁盘上没有数据则从网络上获取
		else {
			queryPigBodyPartList();
		}
	}

	/**
	 * 网络上获取猪品种列表数据
	 */
	private void queryPigCategoriesList() {
		LogUtils.i(TAG, "--请求猪品种列表数据--");
		CategoriesListRequest data = new CategoriesListRequest();
		HttpManager.postJson(context, UrlName.PIG_CATEGORY_LIST.getUrl(), data,
				new JsonHttpResponseHandler<CategoriesListResponse>(context) {
					/**
					 * 成功回调
					 *
					 * @param statusCode 状态码
					 * @param headers    Header
					 * @param jsonObj    服务端返回的对象
					 */
					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            CategoriesListResponse jsonObj) {
						//获取数据成功将数据加入磁盘缓存
						DiskCacheManager.getInstance().putString(DiskCachePath.PIG_CATEGORIES_LIST,
								DiskCachePath.PIG_CATEGORIES_LIST, JsonUtils.getInstance()
										.serializeToJson(jsonObj.data));
						// 重新初始化猪品种数据
						initPigCategories();
					}

					@Override
					public void onCancel() {
						// 没有网络的情况会终止请求
						ToastUtils.getInstance().showToast(context,
								context.getString(R.string.prompt_query_pig_categories_failure));
					}

					@Override
					public void onHandleFailure(String errorMsg) {
						ToastUtils.getInstance().showToast(context,
								context.getString(R.string.prompt_query_pig_categories_failure));
					}

					@Override
					public void onFinish() {

					}
				});
	}

	/**
	 * 网络上获取猪身体部位列表数据
	 */
	private void queryPigBodyPartList() {
		LogUtils.i(TAG, "--请求猪身体部位列表数据--");
		PigPartsListRequest data = new PigPartsListRequest();
		HttpManager.postJson(context, UrlName.PIG_BODY_PART_LIST.getUrl(), data,
				new JsonHttpResponseHandler<PigPartsListResponse>(context) {
					/**
					 * 成功回调
					 *
					 * @param statusCode 状态码
					 * @param headers    Header
					 * @param jsonObj    服务端返回的对象
					 */
					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            PigPartsListResponse jsonObj) {
						//获取数据成功将数据加入磁盘缓存
						DiskCacheManager.getInstance().putString(DiskCachePath.PIG_BODY_PARTS_LIST,
								DiskCachePath.PIG_BODY_PARTS_LIST, JsonUtils.getInstance()
										.serializeToJson(jsonObj.data));
						// 重新初始化猪部位
						initPigParts();
					}

					@Override
					public void onCancel() {
						// 没有网络的情况会终止请求
						ToastUtils.getInstance().showToast(context,
								context.getString(R.string.prompt_query_pig_parts_failure));
					}

					@Override
					public void onHandleFailure(String errorMsg) {
						ToastUtils.getInstance().showToast(context,
								context.getString(R.string.prompt_query_pig_parts_failure));
					}

					@Override
					public void onFinish() {

					}
				});
	}

	private void clearAll() {
		pigCategories.clear();
		pigCategoriesMap.clear();
		pigParts.clear();
		pigPartsMap.clear();
		pigCategories = null;
		pigCategoriesMap = null;
		pigParts = null;
		pigPartsMap = null;
		sellerInfo = null;
		buyerInfo = null;
	}

	/**
	 * 关闭所有缓存,释放资源,在应用退出的时候调用
	 */
	public void release() {
		clearAll();
		// 退出应用的时候清除登录信息
		DiskCacheManager.getInstance().clearCache(DiskCachePath.SELLER_INFO);
		DiskCacheManager.getInstance().clearCache(DiskCachePath.BUYER_INFO);
		DiskCacheManager.getInstance().closeCache();
	}

//	private ArrayList<PigCategory> getPigCategories() {
//		ArrayList<PigCategory> pigCategories = new ArrayList<>();
//		PigCategory pigCategory;
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "1";
//		pigCategory.categoryName = "长白猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "2";
//		pigCategory.categoryName = "荣昌猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "3";
//		pigCategory.categoryName = "内江猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "4";
//		pigCategory.categoryName = "香猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "5";
//		pigCategory.categoryName = "藏猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "6";
//		pigCategory.categoryName = "太湖猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "7";
//		pigCategory.categoryName = "大白猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "8";
//		pigCategory.categoryName = "金华猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "9";
//		pigCategory.categoryName = "陆川猪";
//		pigCategories.add(pigCategory);
//
//		pigCategory = new PigCategory();
//		pigCategory.categoryID = "10";
//		pigCategory.categoryName = "民猪";
//		pigCategories.add(pigCategory);
//		return pigCategories;
//	}


}
