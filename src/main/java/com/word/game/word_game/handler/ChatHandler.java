package com.word.game.word_game.handler;

import com.word.game.word_game.constants.InitialLetter;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.List;

import static com.word.game.word_game.utility.CheckLogic.checkLetter;

@Component
@Log4j2
public class ChatHandler extends TextWebSocketHandler {
    private static List<WebSocketSession> list = new ArrayList<>();
    private static InitialLetter initialLetter = new InitialLetter();
    private static String letter = initialLetter.getLetter();
    private static TextMessage correct = new TextMessage("correct");
    private static TextMessage wrong = new TextMessage("wrong");
    private static TextMessage letterMessage = new TextMessage(letter);

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String payload = message.getPayload();
        payload = payload.substring(payload.lastIndexOf(':') + 1);
        log.info("payload : " + payload);
        log.info("letter : " + letter);

        if(checkLetter(payload, letter)){
            log.info("정답");
            for(WebSocketSession webSocketSession : list) {
                webSocketSession.sendMessage(correct);
            }
        }
        else{
            log.info("틀림");
            for(WebSocketSession webSocketSession : list) {
                webSocketSession.sendMessage(wrong);
            }
        }

        for(WebSocketSession webSocketSession : list){
            webSocketSession.sendMessage(message);
            webSocketSession.sendMessage(letterMessage);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        list.add(session);
        log.info("클라이언트 "+session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        log.info(session + " 접속 해제");
        list.remove(session);
    }

}
