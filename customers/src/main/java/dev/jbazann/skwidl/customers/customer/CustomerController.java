package dev.jbazann.skwidl.customers.customer;

import dev.jbazann.skwidl.customers.customer.dto.CustomerDTO;
import dev.jbazann.skwidl.customers.customer.dto.EditableFieldsDTO;
import dev.jbazann.skwidl.customers.customer.dto.NewCustomerDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerDTO> getCustomer(@RequestParam("id") UUID id,
                                                         @RequestParam("email") String email,
                                                         @RequestParam("name") String name,
                                                         @RequestParam("cuit") String cuit) {
        if (id == null && email == null && name == null && cuit == null) {
            throw new IllegalArgumentException("Must provide at least one of: id, email, name, cuit.");
        }
        return customerService.findCustomersByExample(
                new Customer().id(id).email(email).name(name).cuit(cuit)
        ).stream().map(Customer::toDto).toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@RequestBody @NotNull NewCustomerDTO customer) {
        return customerService.newCustomer(customer).toDto();
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCustomer(@PathVariable("id") UUID id,
                                                      @RequestBody @NotNull EditableFieldsDTO update)
    {
        customerService.updateCustomer(id, update);
    }

    @PostMapping("/{id}/user")
    @ResponseStatus(HttpStatus.OK)
    public void addAllowedUser(@PathVariable("id") UUID customerId,
                                                      @RequestParam("id") UUID userId) {
        customerService.addAllowedUser(customerId, userId);
    }

}