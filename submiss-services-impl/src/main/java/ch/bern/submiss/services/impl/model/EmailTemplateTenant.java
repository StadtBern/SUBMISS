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

package ch.bern.submiss.services.impl.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import ch.bern.submiss.services.api.constants.EmailTemplate;

/**
 * The Class EmailTemplateTenant.
 */
@Entity
@Table(name = "SUB_EMAIL_TEMPLATE_TENANT")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class EmailTemplateTenant {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;

  /** The tenant. */
  @ManyToOne
  @JoinColumn(name = "FK_TENANT")
  private TenantEntity tenant;

  /** The subject. */
  @Column(name = "SUBJECT")
  private String subject;

  /** The content. */
  @Column(name = "CONTENT")
  private String content;

  /** The email template. */
  @ManyToOne
  @JoinColumn(name = "FK_EMAIL_TEMPLATE")
  private EmailTemplateEntity emailTemplate;

  /** The attributes. */
  @OneToMany(mappedBy = "templateTenant", fetch = FetchType.EAGER)
  private List<EmailAttributes> attributes;

  /** The description. */
  @Column(name = "DESCRIPTION")
  private String description;

  /** The is active. */
  @Column(name = "IS_ACTIVE")
  private Boolean isActive;

  /** The available part. */
  @Column(name = "AVAILABLE_PART", length = 4, columnDefinition = "SMALLINT")
  @Enumerated
  private EmailTemplate.AVAILABLE_PART availablePart;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  public TenantEntity getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantEntity tenant) {
    this.tenant = tenant;
  }

  /**
   * Gets the subject.
   *
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the subject.
   *
   * @param subject the new subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Gets the content.
   *
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content.
   *
   * @param content the new content
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Gets the email template.
   *
   * @return the email template
   */
  public EmailTemplateEntity getEmailTemplate() {
    return emailTemplate;
  }

  /**
   * Sets the email template.
   *
   * @param emailTemplate the new email template
   */
  public void setEmailTemplate(EmailTemplateEntity emailTemplate) {
    this.emailTemplate = emailTemplate;
  }

  /**
   * Gets the attributes.
   *
   * @return the attributes
   */
  public List<EmailAttributes> getAttributes() {
    return attributes;
  }

  /**
   * Sets the attributes.
   *
   * @param attributes the new attributes
   */
  public void setAttributes(List<EmailAttributes> attributes) {
    this.attributes = attributes;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the checks if is active.
   *
   * @return the checks if is active
   */
  public Boolean getIsActive() {
    return isActive;
  }

  /**
   * Sets the checks if is active.
   *
   * @param isActive the new checks if is active
   */
  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  /**
   * Gets the available part.
   *
   * @return the available part
   */
  public EmailTemplate.AVAILABLE_PART getAvailablePart() {
    return availablePart;
  }

  /**
   * Sets the available part.
   *
   * @param availablePart the new available part
   */
  public void setAvailablePart(EmailTemplate.AVAILABLE_PART availablePart) {
    this.availablePart = availablePart;
  }

}
