package com.github.yarnd.dli.runner;

import com.github.yarnd.dli.core.LogItem;
import com.github.yarnd.dli.core.ProcessDefinitionReader;
import com.github.yarnd.dli.core.ProcessOrchestrator;
import com.github.yarnd.dli.core.statemachine.ProcessDefinition;
import com.github.yarnd.dli.provider.filestub.StubFileProvider;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Collection;

public class PoCRunner {
    public static void main(String[] args) throws IOException, ParseException {
        Collection<ProcessDefinition> processDefinitions = ProcessDefinitionReader.readProcessDefinitions();

        ProcessOrchestrator processOrchestrator = new ProcessOrchestrator(processDefinitions);

        StubFileProvider logProvider = new StubFileProvider();

        Collection<LogItem> logItems;
        while ((logItems = logProvider.retrieveLogItems()) != null) {
            for (LogItem logItem : logItems) {
                processOrchestrator.processLogItem(logItem);
            }
        }
    }
}
