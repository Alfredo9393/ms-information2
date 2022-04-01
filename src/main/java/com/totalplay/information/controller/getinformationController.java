/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.totalplay.information.controller;

import com.google.gson.Gson;
import com.totalplay.information.model.ImagesModel;
import com.totalplay.information.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author APerez
 */

@RestController
public class getinformationController {
    
    @Autowired
    private InformationService informationService;
    
    @RequestMapping(value="/getinformation",method= RequestMethod.POST)
    public Object getinformation(@RequestBody ImagesModel body){
        System.out.println("+++++++++++/getinformation++++++++++");
        Object obj=null;
        obj = informationService.storequery(body.getIdcommerce());
        return obj;
    }
    
    private static String objectToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
    
}
