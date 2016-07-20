package com.github.yarnd.dli.core.statemachine;

import com.github.yarnd.dli.core.LogItem;

import java.util.Collection;

public class StateTransitionRule {
    private Collection<String> fromStates;
    private String toState;
    private String trigger;

    public StateTransitionRule(Collection<String> fromStates, String toState, String trigger) {
        this.fromStates = fromStates;
        this.toState = toState;
        this.trigger = trigger;
    }

    /**
     * Checks, do we need to make transition
     *
     * @param logItem Log item to analyze
     * @param currentState Current state
     * @return New state or null, if no transition required
     */
    public String checkTransition(LogItem logItem, String currentState) {
        return fromStates.contains(currentState) && logItem.getMessage().contains(trigger) ? toState : null;
    }
}
