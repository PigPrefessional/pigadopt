package com.ai2020lab.pigadopted.common;

/**
 * 磁盘缓存最大容量常量类
 * Created by Justin on 2016/1/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public interface DiskCacheMaxSize {

	long PIG_BODY_PARTS_LIST = (long) (0.1 * 1024 * 1024);

	long PIG_CATEGORIES_LIST = (long) (0.1 * 1024 * 1024);

	long SELLER_INFO = (long) (0.01 * 1024 * 1024);
	long BUYER_INFO = (long) (0.01 * 1024 * 1024);
}
