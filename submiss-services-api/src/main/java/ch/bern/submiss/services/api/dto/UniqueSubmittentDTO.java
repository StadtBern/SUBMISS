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

package ch.bern.submiss.services.api.dto;

import java.util.Set;

public class UniqueSubmittentDTO {

  private CompanyDTO company;
  private Set<CompanyDTO> jointVentures;
  private Set<CompanyDTO> subcontractors;

  public CompanyDTO getCompany() {
    return company;
  }

  public void setCompany(CompanyDTO company) {
    this.company = company;
  }

  public Set<CompanyDTO> getJointVentures() {
    return jointVentures;
  }

  public void setJointVentures(Set<CompanyDTO> jointVentures) {
    this.jointVentures = jointVentures;
  }

  public Set<CompanyDTO> getSubcontractors() {
    return subcontractors;
  }

  public void setSubcontractors(Set<CompanyDTO> subcontractors) {
    this.subcontractors = subcontractors;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((company == null) ? 0 : company.hashCode());
    result = prime * result + ((jointVentures == null) ? 0 : jointVentures.hashCode());
    result = prime * result + ((subcontractors == null) ? 0 : subcontractors.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UniqueSubmittentDTO other = (UniqueSubmittentDTO) obj;
    if (company == null) {
      if (other.company != null)
        return false;
    } else if (!company.equals(other.company))
      return false;
    if (jointVentures == null) {
      if (other.jointVentures != null)
        return false;
    } else if (!jointVentures.equals(other.jointVentures))
      return false;
    if (subcontractors == null) {
      if (other.subcontractors != null)
        return false;
    } else if (!subcontractors.equals(other.subcontractors))
      return false;
    return true;
  }  
}
