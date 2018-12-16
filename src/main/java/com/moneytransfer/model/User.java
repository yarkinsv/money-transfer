package com.moneytransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

  private long userId;

  @JsonProperty(required = true)
  private String userName;

  @JsonProperty(required = true)
  private String emailAddress;

  public User() {
  }

  public User(String userName, String emailAddress) {
    this.userName = userName;
    this.emailAddress = emailAddress;
  }

  public User(long userId, String userName, String emailAddress) {
    this.userId = userId;
    this.userName = userName;
    this.emailAddress = emailAddress;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    if (userId != user.userId) return false;
    if (!userName.equals(user.userName)) return false;
    return emailAddress.equals(user.emailAddress);

  }

  @Override
  public int hashCode() {
    int result = (int) (userId ^ (userId >>> 32));
    result = 31 * result + userName.hashCode();
    result = 31 * result + emailAddress.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "{" +
        "\"userId\":" + userId +
        ", \"userName\":\"" + userName + '\"' +
        ", \"emailAddress\":\"" + emailAddress + '\"' +
        '}';
  }
}
