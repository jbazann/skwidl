package dev.jbazann.skwidl.commons.shared.storage.converters;

import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@WritingConverter
public class ApplicationMemberToBytesConverter implements Converter<ApplicationMember, byte[]> {

    private static Jackson2JsonRedisSerializer<ApplicationMember> serializer = new Jackson2JsonRedisSerializer<>(ApplicationMember.class);

    @Override
    public byte[] convert(ApplicationMember source) {
        return serializer.serialize(source);
    }

}
