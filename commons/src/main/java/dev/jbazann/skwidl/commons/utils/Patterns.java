package dev.jbazann.skwidl.commons.utils;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Patterns {

    public static final Predicate<String> UUIDPattern = Pattern
            .compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$") // perdón
            .asMatchPredicate();

}
