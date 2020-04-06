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

import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.constants.TenderStatus;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/enumerations")
@Singleton
public class EnumerationResource {

  /**
   * Gets the tender status values.
   *
   * @return the tender status values
   */
  @GET
  @Path("/tenderStatus")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTenderStatusValues() {
    EnumMap<TenderStatus, Integer> tenderStatusMap = new EnumMap<>(TenderStatus.class);
    for (TenderStatus status : TenderStatus.values()) {
      tenderStatusMap.put(status, Integer.parseInt(status.getValue()));
    }
    return Response.ok(tenderStatusMap).build();
  }

  @GET
  @Path("/taskTypes")
  @Produces(MediaType.APPLICATION_JSON)
  public List<TaskTypes> getTaskTypes() {
    return Arrays.asList(TaskTypes.values());
  }

  /**
   * Gets the SD category types.
   *
   * @return the SD category types
   */
  @GET
  @Path("/getSDCategoryTypes")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSDCategoryTypes() {
    EnumMap<CategorySD, String> categorySDMap = new EnumMap<>(CategorySD.class);
    for (CategorySD category : CategorySD.values()) {
      categorySDMap.put(category, category.getValue());
    }
    return Response.ok(categorySDMap).build();
  }

}
