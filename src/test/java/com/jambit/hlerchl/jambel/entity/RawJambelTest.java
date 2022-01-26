package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelCommand;
import com.jambit.hlerchl.jambel.exceptions.JambelConnectException;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.exceptions.JambelIoException;
import com.jambit.hlerchl.jambel.exceptions.JambelResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RawJambelTest {

    private JambelCommLink mockedLink;
    private Jambel fixture;

    @BeforeEach
    void setupCommLinkMock() throws JambelException {
        mockedLink = Mockito.mock(JambelCommLink.class);
        Mockito.doThrow(new JambelIoException("Wrong command sent"))
            .when(mockedLink).sendCommand(Mockito.anyString());
         fixture = new RawJambel(mockedLink, 1, 2, 3);
    }

    @Test
    void reset() throws JambelException {
        expectOkCommand(() -> fixture.reset(), "reset");
    }

    @Test
    void setDefaultBlinkTimes() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("blink_time_on=600");
        Mockito.doReturn("OK").when(mockedLink).sendCommand("blink_time_off=300");

        fixture.setDefaultBlinkTimes(600, 300);
    }

    @Test
    void getVersionOfJambel() throws JambelException {
        Mockito.doReturn("Version 1.0 of da funky light")
            .when(mockedLink).sendCommand("version");

        final String version = fixture.version();
        assertEquals(version, "Version 1.0 of da funky light");
    }

    @Test
    void turnGreenOn() throws JambelException {
        expectOkCommand(() -> { fixture.green().on(); }, "set=3,on");
    }

    @Test
    void turnYellowOn() throws JambelException {
        expectOkCommand(() -> { fixture.yellow().on(); }, "set=2,on");
    }

    @Test
    void turnRedOn() throws JambelException {
        expectOkCommand(() -> { fixture.red().on(); }, "set=1,on");
    }

    @Test
    void setModeGreenOn() throws JambelException {
        expectOkCommand(
            () -> { fixture.green().setMode(Jambel.LightStatus.ON); }, "set=3,on");
    }

    @Test
    void setModeYellowOn() throws JambelException {
        expectOkCommand(
            () -> { fixture.yellow().setMode(Jambel.LightStatus.ON); }, "set=2,on");
    }

    @Test
    void setModeRedOn() throws JambelException {
        expectOkCommand(
            () -> { fixture.red().setMode(Jambel.LightStatus.ON); }, "set=1,on");
    }

    @Test
    void turnGreen30MSecOn() throws JambelException {
        expectOkCommand(() -> { fixture.green().on(30); }, "set=3,30");
    }

    @Test
    void turnYellow60MSecOn() throws JambelException {
        expectOkCommand(() -> { fixture.yellow().on(60); }, "set=2,60");
    }

    @Test
    void turnRed90MSecOn() throws JambelException {
        expectOkCommand(() -> { fixture.red().on(90); }, "set=1,90");
    }

    @Test
    void turnRedOff() throws JambelException {
        expectOkCommand(() -> { fixture.red().off(); }, "set=1,off");
    }

    @Test
    void letRedBlink() throws JambelException {
        expectOkCommand(() -> { fixture.red().blink(); }, "set=1,blink");
    }

    @Test
    void letRedFlash() throws JambelException {
        expectOkCommand(() -> { fixture.red().flash(); }, "set=1,flash");
    }

    @Test
    void setModeRedOff() throws JambelException {
        expectOkCommand(
            () -> { fixture.red().setMode(Jambel.LightStatus.OFF); }, "set=1,off");
    }

    @Test
    void setModeRedBlink() throws JambelException {
        expectOkCommand(
            () -> { fixture.red().setMode(Jambel.LightStatus.BLINK); }, "set=1,blink");
    }

    @Test
    void setModeRedFlash() throws JambelException {
        expectOkCommand(
            () -> { fixture.red().setMode(Jambel.LightStatus.FLASH); }, "set=1,flash");
    }

    @Test
    void letRedBlinkInverse() throws JambelException {
        // [sic!] the telnet command really spells "invers"
        expectOkCommand(() -> { fixture.red().blinkInverse(); }, "set=1,blink_invers");
    }

    @Test
    void setModeRedBlinkInverse() throws JambelException {
        // [sic!] the telnet command really spells "invers"
        expectOkCommand(
            () -> { fixture.red().setMode(Jambel.LightStatus.BLINK_INVERSE); },
            "set=1,blink_invers");
    }

    @Test
    void setAllLightsBlinkOnOff() throws JambelException {
        expectOkCommand(() ->  {
            fixture.setAllLights(Jambel.LightStatus.BLINK, Jambel.LightStatus.ON,
                Jambel.LightStatus.OFF);
        }, "set_all=2,1,0");
    }

    @Test
    void setAllLightsFlashBlinkInverseOn() throws JambelException {
        expectOkCommand(() -> {
            fixture.setAllLights(Jambel.LightStatus.FLASH, Jambel.LightStatus.BLINK_INVERSE,
                Jambel.LightStatus.ON);
        }, "set_all=3,4,1");
    }

    @Test
    void setAllLightsOnFlashBlinkForInverseModules() throws JambelException {
        final Jambel inverseFixture = new RawJambel(mockedLink,
            3, 2, 1);
        final String telnetCommand = "set_all=2,3,1";
        Mockito.doReturn("OK").when(mockedLink).sendCommand(telnetCommand);

        inverseFixture.setAllLights(Jambel.LightStatus.ON, Jambel.LightStatus.FLASH,
            Jambel.LightStatus.BLINK);

        Mockito.verify(mockedLink).sendCommand(telnetCommand);
        Mockito.verifyNoMoreInteractions(mockedLink);
    }

    @Test
    void setBlinkTimersForSingleModule() throws JambelException {
        expectOkCommand(() -> {
            fixture.red().setBlinkTimes(800, 400);
        }, "blink_time=1,800,400");
    }

    @Test
    void inverseModuleOrderRedOn() throws JambelException {
        final Jambel inverseFixture = new RawJambel(mockedLink, 3, 2, 1);
        final String telnetCommand = "set=3,on";
        Mockito.doReturn("OK").when(mockedLink).sendCommand(telnetCommand);

        inverseFixture.red().on();

        Mockito.verify(mockedLink).sendCommand(telnetCommand);
        Mockito.verifyNoMoreInteractions(mockedLink);
    }

    @Test
    void inverseModuleOrderRedSetModeOn() throws JambelException {
        final Jambel inverseFixture = new RawJambel(mockedLink, 3, 2, 1);
        final String telnetCommand = "set=3,on";
        Mockito.doReturn("OK").when(mockedLink).sendCommand(telnetCommand);

        inverseFixture.red().setMode(Jambel.LightStatus.ON);

        Mockito.verify(mockedLink).sendCommand(telnetCommand);
        Mockito.verifyNoMoreInteractions(mockedLink);
    }

    @Test
    void throwsOnUnusualAnswer() throws JambelException {
        Mockito.doReturn("OKiDoki").when(mockedLink).sendCommand("set=1,blink");
        Mockito.doReturn("").when(mockedLink).sendCommand("version");

        assertThrows(JambelResponseException.class, () -> fixture.red().blink());
        assertThrows(JambelResponseException.class, () -> fixture.version());
    }

    @Test
    void throwsOnNoAnswer() throws JambelException {
        Mockito.doReturn(null).when(mockedLink).sendCommand("set=1,on");
        Mockito.doReturn(null).when(mockedLink).sendCommand("version");

        assertThrows(JambelResponseException.class, () -> fixture.red().on());
        assertThrows(JambelResponseException.class, () -> fixture.version());
    }

    @Test
    void connectExceptionIsForwarded() throws JambelException {
        Mockito.doThrow(new JambelConnectException("no connection"))
            .when(mockedLink).sendCommand("set=1,blink");

        assertThrows(JambelConnectException.class, () -> fixture.red().blink());
    }

    @Test
    void ioExceptionIsForwarded() throws JambelException {
        Mockito.doThrow(new JambelIoException("broken link"))
            .when(mockedLink).sendCommand("set=1,blink");

        assertThrows(JambelIoException.class, () -> fixture.red().blink());
    }

    @Test
    void statusBrokenResponsePrefix() throws JambelException {
        Mockito.doReturn("stats=1,0,2,0,0").when(mockedLink).sendCommand("status");

        assertThrows(JambelResponseException.class, () -> fixture.status());
    }

    @Test
    void statusUnknownValueInResponse() throws JambelException {
        Mockito.doReturn("status=1,8,2,0,0").when(mockedLink).sendCommand("status");

        assertThrows(JambelResponseException.class, () -> fixture.status());
    }

    @Test
    void statusUnknownStructureOfResponse() throws JambelException {
        Mockito.doReturn("status=1, 1, 2, 0, 0").when(mockedLink).sendCommand("status");

        assertThrows(JambelResponseException.class, () -> fixture.status());
    }

    private void expectOkCommand(JambelCommand command, String telnetCommand) throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand(telnetCommand);

        command.execute();

        Mockito.verify(mockedLink).sendCommand(telnetCommand);
        Mockito.verifyNoMoreInteractions(mockedLink);
    }
}
