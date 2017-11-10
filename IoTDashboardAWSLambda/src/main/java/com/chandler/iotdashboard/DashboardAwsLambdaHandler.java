/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chandler.iotdashboard;

import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author darin
 */
public class DashboardAwsLambdaHandler extends SpeechletRequestStreamHandler {
    
    private static final Set<String> appIds = new HashSet<>();
    
    static {
        //get app ids from dynamodb table (get connection information from env vars)
        
    }
    
    public DashboardAwsLambdaHandler() {
        super(new DashboardAlexaSpeechlet(), appIds);
    }
    
}
