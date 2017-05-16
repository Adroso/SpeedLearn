package com.example.adroso360.speedlearn;

/**
 * Created by Adroso360 ;P on 15/5/17.
 * Thanks to J Holdsworth for Idea
 */

public class Background {
    static void run(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
    }
}

