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

package ch.bern.submiss.services.api.constants;

import java.util.EnumSet;
import java.util.Set;

public enum TaskTypes {
  REGISTRATION_APPLICATION, PROOF_REQUEST, PROOF_REQUEST_PL_XY, NOT_CLOSED_SUBMISSION, CHECK_TENDERLIST, ILLEGAL_THRESHOLD, SET_SURCHARGE_PDATE;

  private static EnumSet<TaskTypes> controllingTask =
      EnumSet.of(PROOF_REQUEST_PL_XY, NOT_CLOSED_SUBMISSION, ILLEGAL_THRESHOLD);

  private static EnumSet<TaskTypes> submissionRelatedTasks = EnumSet.of(PROOF_REQUEST, PROOF_REQUEST_PL_XY,
      NOT_CLOSED_SUBMISSION, CHECK_TENDERLIST, ILLEGAL_THRESHOLD, SET_SURCHARGE_PDATE);

  private static EnumSet<TaskTypes> companyRelatedTasks = EnumSet.of(PROOF_REQUEST, PROOF_REQUEST_PL_XY);

  private static EnumSet<TaskTypes> userRelatedTasks = EnumSet.of(REGISTRATION_APPLICATION);

  public static Set<TaskTypes> getControllingTask() {
    return controllingTask;
  }

  public static Set<TaskTypes> getSubmissionRelatedTasks() {
    return submissionRelatedTasks;
  }

  public static Set<TaskTypes> getCompanyRelatedTasks() {
    return companyRelatedTasks;
  }

  public static Set<TaskTypes> getUserRelatedTasks() {
    return userRelatedTasks;
  }

}
