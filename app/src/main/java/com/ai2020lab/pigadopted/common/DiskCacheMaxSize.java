package com.ai2020lab.pigadopted.common;

/**
 * 磁盘缓存最大容量常量类
 * Created by Justin on 2016/1/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public interface DiskCacheMaxSize {
	/**
	 * 上传文本缓存的最大容量
	 */
	long ANALYSIS_SEND_DATA = (long) (0.1 * 1024 * 1024);

	long ANALYSIS_LIST_DATA = (long) (0.1 * 1024 * 1024);
}
