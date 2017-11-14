/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chandler.iotdashboard;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.slu.entityresolution.Resolution;
import com.amazon.speech.slu.entityresolution.ValueWrapper;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.chandler.iotdashboard.util.AskResponses;
import com.chandler.iotdashboard.util.EnabledSlot;
import com.chandler.iotdashboard.util.Intents;
import com.chandler.iotdashboard.util.ResponseFactory;
import static com.chandler.iotdashboard.util.ResponseFactory.*;
import com.chandler.iotdashboard.util.ShadowStateKeys;
import com.chandler.iotdashboard.util.TellResponses;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darin
 */
public class DashboardAlexaSpeechlet implements SpeechletV2 {

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> sre) {
        log("session started...");
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> sre) {
        log("app launched...");
        return getTellResponse(TellResponses.LAUNCH_RESPONSE);
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> sre) {
        log("on intent...");
        
        Intent intent = sre.getRequest().getIntent();
        
        if (intent != null) {
            switch (intent.getName()) {
                case Intents.DISPLAY_POWER: {
                    return handleDisplayPower(sre);
                }
                case Intents.WAKE_DISPLAY: {
                    return handleWakeDisplay();
                }
                case Intents.NEW_PICTURES: {
                    return handleNewPictures();
                }
                default: {
                    return getAskResponse(AskResponses.NULL_INTENT);
                }
            }
        } else {
            return getAskResponse(AskResponses.NULL_INTENT);
        }
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> sre) {
        log("session ended...");
    }
    
    private SpeechletResponse handleDisplayPower(SpeechletRequestEnvelope<IntentRequest> sre) {
        Intent intent = sre.getRequest().getIntent();
        Slot enabled = intent.getSlot(EnabledSlot.NAME);
        SpeechletResponse response;
        //should only be one resolution and one value (one slot type and one value for that slot)
        if (enabled.getResolutions().getResolutionsPerAuthority().size()==1) {
            Resolution resolution = enabled.getResolutions().getResolutionAtIndex(0);
            if (resolution.getValueWrappers().size()==1) {
                ValueWrapper vw = resolution.getValueWrapperAtIndex(0);
                String reportBackStatus = "on";
                Boolean displayPower = true;    //let's default to "on" for now
                switch (vw.getValue().getId()) {
                    case EnabledSlot.ON_ID: {
                        displayPower = true;
                        reportBackStatus = "on";
                        break;
                    }
                    case EnabledSlot.OFF_ID: {
                        displayPower = false;
                        reportBackStatus = "off";
                        break;
                    }
                    default: {
                        //shouldn't ever get here--i dunno what to do here: log error? create a response?
                        break;
                    }
                }
                //set the display
                String successResponse = "Display is now " + reportBackStatus;
                String result = updateThingShadow(ShadowStateKeys.DISPLAY_POWER, displayPower.toString(), successResponse, "Failed to set display power.");
                response = getTellResponse(result);
            } else {
                response = getTellResponse("There's more than one value wrapper. What?");
            }
        } else {
            //i dunno what to do here
            response = getTellResponse("There's more than one resolution. What?");
        }
        return response;
    }
    
    private SpeechletResponse handleWakeDisplay() {
        String result = updateThingShadow(ShadowStateKeys.DISPLAY_WAKE_TOGGLE, Boolean.TRUE.toString(), "Woke the display.", "Failed to wake the display");
        return ResponseFactory.getTellResponse(result);
    }
    
    private SpeechletResponse handleNewPictures() {
        String result = updateThingShadow(ShadowStateKeys.REFRESH_PICTURES_TOGGLE, Boolean.TRUE.toString(), "Pictures refreshed.", "Failed to refresh pictures.");
        return ResponseFactory.getTellResponse(result);
    }
    
    private String updateThingShadow(String desiredKey, String desiredValue, String successMessage, String failureMessage) {
        try {
            AWSIotData data = AWSIotDataClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
            UpdateThingShadowRequest req = new UpdateThingShadowRequest();
            String thingName = System.getenv("thingName");
            req.setThingName(thingName);
            String payload = "{\"state\":{\"desired\":{\"" + desiredKey + "\":\"" + desiredValue + "\"}}}";
            req.setPayload(ByteBuffer.wrap(payload.getBytes("UTF8")));
            data.updateThingShadow(req);
            return successMessage;
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(DashboardAlexaSpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            return failureMessage;
        }
    }
    
    private void log(String message) {
        Logger.getLogger(getClass().getName()).info(message);
    }

}
