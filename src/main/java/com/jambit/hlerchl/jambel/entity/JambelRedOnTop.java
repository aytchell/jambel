package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelCommLink;
import com.jambit.hlerchl.jambel.link.JambelTelnetLink;

public class JambelRedOnTop extends RawJambel {
    public JambelRedOnTop(JambelCommLink commLink) {
        super(commLink, 3, 2, 1);
    }

    public static Jambel build(String hostAddress, int port) {
        return new JambelRedOnTop(
            new JambelTelnetLink(hostAddress, port));
    }

    public static Jambel build(String hostAddress, int port, int timeoutMilliSeconds) {
        final RawJambel jambel = new JambelRedOnTop(
                new JambelTelnetLink(hostAddress, port));
        jambel.setConnectTimeout(timeoutMilliSeconds);
        return jambel;
    }
}
