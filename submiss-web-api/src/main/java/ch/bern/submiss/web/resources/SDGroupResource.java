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

import ch.bern.submiss.services.api.administration.UserAdministrationService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/sd/group")
@Singleton
public class SDGroupResource {

  @OsgiService
  @Inject
  private UserGroupService userGroupServiceImpl;

  @OsgiService
  @Inject
  private UserAdministrationService userAdministrationService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/all")
  public List<GroupDTO> readAll() {
    return userGroupServiceImpl.listGroups();

  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/permitted")
  public List<GroupDTO> getPermitted() {
    return userAdministrationService.getPermittedGroups();

  }
}
