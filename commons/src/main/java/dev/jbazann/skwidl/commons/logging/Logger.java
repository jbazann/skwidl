package dev.jbazann.skwidl.commons.logging;

public class Logger {

    private final org.slf4j.Logger logger;
    private static final StackWalker stackWalker = StackWalker.getInstance();

    protected Logger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    /**
     * @deprecated
     */
    public void method(StackTraceElement[] stack, Object... params) {
        if (!logger.isDebugEnabled()) return;
        String p = paramsString(params);
        logger.debug(String.format(
                "Call %s with params %s",
                stack.length == 0 ? "UNKNOWN" : stack[0].getMethodName(),
                p
        ));
    }

    public void method(Object... params) {
        if (!logger.isDebugEnabled()) return;
        String p = paramsString(params);
        String caller = stackWalker.walk(s -> s
                .skip(1)
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName)
                .orElse("UNKNOWN"));
        logger.debug(String.format(
                "Call %s with params %s",
                caller,
                p
        ));
    }

    /**
     * Debug logging utility intended to provide more
     * granular observability than what is already covered
     * by AOP logging.
     * @param val the calling method's return value.
     * @return the logged value.
     * @apiNote for transparency, this method's return value is
     * to be ignored, unless doing so requires the creation of a
     * variable, or writing more additional lines than the expected
     * return statement.
     * For example, to log the result of:
     * <pre>{@code
     *     O m(P p) {
     *         ...
     *         return foo(p);
     *     }
     * }</pre>
     * The preferred form is:
     * <pre>{@code
     *     O m(P p) {
     *         ...
     *         return log.result(foo(p));
     *     }
     * }</pre>
     * Over:
     * <pre>{@code
     *     O m(P p) {
     *         ...
     *         O r = foo(p);
     *         log.result(r);
     *         return r;
     *     }
     * }</pre>
     * This is also a valid pattern with fluent APIs or other inline
     * scenarios that simply wrap an unreferenced value.
     * But if the reference is needed for more than this
     * intermediate call, the last form is preferred.
     */
    public <T> T result(T val) {
        if (!logger.isDebugEnabled()) return val;
        String caller = stackWalker.walk(s -> s
                .skip(1)
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName)
                .orElse("UNKNOWN"));
        logger.debug(String.format(
                "Return %s from %s",
                val,
                caller
        ));
        return val;
    }

    /**
     * Debug logging utility intended to provide more
     * granular observability than what is already covered
     * by AOP logging.
     * @param val the calling method's return value.
     * @param identifier a return point identifier, for
     *                   methods with multiple return statements.
     * @return the logged value.
     * @apiNote for transparency, this method's return value is
     * to be ignored, unless doing so requires the creation of a
     * variable, or writing more additional lines than the expected
     * return statement.
     * For example, to log the result of:
     * <pre>{@code
     *     O m(P p) {
     *         ...
     *         return foo(p);
     *     }
     * }</pre>
     * The preferred form is:
     * <pre>{@code
     *     O m(P p) {
     *         ...
     *         return log.result(foo(p));
     *     }
     * }</pre>
     * Over:
     * <pre>{@code
     *     O m(P p) {
     *         ...
     *         O r = foo(p);
     *         log.result(r);
     *         return r;
     *     }
     * }</pre>
     * This is also a valid pattern with fluent APIs or other inline
     * scenarios that simply wrap an unreferenced value.
     * But if the reference is needed for more than this
     * intermediate call, the last form is preferred.
     */
    public <T> T result(T val, String identifier) {
        if (!logger.isDebugEnabled()) return val;
        String caller = stackWalker.walk(s -> s
                .skip(1)
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName)
                .orElse("UNKNOWN"));
        logger.debug(String.format(
                "Return %s from %s at %s.",
                val,
                caller,
                identifier
        ));
        return val;
    }

    public final void debug(String message) {
        logger.debug(message);
    }

    public final void debug(String format, Object... params) {
        logger.debug(format, params);
    }

    /**
     * Convenience exposing {@link org.slf4j.Logger#info}
     * for one-off use cases that aren't better solved
     * by aspect-based logging. This method should only
     * be used when avoiding it is unviable.
     * @param format the format string.
     * @param params a list of arguments.
     */
    public final void info(String format, Object... params) {
        logger.info(format, params);
    }

    private String paramsString(Object... params) {
        StringBuilder p = new StringBuilder("[");
        for (Object param : params) {
            p.append(param).append(";");
        }
        if (p.charAt(p.length() - 1) == ';') p.deleteCharAt(p.length() - 1);
        p.append("]");
        return p.toString();
    }

}
