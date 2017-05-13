package com.example.adroso360.speedlearn;

import org.junit.Test;


/**
 * Created by Adroso360 on 13/5/17.
 */

public class EquationBuilderUnitTest {

    @Test
    public void EquationBuilderUnitTest() {

        for(int i = 0; i < 30; i++) {
            String[] testIfString = GameControl.getEquation();
            System.out.println(testIfString[0]+ " = "+  testIfString[1]);
        }


    }
}
