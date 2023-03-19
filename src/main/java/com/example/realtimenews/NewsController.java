package com.example.realtimenews;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class NewsController {



    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // method for client subscription

    @CrossOrigin
    @RequestMapping(value = "/subscribe" , consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe()  {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e){
            e.printStackTrace();
        }
        sseEmitter.onCompletion(() ->emitters.remove(sseEmitter));
        emitters.add(sseEmitter);
        return sseEmitter;
    }


    // method for dispatching events to all clients
    @PostMapping(value = "/dispatchEvent")
    public void dispatchEventToClient (@RequestParam String title, @RequestParam String text){

        String eventFormatted = new JSONObject()
                .put("title", title)
                .put("text" , text).toString();

        for ( SseEmitter emitter : emitters){
            try {
                emitter.send(SseEmitter.event().name("latestNews").data(eventFormatted));
            } catch (IOException e){
                emitters.remove(emitter);
            }
        }
    }



}
