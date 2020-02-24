package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

public interface Jambel {
    void reset() throws JambelException;
    String version() throws JambelException;
    void setDefaultBlinkTimes(int msecOn, int msecOff) throws JambelException;

    JambelModule green();
    JambelModule yellow();
    JambelModule red();
}
