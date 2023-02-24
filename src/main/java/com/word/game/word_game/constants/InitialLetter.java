package com.word.game.word_game.constants;

public class InitialLetter {
    private String[] initial_letter = {"ㅇㅅ","ㄷㅅ","ㅁㅅ","ㄱㅅ","ㄱㄷ","ㅈㅅ","ㅂㅁ","ㅋㄴ","ㅁㅂ"};

    public String getLetter(){
        double random = Math.random();
        int num = (int)Math.round(random * (initial_letter.length - 1));
        return initial_letter[num];
    }
}
