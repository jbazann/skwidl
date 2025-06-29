package dev.jbazann.skwidl.customers.customer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.customers.customer.dto.CustomerDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
@Entity
@Table(name = "customer", schema = "customer")
public class Customer {

    @Id
    @NotNull
    private UUID id;
    @NotNull @Length(max = 511)
    private String name;
    @NotNull @Email @Length(max = 1023)
    private String email;
    @NotNull @Length(max = 15)
    private String cuit;
    @NotNull @Min(0)
    @Column(name = "max_debt")
    private BigDecimal maxDebt;
    @NotNull // TODO validator(s)
    private BigDecimal budget;
    @NotNull @Min(0)
    @Column(name = "max_active_sites")
    private Integer maxActiveSites;
    @ElementCollection
    @NotNull
    @Column(name = "allowed_users")
    @JoinTable(name = "customer_allowed_users")
    private List<@NotNull UUID> allowedUsers;
    @ElementCollection
    @NotNull
    @Column(name = "active_sites")
    @JoinTable(name = "customer_active_sites")
    private List<@NotNull UUID> activeSites;
    @NotNull @Min(0)
    @Column(name = "pending_sites")
    private Integer pendingSites;

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @ToString
    public static class DefaultValues {
        @Value("${jbazann.customers.maxDebt}")
        private BigDecimal maxDebt;
        @Value("${jbazann.customers.maxActiveSites}")
        private Integer maxActiveSites;
    }

    public CustomerDTO toDto() {
        return new CustomerDTO(id, name, email, cuit, maxDebt, budget, maxActiveSites, allowedUsers, activeSites, pendingSites);
    }

    /**
     * Increment the {@link Customer#pendingSites} counter by 1.
     * @return this;
     */
    public Customer countPendingSite() {
        pendingSites += 1;
        return this;
    }

    /**
     * If possible, decrement the {@link Customer#pendingSites} counter by 1.
     * Do nothing otherwise.
     * @return this;
     */
    public Customer removePendingSite() {
        if (pendingSites > 0) {
            pendingSites -= 1;
        }
        return this;
    }

    /**
     * Adds a user ID to the {@link Customer#allowedUsers} list if it's not already present.
     * Does nothing otherwise.
     * @param userId a user ID that is presumed to be valid.
     * @return this.
     */
    public Customer addAllowedUser(@NotNull UUID userId) {
        if(!allowedUsers.contains(userId)) {
            allowedUsers.add(userId);
        }
        return this;
    }

    /**
     * Removes the first occurrence of a user ID from the {@link Customer#allowedUsers} list.
     * @param userId a user ID that may or may not be on the list.
     * @return this.
     */
    public Customer removeAllowedUser(@NotNull UUID userId) {
        allowedUsers.remove(userId);
        return this;
    }

    /**
     * Adds a site ID to the {@link Customer#activeSites} list if it's not already present.
     * Does nothing otherwise.
     * @param siteId a site ID that is presumed to be valid.
     * @return this.
     */
    public Customer addActiveSite(@NotNull UUID siteId) {
        if(!activeSites.contains(siteId)) {
            activeSites.add(siteId);
        }
        return this;
    }

    /**
     * Removes the first occurrence of a site ID from the {@link Customer#activeSites} list.
     * @param siteId a site ID that may or may not be on the list.
     * @return this.
     */
    public Customer removeActiveSite(@NotNull UUID siteId) {
        activeSites.remove(siteId);
        return this;
    }

    /**
     * If possible, decreases budget by the requested amount.
     * @param amount how much to decrease this customer's budget.
     * @return true if billing succeeded, false when credit is not enough.
     */
    public boolean bill(@NotNull @Min(0) BigDecimal amount) {
        BigDecimal afterBilling = maxDebt.add(budget).subtract(amount);
        if (afterBilling.compareTo(BigDecimal.ZERO) < 0) return false;
        budget = budget.subtract(amount);
        return true;
    }

    /**
     * Increase budget by the requested amount.
     * @param amount how much to increase this customer's budget.
     * @return this.
     */
    public Customer credit(@NotNull @Min(0) BigDecimal amount) {
        budget = budget.add(amount);
        return this;
    }

}
