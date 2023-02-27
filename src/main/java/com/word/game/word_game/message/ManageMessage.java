package com.word.game.word_game.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.word.game.word_game.constants.InitialLetter;
import com.word.game.word_game.json.ProcessWordJson;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

import static com.word.game.word_game.utility.CheckLogic.checkLetter;
import static com.word.game.word_game.json.ConvertToJson.convertToJson;
import static com.word.game.word_game.utility.CheckLogic.checkWordExist;

@Log4j2
public class ManageMessage {
    private static List<WebSocketSession> messageList = new ArrayList<>();
    private static Set<String> wordSet = new HashSet<>();
    private static InitialLetter initialLetter = new InitialLetter();
    private static String letter = initialLetter.getLetter();
    private static int idx = 1;
    private static ProcessWordJson json = new ProcessWordJson();
    private static ObjectMapper mapper = new ObjectMapper();

    //client에게 값이 왔을 떄
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String payload = message.getPayload();
        // json으로 받아온 값 string으로 파싱
        log.info("payload : " + payload);
        JSON jsonForClient = mapper.readValue(payload, JSON.class);
        log.info(jsonForClient.getCommand() + " " + jsonForClient.getMessage());

        //받은 메시지를 브로드캐스트로 모든 클라이언트에게 전송
        for(WebSocketSession webSocketSession : messageList){
            webSocketSession.sendMessage(new TextMessage(convertToJson(jsonForClient.getCommand(), jsonForClient.getMessage())));
            webSocketSession.sendMessage(new TextMessage(convertToJson("firstLetter", letter)));
        }
        log.info("payload : " + payload);
        log.info("letter : " + letter);

        //보낸 메시지와 랜덤으로 받아온 초성인지 판단
        if(checkLetter(jsonForClient.getMessage(), letter)) {
            //입력했던 메시지인지 확인
            if (checkWordExist(wordSet, jsonForClient.getMessage())) {
                List<String> definitions = json.processWord(jsonForClient.getMessage());
                if (definitions.size() > 0) {
                    log.info("정답");
                    wordSet.add(jsonForClient.getMessage());
                    for (WebSocketSession webSocketSession : messageList) {
                        webSocketSession.sendMessage(new TextMessage(convertToJson("answer", "correct")));
                        webSocketSession.sendMessage(new TextMessage(convertToJson("definitions", definitions)));
                    }
                }
                else{
                    log.warn("오답");
                    for (WebSocketSession webSocketSession : messageList) {
                        webSocketSession.sendMessage(new TextMessage(convertToJson("answer","wrong")));
                        webSocketSession.sendMessage(new TextMessage(convertToJson("definitions","사전에 정의되지 않은 단어입니다.") ));
                    }
                }
            }
            else{
                for (WebSocketSession webSocketSession : messageList) {
                    webSocketSession.sendMessage(new TextMessage(convertToJson("answer", "wrong")));
                    webSocketSession.sendMessage(new TextMessage(convertToJson("word", "이미 했던 단어입니다.")));
                }
            }
        }

        else{
            log.info("틀림");
            for(WebSocketSession webSocketSession : messageList) {
                webSocketSession.sendMessage(new TextMessage(convertToJson("answer","wrong")));
            }
        }
    }
    //client와 서버가 connection되자마자
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(convertToJson("ID","user"+idx)));
        idx++;
        if (messageList.size() > 2) {
            session.sendMessage(new TextMessage(convertToJson("status","error")));
            session.close();
        }
        else {
            messageList.add(session);
            Timer timer = new Timer();
            final int[] second = {5};
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        session.sendMessage(new TextMessage(convertToJson("time",String.valueOf(second[0]))));
                    } catch (IOException e) {
                        timer.cancel();
                    }
                    second[0]--;
                    if(second[0] < 0){
                        try {
                            session.sendMessage(new TextMessage(convertToJson("time","time_out")));
                        } catch (IOException e) {
                            timer.cancel();
                        }
                        letter = initialLetter.getLetter();
                        log.info(letter);
                        for(WebSocketSession webSocketSession : messageList){
                            try {
                                webSocketSession.sendMessage(new TextMessage(convertToJson("firstLetter", letter)));
                            } catch (IOException e) {
                                timer.cancel();
                            }
                        }
                        second[0] = 5;
                    }
                }
            }, 0, 1000);
        }
    }
    //client와 server가 connection이 끊겼을 때
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        log.info(session + " 접속 해제");
        messageList.remove(session);
    }

}
