package com.jambit.hlerchl.jambel.link;

import com.jambit.hlerchl.jambel.JambelCommLink;
import com.jambit.hlerchl.jambel.exceptions.JambelConnectException;
import com.jambit.hlerchl.jambel.exceptions.JambelException;
import com.jambit.hlerchl.jambel.exceptions.JambelIoException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JambelTelnetLink implements JambelCommLink {
    private static final int RECEIVE_BUFFER_SIZE = 128;
    private static final int DEFAULT_NUM_CONNECT_RETRIES = 3;
    private static final int DEFAULT_MSEC_UNTIL_FIRST_RETRY = 100;

    private final String hostname;
    private final int port;
    private final TelnetClient telnetClient;
    private final byte[] receiveBuffer;

    @Setter
    private int numberOfConnectRetries = DEFAULT_NUM_CONNECT_RETRIES;
    @Setter
    private int msecUntilRetry = DEFAULT_MSEC_UNTIL_FIRST_RETRY;

    public JambelTelnetLink(TelnetClient telnetClient, String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.telnetClient = telnetClient;
        receiveBuffer = new byte[RECEIVE_BUFFER_SIZE];
    }

    public JambelTelnetLink(String hostname, int port) {
        this(new TelnetClient(), hostname, port);
    }

    public void setConnectTimeout(int milliSeconds) {
        telnetClient.setConnectTimeout(milliSeconds);
    }

    @Override
    public synchronized String sendCommand(String command) throws JambelException {
        int attemptNr = 0;

        for (; ; ) {
            try {
                return connectAndSend(command);
            } catch (JambelConnectException e) {
                if (++attemptNr > numberOfConnectRetries) {
                    throw new JambelConnectException(String.format("While sending '%s'", command), e);
                }
                try {
                    Thread.sleep(msecUntilRetry);
                } catch (InterruptedException ex) {
                    throw new JambelException("Got interrupted while waiting for next connect");
                }
            }
        }
    }

    private String connectAndSend(String command) throws JambelConnectException, JambelIoException {
        try {
            telnetClient.connect(hostname, port);
            try {
                return sendTelnetCommand(stripTrailingCrLf(command));
            } finally {
                telnetClient.disconnect();
            }
        } catch (ConnectException connex) {
            throw new JambelConnectException(String.format("While sending '%s'", command), connex);
        } catch (UnknownHostException uhex) {
            throw new JambelConnectException("Unknown host. Check if '" + hostname +
                                             "' is the correct name and if it's online");
        } catch (IOException ioex) {
            throw new JambelIoException(String.format("While sending '%s'", command), ioex);
        }
    }

    private String sendTelnetCommand(String command) throws IOException {
        // documentation says that we shouldn't close this stream but call disconnect()
        final OutputStream out = telnetClient.getOutputStream();

        log.debug("{}: Sending command '{}\\r\\n' ... ", hostname, command);
        out.write((command + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.flush();

        // documentation says that we shouldn't close this stream but call disconnect()
        final InputStream in = telnetClient.getInputStream();
        return readTelnetResponse(in);
    }

    private String readTelnetResponse(InputStream in) throws IOException {
        int overallBytesReceived = 0;
        int bytesLeftInBuffer = receiveBuffer.length;

        for (; ; ) {
            final int numNewBytesReceived = in.read(receiveBuffer, overallBytesReceived, bytesLeftInBuffer);
            overallBytesReceived += numNewBytesReceived;
            bytesLeftInBuffer -= numNewBytesReceived;

            if ((overallBytesReceived > 2) && bytesEndWithCrLf(receiveBuffer, overallBytesReceived)) {
                final String response = telnetBytesToString(receiveBuffer, overallBytesReceived);
                log.debug("{}: received response '{}'", hostname, response);
                return response;
            }
        }
    }

    private String stripTrailingCrLf(String command) {
        while (command.endsWith("\r\n")) {
            command = command.substring(0, command.length() - 3);
        }

        return command;
    }

    private String telnetBytesToString(byte[] buffer, int numBytes) {
        if (bytesEndWithCrLf(buffer, numBytes)) {
            return utf8BytesToString(buffer, numBytes - 2);
        }
        return utf8BytesToString(buffer, numBytes);
    }

    private boolean bytesEndWithCrLf(byte[] buffer, int numBytes) {
        if (numBytes < 2) {
            return false;
        }
        return (buffer[numBytes - 2] == '\r') && (buffer[numBytes - 1] == '\n');
    }

    private String utf8BytesToString(byte[] buffer, int numBytes) {
        return new String(buffer, 0, numBytes, StandardCharsets.UTF_8);
    }
}
