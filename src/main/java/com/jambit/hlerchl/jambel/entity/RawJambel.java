package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelModule;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.exceptions.JambelResponseException;
import com.jambit.hlerchl.jambel.exceptions.LookupException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class RawJambel implements Jambel {
    private final JambelCommLink commLink;
    private final RawModule redModule;
    private final RawModule yellowModule;
    private final RawModule greenModule;
    private String version = null;

    public RawJambel(JambelCommLink commLink, int redModuleId, int yellowModuleId, int greenModuleId) {
        this.commLink = commLink;
        this.redModule = new RawModule(redModuleId);
        this.yellowModule = new RawModule(yellowModuleId);
        this.greenModule = new RawModule(greenModuleId);
    }

    public void setConnectTimeout(int milliSeconds) {
        commLink.setConnectTimeout(milliSeconds);
    }

    @Override
    public void reset() throws JambelException {
        sendOkCommand("reset");
    }

    @Override
    public String version() throws JambelException {
        if (version == null) {
            version = sendCommandExpectResponse("version");
        }
        return version;
    }

    @Override
    public Status status() throws JambelException {
        final String statusResponse = sendCommandExpectResponse("status");
        if (!statusResponse.startsWith("status=")) {
            throw new JambelResponseException(
                String.format("Expected response for 'status' to start with 'status='; got '%s'",
                    statusResponse));
        }

        return parseStatusResponse(statusResponse);
    }

    @Override
    public void testConnection() throws JambelException
    {
        sendOkCommand("test");
    }

    private Status parseStatusResponse(String statusResponse) throws JambelResponseException {
        List<LightStatus> lightStatus = new ArrayList<>(3);
        try {
            lightStatus.add(interpretStatusId(extractModuleStatus(1, statusResponse)));
            lightStatus.add(interpretStatusId(extractModuleStatus(2, statusResponse)));
            lightStatus.add(interpretStatusId(extractModuleStatus(3, statusResponse)));
        } catch (Exception e) {
            throw new JambelResponseException(
                String.format("Failed to parse response for 'status' (which is '%s')",
                    statusResponse));
        }

        return new Status(
            lightStatus.get(redModule.getModuleId() - 1),
            lightStatus.get(yellowModule.getModuleId() - 1),
            lightStatus.get(greenModule.getModuleId() - 1)
        );
    }

    private LightStatus interpretStatusId(int statusId) throws LookupException {
        switch (statusId) {
            case 0: return LightStatus.OFF;
            case 1: return LightStatus.ON;
            case 2: return LightStatus.BLINK;
            case 3: return LightStatus.FLASH;
            case 4: return LightStatus.BLINK_INVERSE;
            default:
                throw new LookupException("Unknown status identifier");
        }
    }

    private int getStatusId(LightStatus status) throws LookupException {
        switch (status) {
            case OFF: return 0;
            case ON: return 1;
            case BLINK: return 2;
            case FLASH: return 3;
            case BLINK_INVERSE: return 4;
            default:
                throw new LookupException("Unknown status identifier");
        }
    }

    private String getStatusCommand(LightStatus status) throws LookupException {
        switch (status) {
            case OFF: return "off";
            case ON: return "on";
            case BLINK: return "blink";
            case FLASH: return "flash";
            // [sic!] the telnet command really spells "invers"
            case BLINK_INVERSE: return "blink_invers";
            default:
                throw new LookupException("Unknown status identifier");
        }
    }

    private int extractModuleStatus(int moduleId, String statusResponse) {
        final String messageStart = "status=";
        final String statusFormat = "x,";
        final int startIndex = messageStart.length() + ((moduleId - 1) * statusFormat.length());
        String id = statusResponse.substring(startIndex, startIndex + 1);
        return Integer.parseInt(id);
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

    @Override
    public void setAllLights(LightStatus redStatus, LightStatus yellowStatus,
                             LightStatus greenStatus) throws JambelException {
        try {
            Map<Integer, Integer> lightCodes = new HashMap<>(3);
            lightCodes.put(redModule.moduleId, getStatusId(redStatus));
            lightCodes.put(yellowModule.moduleId, getStatusId(yellowStatus));
            lightCodes.put(greenModule.moduleId, getStatusId(greenStatus));

            // the jambel supports up to four modules, so we have to send four values
            // otherwise it will halt and wait for the fourth value
            final String command = "set_all=" +
                lightCodes.get(1) + "," + lightCodes.get(2) + "," + lightCodes.get(3) + ",0";

            sendOkCommand(command);
        } catch (LookupException e) {
            throw new JambelException(e.getMessage());
        }
    }

    private synchronized String sendCommandExpectResponse(String command) throws JambelException {
        final String response = commLink.sendCommand(command);
        if (response == null) {
            throw new JambelResponseException(
                String.format("Received null, expected response for '%s'", command));
        }
        if (response.isEmpty()) {
            throw new JambelResponseException(
                String.format("Received empty string, expected response for '%s'", command));
        }
        return response;
    }

    private void sendOkCommand(String command) throws JambelException {
        final String response = sendCommandExpectResponse(command);
        if (!"OK".equals(response)) {
            throw new JambelResponseException(String.format("Received '%s', expected 'OK' for '%s'",
                    response, command));
        }
    }

    private class RawModule implements JambelModule {
        @Getter
        private final int moduleId;

        RawModule(int moduleId) {
            this.moduleId = moduleId;
        }

        @Override
        public void on() throws JambelException {
            setMode(LightStatus.ON);
        }

        @Override
        public void on(int milliSeconds) throws JambelException {
            sendOkCommand("set=" + moduleId + "," + milliSeconds);
        }


        @Override
        public void off() throws JambelException {
            setMode(LightStatus.OFF);
        }

        @Override
        public void blink() throws JambelException {
            setMode(LightStatus.BLINK);
        }

        @Override
        public void blinkInverse() throws JambelException {
            setMode(LightStatus.BLINK_INVERSE);
        }

        @Override
        public void flash() throws JambelException {
            setMode(LightStatus.FLASH);
        }

        @Override
        public void setMode(Jambel.LightStatus mode) throws JambelException {
            try {
                final String modeName = getStatusCommand(mode);
                sendOkCommand("set=" + moduleId + "," + modeName);
            } catch (LookupException e) {
                throw new JambelException(e.getMessage());
            }
        }

        @Override
        public void setBlinkTimes(int msecOn, int msecOff) throws JambelException {
            sendOkCommand("blink_time=" + moduleId + "," + msecOn + "," + msecOff);
        }
    }
}
