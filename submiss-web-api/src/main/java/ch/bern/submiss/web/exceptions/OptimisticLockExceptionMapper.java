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

package ch.bern.submiss.web.exceptions;

import ch.bern.submiss.services.api.util.ValidationMessages;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;

@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException> {

  @Override
  public Response toResponse(OptimisticLockException exception) {
    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    if (StringUtils.isNotBlank(exception.getMessage())) {
      optimisticLockErrors.add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
        exception.getMessage()));
    } else {
      optimisticLockErrors.add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
        ValidationMessages.OPTIMISTIC_LOCK));
    }
    return Response.status(Status.CONFLICT).entity(optimisticLockErrors).build();
  }
}
