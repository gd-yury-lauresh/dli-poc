package com.github.yarnd.dli.core.statemachine;

import com.github.yarnd.dli.core.LogItem;

import java.util.Collection;

public class ProcessDefinition {
    /**
     * Definition of the trigger to for the process instance
     */
    private ProcessTrigger trigger;

    private String processName;

    private Collection<String> states;

    private long timeout;

    private String startState;

    private String endState;

    private Collection<StateTransitionRule> transitionRules;

    public ProcessDefinition(
            String processName,
            ProcessTrigger trigger,
            long timeout,
            String startState,
            String endState,
            Collection<String> states,
            Collection<StateTransitionRule> transitionRules) {
        this.trigger = trigger;
        this.processName = processName;
        this.states = states;
        this.timeout = timeout;
        this.startState = startState;
        this.endState = endState;
        this.transitionRules = transitionRules;
    }

    public ProcessTrigger getTrigger() {
        return trigger;
    }

    public String getProcessName() {
        return processName;
    }

    public String getStartState() {
        return startState;
    }

    public String getEndState() {
        return endState;
    }

    public Collection<StateTransitionRule> getTransitionRules() {
        return transitionRules;
    }

    public long getTimeout() {
        return timeout;
    }

    public Collection<String> getStates() {
        return states;
    }

    public boolean isTriggered(LogItem logItem) {
        return trigger.isTriggered(logItem);
    }

    public ProcessInstance forkInstance(LogItem logItem) {
        return new ProcessInstance(this, logItem.getSessionId());
    }
}
