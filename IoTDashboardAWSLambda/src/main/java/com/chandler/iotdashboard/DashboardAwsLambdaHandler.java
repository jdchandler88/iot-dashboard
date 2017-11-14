/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chandler.iotdashboard;

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
        //this is only one app, since it's an alexa skill with a very specific purpose
        String appId = System.getenv("supportedApplicationIds");
        appIds.add(appId);
    }
    
    public DashboardAwsLambdaHandler() {
        super(new DashboardAlexaSpeechlet(), appIds);
    }
    
}
