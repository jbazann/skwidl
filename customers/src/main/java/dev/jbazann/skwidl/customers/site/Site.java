package dev.jbazann.skwidl.customers.site;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.customers.site.dto.SiteDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
@Entity
@Table(name = "site", schema = "site")
@Builder
public class Site {

    @Id
    @NotNull
    private UUID id;
    @NotNull @NotEmpty
    private String address;
    @NotEmpty
    private String coordinates;
    @Min(0)
    private BigDecimal budget;
    @Enumerated(EnumType.STRING)
    @NotNull
    private SiteStatus status;
    @NotNull
    private UUID customer;

    public boolean canTransitionTo(@NotNull SiteStatus newStatus) {
        return status.equals(SiteStatus.PENDING) && newStatus.equals(SiteStatus.ACTIVE)
                ||
                status.equals(SiteStatus.ACTIVE) && newStatus.equals(SiteStatus.FINISHED);
    }

    public enum SiteStatus{
        ACTIVE, PENDING, FINISHED
    }

    public SiteDTO toDto() {
        return new SiteDTO(id, address, coordinates, budget, status, customer);
    }

}
