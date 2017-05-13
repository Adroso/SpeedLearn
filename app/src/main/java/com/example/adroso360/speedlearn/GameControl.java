package com.example.adroso360.speedlearn;


/**
 * Created by Adroso360 on 13/5/17.
 */

public class GameControl {
    public static String getEquation (){
        /** This function  will return a random equation
         * for the question bank for the GUI to display
         * to the user.**/

        int firstNumber;
        int secondNumber;
        char[] operators = {'*', '/', '+'};
        char equationOperator;


        firstNumber = (int) (Math.random() * 300);
        secondNumber = (int) (Math.random() * 300);
        equationOperator = operators[(int) (Math.random() * 2)];

        return "placeholder";

    }

    public static String getCurrentAnswer (){
        /** This function  will take the current equation
         * and calculate the answer for it
         * so the game can check the players input.**/

        return "placeholder";

    }
}
