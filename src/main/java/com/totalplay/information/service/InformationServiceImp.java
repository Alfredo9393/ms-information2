/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.totalplay.information.service;

import com.google.gson.Gson;
import com.totalplay.information.client.FireBaseClient;
import com.totalplay.information.model.ImagesModel;
import io.kubemq.sdk.event.Event;
import java.io.IOException;
import javax.net.ssl.SSLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.grpc.stub.StreamObserver;
import io.kubemq.sdk.basic.ServerAddressNotSuppliedException;
import io.kubemq.sdk.event.Channel;
import io.kubemq.sdk.event.EventReceive;
import io.kubemq.sdk.event.Subscriber;
import io.kubemq.sdk.queue.Queue;
import io.kubemq.sdk.subscription.EventsStoreType;
import io.kubemq.sdk.subscription.SubscribeRequest;
import io.kubemq.sdk.subscription.SubscribeType;
import io.kubemq.sdk.tools.Converter;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author APerez
 */
@Service
public class InformationServiceImp implements InformationService, StreamObserver<EventReceive>{
    private static final Logger LOG = LoggerFactory.getLogger(InformationServiceImp.class);

    @Autowired
    FireBaseClient fireBaseClient;
        
    private Object objectImages;     
    private Channel channel;
    private Subscriber subscriber;
    private TaskExecutor taskExecutor;
            
    public InformationServiceImp(Channel channel,Subscriber subscriber,TaskExecutor taskExecutor) {
        this.objectImages =null;
        this.channel = channel;
        this.subscriber = subscriber;
        this.taskExecutor = taskExecutor;
    }
        
    
    @Override
    public Object storequery(String idCommerce) {

        Object obj = new Object();

        publishImages(idCommerce); 
        
        Map<String, Object> mp = new HashMap<>();
        Object objectInformation = null;
        try{
            System.out.println("-----task 2 -------");
            ImagesModel imagesModel = new ImagesModel();
            imagesModel.setIdcommerce(idCommerce);            
            System.out.println("invoke http://IP:8090/storequery  idCommerce:"+idCommerce);
            objectInformation = fireBaseClient.getInformation(imagesModel);
            mp.put("information", objectInformation);
            
        } catch(Exception ex){
            System.out.println("error invoke http://IP:8090/storequery: "+ex);
        }
        
        int max=0;
        while (true) {
            if(objectImages!=null){
                System.out.println("-----task 3 -------"+max);
                LOG.info("data:"+objectToJson(objectImages));
                mp.put("images", objectImages);
                objectImages=null;
                break;
            }
            if(objectImages==null && max>=20000000){
                System.out.println("-----task 3 -------"+max);
                mp.put("images", "No data");
                break;
            }
            max++;
        }
  
        
        obj = mp;
        LOG.info("response: "+objectToJson(obj));
        return obj;

    }

        
    
    public void publishImages(String idCommerce){
        System.out.println("-----task 1 -------");

        Event event = new Event();
        event.setEventId(getid());

        try {
            System.out.println("Set body: [chanel-images-request]");
            event.setBody(Converter.ToByteArray(idCommerce));
        } catch (IOException e) {
            System.out.println("Error set body: [chanel-images-request]");
            System.out.println(e);
        }

        try {
            System.out.println("publish Message in [chanel-images-request]");
            channel.SendEvent(event); 
        } catch (SSLException | ServerAddressNotSuppliedException e) {
            System.out.println("Error set body: [chanel-images-request]");
            System.out.println(e);
        }

    }
    
/*RESPONSE IMAGEN*/
    @PostConstruct
    public void init() {           
        SubscribeRequest subscribeRequest = new SubscribeRequest();
        subscribeRequest.setChannel("chanel-images-response");
        subscribeRequest.setClientID("client-images-response");
        subscribeRequest.setSubscribeType(SubscribeType.EventsStore);
        subscribeRequest.setEventsStoreType(EventsStoreType.StartNewOnly);
        try {
            LOG.info("subscriber: chanel-images-response "+objectToJson(subscribeRequest));
            subscriber.SubscribeToEvents(subscribeRequest, this);
        } catch (ServerAddressNotSuppliedException | SSLException e) {
            LOG.info("Error subscriber: [chanel-images-response] ");
            System.out.println(e);
        }
    }
    
    @Override
    public void onNext(EventReceive eventReceive) {
        LOG.info("  *********** Listener Event: [chanel-images-response] ***********    ");
        try {
            LOG.info("Body: %s {} ", Converter.FromByteArray(eventReceive.getBody()));
            objectImages = Converter.FromByteArray(eventReceive.getBody());
            LOG.info(" Event Receive success");
        } catch (IOException | ClassNotFoundException e) {
            LOG.error("Error EventReceive [chanel-images-response]", e);
        }
    }
    
    
    @Override
    public void onError(Throwable thrwbl) {
    }

    @Override
    public void onCompleted() {
        
    }
    
    /*RESPONSE EXTRAS*/
    private static String objectToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
      
    private static String getid() {
        int min = 10;  
        int max = 900;    
        Integer res1 = (int) (Math.random()*(max-min+1)+min);   
        String res = res1.toString();
        return res;
    }




        
}
