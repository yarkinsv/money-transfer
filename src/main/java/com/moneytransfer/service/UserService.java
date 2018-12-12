package com.moneytransfer.service;

import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

  private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

  private static Logger log = Logger.getLogger(UserService.class);

  /**
   * Find by userName
   *
   * @param userName
   * @return
   * @throws CustomException
   */
  @GET
  @Path("/{userName}")
  public User getUserByName(@PathParam("userName") String userName) throws CustomException {
    if (log.isDebugEnabled())
      log.debug("Request Received for get User by Name " + userName);
    final User user = daoFactory.getUserDAO().getUserByName(userName);
    if (user == null) {
      throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
    }
    return user;
  }

  /**
   * Find all
   *
   * @return
   * @throws CustomException
   */
  @GET
  @Path("/all")
  public Response getAllUsers() throws CustomException {
    List<User> users = daoFactory.getUserDAO().getAllUsers();
    return Response.ok(users).build();
  }

  /**
   * Create User
   *
   * @param user
   * @return
   * @throws CustomException
   */
  @POST
  @Path("/create")
  public User createUser(User user) throws CustomException {
    Matcher matcher = Pattern.compile("(?=.{1,250}$)(.+)@(.+){2,}\\.(.+){2,}").matcher(user.getEmailAddress());
    if (!matcher.find()) {
      throw new WebApplicationException("User email is in wrong pattern", Response.Status.BAD_REQUEST);
    }
    if (daoFactory.getUserDAO().getUserByName(user.getUserName()) != null) {
      throw new WebApplicationException("User name already exist", Response.Status.BAD_REQUEST);
    }
    final long uId = daoFactory.getUserDAO().insertUser(user);
    return daoFactory.getUserDAO().getUserById(uId);
  }

  /**
   * Find by User Id
   *
   * @param userId
   * @param user
   * @return
   * @throws CustomException
   */
  @PUT
  @Path("/{userId}")
  public Response updateUser(@PathParam("userId") long userId, User user) throws CustomException {
    final int updateCount = daoFactory.getUserDAO().updateUser(userId, user);
    if (updateCount == 1) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  /**
   * Delete by User Id
   *
   * @param userId
   * @return
   * @throws CustomException
   */
  @DELETE
  @Path("/{userId}")
  public Response deleteUser(@PathParam("userId") long userId) throws CustomException {
    int deleteCount = daoFactory.getUserDAO().deleteUser(userId);
    if (deleteCount == 1) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Path("/{userId}/image")
  @Produces(MediaType.APPLICATION_SVG_XML)
  public Response getUserImage(@PathParam("userId") long userId) throws IOException {
    byte[] img = UserService.class.getClassLoader().getResource("user.jpg").getFile().getBytes();

    return Response.ok(img).header("hash", calcHashImg(img)).build();
  }

  private int calcHashImg(byte[] img) {
    return Arrays.hashCode(img);
  }
}
