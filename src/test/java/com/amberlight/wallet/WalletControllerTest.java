package com.amberlight.wallet;


import com.amberlight.wallet.model.domain.Transaction;
import com.amberlight.wallet.model.domain.Wallet;
import com.amberlight.wallet.model.dto.TransactionDto;
import com.amberlight.wallet.service.IWalletService;
import com.amberlight.wallet.web.controller.WalletController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link com.amberlight.wallet.web.controller.WalletController}
 */
@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

	private MockMvc mvc;

	@Mock
	private IWalletService walletService;

	@InjectMocks
	private WalletController walletController;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.standaloneSetup(walletController).build();
	}

	@Test
	void getWalletState_whenSuccess() throws Exception {
		String uri = "/wallet/state";
		Integer walletId = 1;
		Integer playerId = 1;
		BigDecimal walletBalance = BigDecimal.valueOf(100);
		Wallet wallet = Wallet.builder().id(walletId).balance(walletBalance).build();
		String expectedResponse = objectMapper.writeValueAsString(wallet);
		when(walletService.getWalletByPlayerId(playerId)).thenReturn(wallet);
		String responseJson = mvc
				.perform(MockMvcRequestBuilders.get(uri).queryParam("playerId", String.valueOf(playerId)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(expectedResponse, responseJson);
		verify(walletService, times(1)).getWalletByPlayerId(playerId);
	}

	@Test
	void getTransactions_whenSuccess() throws Exception {
		String uri = "/wallet/transactions";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		Date transactionDate = new Date();
		BigDecimal transactionAmount = BigDecimal.valueOf(100);
		Transaction transaction = Transaction.builder().id(transactionId)
								  .amount(transactionAmount).date(transactionDate).build();
		List<Transaction> transactions = Collections.singletonList(transaction);
		String expectedResponse = objectMapper.writeValueAsString(transactions);
		when(walletService.getAllTransactionsByWalletId(walletId)).thenReturn(transactions);
		String responseJson = mvc
				.perform(MockMvcRequestBuilders.get(uri).queryParam("walletId", String.valueOf(walletId)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(expectedResponse, responseJson);
		verify(walletService, times(1)).getAllTransactionsByWalletId(walletId);
	}

	@Test
	void debitWallet_whenSuccess() throws Exception {
		String uri = "/wallet/debit";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		BigDecimal transactionAmount = BigDecimal.valueOf(100);
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(walletId).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andReturn();
		verify(walletService, times(1)).debitWallet(any(TransactionDto.class));
	}

	@Test
	void debitWallet_whenTransactionIdIsNull() throws Exception {
		String uri = "/wallet/debit";
		Integer walletId = 1;
		BigDecimal transactionAmount = BigDecimal.valueOf(100);
		TransactionDto transactionDto = TransactionDto.builder().id(null)
										.walletId(walletId).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void debitWallet_whenWalletIdIsNull() throws Exception {
		String uri = "/wallet/debit";
		String transactionId = "asd-asd-asd";
		BigDecimal transactionAmount = BigDecimal.valueOf(100);
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(null).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void debitWallet_whenAmountIsNull() throws Exception {
		String uri = "/wallet/debit";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(walletId).amount(null).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void debitWallet_amountIsZero() throws Exception {
		String uri = "/wallet/debit";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		BigDecimal transactionAmount = BigDecimal.ZERO;
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(walletId).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void debitWallet_amountIsNegative() throws Exception {
		String uri = "/wallet/debit";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		BigDecimal transactionAmount = BigDecimal.valueOf(-50);
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(walletId).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void creditWallet_whenSuccess() throws Exception {
		String uri = "/wallet/credit";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		BigDecimal transactionAmount = BigDecimal.valueOf(100);
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(walletId).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andReturn();
		verify(walletService, times(1)).creditWallet(any(TransactionDto.class));
	}

	@Test
	void creditWallet_whenTransactionIdIsNull() throws Exception {
		String uri = "/wallet/credit";
		Integer walletId = 1;
		BigDecimal transactionAmount = BigDecimal.valueOf(100);
		TransactionDto transactionDto = TransactionDto.builder().id(null)
										.walletId(walletId).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void creditWallet_whenWalletIdIsNull() throws Exception {
		String uri = "/wallet/credit";
		String transactionId = "asd-asd-asd";
		BigDecimal transactionAmount = BigDecimal.valueOf(100);
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(null).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void creditWallet_whenAmountIsNull() throws Exception {
		String uri = "/wallet/credit";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(walletId).amount(null).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void creditWallet_amountIsZero() throws Exception {
		String uri = "/wallet/credit";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		BigDecimal transactionAmount = BigDecimal.ZERO;
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(walletId).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

	@Test
	void creditWallet_amountIsNegative() throws Exception {
		String uri = "/wallet/credit";
		Integer walletId = 1;
		String transactionId = "asd-asd-asd";
		BigDecimal transactionAmount = BigDecimal.valueOf(-50);
		TransactionDto transactionDto = TransactionDto.builder().id(transactionId)
										.walletId(walletId).amount(transactionAmount).build();
		String requestBody = objectMapper.writeValueAsString(transactionDto);
		mvc
				.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}

}
