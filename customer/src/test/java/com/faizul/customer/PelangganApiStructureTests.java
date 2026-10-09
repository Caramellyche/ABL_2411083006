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
 * - /api/pelanggan tersedia (entity Pelanggan)
 * - /api/customers (entity Customer lama) sudah dihapus
 */
@SpringBootTest
@AutoConfigureMockMvc
class PelangganApiStructureTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void pelangganEndpointTersedia() throws Exception {
		mockMvc.perform(get("/api/pelanggan")).andExpect(status().isOk());
	}

	@Test
	void customersEndpointLamaSudahDihapus() throws Exception {
		mockMvc.perform(get("/api/customers")).andExpect(status().isNotFound());
	}
}
