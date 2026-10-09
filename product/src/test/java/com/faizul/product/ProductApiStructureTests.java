package com.faizul.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Memastikan pembagian service product sudah benar:
 * - /api/product tetap ada (Product + JenisProduct tinggal di sini)
 * - /api/pelanggan sudah dipindah ke customer service, jadi harus 404 di sini
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductApiStructureTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void productEndpointMasihAda() throws Exception {
		mockMvc.perform(get("/api/product")).andExpect(status().isOk());
	}

	@Test
	void jenisProductEndpointMasihAda() throws Exception {
		mockMvc.perform(get("/api/product/jenis-product")).andExpect(status().isOk());
	}

	@Test
	void pelangganEndpointSudahTidakAdaDiProduct() throws Exception {
		mockMvc.perform(get("/api/pelanggan")).andExpect(status().isNotFound());
	}
}
