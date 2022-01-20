package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.exceptions.JambelException;
import lombok.Value;

public interface Jambel {
    void reset() throws JambelException;
    String version() throws JambelException;
    void setDefaultBlinkTimes(int msecOn, int msecOff) throws JambelException;
    Status status() throws JambelException;

    // throws on connection error; no other effect
    void testConnection() throws JambelException;

    JambelModule green();
    JambelModule yellow();
    JambelModule red();

    @Value
    class Status {
        LightStatus red;
        LightStatus yellow;
        LightStatus green;
    }

    enum LightStatus {
        OFF,
        ON,
        BLINK,
        FLASH,
        BLINK_INVERSE
    }
}
