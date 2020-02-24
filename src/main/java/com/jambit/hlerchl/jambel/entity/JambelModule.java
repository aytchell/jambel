package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

public interface JambelModule {
    void on() throws JambelException;
    void off() throws JambelException;
    void blink() throws JambelException;
    void blinkInvers() throws JambelException;
    void flash() throws JambelException;
    void setBlinkTimes(int msecOn, int msecOff) throws JambelException;
}
