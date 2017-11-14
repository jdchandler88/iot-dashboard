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
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.chandler.iotdashboard.util.AskResponses;
import com.chandler.iotdashboard.util.EnabledSlot;
import com.chandler.iotdashboard.util.Intents;
import com.chandler.iotdashboard.util.TellResponses;
import org.apache.log4j.Logger;

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
            Resolution res = enabled.getResolutions().getResolutionAtIndex(0);
            if (res.getValueWrappers().size()==1) {
                ValueWrapper vw = res.getValueWrapperAtIndex(0);
                boolean displayPower = true;    //let's default to "on" for now
                switch (vw.getValue().getId()) {
                    case EnabledSlot.ON_ID: {
                        displayPower = true;
                        response = getTellResponse("Display will be turned on.");
                        break;
                    }
                    case EnabledSlot.OFF_ID: {
                        displayPower = false;
                        response = getTellResponse("Display will be turned off.");
                        break;
                    }
                    default: {
                        //shouldn't ever get here--i dunno what to do here
                        //log error? create a response?
                        response = getTellResponse("I'm not sure what you're asking. Sorry.");
                        break;
                    }
                }
                //set the display
                
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
        return getTellResponse("Display will wake up.");
    }
    
    private SpeechletResponse handleNewPictures() {
        return getTellResponse("The board will refresh the batch of pictures.");
    }
    
    private void log(String message) {
        Logger.getLogger(getClass().getName()).info(message);
    }
    
//    private SpeechletResponse getWelcomeResponse() {
//        return getTellResponse(TellResponses.LAUNCH_RESPONSE);
//    }
//    
//    private SpeechletResponse getNullIntentResponse() {
//        String text = AskResponses.NULL_INTENT;
//        return getAskResponse(text);
//    }
    
    private SpeechletResponse getTellResponse(String text) {
        return SpeechletResponse.newTellResponse(getSpeech(text));
    }
    
    private SpeechletResponse getAskResponse(String text) {
        return SpeechletResponse.newAskResponse(getSpeech(text), getReprompt(text));
    }
    
    private PlainTextOutputSpeech getSpeech(String text) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(text);
        return speech;
    }
    
    private Reprompt getReprompt(String text) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(getSpeech(text));
        return reprompt;
    }
    
}
