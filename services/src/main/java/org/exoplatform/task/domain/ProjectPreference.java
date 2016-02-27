/*
 * Copyright (C) 2015 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.task.domain;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
@Entity(name = "TaskProjectPreference")
@ExoEntity
@Table(name = "TASK_PROJECT_PREFERENCE")
@NamedQueries({
        @NamedQuery(
                name = "ProjectPreference.findProjectPreference",
                query = "SELECT p FROM TaskProjectPreference p WHERE p.username = :username and p.type = :refType and p.refId = :refId"
        )
})
public class ProjectPreference implements Serializable {

  public enum Type {
    INCOMING, ALL_TASK, OVERDUE, TODAY, TOMORROW, UPCOMING, PROJECT, LABEL
  }

  @SequenceGenerator(name="SEQ_TASK_PROJECT_PREFERENCE_ID", sequenceName="SEQ_TASK_PROJECT_PREFERENCE_ID")
  @GeneratedValue(strategy= GenerationType.AUTO, generator="SEQ_TASK_PROJECT_PREFERENCE_ID")
  @Column(name = "PROJECT_PREFERENCE_ID")
  @Id
  private long id;

  @Column(name = "USERNAME")
  private String username;

  @Column(name = "REFERENCE_TYPE", nullable = true)
  @Enumerated(EnumType.STRING)
  private Type type = Type.PROJECT;

  /**
   * This is projectId in case of type is PROJECT, and labelId in case type is LABEL
   */
  @Column(name = "REFERENCE_ID")
  private long refId = 0;

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "NAME")
  @Column(name = "VALUE")
  @CollectionTable(name = "PROJECT_PREFERENCE_ATTRIBUTE", joinColumns = {@JoinColumn(name = "PROJECT_PREFERENCE_ID")})
  private Map<String, String> attributes = new HashMap<String, String>();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public long getRefId() {
    return refId;
  }

  public void setRefId(long refId) {
    this.refId = refId;
  }

  public ProjectPreference clone() {
    ProjectPreference ref = new ProjectPreference();
    ref.setId(getId());
    ref.setUsername(getUsername());
    ref.setType(getType());
    ref.setRefId(getRefId());
    Map<String, String> attr = new HashMap<>();
    if (getAttributes() != null) {
      attr.putAll(getAttributes());
    }
    ref.setAttributes(attr);

    return ref;
  }
}
