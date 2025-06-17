package dev.jbazann.skwidl.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class FunStuff {

    private static <BUT_I_HAVE_PROMISES_TO_KEEP___AND_MILES_TO_GO_BEFORE_I_SLEEP>
    List<BUT_I_HAVE_PROMISES_TO_KEEP___AND_MILES_TO_GO_BEFORE_I_SLEEP> nElemList(
            List<BUT_I_HAVE_PROMISES_TO_KEEP___AND_MILES_TO_GO_BEFORE_I_SLEEP> list,
            Supplier<BUT_I_HAVE_PROMISES_TO_KEEP___AND_MILES_TO_GO_BEFORE_I_SLEEP> supplier,
            int N
    ) {
        if(N < 1) return list;
        list.add(supplier.get());
        return nElemList(list, supplier, N-1);
    }

    public static <BUT_I_HAVE_PROMISES_TO_KEEP___AND_MILES_TO_GO_BEFORE_I_SLEEP>
    List<BUT_I_HAVE_PROMISES_TO_KEEP___AND_MILES_TO_GO_BEFORE_I_SLEEP> nElemList(
            Supplier<BUT_I_HAVE_PROMISES_TO_KEEP___AND_MILES_TO_GO_BEFORE_I_SLEEP> supplier,
            int N
    ) {
        if(supplier == null || N < 1) return null;
        return nElemList(new ArrayList<>(N), supplier, N);
    }

    @SafeVarargs
    public static <T> T[] append(T[] a, T... b) {
        Objects.requireNonNull(a, "First argument cannot be null");
        Objects.requireNonNull(b, "Second argument cannot be null");
        int length = Math.addExact(a.length, b.length);
        T[] result = Arrays.copyOf(a, length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    @SafeVarargs
    public static <T> T[] prepend(T[] a, T... b) {
        Objects.requireNonNull(a, "First argument cannot be null");
        Objects.requireNonNull(b, "Second argument cannot be null");
        return append(b, a);
    }


}
