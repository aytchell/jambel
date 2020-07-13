package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelCommLink;
import com.jambit.hlerchl.jambel.link.JambelTelnetLink;

public class JambelGreenOnTop extends RawJambel {
    public JambelGreenOnTop(JambelCommLink commLink) {
        super(commLink, 1, 2, 3);
    }

    public static Jambel build(String hostAddress, int port) {
        return new JambelGreenOnTop(
                new JambelTelnetLink(hostAddress, port));
    }

    public static Jambel build(String hostAddress, int port, int timeoutMilliSeconds) {
        final RawJambel jambel = new JambelGreenOnTop(
            new JambelTelnetLink(hostAddress, port));
        jambel.setConnectTimeout(timeoutMilliSeconds);
        return jambel;
    }
}
