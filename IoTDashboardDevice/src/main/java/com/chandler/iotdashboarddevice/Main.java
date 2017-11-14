/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chandler.iotdashboarddevice;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.chandler.iotdashboarddevice.SampleUtil.KeyStorePasswordPair;

/**
 *
 * @author D
 */
public class Main {
    
    public static void main(String[] args) throws AWSIotException {
        
        String clientEndpoint = "a1dj2b1mygoq87.iot.us-east-1.amazonaws.com";
        String clientId = "fakeClientId";
        String certificateFile = "X:\\Users\\D\\Desktop\\iotkeys\\af17a21073-certificate.pem.crt";
        String privateKeyFile = "X:\\Users\\D\\Desktop\\iotkeys\\af17a21073-private.pem.key";
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        AWSIotMqttClient awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        
        DashboardDevice dev = new DashboardDevice();
        awsIotClient.attach(dev);
        awsIotClient.connect();
        
    }
    
}
