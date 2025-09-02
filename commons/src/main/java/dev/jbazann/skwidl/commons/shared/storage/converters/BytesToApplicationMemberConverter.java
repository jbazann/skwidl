package dev.jbazann.skwidl.commons.shared.storage.converters;

import dev.jbazann.skwidl.commons.identity.ApplicationMember;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@ReadingConverter
public class BytesToApplicationMemberConverter implements Converter<byte[], ApplicationMember> {

    private static Jackson2JsonRedisSerializer<ApplicationMember> serializer = new Jackson2JsonRedisSerializer<>(ApplicationMember.class);

    @Override
    public ApplicationMember convert(byte[] source) {
        return serializer.deserialize(source);
    }
    
}
