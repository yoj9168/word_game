package com.word.game.word_game.controller;

import com.word.game.word_game.json.ProcessWordJson;
import com.word.game.word_game.message.ManageMessage;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {
    @GetMapping("/chat")
    public String index(){
        return "index";
    }

}
