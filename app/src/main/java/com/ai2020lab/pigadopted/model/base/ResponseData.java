package com.ai2020lab.pigadopted.model.base;

import java.io.Serializable;

/**
 * Http响应报文实体类
 * Created by Justin on 2015/11/11.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ResponseData<T> implements Serializable {

	public T data;

	public Description desc = new Description();

	public static class Description {
		/**
		 * 服务器返回状态吗
		 */
		public String result_code;

		/**
		 * 服务器返回状态信息
		 */
		public String result_msg = "";

		/**
		 * 数据模式，0:普通模式(明文)。1:加密模式(base64)
		 */
		public String data_mode = DataMode.NORMAL;

		/**
		 * 报文摘要：MD5,(body字段的密文)
		 */
		public String digest = "";
	}
}
