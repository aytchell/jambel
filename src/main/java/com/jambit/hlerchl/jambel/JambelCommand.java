package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

public interface JambelCommand {
    void execute() throws JambelException;
}
