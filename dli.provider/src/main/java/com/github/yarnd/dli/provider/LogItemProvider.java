package com.github.yarnd.dli.provider;

import com.github.yarnd.dli.core.LogItem;

import java.util.Collection;

public interface LogItemProvider {

    /**
     * Retrieves log items from the source, which currently available. Retrieved items will be removed from the source
     *
     * @return Log items available or null if source ended (if source can be ended)
     */
    Collection<LogItem> retrieveLogItems() throws InterruptedException;
}
