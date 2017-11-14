/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chandler.iotdashboard.util;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

/**
 *
 * @author D
 */
public final class ResponseFactory {
    
    
    public static SpeechletResponse getTellResponse(String text) {
        return SpeechletResponse.newTellResponse(getSpeech(text));
    }
    
    public static SpeechletResponse getAskResponse(String text) {
        return SpeechletResponse.newAskResponse(getSpeech(text), getReprompt(text));
    }
    
    private static PlainTextOutputSpeech getSpeech(String text) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(text);
        return speech;
    }
    
    private static Reprompt getReprompt(String text) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(getSpeech(text));
        return reprompt;
    }
    
}
