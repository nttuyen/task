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

package org.exoplatform.task.management.service;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.task.domain.Priority;
import org.exoplatform.task.management.model.TaskFilterData;
import org.exoplatform.task.management.model.ViewType;
import org.exoplatform.task.util.TaskUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for store and retrieve the filter data of project or label
 */
public class TaskFilterDataStorage {
  private static final Log LOG = ExoLogger.getExoLogger(TaskFilterDataStorage.class);

  private static final Scope SCOPE = Scope.APPLICATION.id("taskManagement");

  private static final String VIEW_TYPE = "viewType";
  private static final String ENABLED = "enabled";
  private static final String SHOW_COMPLETED = "showCompleted";
  private static final String KEYWORD = "keyword";
  private static final String LABELS = "labels";
  private static final String TAGS = "tags";
  private static final String STATUS = "status";
  private static final String ASSIGNEE = "assignee";
  private static final String DUE_DATE = "dueDate";
  private static final String PRIORITY = "priority";

  @Inject
  private SettingService settingService;

  public void setSettingService(SettingService settingService) {
    this.settingService = settingService;
  }

  public TaskFilterData.Filter getFilter(String username, TaskFilterData.FilterKey key) {
    SettingValue<String> value = (SettingValue<String>) settingService.get(Context.USER.id(username), SCOPE, key.toString());

    if (value == null) {
      return new TaskFilterData.Filter();
    }

    try {
      JSONObject json = new JSONObject(value.getValue());
      TaskFilterData.Filter filter = new TaskFilterData.Filter();

      String viewType = json.optString(VIEW_TYPE);
      filter.setViewType(viewType != null ? ViewType.valueOf(viewType.toUpperCase()) : ViewType.LIST);

      filter.setEnabled(json.optBoolean(ENABLED, false));
      filter.setShowCompleted(json.optBoolean(SHOW_COMPLETED, false));
      filter.setKeyword(json.optString(KEYWORD, ""));
      filter.setLabel(optLongArr(LABELS, json));
      filter.setTag(optStringArr(TAGS, json));
      filter.setStatus(json.optLong(STATUS, 0));
      filter.setAssignee(optStringArr(ASSIGNEE, json));

      String due = json.optString(DUE_DATE);
      if (due != null && !due.isEmpty()) {
        filter.setDue(TaskUtil.DUE.valueOf(due.toUpperCase()));
      }

      String priority = json.optString(PRIORITY);
      if (priority != null && !priority.isEmpty()) {
        filter.setPriority(Priority.valueOf(priority.toUpperCase()));
      }

      return filter;
    } catch (JSONException ex) {
      LOG.error("Filter data is not valid", ex);
      return new TaskFilterData.Filter();
    }
  }

  public boolean saveFilter(String username, TaskFilterData.FilterKey key, TaskFilterData.Filter filter) {
    try {
      JSONObject json = new JSONObject();
      json.put(VIEW_TYPE, filter.getViewType().name());
      json.put(ENABLED, filter.isEnabled());
      json.put(SHOW_COMPLETED, filter.isShowCompleted());
      json.put(KEYWORD, filter.getKeyword());
      json.put(LABELS, filter.getLabel());
      json.put(TAGS, filter.getTag());
      json.put(STATUS, filter.getStatus());
      json.put(ASSIGNEE, filter.getAssignee());
      if (filter.getDue() != null) {
        json.put(DUE_DATE, filter.getDue().name());
      }
      if (filter.getPriority() != null) {
        json.put(PRIORITY, filter.getPriority().name());
      }

      SettingValue<String> value = new SettingValue<>(json.toString());
      settingService.set(Context.USER.id(username), SCOPE, key.toString(), value);

      return true;
    } catch (JSONException ex) {
      LOG.error("Can not save filter data", ex);
      return false;
    }
  }

  private List<Long> optLongArr(String name, JSONObject json) throws JSONException {
    List<Long> list = new ArrayList<>();
    JSONArray arr = json.optJSONArray(name);
    if (arr != null && arr.length() > 0) {
      for (int i = 0; i < arr.length(); i++) {
        list.add(arr.getLong(i));
      }
    }
    return list;
  }
  private List<String> optStringArr(String name, JSONObject json) throws JSONException {
    List<String> list = new ArrayList<>();
    JSONArray arr = json.optJSONArray(name);
    if (arr != null && arr.length() > 0) {
      for (int i = 0; i < arr.length(); i++) {
        list.add(arr.getString(i));
      }
    }
    return list;
  }
}
