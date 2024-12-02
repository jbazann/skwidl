package com.jbazann.orders.testdata.entities;

import com.jbazann.orders.order.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class StandardDatasetData {

    private Order order;
    private List<Order> orders;
    private CustomerMock customer;

}