package com.pandoaspen.mercury.velocity.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class WrappedLogger extends Logger {

    private final org.slf4j.Logger logger;

    public WrappedLogger(org.slf4j.Logger logger) {
        super(logger.getName(), null);
        this.logger = logger;
    }

    @Override
    public void log(LogRecord record) {
        if (record.getLevel() == Level.INFO) {
            logger.info(record.getMessage());
        } else if (record.getLevel() == Level.WARNING) {
            logger.warn(record.getMessage());
        } else if (record.getLevel() == Level.SEVERE) {
            logger.error(record.getMessage());
        } else if (record.getLevel() == Level.FINE) {
            logger.debug(record.getMessage());
        } else if (record.getLevel() == Level.FINER) {
            logger.debug(record.getMessage());
        } else if (record.getLevel() == Level.FINEST) {
            logger.trace(record.getMessage());
        } else if (record.getLevel() == Level.CONFIG) {
            logger.debug(record.getMessage());
        }
    }

    @Override
    public boolean isLoggable(Level level) {
        return true;
    }
}
