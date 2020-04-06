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

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A helper class to check that the back-end is responding.
 */
@Path("/ping")
@Singleton
public class PingResource {

  // How often (in minutes) to try refreshing the token.
  public static final int REFRESH_TOKEN_TIMER = 1;

  // The content of the self-reloading page to be returned.
  private static final String IFRAME_TOKEN_PAGE =
      "<html> " + "<head>" + "<meta http-equiv=\"refresh\" content=\"" + (REFRESH_TOKEN_TIMER * 60)
          + "\">" + "</head>" + "</html>";

  /**
   * The default request of this resource to check that the back-end is responding.
   *
   * @return Returns an STATUS OK - 400 response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response ping() {
    return Response.ok().build();
  }

  /**
   * A request to keep a SAML token refreshed.
   * 
   * @return Returns an HTML self-reloading page to keep refreshing the SAML token.
   */
  @GET
  @Path("/token")
  @Produces(MediaType.TEXT_HTML)
  public Response pingToken() {
    return Response.ok(IFRAME_TOKEN_PAGE).build();
  }

}
