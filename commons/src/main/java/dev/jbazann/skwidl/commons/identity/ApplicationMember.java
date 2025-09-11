package dev.jbazann.skwidl.commons.identity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.jbazann.skwidl.commons.async.transactions.entities.TransactionQuorum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Represents the role a given artifact has in the distributed application.
 * Services that make use of certain generic mechanisms from the 'commons' module
 * must declare exactly one Spring bean of this type. Beans defined in different
 * instances of the same service must be equal.
 * <br>
 * Instances of this class may also be used to represent other artifacts,
 * as seen in {@link TransactionQuorum}.
 *
 * @param id A unique ID for each different service. The same for
 *           replicas of the same service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(chain = true, fluent = true)
@ToString
public class ApplicationMember {

    @NotBlank
    String id;

}
