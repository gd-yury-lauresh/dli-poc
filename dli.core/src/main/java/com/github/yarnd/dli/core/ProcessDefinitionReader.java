package com.github.yarnd.dli.core;

import com.github.yarnd.dli.core.statemachine.ProcessDefinition;
import com.github.yarnd.dli.core.statemachine.ProcessTrigger;
import com.github.yarnd.dli.core.statemachine.StateTransitionRule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;

public class ProcessDefinitionReader {

    private static Logger logger = LoggerFactory.getLogger(ProcessDefinitionReader.class);

    private static final String PROCESSES_DEFINITION_FILE = "processes.json";

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_DEFAULT_ENCODING", justification = "Default used")
    public static Collection<ProcessDefinition> readProcessDefinitions() throws IOException, ParseException {

        Collection<ProcessDefinition> processDefinitions = new LinkedList<>();

        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROCESSES_DEFINITION_FILE)));

        JSONArray processes = (JSONArray) jsonObject.get("processes");

        for (Object process : processes) {
            JSONObject processItem = (JSONObject) process;

            String processName = (String) processItem.get("name");
            String startState = (String) processItem.get("start_state");
            String endState = (String) processItem.get("end_state");
            String trigger = (String) processItem.get("trigger");
            Long timeout = (Long)  processItem.get("timeout");

            Collection<String> states = new LinkedList<>();
            for (Object stateObject : (JSONArray) processItem.get("states")) {
                states.add((String) stateObject);
            }

            if (!states.contains(startState) || !states.contains(endState)) {
                logger.error("Process definition malformed for process name {}, start or end state are not in states available");
                continue;
            }

            Collection<StateTransitionRule> rules = new LinkedList<>();
            for (Object ruleObject : (JSONArray) processItem.get("rules")) {
                JSONObject jsonRuleObject = (JSONObject) ruleObject;

                Collection<String> ruleStateFrom = new LinkedList<>();
                for (Object ruleStateFromObject : (JSONArray) jsonRuleObject.get("state_from")) {
                    ruleStateFrom.add((String) ruleStateFromObject);
                }

                String ruleStateTo = (String) jsonRuleObject.get("state_to");
                String ruleTrigger = (String) jsonRuleObject.get("trigger");

                rules.add(new StateTransitionRule(ruleStateFrom,ruleStateTo, ruleTrigger));
            }


            processDefinitions.add(
                    new ProcessDefinition(
                            processName,
                            new ProcessTrigger(trigger),
                            timeout,
                            startState,
                            endState,
                            states,
                            rules
            )
            );


        }

        return processDefinitions;
    }

}
