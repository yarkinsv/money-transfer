package com.taskforce.moneyapp.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.moneytransfer.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class TestUserService extends TestService {

  @Test
  public void testGetUser() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/test2").build();
    HttpGet request = new HttpGet(uri);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(200, statusCode);
    String jsonString = EntityUtils.toString(response.getEntity());
    User user = mapper.readValue(jsonString, User.class);
    assertEquals("test2", user.getUserName());
    assertEquals("test2@gmail.com", user.getEmailAddress());
  }

  @Test
  public void testGetAllUsers() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/all").build();
    HttpGet request = new HttpGet(uri);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(200, statusCode);
    //check the content
    String jsonString = EntityUtils.toString(response.getEntity());
    User[] users = mapper.readValue(jsonString, User[].class);
    assertTrue(users.length > 0);
  }

  @Test
  public void testCreateUser() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/create").build();
    User user = new User("liandre", "liandre@gmail.com");
    String jsonInString = mapper.writeValueAsString(user);
    StringEntity entity = new StringEntity(jsonInString);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(entity);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(200, statusCode);
    String jsonString = EntityUtils.toString(response.getEntity());
    User uAfterCreation = mapper.readValue(jsonString, User.class);
    assertEquals("liandre", uAfterCreation.getUserName());
    assertEquals("liandre@gmail.com", uAfterCreation.getEmailAddress());
  }

  @Test
  public void testCreateExistingUser() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/create").build();
    User user = new User("test1", "test1@gmail.com");
    String jsonInString = mapper.writeValueAsString(user);
    StringEntity entity = new StringEntity(jsonInString);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(entity);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(400, statusCode);
  }

  @Test
  public void testUpdateUser() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/2").build();
    User user = new User(2L, "test1", "test1123@gmail.com");
    String jsonInString = mapper.writeValueAsString(user);
    StringEntity entity = new StringEntity(jsonInString);
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(entity);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(200, statusCode);
  }

  @Test
  public void testUpdateNonExistingUser() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/100").build();
    User user = new User(2L, "test1", "test1123@gmail.com");
    String jsonInString = mapper.writeValueAsString(user);
    StringEntity entity = new StringEntity(jsonInString);
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(entity);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(404, statusCode);
  }

  @Test
  public void testDeleteUser() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/3").build();
    HttpDelete request = new HttpDelete(uri);
    request.setHeader("Content-type", "application/json");
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(200, statusCode);
  }

  @Test
  public void testDeleteNonExistingUser() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/300").build();
    HttpDelete request = new HttpDelete(uri);
    request.setHeader("Content-type", "application/json");
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(404, statusCode);
  }

  @Test
  public void testCreateUserWithInvalidEmail() throws IOException, URISyntaxException {
    URI uri = builder.setPath("/user/create").build();
    User user = new User("liandre", "@lian%dre-gmail.com");
    String jsonInString = mapper.writeValueAsString(user);
    StringEntity entity = new StringEntity(jsonInString);
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(entity);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    assertEquals(400, statusCode);
  }
}
