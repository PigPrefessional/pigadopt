package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.ai2020lab.pigadopted.model.base.ResultCode;

/**
 * 猪照片比对响应实体类-照片比对属于比较耗时的操作，请求返回结果并不代表比对的成功与否
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigPhotoAnalysisResponse extends ResponseData<PigPhotoAnalysisResponse.PigPhotoAnalysisResult> {

	public class PigPhotoAnalysisResult extends ResultCode {

	}
}
