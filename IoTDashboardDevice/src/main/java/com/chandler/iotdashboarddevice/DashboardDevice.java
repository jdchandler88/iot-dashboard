/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chandler.iotdashboarddevice;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotDeviceProperty;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.shadow.AbstractAwsIotDevice;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This listens for messages from the web regarding how it should 
 * control itself (display on/off,  wake, refresh pictures, etc.)
 * 
 * To save money, this device will only publish *changes* to the IoT shadow. To 
 * do this, {@link AbstractAwsIotDevice#onDeviceReport()} is overridden to return
 * null if no changes (no report is sent if json is null). This is kludgy, but
 * it should save dat money.
 * 
 * @author D
 */
public class DashboardDevice extends AWSIotDevice {
    
    @AWSIotDeviceProperty
    private boolean displayPower;
    
    @AWSIotDeviceProperty
    private boolean displayWakeToggle;
    
    @AWSIotDeviceProperty
    private boolean refreshPicturesToggle;

    private String previousJsonState;
    
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    
    public DashboardDevice() {
        super("WallDisplay");
    }

    @Override
    public final String onDeviceReport() {
        String curState = super.onDeviceReport();
        if (!curState.equals(previousJsonState)) {
            previousJsonState = curState;
            System.out.println("\tSTATE: reporting");
            return curState;
        } else {
            System.out.println("\tSTATE: stale");
            return null;
        }
    }
    
    public boolean getDisplayPower() {
        //read from the device
        return displayPower;
    }

    public void setDisplayPower(boolean displayPower) {
        //write to the device
        System.out.println("display power set to " + displayPower);
        this.displayPower = displayPower;
    }

    public boolean getDisplayWakeToggle() {
        //shouldn't really do anything
        return displayWakeToggle;
    }

    public void setDisplayWakeToggle(boolean displayWakeToggle) {
        //only alexa can set this to true and only we can set it to off. this guard prevents infinite update loop when setting to off ourselves
        if (displayWakeToggle && (displayWakeToggle^this.displayWakeToggle)) {
            //trigger device to wake display

            //after some time, set both this property and the 'desired' state to false
            scheduleReset("displayWakeToggle");
        }
        
        System.out.println("wake set to " + displayWakeToggle);
        this.displayWakeToggle = displayWakeToggle;
        
    }

    public boolean getRefreshPicturesToggle() {
        //shouldn't really do anything
        return refreshPicturesToggle;
    }

    public void setRefreshPicturesToggle(boolean refreshPicturesToggle) {
        //only alexa can set this to on and only we can set it to off. this guard prevents infinite update loop when setting to off ourselves
        if (refreshPicturesToggle && (refreshPicturesToggle^this.refreshPicturesToggle)) {
            //trigger device to refresh pictures
        
            //after some time, set the desired property to false. that will trigger this to be set to false.
            scheduleReset("refreshPicturesToggle");
        }
        System.out.println("refresh pictures set to " + refreshPicturesToggle);
        this.refreshPicturesToggle = refreshPicturesToggle;
    }
    
    /**
     * Do the reset in a certain amount of time (for now, hard-coded to 3 secs)
     * @param key property to reset (set to false)
     */
    private void scheduleReset(String key) {
        ses.schedule(() -> resetToggleState(key), 3, TimeUnit.SECONDS);
    }
    
    /**
     * This sets the desired state of the supplied key to false. This will effectively
     * reset the value in this device. We would receive a message anyway, so we're  not
     * gaining anything by calling the setter ourselves here. Let the cloud do it for us!
     * @param key property to reset (set to false)
     */
    private void resetToggleState(String key) {
        //update the document in the right places
        try {
            String update = "{\"state\":{\"desired\":{\"" + key + "\":\"" + Boolean.FALSE.toString() + "\"}}}";
            update(update);
        } catch (AWSIotException ex) {
            Logger.getLogger(DashboardDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
