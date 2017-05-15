package com.example.adroso360.speedlearn;

/**
 * Created by Adroso360 on 15/5/17.
 */

public class Background {
    static void run(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
    }
}

