package com.example.realtimenews;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;
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
    public void dispatchEventToClient (@RequestParam String freshNews){

        for ( SseEmitter emitter : emitters){
            try {
                emitter.send(SseEmitter.event().name("latestNews").data(freshNews));
            } catch (IOException e){
                emitters.remove(emitter);
            }
        }
    }



}
