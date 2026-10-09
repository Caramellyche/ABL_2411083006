package com.faizul.customer;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faizul.customer.client.ProductClient;
import com.faizul.customer.vo.Produk;

/**
 * Verifikasi relasi Pelanggan <-> Product lewat panggilan antar-service:
 * - saat produk ada di product service -> productId tersimpan di pelanggan
 * - saat produk tidak ada -> 404, tidak disimpan
 */
@SpringBootTest
@AutoConfigureMockMvc
class PelangganProductTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ProductClient productClient;

	private long buatPelanggan() throws Exception {
		String body = "{\"nama\":\"Budi\",\"email\":\"budi@example.com\","
				+ "\"nomorTelepon\":\"08123\",\"alamat\":\"Jakarta\"}";
		String resp = mockMvc.perform(post("/api/pelanggan")
				.contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return objectMapper.readTree(resp).get("id").asLong();
	}

	@Test
	void hubungkanProductYangAda() throws Exception {
		long pelangganId = buatPelanggan();
		when(productClient.getProduk(10L)).thenReturn(new Produk(10L, "Kopi", 15000.0));

		mockMvc.perform(post("/api/pelanggan/" + pelangganId + "/product/10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.productIds[0]").value(10));
	}

	@Test
	void hubungkanProductYangTidakAda() throws Exception {
		long pelangganId = buatPelanggan();
		when(productClient.getProduk(99L)).thenReturn(null);

		mockMvc.perform(post("/api/pelanggan/" + pelangganId + "/product/99"))
				.andExpect(status().isNotFound());
	}
}
