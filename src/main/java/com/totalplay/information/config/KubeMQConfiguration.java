/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.totalplay.information.config;
import io.kubemq.sdk.event.Channel;
import io.kubemq.sdk.event.Subscriber;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author APerez
 */
@Configuration
@ConfigurationProperties("kubemq")
public class KubeMQConfiguration {
    private String address;

    @Bean
    public Subscriber subscriber() {
        return new Subscriber(address);
    }

    @Bean
    public Channel channel() {
        return new Channel("chanel-images-request", "client-images-request", true, address);
    }

    String getAddress() {
        return address;
    }

    void setAddress(String address) {
        this.address = address;
    }
}