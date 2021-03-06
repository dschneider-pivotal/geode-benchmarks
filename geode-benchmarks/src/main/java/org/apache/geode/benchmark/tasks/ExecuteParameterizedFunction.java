/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.benchmark.tasks;

import java.io.Serializable;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import benchmark.geode.data.FunctionWithArguments;
import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;

public class ExecuteParameterizedFunction extends BenchmarkDriverAdapter implements Serializable {

  private Region region;
  long keyRange;
  long functionIDRange;
  private Function function;

  public ExecuteParameterizedFunction(long keyRange, long functionIDRange) {
    this.keyRange = keyRange;
    this.functionIDRange = functionIDRange;
    this.function = new FunctionWithArguments();
  }

  @Override
  public void setUp(BenchmarkConfiguration cfg) throws Exception {
    super.setUp(cfg);
    ClientCache cache = ClientCacheFactory.getAnyInstance();
    region = cache.getRegion("region");
    FunctionService.registerFunction(function);
  }

  @Override
  public boolean test(Map<Object, Object> ctx) throws Exception {
    long minId = ThreadLocalRandom.current().nextLong(0, this.keyRange - functionIDRange);
    long maxId = minId + functionIDRange;
    Map<String, Long> argumentMap = new HashMap<>();
    argumentMap.put("maxID", maxId);
    argumentMap.put("minID", minId);
    ResultCollector resultCollector = FunctionService
        .onRegion(region)
        .setArguments(argumentMap)
        .execute(function);
    List results = (List) resultCollector.getResult();
    validateResults(results, minId, maxId);
    return true;

  }

  private void validateResults(List results, long minId, long maxId)
      throws UnexpectedException {
    for (Object result : results) {
      ArrayList<Long> IDs = (ArrayList<Long>) result;
      for (Long id : IDs) {
        if (id < minId || id > maxId) {
          throw new UnexpectedException("Invalid ID value received [minID= " + minId
              + " maxID= " + maxId + " ] Portfolio ID received = " + id);
        }
      }
    }
  }
}
