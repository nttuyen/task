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

  Project updateProjectInfo(long id, String param, String[] values)
      throws ProjectNotFoundException, ParameterEntityException;

  void deleteProject(long projectId, boolean deleteChild) throws ProjectNotFoundException;
  void deleteProject(Project project, boolean deleteChild);

  Project cloneProjectById(long id, boolean cloneTask) throws ProjectNotFoundException;

  Project getProjectById(Long id) throws ProjectNotFoundException;

  //TODO: move this method to TaskService?
  Task createTaskToProjectId(long id, Task task) throws ProjectNotFoundException;

  /**
   * TODO: This method is not used any more
   * @param id
   * @param permission
   * @param type
   * @return
   * @throws ProjectNotFoundException
   * @throws NotAllowedOperationOnEntityException
   */
  /*@Deprecated
  Project removePermissionFromProjectId(Long id, String permission, String type)
      throws ProjectNotFoundException, NotAllowedOperationOnEntityException;*/

  /**
   * TODO: This method is not used any more
   * @param id
   * @param permissions
   * @param type
   * @return
   * @throws ProjectNotFoundException
   * @throws NotAllowedOperationOnEntityException
   */
  /*@Deprecated
  Project addPermissionsFromProjectId(Long id, String permissions, String type)
      throws ProjectNotFoundException, NotAllowedOperationOnEntityException;*/
  
  List<Project> getProjectTreeByMembership(List<String> memberships);
 
  List<Project> findProjectByKeyWord(Identity identity, String keyword, OrderBy order);

}
