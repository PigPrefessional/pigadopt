package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪猪照片上传响应数据实体类
 * Created by Justin on 2015/12/3.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PhotoUploadResponse extends ResponseData<PhotoUploadResponse.PigPhoto> {

	public class PigPhoto {

		@Expose
		@SerializedName("pig_photo")
		public String pigPhotoUrl;
	}
}
