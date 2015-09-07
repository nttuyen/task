package org.exoplatform.task.service;

import java.util.List;

import org.exoplatform.services.security.Identity;
import org.exoplatform.task.dao.OrderBy;
import org.exoplatform.task.domain.Project;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.exception.NotAllowedOperationOnEntityException;
import org.exoplatform.task.exception.ParameterEntityException;
import org.exoplatform.task.exception.ProjectNotFoundException;

/**
 * Created by TClement on 6/3/15.
 */
public interface ProjectService {

  Project createProject(Project project, boolean createDefaultStatus);
  Project createProject(Project project, long parentId) throws ProjectNotFoundException;

  Project saveProjectField(long projectId, String fieldName, String[] values)
      throws ProjectNotFoundException, ParameterEntityException;

  void deleteProject(long projectId, boolean deleteChild) throws ProjectNotFoundException;
  void deleteProject(Project project, boolean deleteChild);

  Project cloneProject(long projectId, boolean cloneTask) throws ProjectNotFoundException;

  Project getProject(Long projectId) throws ProjectNotFoundException;

  List<Project> getProjectTreeByMembership(List<String> memberships);

  List<Project> findProjectByKeyWord(Identity identity, String keyword, OrderBy order);

}
