/*
 *  Copyright 2019 Pivotal, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.netflix.spinnaker.orca.clouddriver.tasks.providers.cf;

import static com.netflix.spinnaker.orca.api.pipeline.models.ExecutionType.PIPELINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableMap;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.clouddriver.KatoService;
import com.netflix.spinnaker.orca.clouddriver.model.TaskId;
import com.netflix.spinnaker.orca.pipeline.model.PipelineExecutionImpl;
import com.netflix.spinnaker.orca.pipeline.model.StageExecutionImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CloudFoundryDestroyServiceTaskTest {
  @Test
  void shouldMakeRequestToKatoToDestroyService() {
    KatoService kato = mock(KatoService.class);
    String cloudProvider = "my-cloud";
    String credentials = "cf-foundation";
    String region = "org > space";
    TaskId taskId = new TaskId("kato-task-id");
    Map<String, Object> context = new HashMap<>();
    context.put("cloudProvider", cloudProvider);
    context.put("credentials", credentials);
    context.put("region", region);
    when(kato.requestOperations(
            matches(cloudProvider),
            eq(Collections.singletonList(Collections.singletonMap("destroyService", context)))))
        .thenReturn(taskId);
    CloudFoundryDestroyServiceTask task = new CloudFoundryDestroyServiceTask(kato);

    String type = "destroyService";
    Map<String, Object> expectedContext =
        new ImmutableMap.Builder<String, Object>()
            .put("notification.type", type)
            .put("kato.last.task.id", taskId)
            .put("service.region", region)
            .put("service.account", credentials)
            .build();
    TaskResult expected =
        TaskResult.builder(ExecutionStatus.SUCCEEDED).context(expectedContext).build();

    TaskResult result =
        task.execute(
            new StageExecutionImpl(
                new PipelineExecutionImpl(PIPELINE, "orca"), "destroyService", context));

    assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
  }
}
