package dev.jbazann.skwidl.commons.logging;

public class LoggerFactory {

    public static Logger get(Class<?> clazz) {
        return new Logger(org.slf4j.LoggerFactory.getLogger(clazz));
    }

}
