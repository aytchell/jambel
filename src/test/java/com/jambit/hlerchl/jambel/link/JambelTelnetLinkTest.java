package com.jambit.hlerchl.jambel.link;

import com.jambit.hlerchl.jambel.exceptions.JambelConnectException;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import org.apache.commons.net.telnet.TelnetClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class JambelTelnetLinkTest {

    @Test
    void setConnectTimeoutIsPassedThrough() {
        TelnetClient tc = Mockito.mock(TelnetClient.class);
        final JambelTelnetLink link = new JambelTelnetLink(tc, "localhost", 1337);
        link.setConnectTimeout(4711);

        verify(tc).setConnectTimeout(4711);
    }

    /**
     * Test the "happy path" when a command is sent and the answer is received
     * @throws IOException should never happen
     * @throws JambelException should never happen
     */
    @Test
    void sendCommand() throws IOException, JambelException {
        // Prepare the mocks
        TelnetClient tc = Mockito.mock(TelnetClient.class);
        final InputStream mockedInput =
            new ByteArrayInputStream("OK\r\n".getBytes(StandardCharsets.UTF_8));
        final ByteArrayOutputStream mockedOutput = new ByteArrayOutputStream();
        doReturn(mockedInput).when(tc).getInputStream();
        doReturn(mockedOutput).when(tc).getOutputStream();

        // Execute the actual call
        final JambelTelnetLink link = new JambelTelnetLink(tc, "localhost", 1337);
        final String response = link.sendCommand("reset");
        assertEquals("OK", response);

        // Check the mocks if they where called as expected
        verify(tc).connect("localhost", 1337);
        verify(tc).disconnect();

        final String sentCommand = new String(mockedOutput.toByteArray(), StandardCharsets.UTF_8);
        assertEquals("reset\r\n", sentCommand);
    }

    /**
     * In case the jambel can't be found (we or the jambel are offline, DNS error,
     * broken switch, ...) there will be a UnknownHostException.
     *
     * Let's see if this is handled
     * @throws JambelException should never happen
     * @throws IOException should never happen
     */
    @Test
    void unknownHostExceptionLeadsToConnectExcetion() throws JambelException, IOException {
        TelnetClient tc = Mockito.mock(TelnetClient.class);
        doThrow(new UnknownHostException())
            .when(tc).connect(anyString(), anyInt());

        final JambelTelnetLink link = new JambelTelnetLink(tc, "localhost", 1337);
        assertThrows(JambelConnectException.class, () -> link.sendCommand("reset"));
    }

    /**
     * Check if we're reading the complete answer
     *
     * In one of my manual tests I looked like the jambel is sending its answer reeeeaaaaally
     * slow (or I forgot a parameter during telnet negotiation?!)
     * Even though the usual answer consists of only four characters it needs several calls to
     * 'read()' those four characters. Let's check if this works ...
     * (This needs a bit more mockery with the input stream)
     */
    @Test
    void jambelReadsCompleteAnswer() throws IOException, JambelException {
        TelnetClient tc = Mockito.mock(TelnetClient.class);
        final InputStream mockedInput = Mockito.mock(InputStream.class);
        final ByteArrayOutputStream mockedOutput = new ByteArrayOutputStream();
        doReturn(mockedInput).when(tc).getInputStream();
        doReturn(mockedOutput).when(tc).getOutputStream();

        // setup mock for InputStream: for each successive read(...) call we only place one
        // character into the given buffer (at the correct offset)
        doAnswer(new Answer() {
            private int count = 0;
            public Object answer(InvocationOnMock invocation) throws IOException {
                Object[] args = invocation.getArguments();
                byte[] buffer = ((byte[]) args[0]);
                int offset = (int)args[1];

                switch (count) {
                    case 0: buffer[offset] = 'O'; break;
                    case 1: buffer[offset] = 'K'; break;
                    case 2: buffer[offset] = '\r'; break;
                    case 3: buffer[offset] = '\n'; break;
                    case 4: throw new IOException("Out of luck");
                }
                ++count;

                return 1;
            }
        }).when(mockedInput).read(any(byte[].class), anyInt(), anyInt());

        // enagage ...
        final JambelTelnetLink link = new JambelTelnetLink(tc, "localhost", 1337);
        final String response = link.sendCommand("set=2,on");
        assertEquals("OK", response);

        final String sentCommand = new String(mockedOutput.toByteArray(), StandardCharsets.UTF_8);
        assertEquals("set=2,on\r\n", sentCommand);
    }

    /**
     * Try again if a 'connection refused' occurs
     *
     * The jambel's TCP stack is very simple. Very simple. It only allows one connection at a
     * time and after the connection was teared down it needs some time until a new connection
     * is accepted.
     * During manual tests I had the case that if you send multiple commands quickly in a row
     * the second command will break with a 'connection refused' error. In this case the code
     * should wait a bit and retry the connection setup.
     * This test returns a 'connection refused' on the first attempt but works on the second
     * attempt. Just like the jambel does ...
     * @throws JambelException Should never happen
     * @throws IOException Should never happen
     */
    @Test
    void tryAgainOnConnectError() throws JambelException, IOException {
        // Prepare the mocks
        TelnetClient tc = Mockito.mock(TelnetClient.class);
        final InputStream mockedInput =
            new ByteArrayInputStream("OK\r\n".getBytes(StandardCharsets.UTF_8));
        final ByteArrayOutputStream mockedOutput = new ByteArrayOutputStream();
        doReturn(mockedInput).when(tc).getInputStream();
        doReturn(mockedOutput).when(tc).getOutputStream();

        // throw on first 'connect()' call. All successive calls pass
        doAnswer(new Answer() {
            private int count = 0;
            public Object answer(InvocationOnMock invocation) throws ConnectException {
                if (0 == count) {
                    ++count;
                    throw new ConnectException("Connection refused by peer");
                }
                return null;
            }
        }).when(tc).connect(anyString(), anyInt());

        // Execute the actual call
        final JambelTelnetLink link = new JambelTelnetLink(tc, "localhost", 1337);
        final String response = link.sendCommand("set=3,off");
        assertEquals("OK", response);

        // Check the mock if they where called as expected
        verify(tc).disconnect();

        final String sentCommand = new String(mockedOutput.toByteArray(), StandardCharsets.UTF_8);
        assertEquals("set=3,off\r\n", sentCommand);
    }
}
