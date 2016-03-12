/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aimedia;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;

import com.ai2020lab.aiutils.common.LogUtils;

import java.io.File;

/**
 * 音频录制工具类
 * <p/>
 * 使用方法:<br>
 * 1.调用boolean prepareRecorder(String audioPath, int sourceType)方法初始化录音机<br>
 * 2.调用boolean startRecorder()方法开始录音<br>
 * 3.调用boolean stopRecorder()方法停止录音<br>
 * <p/>
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class AudioRecorderManager {
	/**
	 * 日志标题
	 */
	private final static String TAG = AudioRecorderManager.class
			.getSimpleName();

	/**
	 * 音频文件保存路径
	 */
	private String audioPath;
	/**
	 * MediaRecorder对象的引用
	 */
	private MediaRecorder recorder;

	/**
	 * MediaRecorder当前状态,初始化的时候为IDLE
	 */
	private int status = MediaRecorderStatus.IDLE;

	/**
	 * 录音机采集音频源-默认为麦克风
	 */
	private int sourceType = AudioSourceType.MIC;

	private AudioRecorderManager() {
	}

	/**
	 * 工厂方法
	 *
	 * @return 返回MediaRecorderManager对象的引用
	 */
	public static AudioRecorderManager getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * 得到MediaRecorder当前状态
	 * <p/>
	 * prepare()方法调用成功返回状态PREPARE,start()方法调用成功返回状态START<br>
	 * stop()方法调用成功返回状态STOP,3个方法调用产生异常和还没有调用任何方法时候返回状态IDLE
	 *
	 * @return 返回MediaRecorder的当前状态, 返回状态码在常量类{@link MediaRecorderStatus}中定义
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 得到MediaRecorder采集音频源类型
	 * <p/>
	 * 音频源类型在常量类AudioRecorderType中定义,分为MIC和PHONE,默认为MIC
	 *
	 * @return 返回MediaRecorder采集音频源类型
	 */
	public int getSourceType() {
		return sourceType;
	}

	/**
	 * 得到MediaRecorder对象
	 *
	 * @return
	 */
	public MediaRecorder getRecorder() {
		return recorder;
	}

	/**
	 * 得到音频文件保存路径
	 *
	 * @return
	 */
	public String getAudioPath() {
		return audioPath;
	}

	/**
	 * 得到音频文件名
	 *
	 * @return
	 */
	public String getAudioName() {
		File f = new File(audioPath);
		if (f.exists()) {
			return f.getName();
		}
		return null;
	}

	/**
	 * 获取音频最大振幅
	 *
	 * @return 出现异常将返回0，可能是MediaRecorder对象为空或者Audio source还没有设置
	 */
	public int getMaxAmplitude() {
		int maxAmplitude = 0;
		try {
			recorder.getMaxAmplitude();
		} catch (Exception e) {
			LogUtils.e(TAG, "获取音频最大振幅异常", e);
		}
		return maxAmplitude;
	}

	/**
	 * 设置参数
	 *
	 * @param audioPath  音频文件保存全路径
	 * @param sourceType 音频采集源类型
	 * @return
	 */
	private boolean setParameters(String audioPath, int sourceType) {
		if (audioPath == null || audioPath.equals("")) {
			return false;
		}
		if (this.audioPath == null) {
			this.audioPath = audioPath;
		} else {
			return false;
		}
		this.sourceType = sourceType;
		LogUtils.d(TAG, "录音参数设置成功");
		return true;
	}

	/**
	 * 设置音频采集源<br>
	 * 默认为AudioSource.MIC
	 *
	 * @param recorder
	 * @param sourceType
	 */
	private void setAudioSource(MediaRecorder recorder, int sourceType) {
		switch (sourceType) {
			case AudioSourceType.MIC:
				recorder.setAudioSource(AudioSource.MIC);
				break;
			case AudioSourceType.PHONE_DOUBLELINK:
				recorder.setAudioSource(AudioSource.VOICE_CALL);
				break;
			case AudioSourceType.PHONE_UPLINK:
				recorder.setAudioSource(AudioSource.VOICE_UPLINK);
				break;
			case AudioSourceType.PHONE_DOWNLINK:
				recorder.setAudioSource(AudioSource.VOICE_DOWNLINK);
				break;
			default:
				recorder.setAudioSource(AudioSource.MIC);
				break;
		}
	}

	/**
	 * 初始化录音
	 *
	 * @return boolean
	 */
	private boolean initRecorder() {
		try {
			if (this.recorder == null) {
				recorder = new MediaRecorder();
			} else {
				recorder.reset();
			}
			// 设置录音的声音来源
			// 标准的手机不支持双向录音，可使用免提来实现
			// recorder.setAudioSource(AudioSource.MIC);
			// 录制上行和下行录音，有可能不会成功
			setAudioSource(recorder, sourceType);
			// 设置录音的输出格式,必须在设置声音编码格式之前设置
			// 指定文件格式
			recorder.setOutputFormat(OutputFormat.THREE_GPP);
			// 设置声音编码格式
			recorder.setAudioEncoder(AudioEncoder.AMR_NB);
			// 设置保存音频文件的路径
			recorder.setOutputFile(audioPath);
			LogUtils.i(TAG, "录音文件路径:" + audioPath);
			recorder.prepare();
			return true;
		} catch (Exception e) {
			LogUtils.e(TAG, "初始化录音异常", e);
		}
		return false;
	}

	/**
	 * 初始化录音
	 * <p/>
	 * 初始化录音失败自动释放资源<br>
	 * 初始化录音成功,MediaRecorder状态将迁移到PREPARE;初始化录音失败,MediaRecorder状态将迁移到IDLE
	 *
	 * @param audioPath  音频文件保存全路径
	 * @param sourceType 音频采集源类型,在常量类AudioSourceType中定义,如果传入未指定的无效值，MediaRecorder将默认指定音频源为MIC
	 * @return true-初始化成功，false-初始化失败
	 */
	public boolean prepareRecorder(String audioPath, int sourceType) {
		if (!setParameters(audioPath, sourceType)) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			LogUtils.d(TAG, "录音参数设置失败");
			return false;
		}
		if (!initRecorder()) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			return false;
		}
		status = MediaRecorderStatus.PREPARE;
		LogUtils.d(TAG, "初始化录音");
		return true;
	}

	/**
	 * 开始录音
	 * <p/>
	 * 开始录音失败自动释放资源<br>
	 * 开始录音成功,MediaRecorder状态将迁移到START;开始录音失败,MediaRecorder状态将迁移到IDLE
	 *
	 * @return true-开始录音成功，false-开始录音失败
	 */
	public boolean startRecorder() {
		try {
			if (recorder != null) {
				recorder.start();
				status = MediaRecorderStatus.START;
				LogUtils.d(TAG, "开始录音");
				return true;
			} else {
				releaseRecorder();
				status = MediaRecorderStatus.IDLE;
				LogUtils.d(TAG, "MediaRecorder对象为空，开始录音失败");
				return false;
			}
		} catch (Exception e) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			LogUtils.e(TAG, "开始录音异常", e);
			return false;
		}
	}

	/**
	 * 停止录音<br>
	 * 停止录音成功或失败都会自动释放资源<br>
	 * 停止录音成功,MediaRecorder状态将迁移到STOP;停止录音失败,MediaRecorder状态将迁移到IDLE
	 *
	 * @return true-停止录音成功，false-停止录音失败
	 */
	public boolean stopRecorder() {
		try {
			if (recorder != null) {
				recorder.stop();
				releaseRecorder();
				status = MediaRecorderStatus.STOP;
				LogUtils.d(TAG, "停止录音");
				return true;
			} else {
				releaseRecorder();
				status = MediaRecorderStatus.IDLE;
				LogUtils.d(TAG, "MediaRecorder对象为空，停止录音失败");
				return false;
			}
		} catch (Exception e) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			LogUtils.e(TAG, "停止录音异常", e);
			return false;
		}
	}

	/**
	 * 释放资源并重置参数
	 */
	private void releaseRecorder() {
		if (recorder != null) {
			recorder.reset();
			recorder.release();
			LogUtils.d(TAG, "释放MediaRecorder对象资源");
		}
		// 重置参数
		resetParameters();
	}

	/**
	 * 重置参数
	 */
	private void resetParameters() {
		this.recorder = null;
		this.audioPath = null;
		LogUtils.d(TAG, "重置录音参数");
	}

	private static class SingletonHolder {
		public static AudioRecorderManager instance = new AudioRecorderManager();
	}

}
