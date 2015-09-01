package org.exoplatform.task.service;

import java.util.List;
import java.util.Set;

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

  /**
   * use {@link org.exoplatform.task.utils.ProjectUtil#newProjectInstance(String, String, java.util.Set, java.util.Set)}
   * and {@link #createProject(org.exoplatform.task.domain.Project, long)}
   * @param name
   * @param description
   * @param parentId
   * @param username
   * @return
   * @throws ProjectNotFoundException
   */
  @Deprecated
  Project createDefaultStatusProjectWithManager(String name, String description, Long parentId, String username)
      throws ProjectNotFoundException;

  /**
   * use {@link org.exoplatform.task.utils.ProjectUtil#newProjectInstance(String, String, java.util.Set, java.util.Set)}
   * and {@link #createProject(org.exoplatform.task.domain.Project, long)}
   * @param parentId
   * @param name
   * @param description
   * @param managers
   * @param participators
   * @return
   * @throws ProjectNotFoundException
   */
  @Deprecated
  Project createDefaultStatusProjectWithAttributes(Long parentId, String name, String description,
                                                   Set<String> managers, Set<String> participators)
      throws ProjectNotFoundException;

  /**
   * use {@link #createProject(org.exoplatform.task.domain.Project, boolean)}
   * @param project
   * @return
   */
  @Deprecated
  Project createDefaultStatusProject(Project project);

  /**
   * use {@link #createProject(org.exoplatform.task.domain.Project, boolean)}
   * @param project
   * @return
   */
  @Deprecated
  Project createProject(Project project);
  Project createProject(Project project, boolean createDefaultStatus);
  Project createProject(Project project, long parentId) throws ProjectNotFoundException;

  Project updateProjectInfo(long id, String param, String[] values)
      throws ProjectNotFoundException, ParameterEntityException;

  /**
   * use {@link #deleteProject(long, boolean)}
   * @param id
   * @param deleteChild
   * @throws ProjectNotFoundException
   */
  @Deprecated
  void deleteProjectById(long id, boolean deleteChild) throws ProjectNotFoundException;

  void deleteProject(long id, boolean deleteChild) throws ProjectNotFoundException;
  void deleteProject(Project project, boolean deleteChild);

  Project cloneProjectById(long id, boolean cloneTask) throws ProjectNotFoundException;

  Project getProjectById(Long id) throws ProjectNotFoundException;

  //TODO: move this method to TaskService?
  Task createTaskToProjectId(long id, Task task) throws ProjectNotFoundException;

  /**
   * Use {@link org.exoplatform.task.service.TaskService#findTaskByQuery(org.exoplatform.task.dao.TaskQuery)}
   * @param ids
   * @param orderBy
   * @return
   */
  @Deprecated
  List<Task> getTasksByProjectId(List<Long> ids, OrderBy orderBy);

  /**
   * use {@link org.exoplatform.task.service.TaskService#findTaskByQuery(org.exoplatform.task.dao.TaskQuery)}
   * @param ids
   * @param orderBy
   * @param keyword
   * @return
   */
  @Deprecated
  List<Task> getTasksWithKeywordByProjectId(List<Long> ids, OrderBy orderBy, String keyword);

  /**
   * TODO: This method is not used any more
   * @param id
   * @param permission
   * @param type
   * @return
   * @throws ProjectNotFoundException
   * @throws NotAllowedOperationOnEntityException
   */
  @Deprecated
  Project removePermissionFromProjectId(Long id, String permission, String type)
      throws ProjectNotFoundException, NotAllowedOperationOnEntityException;

  /**
   * TODO: This method is not used any more
   * @param id
   * @param permissions
   * @param type
   * @return
   * @throws ProjectNotFoundException
   * @throws NotAllowedOperationOnEntityException
   */
  @Deprecated
  Project addPermissionsFromProjectId(Long id, String permissions, String type)
      throws ProjectNotFoundException, NotAllowedOperationOnEntityException;
  
  List<Project> getProjectTreeByMembership(List<String> memberships);
 
  List<Project> findProjectByKeyWord(Identity identity, String keyword, OrderBy order);

}
