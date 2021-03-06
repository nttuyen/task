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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
@Entity
@Table(name = "TASK_USER_SETTING")
public class UserSetting {
  @Id
  @Column(name = "USERNAME")
  private String username;

  @Column(name = "SHOW_HIDDEN_PROJECT")
  private boolean showHiddenProject = false;

  @ManyToMany(cascade = CascadeType.REMOVE)
  @JoinTable(
          name = "TASK_HIDDEN_PROJECT",
          joinColumns = {@JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")},
          inverseJoinColumns = {@JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID")}
  )
  private Set<Project> hiddenProjects = new HashSet<Project>();

  public UserSetting() {

  }

  public UserSetting(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public boolean isShowHiddenProject() {
    return showHiddenProject;
  }

  public void setShowHiddenProject(boolean showHiddenProject) {
    this.showHiddenProject = showHiddenProject;
  }

  public Set<Project> getHiddenProjects() {
    return hiddenProjects;
  }

  public void setHiddenProjects(Set<Project> hiddenProjects) {
    this.hiddenProjects = hiddenProjects;
  }

  public boolean isHiddenProject(Project project) {
    if (project == null) return false;

    for (Project p : hiddenProjects) {
      if (p.getId() == project.getId()) {
        return true;
      }
    }
    return false;
  }
}
