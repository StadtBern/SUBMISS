/*
 *  Submiss, eProcurement system for managing tenders
 *  Copyright (C) 2019 Stadt Bern
 *  Licensed under the EUPL, Version 1.2 or - as soon as they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at:
 *  https://joinup.ec.europa.eu/collection/eupl
 *  Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the Licence for the specific language governing permissions and limitations
 *  under the Licence.
 */

package ch.bern.submiss.web.resources;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.lang3.StringUtils;

@Path("/auth")
@Singleton
public class AuthResource {

  private static final String SSO_COOKIE_NAME = "org.apache.cxf.websso.context";
  private static final String SSO_RELAY_STATE_COOKIE_NAME = "RelayState";
  private static final String REDIRECT_TO_COOKIE_NAME = "redirectTo";
  /**
   * JUL reference
   */
  private static final Logger LOGGER = Logger.getLogger(AuthResource.class.getName());

  /**
   * A standard endpoint to enforce authentication. It optionally saves a URL to redirect the user
   * to, once authentication takes place and SSO Filter redirects back to this endpoint.
   */
  @GET
  @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@CookieParam(REDIRECT_TO_COOKIE_NAME) String redirectCookie,
    @CookieParam(SSO_COOKIE_NAME) String ssoCookie,
    @CookieParam(SSO_RELAY_STATE_COOKIE_NAME) String relayState)
    throws UnsupportedEncodingException, URISyntaxException {
    ResponseBuilder response;
    if (StringUtils.isNotBlank(redirectCookie)) {
      LOGGER.log(Level.FINE, "Found a redirect cookie, will now redirect the user to {0}.",
        URLDecoder.decode(redirectCookie, "UTF-8"));
      response = Response.temporaryRedirect(new URI(URLDecoder.decode(redirectCookie, "UTF-8")));
      /* Remove redirect Cookie */
      NewCookie newCookie = new NewCookie(REDIRECT_TO_COOKIE_NAME,
        "", "/", null, "", 0, false, true);
      response.cookie(newCookie);
    } else {
      LOGGER.log(Level.FINE, "Redirecting user to /.");
      response = Response.temporaryRedirect(new URI("/"));
    }
    return response.build();
  }
}
