package com.github.yarnd.dli.core.statemachine;

import com.github.yarnd.dli.core.LogItem;

public class ProcessTrigger {

    String triggerLine;

    public ProcessTrigger(String triggerLine) {
        this.triggerLine = triggerLine;
    }

    public boolean isTriggered(LogItem logItem) {
        return logItem.getMessage().contains(triggerLine);
    }
}
