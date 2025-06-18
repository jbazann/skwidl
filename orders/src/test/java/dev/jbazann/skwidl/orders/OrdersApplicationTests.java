package dev.jbazann.skwidl.orders;

import dev.jbazann.skwidl.orders.mocks.CustomerServiceMockClient;
import dev.jbazann.skwidl.orders.mocks.ProductServiceMockClient;
import dev.jbazann.skwidl.orders.order.dto.NewOrderDTO;
import dev.jbazann.skwidl.orders.order.dto.OrderDTO;
import dev.jbazann.skwidl.orders.order.dto.StatusUpdateDTO;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;
import dev.jbazann.skwidl.orders.order.services.CustomerServiceClient;
import dev.jbazann.skwidl.orders.order.OrderRepository;
import dev.jbazann.skwidl.orders.order.services.ProductServiceClient;
import dev.jbazann.skwidl.orders.testdata.StandardDataset;
import org.junit.jupiter.api.Assertions;
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
	private CustomerServiceClient customersRemoteServiceMock;
	@TestBean(enforceOverride = true)
	private ProductServiceClient productsRemoteServiceMock;

	public static CustomerServiceClient customersRemoteServiceMock() {
		return new CustomerServiceMockClient();
	}

	public static ProductServiceClient productsRemoteServiceMock() {
		return new ProductServiceMockClient();
	}


	@Autowired
	OrdersApplicationTests(WebTestClient webClient, OrderRepository orderRepository) {
        this.webClient = webClient;
        this.orderRepository = orderRepository;
    }

	@BeforeEach
	void clearMongoStoredData() {
		orderRepository.deleteAll();
		StandardDataset.resetData();
		orderRepository.saveAll(StandardDataset.DATA);
	}

	@Test
	void getOrderById() {
		Assertions.assertTrue(StandardDataset.PERSISTED_LIST_FROM_CUSTOMER.hasOrder());
		final UUID ORDER_ID = StandardDataset.PERSISTED_LIST_FROM_CUSTOMER.orderId();
		webClient.get().uri("/order/{id}",ORDER_ID)
				.exchange()
				.expectStatus().isOk()
				.expectBody(OrderDTO.class)
				.isEqualTo(StandardDataset.PERSISTED_LIST_FROM_CUSTOMER.asOrderDTO());
	}

	@Test
	void getCustomerOrders() {
		Assertions.assertTrue(StandardDataset.PERSISTED_LIST_FROM_CUSTOMER.hasCustomer());
		final UUID CUSTOMER_ID = StandardDataset.PERSISTED_LIST_FROM_CUSTOMER.customerId();
		final OrderDTO NOT_EXPECTED = StandardDataset.NOT_FOUND.asOrderDTO();
		final List<OrderDTO> EXPECTED = StandardDataset.DATA.stream()
				.filter(order -> order.getCustomer().equals(CUSTOMER_ID))
				.map(Order::toDto)
				.toList();
		assertFalse(EXPECTED.isEmpty());
		assertNotEquals(NOT_EXPECTED.getCustomer(), CUSTOMER_ID);
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
				StandardDataset.NO_CUSTOMER,
				StandardDataset.NO_DETAIL,
				StandardDataset.NO_SITE,
				StandardDataset.EMPTY_DETAIL,
				StandardDataset.NO_USER
		);
		assertTrue(cases.stream().allMatch(StandardDataset::hasOrder));
		cases.stream().map(StandardDataset::asNewOrderDTO).forEach(order -> webClient.post().uri("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(order)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class));
	}

	@Test
	void newOrder() {
		Assertions.assertTrue(StandardDataset.NEW_VALID.hasOrder());
		final NewOrderDTO NEW_ORDER = StandardDataset.NEW_VALID.asNewOrderDTO();
		webClient.post().uri("/order")
				.bodyValue(NEW_ORDER)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(OrderDTO.class)
				.value(response -> {
					assertNotNull(response.getId());
					assertEquals(NEW_ORDER.getCustomer(), response.getCustomer());
					assertEquals(NEW_ORDER.getSite(), response.getSite());
					assertEquals(NEW_ORDER.getUser(), response.getUser());
					assertEquals(NEW_ORDER.getNote(), response.getNote());
					NEW_ORDER.getDetail().forEach(detail -> assertTrue(response.getDetail().stream()
                            .anyMatch(d -> Objects.equals(d.getProduct(), detail.getProduct()) &&
                                    Objects.equals(d.getAmount(), detail.getAmount()))));
					response.getDetail().forEach(detail -> {
						assertNotNull(detail.getId());
						assertNotNull(detail.getTotalCost());
						assertNotNull(detail.getDiscount());
						assertNotNull(detail.getUnitCost());
					});
				});
	}

	@Test
	void updateInvalid() {
		Assertions.assertTrue(StandardDataset.NOT_FOUND.hasOrder());
		final StatusUpdateDTO UPDATE = new StatusUpdateDTO(StatusHistory.Status.DELIVERED, "Some valid detail.");
		webClient.put().uri("/order/{id}", StandardDataset.NOT_FOUND.orderId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(UPDATE)
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isNotFound()
				.expectBody(String.class);
	}

}
