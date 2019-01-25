package com.newland.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.SparseIntArray;

import com.payment.appconst.params.ParamsConst;
import com.newland.payment.R;
import com.nlutils.util.LoggerUtils;
import com.nlutils.util.StringUtils;
import com.payment.appconst.sound.SoundType;
import com.utils.ParamsUtils;
import com.utils.thread.ThreadPool;

/**
 * android  音效
 *
 * @author chenkh
 * @date 2015/7/29
 */
public class SoundUtils {

    private static SoundUtils INSTANCE;
    private static boolean sTtsExist = true;
    private SoundPool mSoundPool;

    private SparseIntArray mSoundId;

    private SoundUtils() {
    }

    public static SoundUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SoundUtils();
        }
        return INSTANCE;
    }

    public void load(Context context) {
        if (mSoundPool == null) {
            context = context.getApplicationContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                SoundPool.Builder builder = new SoundPool.Builder();
                //音频数量
                builder.setMaxStreams(5);

                AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
                //设置音频流属性
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_SYSTEM);
                builder.setAudioAttributes(attrBuilder.build());

                mSoundPool = builder.build();
            } else {
                mSoundPool = new SoundPool(6, AudioManager.STREAM_SYSTEM, 5);
            }

            mSoundId = new SparseIntArray();
            mSoundId.put(SoundType.BEEP, mSoundPool.load(context, R.raw.shortbeep, 1));
            mSoundId.put(SoundType.FAIL, mSoundPool.load(context, R.raw.transfail, 1));
            mSoundId.put(SoundType.BRING_CARD, mSoundPool.load(context, R.raw.bringcard, 1));
            mSoundId.put(SoundType.SUCC, mSoundPool.load(context, R.raw.transsucc, 1));
            mSoundId.put(SoundType.INPUT_PIN, mSoundPool.load(context, R.raw.enterpin, 1));
            mSoundId.put(SoundType.INPUT_AMOUNT, mSoundPool.load(context, R.raw.enteramount, 1));
            mSoundId.put(SoundType.SWIPE_CARD, mSoundPool.load(context, R.raw.swipecard, 1));
            mSoundId.put(SoundType.CLICK_PSW_KEYBOARD, mSoundPool.load(context, R.raw.click_keyboard, 1));
        }
    }

    public void play(int soundType) {

        switch (soundType) {
            case SoundType.BEEP:
            case SoundType.CLICK_PSW_KEYBOARD:
                mSoundPool.play(mSoundId.get(soundType), 1, 1, 0, 0, 1);
                break;

            case SoundType.BRING_CARD:
            case SoundType.SUCC:
            case SoundType.INPUT_PIN:
            case SoundType.INPUT_AMOUNT:
            case SoundType.SWIPE_CARD:
            case SoundType.FAIL:
                if (ParamsUtils.getBoolean(ParamsConst.PARAMS_KEY_IS_SOUND_TIP)) {
                    mSoundPool.play(mSoundId.get(soundType), 1, 1, 0, 0, 1);
                }
                break;
            default:
                LoggerUtils.e("no the audio file ,soundType =" + soundType);
                break;
        }
    }

    /**
     * 语音播放
     *
     * @param content 内容
     */
    public void play(final Context context, final String content) {
        if (sTtsExist && !StringUtils.isEmpty(content) && context != null) {
            ThreadPool.newThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent("android.intent.action.NEWLAND.TTS");
                    intent.putExtra("content", content);
                    ComponentName componentName = context.startService(intent);
                    sTtsExist = componentName != null;
                }
            }).start();
        }
    }

    /**
     * 是否存在TTS应用
     */
    public boolean isTtsExist() {
        return sTtsExist;
    }
}
