package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelModule;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.exceptions.JambelResponseException;
import com.jambit.hlerchl.jambel.JambelCommLink;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RawJambel implements Jambel {
    private final JambelCommLink commLink;
    private final JambelModule redModule;
    private final JambelModule yellowModule;
    private final JambelModule greenModule;
    private String version = null;

    public RawJambel(JambelCommLink commLink, int redModuleId, int yellowModuleId, int greenModuleId) {
        this.commLink = commLink;
        this.redModule = new RawModule(redModuleId);
        this.yellowModule = new RawModule(yellowModuleId);
        this.greenModule = new RawModule(greenModuleId);
    }

    @Override
    public void reset() throws JambelException {
        sendOkCommand("reset");
    }

    @Override
    public synchronized String version() throws JambelException {
        if (version == null) {
            final String response = commLink.sendCommand("version");
            if (response == null) {
                throw new JambelResponseException("Received null, expected response for 'version'");
            }
            if (response.isEmpty()) {
                throw new JambelResponseException("Received empty string, expected response for 'version'");
            }
            version = response;
        }
        return version;
    }

    @Override
    public void setDefaultBlinkTimes(int msecOn, int msecOff) throws JambelException {
        sendOkCommand("blink_time_on=" + msecOn);
        sendOkCommand("blink_time_off=" + msecOff);
    }

    @Override
    public JambelModule green() {
        return greenModule;
    }

    @Override
    public JambelModule yellow() {
        return yellowModule;
    }

    @Override
    public JambelModule red() {
        return redModule;
    }

    private void sendOkCommand(String command) throws JambelException {
        final String response = commLink.sendCommand(command);
        if (response == null) {
            throw new JambelResponseException(String.format("Received null, expected 'OK' for '%s'", command));
        }
        if (!"OK".equals(response)) {
            throw new JambelResponseException(String.format("Received '%s', expected 'OK' for '%s'",
                    response, command));
        }
    }

    private class RawModule implements JambelModule {
        private final int moduleId;

        RawModule(int moduleId) {
            this.moduleId = moduleId;
        }

        @Override
        public void on() throws JambelException {
            sendOkCommand("set=" + moduleId + ",on");
        }

        @Override
        public void off() throws JambelException {
            sendOkCommand("set=" + moduleId + ",off");
        }

        @Override
        public void blink() throws JambelException {
            sendOkCommand("set=" + moduleId + ",blink");
        }

        @Override
        public void blinkInvers() throws JambelException {
            sendOkCommand("set=" + moduleId + ",blink_invers");
        }

        @Override
        public void flash() throws JambelException {
            sendOkCommand("set=" + moduleId + ",flash");
        }

        @Override
        public void setBlinkTimes(int msecOn, int msecOff) throws JambelException {
            sendOkCommand("blink_time=" + moduleId + "," + msecOn + "," + msecOff);
        }
    }
}
