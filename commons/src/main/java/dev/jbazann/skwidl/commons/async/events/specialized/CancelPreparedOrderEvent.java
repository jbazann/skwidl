package dev.jbazann.skwidl.commons.async.events.specialized;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data()
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
@EqualsAndHashCode(callSuper = true)
public class CancelPreparedOrderEvent extends DomainEvent {

    @NotNull
    private UUID orderId;
    @NotNull
    private UUID customerId;
    @NotNull @Min(0)
    private BigDecimal returnedFunds;
    @NotNull @NotEmpty
    private Map<@NotNull UUID, @NotNull @Min(0) Integer> returnedStock;

    @Override
    protected DomainEvent copy() {
        return new CancelPreparedOrderEvent()
                .orderId(orderId)
                .customerId(customerId)
                .returnedFunds(returnedFunds)
                .returnedStock(returnedStock);
    }

}
