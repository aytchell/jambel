package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelCommLink;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.exceptions.JambelIoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class JambelRedOnTopTest {

    private JambelCommLink mockedLink;
    private Jambel fixture;

    @BeforeEach
    void setupCommLinkMock() throws JambelException {
        mockedLink = Mockito.mock(JambelCommLink.class);
        Mockito.doThrow(new JambelIoException("Wrong command sent"))
            .when(mockedLink).sendCommand(Mockito.anyString());
        fixture = new JambelRedOnTop(mockedLink);
    }

    @Test
    void turnGreenOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=1,on");

        fixture.green().on();
    }

    @Test
    void turnYellowOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=2,on");

        fixture.yellow().on();
    }

    @Test
    void turnRedOn() throws JambelException {
        Mockito.doReturn("OK").when(mockedLink).sendCommand("set=3,on");

        fixture.red().on();
    }
}
