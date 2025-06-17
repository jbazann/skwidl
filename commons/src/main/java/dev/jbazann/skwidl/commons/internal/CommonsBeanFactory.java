package dev.jbazann.skwidl.commons.internal;

import dev.jbazann.skwidl.commons.exceptions.CommonsInternalException;
import dev.jbazann.skwidl.commons.utils.FunStuff;
import org.springframework.beans.factory.ObjectProvider;

public class CommonsBeanFactory <B> {

    private final ObjectProvider<B> provider;
    private Object[] leadingArgs;

    public CommonsBeanFactory(ObjectProvider<B> provider, Object... args) {
        this.provider = provider;
        this.leadingArgs = args;
    }

    /** @noinspection unchecked */
    public <T> T create(Object... args) {
        try {
            if (leadingArgs.length == args.length && args.length == 0) {
                return (T) provider.getObject();
            }
            return (T) provider.getObject(FunStuff.append(leadingArgs,args));
        } catch (ClassCastException e) {
            throw new CommonsInternalException(e);
        }
    }

    /** @noinspection UnusedReturnValue */
    public CommonsBeanFactory<B> leadingArgs(Object... args) {
        this.leadingArgs = args;
        return this;
    }

}
