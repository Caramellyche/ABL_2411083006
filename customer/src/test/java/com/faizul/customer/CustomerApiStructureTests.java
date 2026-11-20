package com.faizul.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Memastikan customer service sekarang melayani Pelanggan:
 * - /api/customer tersedia
 * - /api/pelanggan lama sudah dihapus
 */
@SpringBootTest
@AutoConfigureMockMvc
class CustomerApiStructureTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void customerEndpointTersedia() throws Exception {
		mockMvc.perform(get("/api/customer")).andExpect(status().isOk());
	}

	@Test
	void pelangganEndpointLamaSudahDihapus() throws Exception {
		mockMvc.perform(get("/api/pelanggan")).andExpect(status().isNotFound());
	}
}
