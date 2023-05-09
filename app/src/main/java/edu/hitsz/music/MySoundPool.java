package edu.hitsz.music;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;

import edu.hitsz.R;

public class MySoundPool {

    public static SoundPool mysp;
    public static HashMap<String, Integer> soundPoolMap;
    public static boolean haveAudio = false;

    public static int playMusic(String musicName, boolean loop) {
        if (haveAudio) {
            int id = -1;
            if (soundPoolMap != null) {
                int l = 0;
                if (loop) {
                    l = -1;
                }
                id = mysp.play(soundPoolMap.get(musicName), 1, 1, 0, l, 1);
            }
            return id;
        }
        return -1;
    }

    public static void playMusic(String musicName) {
        if (haveAudio) {
            if (soundPoolMap != null) {
                mysp.play(soundPoolMap.get(musicName), 1, 1, 0, 0, 1);
            }
        }
    }

    public static void initialSoundPoolMap(Context context) {
        //todo 创建SoundPool对象
        createSoundPool();
        soundPoolMap = new HashMap<String, Integer>();
        soundPoolMap.put("bgm", mysp.load(context, R.raw.bgm, 1));
        soundPoolMap.put("bullet_hit", mysp.load(context, R.raw.bullet_hit, 1));
        soundPoolMap.put("get_supply", mysp.load(context, R.raw.get_supply, 1));
        soundPoolMap.put("bomb_explosion", mysp.load(context, R.raw.bomb_explosion, 1));
        soundPoolMap.put("game_over", mysp.load(context, R.raw.game_over, 1));
        soundPoolMap.put("bgm_boss", mysp.load(context, R.raw.bgm_boss, 1));

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void createSoundPool() {
        if (mysp == null) {
            // Android 5.0 及 之后版本
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes;
                audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mysp = new SoundPool.Builder()
                        .setMaxStreams(10)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else { //Android 5.0 以前版本
                mysp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);  // 创建SoundPool
            }
        }
    }
}
