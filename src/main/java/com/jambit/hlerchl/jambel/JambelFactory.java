package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.entity.JambelGreenOnTop;
import com.jambit.hlerchl.jambel.entity.JambelRedOnTop;

public class JambelFactory {
    public static Jambel build(String hostAddress, int port, boolean readOnTop) {
        if (readOnTop) {
            return JambelRedOnTop.build(hostAddress, port);
        } else {
            return JambelGreenOnTop.build(hostAddress, port);
        }
    }
}
