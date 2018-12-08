package com.moneytransfer.service;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;

import java.util.List;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Account Service
 */
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountService {

  private final AccountDAO accountDAO = DAOFactory.getDAOFactory(DAOFactory.H2).getAccountDAO();

  private static Logger log = Logger.getLogger(AccountService.class);

  /**
   * Find all accounts
   *
   * @return
   * @throws CustomException
   */
  @GET
  @Path("/all")
  public Response getAllAccounts() throws CustomException {
    List<Account> accounts = accountDAO.getAllAccounts();
    return Response.ok(accounts).build();
  }

  /**
   * Find by account id
   *
   * @param accountId
   * @return
   * @throws CustomException
   */
  @GET
  @Path("/{accountId}")
  public Response getAccount(@PathParam("accountId") long accountId) throws CustomException {
    return Response.ok(accountDAO.getAccountById(accountId)).build();
  }

  @GET
  @Path("/by_user/{user}/{code}")
  public Response getAccount(@PathParam("user") String user, @PathParam("code") String code) throws CustomException {
    return Response.ok(accountDAO.getAccountByUser(user, code)).build();
  }

  /**
   * Find balance by account Id
   *
   * @param accountId
   * @return
   * @throws CustomException
   */
  @GET
  @Path("/{accountId}/balance")
  public BigDecimal getBalance(@PathParam("accountId") long accountId) throws CustomException {
    final Account account = accountDAO.getAccountById(accountId);

    if (account == null) {
      throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
    }
    return account.getBalance();
  }

  /**
   * Create Account
   *
   * @param account
   * @return
   * @throws CustomException
   */
  @POST
  @Path("/create")
  public Response createAccount(Account account) throws CustomException {
    final long accountId = accountDAO.createAccount(account);
    return Response.ok(accountDAO.getAccountById(accountId)).build();
  }

  /**
   * Deposit amount by account Id
   *
   * @param accountId
   * @param amount
   * @return
   * @throws CustomException
   */
  @PUT
  @Path("/{accountId}/deposit/{amount}")
  public Response deposit(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws CustomException {

    if (amount.compareTo(MoneyUtil.zeroAmount) <= 0) {
      throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
    }

    accountDAO.updateAccountBalance(accountId, amount.setScale(4, RoundingMode.HALF_EVEN));
    return Response.ok(accountDAO.getAccountById(accountId)).build();
  }

  /**
   * Withdraw amount by account Id
   *
   * @param accountId
   * @param amount
   * @return
   * @throws CustomException
   */
  @PUT
  @Path("/{accountId}/withdraw/{amount}")
  public Response withdraw(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws CustomException {
    if (amount.compareTo(MoneyUtil.zeroAmount) <= 0) {
      throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
    }
    BigDecimal delta = amount.negate();
    if (log.isDebugEnabled()) {
      log.debug("Withdraw service: delta change to account  " + delta + " Account ID = " + accountId);
    }
    accountDAO.updateAccountBalance(accountId, delta.setScale(4, RoundingMode.HALF_EVEN));
    return Response.ok(accountDAO.getAccountById(accountId)).build();
  }

  /**
   * Delete amount by account Id
   *
   * @param accountId
   * @return
   * @throws CustomException
   */
  @DELETE
  @Path("/{accountId}")
  public Response deleteAccount(@PathParam("accountId") long accountId) throws CustomException {
    int deleteCount = accountDAO.deleteAccountById(accountId);
    if (deleteCount == 1) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }
}
