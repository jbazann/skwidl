package com.jbazann.orders;

import com.jbazann.orders.mocks.CustomersRemoteServiceMock;
import com.jbazann.orders.mocks.ProductsRemoteServiceMock;
import com.jbazann.orders.order.dto.NewOrderDTO;
import com.jbazann.orders.order.dto.OrderDTO;
import com.jbazann.orders.order.dto.StatusUpdateDTO;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;
import com.jbazann.orders.order.services.CustomersRemoteServiceInterface;
import com.jbazann.orders.order.OrderRepository;
import com.jbazann.orders.order.services.ProductsRemoteServiceInterface;
import com.jbazann.orders.testdata.StandardDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.jbazann.orders.testdata.StandardDataset.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(BasicMockMvcTestConfiguration.class)
class OrdersApplicationTests {

	private final WebTestClient webClient;
	private final OrderRepository orderRepository;

	/*
	 * These fields and static factory methods only exist because @TestBean
	 * only works this way. It still appears to be the simplest solution
	 * to replace Beans with mocks.
	 */
	@TestBean(enforceOverride = true)
	private CustomersRemoteServiceInterface customersRemoteServiceMock;
	@TestBean(enforceOverride = true)
	private ProductsRemoteServiceInterface productsRemoteServiceMock;

	public static CustomersRemoteServiceInterface customersRemoteServiceMock() {
		return new CustomersRemoteServiceMock();
	}

	public static ProductsRemoteServiceInterface productsRemoteServiceMock() {
		return new ProductsRemoteServiceMock();
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
		assertTrue(PERSISTED_LIST_FROM_CUSTOMER.hasOrder());
		final UUID ORDER_ID = PERSISTED_LIST_FROM_CUSTOMER.orderId();
		webClient.get().uri("/order/{id}",ORDER_ID)
				.exchange()
				.expectStatus().isOk()
				.expectBody(OrderDTO.class)
				.isEqualTo(PERSISTED_LIST_FROM_CUSTOMER.asOrderDTO());
	}

	@Test
	void getCustomerOrders() {
		assertTrue(PERSISTED_LIST_FROM_CUSTOMER.hasCustomer());
		final UUID CUSTOMER_ID = PERSISTED_LIST_FROM_CUSTOMER.customerId();
		final OrderDTO NOT_EXPECTED = NOT_FOUND.asOrderDTO();
		final List<OrderDTO> EXPECTED = DATA.stream()
				.filter(order -> order.customer().equals(CUSTOMER_ID))
				.map(Order::toDto)
				.toList();
		assertFalse(EXPECTED.isEmpty());
		assertNotEquals(NOT_EXPECTED.customer(), CUSTOMER_ID);
		assertFalse(EXPECTED.contains(NOT_EXPECTED));
		webClient.get().uri("/customer/{id}", CUSTOMER_ID)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isOk()
				.expectBodyList(OrderDTO.class)
				.hasSize(EXPECTED.size())
				.contains(EXPECTED.toArray(new OrderDTO[0]))
				.doesNotContain(NOT_EXPECTED);
	}

	@Test
	void newOrderInvalid() {
		List<StandardDataset> cases = List.of(
				NO_CUSTOMER,
				NO_DETAIL,
				NO_SITE,
				EMPTY_DETAIL,
				NO_USER
		);
		assertTrue(cases.stream().allMatch(StandardDataset::hasOrder));
		cases.stream().map(StandardDataset::asNewOrderDTO).forEach(order -> {
			webClient.post().uri("/order")
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(order)
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().is4xxClientError()
					.expectBody(String.class);
		});
	}

	@Test
	void newOrder() {
		assertTrue(NEW_VALID.hasOrder());
		final NewOrderDTO NEW_ORDER = NEW_VALID.asNewOrderDTO();
		webClient.post().uri("/order")
				.bodyValue(NEW_ORDER)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(OrderDTO.class)
				.value(response -> {
					assertNotNull(response.id());
					assertEquals(NEW_ORDER.customer(), response.customer());
					assertEquals(NEW_ORDER.site(), response.site());
					assertEquals(NEW_ORDER.user(), response.user());
					assertEquals(NEW_ORDER.note(), response.note());
					NEW_ORDER.detail().forEach(detail -> {
						assertTrue(response.detail().stream()
								.anyMatch(d -> Objects.equals(d.product(), detail.product()) &&
										Objects.equals(d.amount(), detail.amount())));
					});
					response.detail().forEach(detail -> {
						assertNotNull(detail.id());
						assertNotNull(detail.totalCost());
						assertNotNull(detail.discount());
						assertNotNull(detail.unitCost());
					});
				});
	}

	@Test
	void updateInvalid() {
		assertTrue(NOT_FOUND.hasOrder());
		final StatusUpdateDTO UPDATE = new StatusUpdateDTO(StatusHistory.Status.DELIVERED, "Some valid detail.");
		webClient.put().uri("/order/{id}", NOT_FOUND.orderId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(UPDATE)
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isNotFound()
				.expectBody(String.class);
	}

}
