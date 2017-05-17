package com.example.adroso360.speedlearn;

import android.content.Context;
import android.media.SoundPool;

/**
 * Created by Adroso360 on 17/5/17.
 *
 */

public class SoundHelper {
    private SoundPool pool;
    private Context context;

    public SoundHelper(Context context){
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(6);
        pool = builder.build();


    }
    public int addSound(int resID){
        return pool.load(context, resID,1);
    }

    public void play(int soundID, int loop){
        pool.play(soundID, 1,1,1,loop,1);


    }
}
