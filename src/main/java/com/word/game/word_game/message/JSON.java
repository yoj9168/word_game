package com.word.game.word_game.message;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class JSON {
    private String command;
    private String message;
    private List<String> list;

    public JSON(String command, String message){
        this.command = command;
        this.message = message;
    }
    public JSON(String command, List<String> list) {
        this.command = command;
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    public String getCommand() {
        return command;
    }

    public String getMessage() {
        return message;
    }
}
