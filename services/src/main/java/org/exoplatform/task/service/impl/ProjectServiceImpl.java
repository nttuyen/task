/* 
* Copyright (C) 2003-2015 eXo Platform SAS.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see http://www.gnu.org/licenses/ .
*/
package org.exoplatform.task.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.task.dao.DAOHandler;
import org.exoplatform.task.dao.OrderBy;
import org.exoplatform.task.domain.Project;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.exception.EntityNotFoundException;
import org.exoplatform.task.exception.ParameterEntityException;
import org.exoplatform.task.service.ProjectService;
import org.exoplatform.task.service.StatusService;
import org.exoplatform.task.service.TaskService;

/**
 * Created by The eXo Platform SAS
 * Author : Thibault Clement
 * tclement@exoplatform.com
 * 6/3/15
 */
@Singleton
public class ProjectServiceImpl implements ProjectService {

  private static final Log LOG = ExoLogger.getExoLogger(ProjectServiceImpl.class);

  @Inject
  StatusService statusService;
  
  @Inject
  TaskService taskService;
  
  @Inject
  DAOHandler daoHandler;

  public ProjectServiceImpl() {
  }

  //For testing purpose only
  public ProjectServiceImpl(StatusService statusService, TaskService taskService, DAOHandler daoHandler) {
    this.daoHandler = daoHandler;
    this.statusService = statusService;
    this.taskService = taskService;
  }

  @Override
  @ExoTransactional
  public Project createProject(Project project) {
    Project proj = daoHandler.getProjectHandler().create(project);
    return proj;
  }

  @Override
  public Project createProject(Project project, long parentId) throws EntityNotFoundException {
    Project parentProject = daoHandler.getProjectHandler().find(parentId);
    if (parentProject != null) {
      project.setParent(parentProject);
      //If parent, list of members/participators of parents override the list of members/participators in parameter
      project.setParticipator(new HashSet<String>(parentProject.getParticipator()));
      //If parent, list of manager of parents override the list of managers in parameter
      project.setManager(new HashSet<String>(parentProject.getManager()));

      //persist project
      project = createProject(project);

      //inherit status from parent
      List<Status> prSt = new LinkedList<Status>(parentProject.getStatus());
      Collections.sort(prSt);
      for (Status st : prSt) {
        statusService.createStatus(project, st.getName());
      }
      return project;
    } else {
      LOG.info("Can not find project for parent with ID: " + parentId);
      throw new EntityNotFoundException(parentId, Project.class);
    }
  }

  @Override
  @ExoTransactional
  public Project saveProjectField(long projectId, String fieldName, String[] values)
      throws EntityNotFoundException, ParameterEntityException {

    String val = values != null && values.length > 0 ? values[0] : null;

    Project project = getProject(projectId); //Can throw ProjectNotFoundException

    if("name".equalsIgnoreCase(fieldName)) {
      if(val == null || val.isEmpty()) {
        LOG.info("Name of project must not empty");
        throw new ParameterEntityException(projectId, Project.class, fieldName, val, "must not be empty", null);
      }
      project.setName(val);
    } else if("manager".equalsIgnoreCase(fieldName)) {
      Set<String> manager = new HashSet<String>();
      if(values != null) {
        for (String v : values) {
          manager.add(v);
        }
      }
      project.setManager(manager);
    } else if("participator".equalsIgnoreCase(fieldName)) {
      Set<String> participator = new HashSet<String>();
      if(values != null || true) {
        for (String v : values) {
          participator.add(v);
        }
      }
      project.setParticipator(participator);
    } else if("dueDate".equalsIgnoreCase(fieldName)) {
      if(val == null || val.isEmpty()) {
        project.setDueDate(null);
      } else {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
          Date date = df.parse(val);
          project.setDueDate(date);
        } catch (ParseException e) {
          LOG.info("can not parse date string: " + val);
          throw new ParameterEntityException(projectId, Project.class, fieldName, val, "cannot be parse to date", e);
        }
      }
    } else if("description".equalsIgnoreCase(fieldName)) {
      project.setDescription(val);
    } else if ("color".equalsIgnoreCase(fieldName)) {
      project.setColor(val);
    } else if ("calendarIntegrated".equalsIgnoreCase(fieldName)) {
      project.setCalendarIntegrated(Boolean.parseBoolean(val));
    } else if ("parent".equalsIgnoreCase(fieldName)) {
      try {
        long pId = Long.parseLong(val);
        if (pId == 0) {
          project.setParent(null);
        } else if (pId == project.getId()) {
          throw new ParameterEntityException(pId, Project.class, fieldName, val, "project can not be child of itself", null);
        } else {
          Project parent = this.getProject(pId);
          project.setParent(parent);
        }
      } catch (NumberFormatException ex) {
        LOG.info("can not parse date string: " + val);
        throw new ParameterEntityException(projectId, Project.class, fieldName, val, "cannot be parse to Long", ex);
      }
    } else {
      LOG.info("Field name: " + fieldName + " is not supported for entity Project");
      throw new ParameterEntityException(projectId, Project.class, fieldName, val, "is not supported for the entity Project", null);
    }

    Project obj = daoHandler.getProjectHandler().update(project);
    return obj;
  }

  @Override
  @ExoTransactional
  public void deleteProject(long id, boolean deleteChild) throws EntityNotFoundException {
    deleteProject(getProject(id), deleteChild);
  }

  @Override
  @ExoTransactional
  public void deleteProject(Project project, boolean deleteChild) {
    if (!deleteChild && project.getChildren() != null) {
      Project parent = project.getParent();
      for (Project child : project.getChildren()) {
        child.setParent(parent);
      }
      project.getChildren().clear();
    }
    daoHandler.getProjectHandler().delete(project);
  }

  @Override
  @ExoTransactional
  public Project cloneProject(long id, boolean cloneTask) throws EntityNotFoundException {

    Project project = getProject(id); //Can throw ProjectNotFoundException

    Project newProject = project.clone(cloneTask);
    createProject(newProject);

    return newProject;

  }

  @Override
  public Project getProject(Long id) throws EntityNotFoundException {

    Project project = daoHandler.getProjectHandler().find(id);
    if (project == null) throw new EntityNotFoundException(id, Project.class);

    return project;

  }

  @Override
  public List<Project> findProjects(List<String> memberships, String keyword, OrderBy order) {
    return daoHandler.getProjectHandler().findAllByMembershipsAndKeyword(memberships, keyword, order);
  }
}

