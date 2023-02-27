package com.word.game.word_game.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.word.game.word_game.json.ProcessWordJson;
import com.word.game.word_game.message.JSON;
import com.word.game.word_game.message.ManageMessage;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static com.word.game.word_game.json.ConvertToJson.convertToJson;

@Component
public class ChatHandler extends TextWebSocketHandler {
    private ManageMessage manageMessage;
    private static int idx = 1;

    private static ObjectMapper mapper = new ObjectMapper();

    public ChatHandler(){
         manageMessage = new ManageMessage();
    }
    private boolean status = false;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String payload = message.getPayload();
        JSON jsonForClient = mapper.readValue(payload, JSON.class);
        System.out.println(jsonForClient.getCommand() + " " + jsonForClient.getMessage());
        if(jsonForClient.getCommand().equals("game_start") && jsonForClient.getMessage().equals("on")){
            status = true;
        }
        if(status) {
            manageMessage.handleTextMessage(session, message, true);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        session.sendMessage(new TextMessage(convertToJson("ID","user"+idx)));
        idx++;
        manageMessage.afterConnectionEstablished(session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        manageMessage.afterConnectionClosed(session, status);
    }


}
