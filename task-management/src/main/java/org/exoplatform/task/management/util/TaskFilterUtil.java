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

package org.exoplatform.task.management.util;

import org.exoplatform.task.domain.Priority;
import org.exoplatform.task.domain.ProjectPreference;
import org.exoplatform.task.management.model.TaskFilterData;
import org.exoplatform.task.management.model.ViewType;
import org.exoplatform.task.service.UserService;
import org.exoplatform.task.util.ProjectUtil;
import org.exoplatform.task.util.TaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class TaskFilterUtil {
  private static final String PREFIX = "filter_";

  public static TaskFilterData.Filter getFilter(String username, TaskFilterData.FilterKey key, UserService userService) {
    Object[] p = parseKey(key);
    ProjectPreference.Type type = (ProjectPreference.Type)p[0];
    long id = (Long)p[1];

    if (id == -100) {
      return new TaskFilterData.Filter();
    }

    ProjectPreference pref = userService.getProjectPreference(username, type, id);

    TaskFilterData.Filter filter = new TaskFilterData.Filter();
    if (pref != null) {
      Map<String, String> attrs = pref.getAttributes();

      String viewType = getFilterAttr("view_type", attrs);
      filter.setViewType(viewType != null ? ViewType.valueOf(viewType.toUpperCase()) : ViewType.LIST);

      filter.setEnabled(parseBoolean("enabled", attrs));
      filter.setShowCompleted(parseBoolean("show_completed", attrs));
      filter.setKeyword(getFilterAttr("keyword", attrs));
      filter.setLabel(parseListLong("labels", attrs));
      filter.setTag(parseListString("tags", attrs));
      filter.setStatus(parseLong("status", attrs));
      filter.setAssignee(parseListString("assignees", attrs));
      filter.setDue(parseDue("due_date", attrs));
      filter.setPriority(parsePriority("priority", attrs));
    }
    return filter;
  }

  public static void saveFilter(String username, TaskFilterData.FilterKey key, TaskFilterData.Filter filter, UserService userService) {
    Object[] p = parseKey(key);
    ProjectPreference.Type type = (ProjectPreference.Type)p[0];
    long id = (Long)p[1];

    // Do not save with this id because the key is not valid (it require projectId or labelId must not be null)
    if (id == -100) {
      return;
    }

    ProjectPreference pref = userService.getProjectPreference(username, type, id);
    if (pref == null) {
      pref = new ProjectPreference();
      pref.setUsername(username);
      pref.setType(type);
      pref.setRefId(id);
    }

    Map<String, String> attr = pref.getAttributes();
    putAttr("view_type", filter.getViewType() == null ? ViewType.LIST.name() : filter.getViewType().name(), attr);
    putAttr("enabled", filter.isEnabled(), attr);
    putAttr("show_completed", filter.isShowCompleted(), attr);
    putAttr("keyword", filter.getKeyword(), attr);
    putList("labels", filter.getLabel(), attr);
    putList("tags", filter.getTag(), attr);
    putAttr("status", filter.getStatus(), attr);
    putList("assignees", filter.getAssignee(), attr);
    putAttr("due_date", filter.getDue() != null ? filter.getDue().name() : null, attr);
    putAttr("priority", filter.getPriority() != null ? filter.getPriority().name() : null, attr);

    pref.setAttributes(attr);

    userService.saveProjectPreference(pref);
  }

  private static Object[] parseKey(TaskFilterData.FilterKey key) {
    ProjectPreference.Type type = ProjectPreference.Type.PROJECT;
    long id = -100;

    if (key.getLabelId() != null) {
      type = ProjectPreference.Type.LABEL;
      id = key.getLabelId();

    } else if (key.getProjectId() != null) {
      id = key.getProjectId();
      type = ProjectPreference.Type.PROJECT;

      if (id == ProjectUtil.INCOMING_PROJECT_ID) {
        type = ProjectPreference.Type.INCOMING;

      } else if (id == ProjectUtil.TODO_PROJECT_ID) {
        TaskUtil.DUE due = key.getDueDate();
        if (due == null) {
          type = ProjectPreference.Type.ALL_TASK;
        } else {
          type = ProjectPreference.Type.valueOf(due.name().toUpperCase());
        }
      }
    }

    return new Object[] {type, id};
  }

  private static String getFilterAttr(String name, Map<String, String> attrs) {
    if (name == null || name.trim().isEmpty()) {
      return null;
    }
    return attrs.get(PREFIX + name.toLowerCase());
  }
  private static boolean parseBoolean(String name, Map<String, String> attrs) {
    String s = getFilterAttr(name, attrs);
    return Boolean.valueOf(s).booleanValue();
  }
  private static long parseLong(String name, Map<String, String> attrs) {
    long ret = 0;
    String val = getFilterAttr(name, attrs);
    if (val != null) {
      try {
        ret = Long.parseLong(val);
      } catch (NumberFormatException ex) {
        //Ignore this value
      }
    }
    return ret;
  }
  private static TaskUtil.DUE parseDue(String name, Map<String, String> attrs) {
    String val = getFilterAttr(name, attrs);
    if (val == null || val.isEmpty()) {
      return null;
    } else {
      return TaskUtil.DUE.valueOf(val.toUpperCase());
    }
  }
  private static Priority parsePriority(String name, Map<String, String> attrs) {
    String val = getFilterAttr(name, attrs);
    if (val == null || val.isEmpty()) {
      return null;
    } else {
      return Priority.valueOf(val.toUpperCase());
    }
  }
  private static List<String> parseListString(String name, Map<String, String> attrs) {
    String val = getFilterAttr(name, attrs);
    List<String> list = new ArrayList<>();
    if (val != null && val.trim().length() > 0) {
      for (String s : val.split(",")) {
        if (s.trim().length() > 0) {
          list.add(s.trim());
        }
      }
    }
    return list;
  }
  private static List<Long> parseListLong(String name, Map<String, String> attrs) {
    List<Long> list = new ArrayList<>();

    String ids = getFilterAttr(name, attrs);
    if (ids != null && ids.trim().length() > 0) {
      String[] arr = ids.split(",");
      for (String s : arr) {
        try {
          list.add(Long.parseLong(s.trim()));
        } catch (NumberFormatException ex) {
          // Ignore this exception
        }
      }
    }

    return list;
  }

  private static void putAttr(String name, Object value, Map<String, String> attrs) {
    if (value == null || String.valueOf(value).isEmpty()) {
      attrs.remove(PREFIX + name);
    } else {
      attrs.put(PREFIX + name, String.valueOf(value));
    }
  }
  private static void putList(String name, List ids, Map<String, String> attrs) {
    StringBuilder sb = new StringBuilder();

    if (ids != null && ids.size() > 0) {
      for (Object id : ids) {
        sb.append(String.valueOf(id)).append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
    }

    putAttr(name, sb.toString(), attrs);
  }
}
