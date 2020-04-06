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

import ch.bern.submiss.services.api.administration.SDVatService;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("sd/vat")
@Singleton
public class SDVatResource {
  @OsgiService
  @Inject
  private SDVatService sDVatService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<MasterListValueHistoryDTO> readAll() {
    return sDVatService.readAll();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getVatsBySubmission/{id}")
  public List<MasterListValueHistoryDTO> getVatsBySubmission(@PathParam("id") String submissionId) {
     List<MasterListValueHistoryDTO> updatedHistoryValues=sDVatService.readAll();
     List<MasterListValueHistoryDTO> returnedHistoryValues=new ArrayList<>();

     for(MasterListValueHistoryDTO vatHistoryDTO: updatedHistoryValues) {
    	 MasterListValueHistoryDTO returnedVatHistoryDTO=
    			 sDVatService.readVatBySubmission(submissionId,vatHistoryDTO.getMasterListValueId().getId());
    	 returnedHistoryValues.add(returnedVatHistoryDTO);
     }
     return returnedHistoryValues;
  }
}
