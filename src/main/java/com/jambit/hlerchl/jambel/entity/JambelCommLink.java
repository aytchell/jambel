package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

public interface JambelCommLink {
    void setConnectTimeout(int milliSeconds);

    String sendCommand(String command) throws JambelException;
}
