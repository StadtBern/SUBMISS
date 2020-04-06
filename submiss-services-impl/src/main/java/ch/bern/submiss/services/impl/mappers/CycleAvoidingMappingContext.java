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

package ch.bern.submiss.services.impl.mappers;

import java.util.IdentityHashMap;
import java.util.Map;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

/**
 * This Mapper is used to avoid error because of cycle dependency on Entities and DTOs.
 *
 */
public class CycleAvoidingMappingContext {
  private Map<Object, Object> knownInstances = new IdentityHashMap<>();

  @BeforeMapping
  public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
    return (T) knownInstances.get(source);
  }

  @BeforeMapping
  public void storeMappedInstance(Object source, @MappingTarget Object target) {
    knownInstances.put(source, target);
  }
}
