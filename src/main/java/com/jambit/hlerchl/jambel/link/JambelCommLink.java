package com.jambit.hlerchl.jambel.link;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

public interface JambelCommLink {
    String sendCommand(String command) throws JambelException;
}
