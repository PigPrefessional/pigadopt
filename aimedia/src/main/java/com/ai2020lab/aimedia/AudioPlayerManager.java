/*
 * Copyright 2016 Justin Z
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ai2020lab.aimedia;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.ai2020lab.aiutils.common.LogUtils;

import java.io.IOException;


/**
 * 音频播放工具类
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class AudioPlayerManager {
	/**
	 * 只播放一次
	 */
	public final static int LOOP_TIMES_ONLY_ONE = 0;
	/**
	 * 无限循环
	 */
	public final static int LOOP_TIMES_INFINITE = -1;
	/**
	 * 日志标题
	 */
	private final static String TAG = AudioPlayerManager.class
			.getSimpleName();
	/**
	 * 音频文件保存路径
	 */
	private String audioPath;
	/**
	 * MediaPlayer对象
	 */
	private MediaPlayer mediaPlayer;
	/**
	 * 循环播放次数，0为1次，-1为无限循环，大于0的整数为循环次数，默认为0
	 */
	private int loopTimes = LOOP_TIMES_ONLY_ONE;

	/**
	 * 循环播放的时候用于记录当前循环播放的次数
	 */
	private int currentPlayTimes = 0;

	/**
	 * MediaPlayer当前状态,初始化的时候为IDLE
	 */
	private int status = MediaPlayerStatus.IDLE;

	/**
	 * 循环播放全部完成事件监听
	 */
	private OnPlayFinishedListener onPlayFinishedListener;

	private AudioPlayerManager() {
	}

	/**
	 * 工厂方法
	 *
	 * @return 返回AudioPlayerManager对象的引用
	 */
	public static AudioPlayerManager getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * 绑定循环播放全部完成事件监听器<br>
	 * 该监听只在有限次循环播放时候有效
	 *
	 * @param onPlayFinishedListener OnPlayFinishedListener
	 */
	public void setOnPlayFinishedListener(OnPlayFinishedListener onPlayFinishedListener) {
		this.onPlayFinishedListener = onPlayFinishedListener;
	}

	/**
	 * 得到MediaPlayer对象
	 *
	 * @return
	 */
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	/**
	 * 得到MediaPlayer指定的循环播放次数
	 *
	 * @return
	 */
	public int getLoopTimes() {
		return loopTimes;
	}

	/**
	 * 得到循环播放的时候当前已经播放的次数
	 *
	 * @return
	 */
	public int getCurrentLoopTimes() {
		return currentPlayTimes;
	}

	/**
	 * 得到MediaPlayer当前状态
	 * <p/>
	 * prepare()方法调用成功返回状态PREPARE，start()方法调用成功返回状态START，<br>
	 * pause()方法调用成功返回状态PAUSE，stop()方法调用成功返回状态STOP，<br>
	 * 这4个方法调用产生异常和还没有调用任何方法时候返回状态IDLE
	 *
	 * @return 返回MediaPlayer的当前状态, 返回状态码在常量类{@link MediaPlayerStatus}中定义
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 得到音频文件播放时长，单位为毫秒
	 *
	 * @return 返回音频文件的播放时间长度，单位为毫秒，如果MediaPlayer没有初始化则返回-1
	 */
	public int getDuration() {
		if (mediaPlayer != null && status == MediaPlayerStatus.PREPARE) {
			return mediaPlayer.getDuration();
		} else {
			return -1;
		}
	}

	/**
	 * 设置参数
	 *
	 * @param audioPath 音频文件保存全路径
	 * @return Boolean
	 */
	private boolean setParameters(String audioPath) {
		if (audioPath == null || audioPath.equals("")) {
			LogUtils.d(TAG, "音频文件路径不能为空!");
			return false;
		}
		if (this.audioPath == null) {
			this.audioPath = audioPath;
		} else {
			return false;
		}
		LogUtils.d(TAG, "录音参数设置成功");
		return true;
	}

	/**
	 * 设置MediaPlayer的DataSource
	 * <p/>
	 *
	 * @param audioPath   音频文件路径
	 * @param mediaPlayer MediaPlayer
	 * @return boolean
	 */
	private boolean setPlayerDataSource(String audioPath, MediaPlayer mediaPlayer) {
		try {
			mediaPlayer.setDataSource(audioPath);
			return true;
		} catch (IOException e) {
			LogUtils.e(TAG, "设置MediaPlayer DataSource异常", e);
			return false;
		}
	}

	/**
	 * 初始化播放
	 *
	 * @return boolean
	 */
	private boolean initRecorder() {
		if (this.mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		} else {
			mediaPlayer.reset();
		}
		if (!setPlayerDataSource(audioPath, mediaPlayer)) {
			return false;
		}
		try {
			mediaPlayer.prepare();
			return true;
		} catch (Exception e) {
			LogUtils.e(TAG, "初始化音频播放异常", e);
			return false;
		}
	}

	/**
	 * 初始化音频播放
	 * <p/>
	 * 初始化音频播放失败自动释放资源<br>
	 * 初始化音频播放成功,MediaPlayer状态将迁移到PREPARE;初始化录音失败,MediaPlayer状态将迁移到IDLE
	 *
	 * @param audioPath 音频文件保存全路径
	 * @return true-初始化成功，false-初始化失败
	 */
	public boolean preparePlayer(String audioPath) {
		if (!setParameters(audioPath)) {
			releasePlayer();
			LogUtils.d(TAG, "音频播放参数设置失败");
			return false;
		}
		if (!initRecorder()) {
			releasePlayer();
			LogUtils.d(TAG, "初始化音频播放失败");
			return false;
		}
		status = MediaPlayerStatus.PREPARE;
		LogUtils.d(TAG, "初始化音频播放成功");
		return true;
	}

	/**
	 * 开始音频播放
	 *
	 * @param playTimes 指定的循环播放次数。-1代表无限循环播放，0代表只播放一次，loopTimes>=0代表的循环播放的次数
	 * @return true-开始音频播放成功，false-开始音频播放失败
	 */
	public boolean startPlayer(int playTimes) {
		LogUtils.d(TAG, "当前状态为：" + status);
		this.loopTimes = playTimes;
		// 播放次数必须为大于等于-1的整数
		if (loopTimes < -1) {
			LogUtils.d(TAG, "指定循环播放次数必须大于等于-1，开始音频播放失败");
			return false;
		}
		try {
			if (status != MediaPlayerStatus.IDLE) {
				// 无限循环
				if (loopTimes == LOOP_TIMES_INFINITE) {
					if (!mediaPlayer.isLooping()) {
						mediaPlayer.setLooping(true);
					}
				}
				// 只播放一次
				else if (loopTimes == LOOP_TIMES_ONLY_ONE) {
					mediaPlayer.setLooping(false);
				}
				// 有限播放n次
				else {
					mediaPlayer.setLooping(false);
					mediaPlayer.setOnCompletionListener(new
							CompletionListener());
				}
				mediaPlayer.start();
				status = MediaPlayerStatus.START;
				LogUtils.d(TAG, "开始音频播放");
				return true;
			} else {
				LogUtils.d(TAG, "MediaPlayer状态为IDLE或START，开始音频播放失败");
				return false;
			}
		} catch (Exception e) {
			releasePlayer();
			LogUtils.e(TAG, "开始音频播放异常", e);
			return false;
		}
	}

	/**
	 * 从暂停中恢复播放
	 *
	 * @return true-开始音频播放成功，false-开始音频播放失败
	 */
	public boolean startPlayer() {
		try {
			if (status != MediaPlayerStatus.IDLE && status != MediaPlayerStatus.START) {
				mediaPlayer.start();
				status = MediaPlayerStatus.START;
				LogUtils.d(TAG, "开始音频播放");
				return true;
			} else {
				LogUtils.d(TAG, "MediaPlayer状态为IDLE或START，开始音频播放失败");
				return false;
			}
		} catch (Exception e) {
			releasePlayer();
			LogUtils.e(TAG, "开始音频播放异常", e);
			return false;
		}
	}

	/**
	 * 暂停音频播放，只在播放中有效
	 *
	 * @return-暂停音频播放成功，false-暂停音频播放失败
	 */
	public boolean pausePlayer() {
		try {
			if (status == MediaPlayerStatus.START) {
				mediaPlayer.pause();
				status = MediaPlayerStatus.PAUSE;
				LogUtils.d(TAG, "暂停音频播放");
				return true;
			} else {
				LogUtils.d(TAG, "MediaPlayer状态不是START，暂停音频播放失败");
				return false;
			}
		} catch (Exception e) {
			releasePlayer();
			LogUtils.e(TAG, "暂停音频播放异常", e);
			return false;
		}
	}

	/**
	 * 停止音频播放，在播放中和暂停中有效
	 *
	 * @return-停止音频播放成功，false-停止音频播放失败
	 */
	public boolean stopPlayer() {
		try {
			if (status == MediaPlayerStatus.START || status == MediaPlayerStatus.PAUSE) {
				mediaPlayer.stop();
				status = MediaPlayerStatus.STOP;
				LogUtils.d(TAG, "停止音频播放");
				return true;
			} else {
				LogUtils.d(TAG, "MediaPlayer状态不是START或PAUSE，停止音频播放失败");
				return false;
			}
		} catch (Exception e) {
			releasePlayer();
			LogUtils.e(TAG, "停止音频播放异常", e);
			return false;
		}
	}

	/**
	 * 从指定位置开始播放
	 *
	 * @param milliseconds 指定开始播放的位置，单位为毫秒
	 */
	public boolean seekTo(int milliseconds) {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.seekTo(milliseconds);
				return true;
			} else {
				releasePlayer();
				LogUtils.d(TAG, "MediaPlayer对象为空，指定音频开始播放位置失败");
				return false;
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "指定音频开始播放位置异常", e);
			return false;
		}
	}

	/**
	 * 释放播放器
	 */
	public void releasePlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
		}
		// 重置参数
		resetParameters();
	}

	/**
	 * 重置参数
	 */
	private void resetParameters() {
		this.mediaPlayer = null;
		this.audioPath = null;
		this.status = MediaPlayerStatus.IDLE;
		this.loopTimes = LOOP_TIMES_ONLY_ONE;
		this.currentPlayTimes = 0;
		LogUtils.d(TAG, "重置录音参数");
	}

	/**
	 * 循环播放全部结束监听
	 *
	 * @author Justin Z
	 * @ClassName: OnPlayFinishedListener
	 * @date 2014-8-1 下午3:25:33
	 */
	public interface OnPlayFinishedListener {

		void playFinished(int loopTimes);
	}

	private static class SingletonHolder {
		public static AudioPlayerManager instance = new AudioPlayerManager();
	}

	/**
	 * 播放完成监听
	 *
	 * @author Justin Z
	 * @ClassName: CompletionListener
	 * @date 2014-8-1 下午1:02:21
	 */
	private class CompletionListener implements OnCompletionListener {

		// private int playTimes;
		//
		// public CompletionListener(int playTimes) {
		// this.playTimes = playTimes;
		// }

		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			LogUtils.d(TAG, "loopTimes：" + loopTimes);
			// LogUtils.d(TAG, "playTimes：" + playTimes);
			if (loopTimes > 0) {
				currentPlayTimes++;
				LogUtils.d(TAG, "循环播放第" + currentPlayTimes + "次完成");
				if (currentPlayTimes < loopTimes) {
					seekTo(0);
					startPlayer(loopTimes);
				} else {
					// // 结束播放
					// stopPlayer();
					// releasePlayer();
					int times = currentPlayTimes;
					currentPlayTimes = 0;
					if (onPlayFinishedListener != null) {
						onPlayFinishedListener.playFinished(times);
					}
				}
			}

		}

	}

}
