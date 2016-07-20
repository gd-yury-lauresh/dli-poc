package com.github.yarnd.dli.core;

import java.util.Date;

public class LogItem {
    private Date date;
    private String sessionId;
    private String message;

    public LogItem(Date date, String sessionId, String message) {
        this.date = (Date) date.clone();
        this.sessionId = sessionId;
        this.message = message;
    }

    public Date getDate() {
        return (Date) date.clone();
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMessage() {
        return message;
    }
}
