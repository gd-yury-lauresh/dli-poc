package com.github.yarnd.dli.provider;

import com.github.yarnd.dli.core.LogItem;
import com.github.yarnd.dli.provider.filestub.StubFileProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class StubFileProviderTest {

    @Test
    public void happyPathTest() throws IOException {
        StubFileProvider provider = new StubFileProvider();
        Collection<LogItem> allLogItems = new LinkedList<>();
        Collection<LogItem> logItems;
        while ((logItems = provider.retrieveLogItems()) != null) {
            allLogItems.addAll(logItems);
        }

        Assert.assertTrue(allLogItems.size() == 10);
    }
}
