package dev.jbazann.skwidl.customers.customer;

import dev.jbazann.skwidl.customers.customer.dto.CustomerDTO;
import dev.jbazann.skwidl.customers.customer.dto.EditableFieldsDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> getCustomer(@RequestParam("id") UUID id,
                                                         @RequestParam("email") String email,
                                                         @RequestParam("name") String name,
                                                         @RequestParam("cuit") String cuit) {
        if (id == null && email == null && name == null && cuit == null) {
            throw new IllegalArgumentException("Must provide at least one of: id, email, name, cuit.");
        }
        return ResponseEntity.ok(
                customerService.findCustomersByExample(
                        new Customer().id(id).email(email).name(name).cuit(cuit)
                ).stream().map(Customer::toDto).toList()
        );
    }

    @PostMapping()
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody @NotNull CustomerDTO customer) {
        customer.id(customerService.generateCustomerId()); // TODO logic in controller
        return ResponseEntity.ok(customerService.newCustomer(customer.toEntity()).toDto());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateCustomer(@PathVariable("id") UUID id,
                                                      @RequestBody @NotNull EditableFieldsDTO update)
    {
        customerService.updateCustomer(id, update);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/allowed-user")
    public ResponseEntity<Void> addAllowedUser(@PathVariable("id") UUID customerId,
                                                      @RequestParam("id") UUID userId) {
        customerService.addAllowedUser(customerId, userId);
        return ResponseEntity.ok().build();
    }

}