package com.github.yarnd.dli.core;

import com.github.yarnd.dli.core.statemachine.ProcessDefinition;
import com.github.yarnd.dli.core.statemachine.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProcessOrchestrator {

    private static Logger logger = LoggerFactory.getLogger(ProcessOrchestrator.class);

    /**
     * key is process name, value is process definition
     */

    private Map<String,ProcessDefinition> processDefinitions;

    /**
     * key is sessionId, value is map of process name : process instance
     */
    private Map<String, Map<String, ProcessInstance>> processInstances = new HashMap<>();

    public ProcessOrchestrator(Collection<ProcessDefinition> processDefinitions) {
        this.processDefinitions = new HashMap<>();

        for (ProcessDefinition processDefinition : processDefinitions) {
            this.processDefinitions.put(processDefinition.getProcessName(), processDefinition);
        }
    }

    // TODO: Rewrite in clear way, add custom signal support
    public void processLogItem(LogItem logItem) {
        // get all session instances, and init, if empty
        Map<String, ProcessInstance> sessionInstances= processInstances.get(logItem.getSessionId());

        if (sessionInstances == null) {
            sessionInstances = new HashMap<>();
            processInstances.put(logItem.getSessionId(),sessionInstances);
        }


        // Fork new instances, if they need to be forked
        Set<String> instanceNamesForSessionId = sessionInstances.keySet();

        // for every existing process definition
        for (ProcessDefinition processDefinition : processDefinitions.values()) {
            // if process is not started for this session, but need to be started
            if (!instanceNamesForSessionId.contains(processDefinition.getProcessName()) && processDefinition.isTriggered(logItem)) {
                logger.debug("Fork process {} on log message {} for sessionId {}",processDefinition.getProcessName(), logItem.getMessage(), logItem.getSessionId());
                ProcessInstance newProcessInstance = processDefinition.forkInstance(logItem);
                sessionInstances.put(processDefinition.getProcessName(), newProcessInstance);
            }
        }


        Iterator<Map.Entry<String,ProcessInstance>> sessionInstancesIterator = sessionInstances.entrySet().iterator();

        while (sessionInstancesIterator.hasNext()) {
            ProcessInstance processInstance = sessionInstancesIterator.next().getValue();
            String processName = processInstance.getProcessDefinition().getProcessName();

            processInstance.processLogItem(logItem);

            if (processInstance.isFinished()) {
                logger.debug("Process instance name {}, sessionId {} finished work with state {}", processName, processInstance.getSessionId(), processInstance.getCurrentState());
                sessionInstancesIterator.remove();
                continue;
            }

            if (processInstance.isTimedOut()) {
                logger.debug("Process instance name {}, sessionId {} TIMED OUT with state {}", processName, processInstance.getSessionId(), processInstance.getCurrentState());
                sessionInstancesIterator.remove();
            }

        }

        // remove empty sessions
        if (processInstances.get(logItem.getSessionId()).size() == 0) {
            processInstances.remove(logItem.getSessionId());
        }

    }






}
