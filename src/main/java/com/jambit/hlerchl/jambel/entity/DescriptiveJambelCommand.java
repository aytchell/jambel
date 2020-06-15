package com.jambit.hlerchl.jambel.entity;

import com.jambit.hlerchl.jambel.JambelCommand;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
public class DescriptiveJambelCommand implements JambelCommand {
    @Delegate
    private final JambelCommand impl;
    private final String description;

    @Override
    public String toString() {
        return description;
    }
}
