package com.faizul.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.HttpStatus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Memastikan pembagian service product sudah benar:
 * - jenis product disimpan sebagai field di Product; CRUD JenisProduct dihapus
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
	void jenisProductEndpointSudahDihapus() throws Exception {
		mockMvc.perform(get("/api/product/jenis-product")).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	void productMenyimpanJenisProductSebagaiTeks() throws Exception {
		mockMvc.perform(post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"nama":"Kopi","harga":15000,"deskripsi":"Kopi hitam","jenisProduct":"Minuman"}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.jenisProduct").value("Minuman"));
	}

	@Test
	void pelangganEndpointSudahTidakAdaDiProduct() throws Exception {
		mockMvc.perform(get("/api/pelanggan")).andExpect(status().isNotFound());
	}
}
