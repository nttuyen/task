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

  Status getStatus(long statusId);

  Status getDefaultStatus(long projectId);
    
  Status createStatus(Project project, String status);
  
  Status deleteStatus(long statusId) throws StatusNotFoundException, NotAllowedOperationOnEntityException;
  
  Status updateStatus(long statusId, String statusName) throws StatusNotFoundException, NotAllowedOperationOnEntityException;
}
