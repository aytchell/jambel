package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

public interface JambelCommLink {
    void setConnectTimeout(int milliSeconds);

    String sendCommand(String command) throws JambelException;
}
