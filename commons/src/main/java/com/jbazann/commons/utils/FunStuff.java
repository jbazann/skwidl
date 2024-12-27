package com.jbazann.commons.utils;

import java.util.ArrayList;
import java.util.List;
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

}
