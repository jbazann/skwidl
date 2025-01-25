package dev.jbazann.skwidl.products;

import dev.jbazann.skwidl.products.category.dto.CategoryDTO;
import dev.jbazann.skwidl.products.product.ProductRepository;
import dev.jbazann.skwidl.products.product.dto.NewProductDTO;
import dev.jbazann.skwidl.products.product.dto.ProductDTO;
import dev.jbazann.skwidl.products.product.testdata.ProductDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static dev.jbazann.skwidl.products.integration.testdata.IntegrationDataset.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProductsApplicationTests {

	private final ProductRepository productRepository;
	private final WebTestClient webClient;

	@Autowired
    ProductsApplicationTests(ProductRepository productRepository, WebTestClient webClient) {
        this.productRepository = productRepository;
        this.webClient = webClient;
    }

	@BeforeEach
	void clearPostgresData() {
		productRepository.deleteAll();
		resetData();
		productRepository.saveAll(ProductDataset.PERSISTED_PRODUCTS);
	}

	@Test
	void createProduct() {
		NewProductDTO dto = ProductDataset.CREATE_PRODUCT.entry().asNewProductDTO();
		ProductDTO result = ProductDataset.CREATE_PRODUCT.entry().asProductDTO();
		webClient.post().uri("/product")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(dto)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isCreated()
				.expectBody(ProductDTO.class)
				.value(p -> assertEquals(result.id(p.id()),p));
	}

	@Test
	void deleteProduct() {
		ProductDTO dto = ProductDataset.PERSISTED.entry().asProductDTO();
		String reason = "A valid deletion reason string, 14 $#$!#$ ! /)#$YT!\"#$%&/()=?\\°|¬;,-_";
		UUID transactionId = webClient.post().uri("/removed/product/{id}", dto.id())
				.contentType(MediaType.TEXT_PLAIN)
				.bodyValue(reason)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody(UUID.class)
				.returnResult()
				.getResponseBody();
		// TODO check published message
		// TODO check transaction completion
	}

    @Test
	void findCategoryByName() {
		String QUERY = PERSISTED_GENERIC.entry().category().name();
		webClient.get().uri("/category/{query}", QUERY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isOk()
				.expectBodyList(CategoryDTO.class)
				.hasSize(1)
				.contains(PERSISTED_GENERIC.asCategoryDTO());
	}



}
