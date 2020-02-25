package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelCommLink;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.exceptions.JambelIoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Mockito.doReturn("OK").when(mockedLink).sendCommand("reset");

        fixture.reset();
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
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=3,on");

        fixture.green().on();
    }

    @Test
    void turnYellowOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=2,on");

        fixture.yellow().on();
    }

    @Test
    void turnRedOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,on");

        fixture.red().on();
    }

    @Test
    void turnRedOff() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,off");

        fixture.red().off();
    }

    @Test
    void letRedBlink() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,blink");

        fixture.red().blink();
    }

    @Test
    void letRedBlinkInvers() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,blink_invers");

        fixture.red().blinkInvers();
    }

    @Test
    void setBlinkTimersForSingleModule() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("blink_time=1,800,400");

        fixture.red().setBlinkTimes(800, 400);
    }

    @Test
    void inverseModuleOrderWorks() throws JambelException {
        final Jambel inverseFixture = new RawJambel(mockedLink, 3, 2, 1);
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=3,on");

        inverseFixture.red().on();
    }
}
