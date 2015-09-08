package org.exoplatform.task.service;

import java.util.List;

import org.exoplatform.task.domain.Project;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.exception.EntityNotFoundException;
import org.exoplatform.task.exception.NotAllowedOperationOnEntityException;

public interface StatusService {

  /**
   * TODO: Move this method to Util class?
   * @return
   */
  List<String> getDefaultStatus();

  Status getStatus(long statusId);

  Status getDefaultStatus(long projectId);
    
  Status createStatus(Project project, String status);
  
  Status deleteStatus(long statusId) throws EntityNotFoundException, NotAllowedOperationOnEntityException;
  
  Status updateStatus(long statusId, String statusName) throws EntityNotFoundException, NotAllowedOperationOnEntityException;
}
