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

import ch.bern.submiss.services.api.administration.LexiconService;
import ch.bern.submiss.services.api.util.LookupValues;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.util.rest.HttpCacheHelper;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * Endpoint to i18n-related resources.
 *
 * @author European Dynamics SA
 */
@Path("/lexicon")
@Singleton
public class LexiconResource {

  /**
   * JUL reference
   */
  private static final Logger LOGGER = Logger.getLogger(LexiconResource.class.getName());

  @OsgiService
  @Inject
  private LexiconService lexiconService;

  @GET
  @Path("/translations")
  @Produces(MediaType.APPLICATION_JSON)
  /**
   * Returns the complete set of translations for a specific language locale.
   *
   * @param lang The locale to fetch the translations for.
   * @return
   *         <ul>
   *         <li>200 (OK)</li>
   *         <li>304 (Not Modified) - When the translations have not been modified</li>
   *         <li>404 (Not Found) - The requested locale is not supported.</li>
   *         </ul>
   */
  public Response get(@QueryParam("lang") String lang, @Context Request request) {
    /**
     * If no language is requested, or if the requested language is not supported return an error
     * response.
     */
    if (StringUtils.isBlank(lang) || lang.equals("undefined")
      || !lexiconService.isLanguageAvailableForLocale(lang)) {
      LOGGER.log(Level.FINE, "Requested language does not exist: " + "{0}", lang);
      return Response.status(Status.NOT_FOUND).build();
    }
    /** Get the group with the translations. */
    GroupDTO group = lexiconService.getGroupByName(LookupValues.DEFAULT_LEXICON_GROUP);
    /** Prepare the response. */
    return HttpCacheHelper.expiringETagResponse(LookupValues.LEXICON_CACHE_TRANSLATIONS_DURATION,
      TimeUnit.DAYS, new Callable<String>() {
        @Override
        public String call() {
          return String.valueOf(lexiconService.getLastUpdateForLocale(group.getId(), lang));
        }
      }, new Callable<Object>() {
        @Override
        public Object call() {
          return lexiconService.getTranslations(group.getId(), lang);
        }
      }, request).build();
  }

  @GET
  @Path("/languages")
  @Produces(MediaType.APPLICATION_JSON)
  /**
   * Returns the list of available languages in the system.
   *
   * @return
   */
  public Response languages() {
    /** Prepare the response. */
    return HttpCacheHelper.expiringResponse(LookupValues.LEXICON_CACHE_TRANSLATIONS_DURATION,
      TimeUnit.DAYS, lexiconService.getActiveLanguages()).build();
  }
}
