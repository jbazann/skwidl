package com.jbazann.orders;

import com.jbazann.orders.mocks.CustomersRemoteServiceMock;
import com.jbazann.orders.order.dto.OrderDTO;
import com.jbazann.orders.order.dto.StatusUpdateDTO;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;
import com.jbazann.orders.order.services.CustomersRemoteServiceInterface;
import com.jbazann.orders.order.OrderRepository;
import com.jbazann.orders.testdata.StandardDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static com.jbazann.orders.testdata.StandardDataset.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(BasicMockMvcTestConfiguration.class)
class OrdersApplicationTests {

	private final WebTestClient webClient;
	private final OrderRepository orderRepository;

	/**
	 * This is only a field because @TestBean doesn't work on methods.
	 */
	@TestBean(enforceOverride = true)
	private CustomersRemoteServiceInterface customersRemoteServiceMock;

	public static CustomersRemoteServiceInterface customersRemoteServiceMock() {
		return new CustomersRemoteServiceMock();
	}

	@Autowired
	OrdersApplicationTests(WebTestClient webClient, OrderRepository orderRepository) {
        this.webClient = webClient;
        this.orderRepository = orderRepository;
    }

	@BeforeEach
	void clearMongoStoredData() {
		orderRepository.deleteAll();
		resetData();
		orderRepository.saveAll(DATA);
	}

	@Test
	void getOrderById() {
		final UUID id = PERSISTED_LIST_FROM_CUSTOMER.orderId();
		webClient.get().uri("/orders/{id}",id)
				.exchange()
				.expectStatus().isOk()
				.expectBody(OrderDTO.class)
				.isEqualTo(Objects.requireNonNull(PERSISTED_LIST_FROM_CUSTOMER.asOrderDTO()));
	}

	@Test
	void getCustomerOrders() {
		final UUID id = PERSISTED_LIST_FROM_CUSTOMER.customerId();
		List<OrderDTO> expected = DATA.stream()
				.filter(order -> order.customer().equals(id))
				.map(Order::toDto)
				.toList();
		webClient.get().uri("/customer/{id}", id)
				.exchange()
				.expectStatus().isOk()
				.expectBody(List.class)
				.isEqualTo(expected);
	}

	@Test
	void newOrderInvalid() {
		Stream.of(
				NO_CUSTOMER,
				NO_DETAIL,
				NO_SITE,
				EMPTY_DETAIL,
				NO_USER
		).map(StandardDataset::asNewOrderDTO).forEach(order -> {
			webClient.post().uri("/order")
					.bodyValue(order)
					.exchange()
					.expectStatus().is4xxClientError();
		});
	}

	@Test
	void newOrder() {
		webClient.post().uri("/order")
				.bodyValue(NEW_VALID.asNewOrderDTO())
				.exchange()
				.expectStatus().isCreated()
				.expectBody(OrderDTO.class)
				.value(response -> {
					assertNotNull(response.id());
					assertEquals(response, NEW_VALID.asOrderDTO().id(response.id()));
				});
	}

	@Test
	void updateInvalid() {
		StatusUpdateDTO update = new StatusUpdateDTO(StatusHistory.Status.DELIVERED, "Some valid detail.");
		webClient.put().uri("/order/{id}", NOT_FOUND.orderId())
				.bodyValue(update)
				.exchange()
				.expectStatus().isNotFound();

	}

}
