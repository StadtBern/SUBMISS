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

package ch.bern.submiss.services.api.administration;

import java.util.Date;
import java.util.List;

import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;

import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyOfferDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.CompanySearchDTO;

/**
 * The Interface CompanyService.
 */
public interface CompanyService {

  /**
   * Retrieve sorted all companies that satisfy fields from searchForm.
   *
   * @param searchDTO the search DTO
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return The list of companies
   */
  List<CompanyDTO> search(CompanySearchDTO searchDTO, int page, int pageItems, String sortColumn,
    String sortType);

  /**
   * Find the count of companies.
   *
   * @param companyDTO the company DTO
   * @return The count of companies
   */
  long companyCount(CompanySearchDTO companyDTO);

  /**
   * Searches for company entries that contain a word that starts with the query parameter in the
   * given column.
   *
   * @param column the column to search in
   * @param query the query to search for
   * @return a distinct list of the column values
   */
  List<String> fullTextSearch(String column, String query);

  /**
   * Creates the company.
   *
   * @param companyDTO the company DTO
   * @return the string
   */
  String createCompany(CompanyDTO companyDTO);

  /**
   * Deletes a company.
   *
   * @param id the UUID of the company to be deleted
   */
  void deleteCompany(String id);

  /**
   * Finds a company based on it's UUID.
   *
   * @param id the company UUID
   * @return the company
   */
  CompanyDTO getCompanyById(String id);

  /**
   * Updates a company.
   *
   * @param companyDTO the company DTO
   * @param archivedPrevious true if archived is previously checked
   * @return the validation error
   */
  ValidationError updateCompany(CompanyDTO companyDTO, Boolean archivedPrevious);

  /**
   * Find if telephone unique.
   *
   * @param companyTel the company tel
   * @param companyId the company id
   * @return the boolean
   */
  Boolean findIfTelephoneUnique(String companyTel, String companyId);

  /**
   * Get proof by company id.
   *
   * @param id the company UUID
   * @return proof by company.
   */
  List<CompanyProofDTO> getProofByCompanyId(String id);

  /**
   * Updates proofs.
   *
   * @param companyProofDTOs the company proof DT os
   * @return the validation error
   */
  ValidationError updateProofs(List<CompanyProofDTO> companyProofDTOs);

  /**
   * Find if company participates.
   *
   * @param id the company UUID
   * @return the boolean
   */
  Boolean findIfCompanyParticipate(String id);

  /**
   * Gets offer by company id.
   *
   * @param id the company UUID
   * @param subContractors the sub-contractors
   * @return offer by company id
   */
  List<CompanyOfferDTO> getOfferByCompanyId(String id, boolean subContractors);

  /**
   * This method calculates the expiration date of proof list.
   *
   * @param companyId the company id
   * @return the date
   */
  Date calculateExpirationDate(String companyId);

  /**
   * Gets the all non migrated companies.
   *
   * @return the all non migrated companies
   */
  List<CompanyDTO> getAllNonMigratedCompanies();

  /**
   * This method checks the proof status of company regardless the consultation.
   *
   * @param companyDTO the company DTO
   * @return the proof state
   */
  int getProofState(CompanyDTO companyDTO);

  /**
   * Gets the company emails by id.
   *
   * @param companyIds the company ids
   * @return the company emails by id
   */
  List<String> getCompanyEmailsById(List<String> companyIds);

  /**
   * Gets the all companies.
   *
   * @return the all companies
   */
  List<CompanyDTO> getAllCompanies();


  /**
   * Gets the searched companies.
   *
   * @param companiesIDs the companies I ds
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return the searched companies
   */
  List<CompanyDTO> getSearchedCompanies(List<String> companiesIDs, String sortColumn,
    String sortType);

  /**
   * Gets the companies by the country id.
   *
   * @param countryId the country id
   * @return the companies
   */
  List<CompanyDTO> getCompaniesByCountryId(String countryId);

  /**
   * Gets the companies by id.
   *
   * @param companyIds the company ids
   * @return the companies by id
   */
  List<CompanyDTO> getCompaniesById(List<String> companyIds);

  /**
   * Retrieve company proof status.
   *
   * @param companyId the company id
   * @return the int
   */
  int retrieveCompanyProofStatus(String companyId);
}
