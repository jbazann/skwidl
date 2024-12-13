package com.jbazann.commons.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class TimeProvider {

    /**
     * @return {@link java.time.LocalDateTime#now()} with truncated precision to account
     * for the most limiting (de-)serialization format used by the application. This
     * guarantees {@link Object#equals(Object)} behavior without additional
     * configuration.
     */
    public static LocalDateTime localDateTimeNow() {
        // Storing to MongoDB loses precision up to milliseconds.
        // This class/method is as far as I'm willing to go to deal with this issue.
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }

}
