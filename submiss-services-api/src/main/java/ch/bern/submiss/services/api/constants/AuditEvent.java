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

/**
 * The Enum AuditEvent.
 */
public enum AuditEvent {

  /** The create. */
  CREATE,
  /** The update. */
  UPDATE,
  /** The delete. */
  DELETE,
  /** The add. */
  ADD,
  /** The status change. */
  STATUS_CHANGE,
  /** The controlling task settled. */
  CONTROLLING_TASK_SETTLED,
  /** The todo task settled. */
  TODO_TASK_SETTLED,
  /** The remove. */
  REMOVE,
  /** The data moved. */
  DATA_MOVED,  
  /** The activation. */
  ACTIVATION,  
  /** The deactivation. */
  DEACTIVATION;
}
