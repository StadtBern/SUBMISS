package ch.bern.submiss.web.exceptions;

import ch.bern.submiss.services.api.exceptions.AuthorisationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthorisationExceptionMapper implements ExceptionMapper<AuthorisationException> {

  @Override
  public Response toResponse(AuthorisationException exception) {
    return Response.status(Status.FORBIDDEN).build();
  }
}


