package com.example.adroso360.speedlearn;


/**
 * Created by Adroso360 on 13/5/17.
 */

public class GameControl {
    private static String builtEquation;
    public static String[] getEquation (){
        /** This function  will return a random equation with answer
         * from the question bank for the GUI to display
         * to the user. and for the game to know the answer to what is being displayed**/

        String [][] questionBank = {{"5y + 1 = 31", "6"}, {"-4y + 7 = -37", "11"}, {"-y - 3 = -4", "1"}, {"8y - 12 = 60", "9"}, {"(7y / 5 - 1 = 83", "60"},{"-6y + 9 = -45", "9"},{"-3y - 12 = -30", "6"}, {"-3y + 1 = -20", "7"}, {"-3y - 9 = -21", "4"}, {"4y - 10 = 38", "12"}, {"7y + 9 = 86", "11"}, {"-6y + 8 = -46", "9"}, {"2y - 2 = 2", "2"}, {"(y/7) - 6 = -1", "35"}, {"48/6 = y", "8"}, {"12 x 3 = y", "36"}, {"8 x 3 x 2", "48"}};


        int randomChose;
        randomChose = (int) (Math.random() * questionBank.length);

        return questionBank[randomChose];

    }
}
