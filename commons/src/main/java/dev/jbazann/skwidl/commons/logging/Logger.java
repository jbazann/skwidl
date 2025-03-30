package dev.jbazann.skwidl.commons.logging;

import java.lang.reflect.Method;

public class Logger {

    private final org.slf4j.Logger logger;

    protected Logger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public void method(StackTraceElement[] stack, Object... params) {
        StringBuilder p = new StringBuilder("[");
        for (Object param : params) {
            p.append(param).append(";");
        }
        if (p.charAt(p.length() - 1) == ';') p.deleteCharAt(p.length() - 1);
        p.append("]");
        logger.debug(String.format(
                "Call %s with params %s",
                stack.length == 0 ? "UNKNOWN" : stack[0].getMethodName(),
                p
        ));
    }

    public void result(Object val) {
        logger.debug(String.format(
                "Return %s",
                val
        ));
    }

    public void result(Object val, String identifier) {
        logger.debug(String.format(
                "Return %s at %s",
                val
        ));
    }

    public void debug(String format, Object... params) {
        logger.debug(format,params);
    }

}
