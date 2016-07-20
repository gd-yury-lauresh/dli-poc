package com.github.yarnd.dli.core;

import com.github.yarnd.dli.core.statemachine.ProcessDefinition;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public class ProcessDefinitionReaderTest {

    @Test
    public void happyPathTest() throws IOException, ParseException {
        Collection<ProcessDefinition> processDefinitions = ProcessDefinitionReader.readProcessDefinitions();
        Assert.assertTrue(processDefinitions.size()==2);
        ProcessDefinition processDefinition = processDefinitions.iterator().next();
        Assert.assertTrue(processDefinition.getProcessName().equals("CategoryBrowse"));
        Assert.assertTrue(processDefinition.getStates().size()==6);
        Assert.assertTrue(processDefinition.getTransitionRules().iterator().next().checkTransition(new LogItem(new Date(),null,"It was User category browse message"),"PRODUCT_VIEW").equals("CATEGORY_VIEW"));

    }
}
