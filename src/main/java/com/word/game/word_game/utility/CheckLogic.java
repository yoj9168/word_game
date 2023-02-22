package com.word.game.word_game.utility;

public class CheckLogic {
    public static boolean checkLetter(String message, String mission){
        String[] chs = {
                "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ",
                "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ",
                "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ",
                "ㅋ", "ㅌ", "ㅍ", "ㅎ"
        };

        for(int i = 0; i < 2; i++){
            char chName = message.charAt(i);
            String missionLetter = String.valueOf(mission.charAt(i));
            if(chName >= 0xAC00){
                int uniVal = chName - 0xAC00;
                int cho = ((uniVal - (uniVal % 28))/28)/21;
                if(!chs[cho].equals(missionLetter))
                    return false;
            }
            else{
                return false;
            }
        }
        return true;
    }
}
