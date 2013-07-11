/*
 * Copyright (c) 2013 Malhar Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datatorrent.lib.sql;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to implement column equal operator.
 */
public class SelectEqualCondition extends SelectCondition
{
  /**
   * Equal column map. 
   */
	private HashMap<String, Object> equalMap = new  HashMap<String, Object>();
	
	/**
	 * Add column equal condition.
	 */
	public void addEqualValue(String column, Object value) 
	{
		equalMap.put(column, value);
	}

	/**
	 * Check valid row.
	 */
	@Override
	public boolean isValidRow(HashMap<String, Object> row)
	{
		// no conditions  
		if (equalMap.size() == 0) return true;
		
		// compare each condition value
		for (Map.Entry<String, Object> entry : equalMap.entrySet()) {
			if (!row.containsKey(entry.getKey())) return false;
			Object value = row.get(entry.getKey());
			if (entry.getValue() == null) {
				if (value == null) return true;
				return false;
			}
			if (value == null) return false;
			if (!entry.getValue().equals(value)) return false;
		}
		return true;
	}
}
