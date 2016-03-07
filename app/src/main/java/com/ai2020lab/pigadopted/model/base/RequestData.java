package com.ai2020lab.pigadopted.model.base;

/**
 * http请求报文实体类
 * Created by Justin on 2015/11/11.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class RequestData<T> {

	public T data;

	public Description desc = new Description();

	public static class Description {

		/**
		 * 数据模式，0:普通模式(明文)。1:加密模式(base64)
		 */
		public int data_mode = DataMode.NORMAL;

		/**
		 * 报文摘要：MD5,(body字段的密文)
		 */
		public String digest = "";

		public Description() {
		}

		public Description(int data_mode, String digest) {
			this.data_mode = data_mode;
			this.digest = digest;
		}
	}


}
