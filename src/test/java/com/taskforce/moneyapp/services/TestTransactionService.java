package com.taskforce.moneyapp.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.moneytransfer.model.Account;
import com.moneytransfer.model.UserTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public abstract class TestTransactionService extends TestService {

  @Test
  public void testDeposit() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/account/1/deposit/100").build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(200, statusCode);
    String jsonString = EntityUtils.toString(response.getEntity());
    Account afterDeposit = mapper.readValue(jsonString, Account.class);
    assertEquals(afterDeposit.getBalance(), new BigDecimal(200).setScale(4, RoundingMode.HALF_EVEN));
  }

  @Test
  public void testWithDrawSufficientFund() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/account/2/withdraw/100").build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(200, statusCode);
    String jsonString = EntityUtils.toString(response.getEntity());
    Account afterDeposit = mapper.readValue(jsonString, Account.class);
    assertEquals(afterDeposit.getBalance(), new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN));
  }

  @Test
  public void testWithDrawNonSufficientFund() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/account/2/withdraw/1000.23456").build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    String responseBody = EntityUtils.toString(response.getEntity());
    assertEquals(500, statusCode);
    assertTrue(responseBody.contains("Not sufficient Fund"));
  }

  @Test
  public void testTransactionEnoughFund() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/transaction").build();
    BigDecimal amount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
    UserTransaction transaction = new UserTransaction("EUR", amount, 3L, 4L);
    String jsonInString = mapper.writeValueAsString(transaction);
    StringEntity entity = new StringEntity(jsonInString);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(entity);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(200, statusCode);
  }

  @Test
  public void testTransactionNotEnoughFund() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/transaction").build();
    BigDecimal amount = new BigDecimal(100000).setScale(4, RoundingMode.HALF_EVEN);
    UserTransaction transaction = new UserTransaction("EUR", amount, 3L, 4L);
    String jsonInString = mapper.writeValueAsString(transaction);
    StringEntity entity = new StringEntity(jsonInString);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(entity);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(500, statusCode);
  }

  @Test
  public void testTransactionDifferentCcy() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/transaction").build();
    BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
    UserTransaction transaction = new UserTransaction("USD", amount, 3L, 4L);
    String jsonInString = mapper.writeValueAsString(transaction);
    StringEntity entity = new StringEntity(jsonInString);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(entity);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(500, statusCode);
  }
}
