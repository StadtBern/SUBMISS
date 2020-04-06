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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ch.bern.submiss.services.api.constants.EmailTemplate;

@Entity
@Table(name = "SUB_EMAIL_ATTRIBUTES")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class EmailAttributes {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  
  @ManyToOne
  @JoinColumn(name = "FK_EMAIL_TEMPLATE_TENANT")
  private EmailTemplateTenant templateTenant;
  
  @Column(name = "EMAIL_SEND_TYPE")
  @Enumerated(EnumType.STRING)
  private EmailTemplate.SEND_TYPE sendType;
  
  @Column(name = "RECIEVER_ROLE")
  private String recieverRole;
  
  @ManyToOne
  @JoinColumn(name = "FK_DEPARTMENT")
  private DepartmentEntity department;
  
  @Column(name = "RECIEVER")
  private String reciever;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public EmailTemplateTenant getTemplateTenant() {
    return templateTenant;
  }

  public void setTemplateTenant(EmailTemplateTenant templateTenant) {
    this.templateTenant = templateTenant;
  }

  public EmailTemplate.SEND_TYPE getSendType() {
    return sendType;
  }

  public void setSendType(EmailTemplate.SEND_TYPE sendType) {
    this.sendType = sendType;
  }

  public String getRecieverRole() {
    return recieverRole;
  }

  public void setRecieverRole(String recieverRole) {
    this.recieverRole = recieverRole;
  }

  public DepartmentEntity getDepartment() {
    return department;
  }

  public void setDepartment(DepartmentEntity department) {
    this.department = department;
  }

  public String getReciever() {
    return reciever;
  }

  public void setReciever(String reciever) {
    this.reciever = reciever;
  }
  
  
}
