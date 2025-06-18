package dev.jbazann.skwidl.customers.customer;

import dev.jbazann.skwidl.customers.customer.dto.CustomerDTO;
import dev.jbazann.skwidl.customers.customer.dto.EditableFieldsDTO;
import dev.jbazann.skwidl.customers.customer.dto.NewCustomerDTO;
import dev.jbazann.skwidl.customers.customer.exceptions.InsufficientCreditException;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
                new Customer().setId(id).setEmail(email).setName(name).setCuit(cuit)
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

    @GetMapping("/{id}/wallet")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getBudget(@PathVariable("id") UUID customerId) {
        return customerService.getCustomerBudget(customerId);
    }

    @PostMapping(path = "/{id}/wallet", params = "operation=bill")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal bill(@PathVariable("id") UUID customerId, @RequestBody BigDecimal amount) {
        return customerService.bill(customerId, amount);
    }

    @PostMapping(path = "/{id}/wallet", params = "operation=credit")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal credit(@PathVariable("id") UUID customerId, @RequestBody BigDecimal amount) {
        return customerService.credit(customerId, amount);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public BigDecimal insufficientCreditException(InsufficientCreditException exception) {
        return exception.difference();
    }

}