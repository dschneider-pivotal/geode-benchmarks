/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.geode.benchmark.tasks;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import benchmark.geode.data.Portfolio;
import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

public class PutTask extends BenchmarkDriverAdapter implements Serializable {

  private Region<Object, Object> region;

  private long keyRange;

  public PutTask(long keyRange) {
    this.keyRange = keyRange;
  }

  @Override
  public void setUp(BenchmarkConfiguration cfg) throws Exception {
    super.setUp(cfg);
    ClientCache cache = ClientCacheFactory.getAnyInstance();
    region = cache.getRegion("region");
  }

  @Override
  public boolean test(Map<Object, Object> ctx) throws Exception {
    long key = ThreadLocalRandom.current().nextLong(0, this.keyRange);
    region.put(key, new Portfolio(key));
    return true;
  }
}
