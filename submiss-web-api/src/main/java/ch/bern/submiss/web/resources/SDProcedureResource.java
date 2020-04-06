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

import ch.bern.submiss.services.api.administration.SDProcedureService;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/sd/procedure")
@Singleton
public class SDProcedureResource {

  @OsgiService
  @Inject
  private SDProcedureService sDProcedureService;

  /*
   * @POST
   * 
   * @Path("/create")
   * 
   * @Produces(MediaType.APPLICATION_JSON)
   * 
   * @Consumes(MediaType.APPLICATION_JSON) public SDDossierTypDTO create(SDDossierTypDTO
   * sdDossierTypDTO) { return KarafHolder.getService(SDDossierTypService.class)
   * .createDossierTyp(sdDossierTypDTO); }
   * 
   * @GET
   * 
   * @Path("/read/{id: [0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
   * 
   * @Produces(MediaType.APPLICATION_JSON) public SDDossierTypDTO read(@PathParam("id") String id) {
   * return KarafHolder.getService(SDDossierTypService.class).readDossierTyp(id); }
   * 
   * @PUT
   * 
   * @Path("/update")
   * 
   * @Produces(MediaType.APPLICATION_JSON)
   * 
   * @Consumes(MediaType.APPLICATION_JSON) public SDDossierTypDTO update(SDDossierTypDTO
   * sdDossierTypDTO) { return
   * KarafHolder.getService(SDDossierTypService.class).updateDossierTyp(sdDossierTypDTO); }
   * 
   * @DELETE
   * 
   * @Path("/delete/{id: [0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}") public void
   * delete(@PathParam("id") String id) {
   * KarafHolder.getService(SDDossierTypService.class).deleteDossierTyp(id); }
   */

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<MasterListValueHistoryDTO> readAll() {
    return sDProcedureService.readAll();
  }
}
