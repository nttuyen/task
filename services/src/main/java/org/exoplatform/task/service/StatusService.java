package org.exoplatform.task.service;

import java.util.List;

import org.exoplatform.task.domain.Project;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.exception.NotAllowedOperationOnEntityException;
import org.exoplatform.task.exception.StatusNotFoundException;

public interface StatusService {

  /**
   * TODO: Move this method to Util class?
   * @return
   */
  List<String> getDefaultStatus();

  Status getStatusById(long statusId);

  /**
   * TODO: rename this method to: getDefaultStatusOfProject() ?
   * @param projectId
   * @return
   */
  Status findLowestRankStatusByProject(long projectId);
    
  Status createStatus(Project project, String status);
  
  Status deleteStatus(long statusID) throws StatusNotFoundException, NotAllowedOperationOnEntityException;
  
  Status updateStatus(long id, String name) throws StatusNotFoundException, NotAllowedOperationOnEntityException;

  /**
   * TODO: this method will never be used
   * @param statusID
   * @param otherID
   * @return
   * @throws NotAllowedOperationOnEntityException
   */
  Status swapPosition(long statusID, long otherID) throws NotAllowedOperationOnEntityException;
}
