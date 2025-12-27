package com.microservice_project.productService;

import com.microservice_project.productService.dto.ProductRequest;
import com.microservice_project.productService.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import tools.jackson.databind.ObjectMapper;


import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

 	@Container
	static MongoDBContainer mongoDBContainer=new MongoDBContainer("mongo:4.4.2");

	 @Autowired
	private MockMvc mockMvc;

	 @Autowired
	 private ObjectMapper objectMapper;

	 @Autowired
	 private ProductRepository productRepository;



	 @DynamicPropertySource
	 static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry){
		 dymDynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	 }

	@BeforeEach
	void cleanDatabase() {
		productRepository.deleteAll();
	}

	@DynamicPropertySource
	static void setProperties2(DynamicPropertyRegistry registry) {
		String uri = mongoDBContainer.getReplicaSetUrl();
		System.out.println(">>> USING MONGODB URI: " + uri);
		registry.add("spring.data.mongodb.uri", () -> uri);
	}




	@Test
	void shouldCreateProduct() throws Exception {
		Thread.sleep(20000);
		 ProductRequest productRequest=getProductRequest();
		 String productRequestString=objectMapper.writeValueAsString(productRequest);

	mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
			.contentType(String.valueOf(MediaType.APPLICATION_JSON))
			.content(productRequestString)
			)
			.andExpect(status().isCreated());
        Assertions.assertEquals(1, productRepository.findAll().size());
	}

	private ProductRequest getProductRequest() {
		 return ProductRequest.builder()
				 .name("IPhone 13")
				 .description("IPhone 13")
				 .price(BigDecimal.valueOf(30000))
				 .build();
	}

}
