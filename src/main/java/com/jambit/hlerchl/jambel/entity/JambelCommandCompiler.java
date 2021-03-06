package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.Jambel;
import com.jambit.hlerchl.jambel.JambelCommand;
import com.jambit.hlerchl.jambel.JambelModule;
import com.jambit.hlerchl.jambel.exceptions.JambelCompileException;

public class JambelCommandCompiler {
    public static JambelCommand compile(Jambel jambel, String command) throws JambelCompileException {
        if (command == null) {
            throw new JambelCompileException("No command given");
        }

        final String[] cmdParts = command.trim().split("  *");
        if (cmdParts.length == 0) {
            throw new JambelCompileException("No command given");
        }

        return new DescriptiveJambelCommand(compile(jambel, cmdParts), command);
    }

    private static JambelCommand compile(Jambel jambel, String[] cmdParts) throws JambelCompileException {
        switch (cmdParts[0]) {
            case "reset":
                ensureNumberOfParameters("reset", 0, cmdParts.length - 1);
                return compileResetCmd(jambel);
            case "test_connection":
                ensureNumberOfParameters("test_connection", 0, cmdParts.length - 1);
                return compileTestConnectionCmd(jambel);
            case "set_blink_times":
                ensureNumberOfParameters("set_blink_times", 2, cmdParts.length - 1);
                return compileSetBlinkTimesCmd(jambel, cmdParts);
            case "red":
                return compileModuleCmd(jambel.red(), cmdParts);
            case "yellow":
                return compileModuleCmd(jambel.yellow(), cmdParts);
            case "green":
                return compileModuleCmd(jambel.green(), cmdParts);
            case "set_ryg":
                ensureNumberOfParameters("set_ryg", 3, cmdParts.length - 1);
                return compileSetRygCmd(jambel, cmdParts);
        }

        final String command = String.join(" ", cmdParts);
        throw new JambelCompileException("'" + command + "' is no valid jambel command");
    }

    private static void ensureNumberOfParameters(
        String cmdName, int expectedNum, int actualNum) throws JambelCompileException {
        if (actualNum != expectedNum) {
            String message = "Command '" + cmdName + "' is expected to have ";
            switch (expectedNum) {
                case 0: message = message + "no parameters"; break;
                case 1: message = message + "one parameter"; break;
                default: message = message + expectedNum + " parameters";
            }
            throw new JambelCompileException(message);
        }
    }

    private static JambelCommand compileResetCmd(Jambel jambel) {
        return jambel::reset;
    }

    private static JambelCommand compileTestConnectionCmd(Jambel jambel) {
        return jambel::testConnection;
    }

    private static JambelCommand compileSetBlinkTimesCmd(Jambel jambel, String[] cmdParts)
        throws JambelCompileException {
        if (cmdParts.length != 3) {
            throw new JambelCompileException(
                "jambel-command 'set_blink_times' requires exactly two arguments");
        }

        try {
            final int onTimeMsec = Integer.parseInt(cmdParts[1]);
            final int offTimeMsec = Integer.parseInt(cmdParts[2]);

            return () -> jambel.setDefaultBlinkTimes(onTimeMsec, offTimeMsec);
        } catch (Exception e) {
            throw new JambelCompileException(
                String.format("Failed to parse on/off times for set_blink_times ('%s')", e.getMessage()));
        }
    }

    private static JambelCommand compileModuleCmd(
        JambelModule module, String[] cmdParts) throws JambelCompileException {
        if (cmdParts.length < 2) {
            throw new JambelCompileException(
                "jambel-command '" + cmdParts[0] + "' requires another parameter");
        }

        switch (cmdParts[1]) {
            case "on": return module::on;
            case "on_for":
                ensureNumberOfParameters(cmdParts[0] + " on_for", 1,
                    cmdParts.length - 2);
                return compileOnForCmd(module, cmdParts);
            case "off": return module::off;
            case "blink": return module::blink;
            case "blink_inverse": return module::blinkInverse;
            case "flash": return module::flash;
            case "set_on_off_times":
                ensureNumberOfParameters(cmdParts[0] + " set_on_off_times", 2,
                    cmdParts.length - 2);
                return compileSetBlinkTimesCmd(module, cmdParts);
        }

        final String command = String.join(" ", cmdParts);
        throw new JambelCompileException("'" + command + "' is no valid jambel command");
    }

    private static JambelCommand compileOnForCmd(JambelModule module, String[] cmdParts)
        throws JambelCompileException {
        try {
            final int onTimeMsec = Integer.parseInt(cmdParts[2]);

            return () -> module.on(onTimeMsec);
        } catch (Exception e) {
            throw new JambelCompileException(String.format(
                "Failed to parse on duration for '%s on_for' (%s)", cmdParts[0], e.getMessage()));
        }
    }

    private static JambelCommand compileSetBlinkTimesCmd(JambelModule module, String[] cmdParts)
        throws JambelCompileException {
        try {
            final int onTimeMsec = Integer.parseInt(cmdParts[2]);
            final int offTimeMsec = Integer.parseInt(cmdParts[3]);

            return () -> module.setBlinkTimes(onTimeMsec, offTimeMsec);
        } catch (Exception e) {
            throw new JambelCompileException(String.format(
                "Failed to parse on/off times for '%s set_blink_times' (%s)", cmdParts[0], e.getMessage()));
        }
    }

    private static JambelCommand compileSetRygCmd(Jambel jambel, String[] cmdParts)
    throws JambelCompileException {
        try {
            final Jambel.LightStatus redMode = interpretStatus(cmdParts[1]);
            final Jambel.LightStatus yellowMode = interpretStatus(cmdParts[2]);
            final Jambel.LightStatus greenMode = interpretStatus(cmdParts[3]);

            return () -> jambel.setAllLights(redMode, yellowMode, greenMode);
        } catch (Exception e) {
            throw new JambelCompileException(String.format(
                "Failed to parse light modes for 'set_ryg' (%s)", e.getMessage()));
        }
    }

    private static Jambel.LightStatus interpretStatus(String status) {
        final String upperCaseStatus = status.toUpperCase();
        return Jambel.LightStatus.valueOf(upperCaseStatus);
    }
}
