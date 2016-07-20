package com.github.yarnd.dli.core.statemachine;

import com.github.yarnd.dli.core.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessInstance {

    private static Logger logger = LoggerFactory.getLogger(ProcessInstance.class);

    private ProcessDefinition processDefinition;

    private String sessionId;

    private String currentState;

    private long timeoutTime;

    public boolean processLogItem(LogItem logItem) {
        if (!this.sessionId.equals(logItem.getSessionId())) {
            logger.warn("Wrong sessionId passed to process instance, instance has {}, arrived {}", this.sessionId, logItem.getSessionId());
            return false;
        }

        boolean transitionTriggered = false;

        for (StateTransitionRule transitionRule : processDefinition.getTransitionRules()) {
            String newState;

            if ((newState = transitionRule.checkTransition(logItem, this.currentState)) != null) {
                logger.debug("Process {} moved from {} to {} for sessionId {}", processDefinition.getProcessName(), currentState, newState, logItem.getSessionId());
                this.moveToState(newState);
                transitionTriggered = true;
            }
        }

        return transitionTriggered;
    }

    private void moveToState(String newState) {
        if (processDefinition.getStates().contains(newState)) {
            currentState = newState;
        } else {
            logger.error("State {} is invalid for process {}, rejected", newState, processDefinition.getProcessName());
        }
    }

    public ProcessInstance(ProcessDefinition processDefinition, String sessionId) {
        this.processDefinition = processDefinition;
        this.sessionId = sessionId;
        this.currentState = processDefinition.getStartState();
        this.timeoutTime = System.currentTimeMillis() + processDefinition.getTimeout() * 1000;
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getCurrentState() {
        return currentState;
    }

    public boolean isFinished() {
        return this.currentState.equals(this.processDefinition.getEndState());
    }

    public boolean isTimedOut() {
        return System.currentTimeMillis() > timeoutTime;
    }
}
