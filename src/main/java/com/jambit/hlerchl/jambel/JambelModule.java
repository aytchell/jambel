package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

public interface JambelModule {
    void on() throws JambelException;
    void off() throws JambelException;
    void blink() throws JambelException;
    void blinkInverse() throws JambelException;
    void flash() throws JambelException;
    void setBlinkTimes(int msecOn, int msecOff) throws JambelException;
}
