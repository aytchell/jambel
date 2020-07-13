package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.entity.JambelCommandCompiler;
import com.jambit.hlerchl.jambel.entity.JambelGreenOnTop;
import com.jambit.hlerchl.jambel.entity.JambelRedOnTop;
import com.jambit.hlerchl.jambel.exceptions.JambelCompileException;

public class JambelFactory {
    public static Jambel build(String hostAddress, int port, boolean redOnTop) {
        if (redOnTop) {
            return JambelRedOnTop.build(hostAddress, port);
        } else {
            return JambelGreenOnTop.build(hostAddress, port);
        }
    }

    public static Jambel build(String hostAddress, int port, boolean redOnTop, int timeoutMilliSeconds) {
        if (redOnTop) {
            return JambelRedOnTop.build(hostAddress, port, timeoutMilliSeconds);
        } else {
            return JambelGreenOnTop.build(hostAddress, port, timeoutMilliSeconds);
        }
    }

    public static JambelCommand compileCommand(Jambel jambel, String command)
        throws JambelCompileException {
        return JambelCommandCompiler.compile(jambel, command);
    }
}
