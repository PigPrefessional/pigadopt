package com.ai2020lab.pigadopted.model.hogpen;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪圈照片上传响应实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HogpenPhotoUploadResponse extends ResponseData<HogpenPhotoUploadResponse.hogpenPhotoUploadResult> {

	public class hogpenPhotoUploadResult {

		@Expose
		@SerializedName("hogpen_photo")
		public String hogpenPhoto;
	}
}
