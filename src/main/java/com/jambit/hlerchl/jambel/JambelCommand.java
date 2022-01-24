package com.jambit.hlerchl.jambel;

import com.jambit.hlerchl.jambel.exceptions.JambelException;

/**
 * Interface for a closure object for executing a specific command.
 * <p>
 * Instances of JambelCommand are created by {@link JambelFactory#compileCommand}.
 * See there for information on which commands are available and how they are
 * built.
 * </p>
 */
public interface JambelCommand {
    /**
     * Execute a specific command on a specific {@link Jambel}.
     * <p>
     * JambelCommands can be executed multiple times. They might alter the
     * state of the bound jambel. A caller of this method doesn't need to
     * know anything about jambels.
     * <p>
     * If the command succeeds, nothing is returned. If the command fails
     * an exception will inform the caller.
     *
     * @throws JambelException thrown in case the jambel couldn't be reached
     *      or behaved unexpectedly
     */
    void execute() throws JambelException;
}
