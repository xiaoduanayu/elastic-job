/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.plugin.job.type.dataflow.throughput.streaming;

import com.dangdang.ddframe.job.internal.job.dataflow.AbstractDataFlowElasticJob;
import com.dangdang.ddframe.job.plugin.job.type.ElasticJobAssert;
import com.dangdang.ddframe.job.plugin.job.type.fixture.FooStreamingBatchThroughputDataFlowElasticJob;
import com.dangdang.ddframe.job.plugin.job.type.fixture.JobCaller;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class StreamingBatchThroughputDataFlowElasticJobTest extends AbstractStreamingThroughputDataFlowElasticJobTest {
    
    @Override
    protected boolean isStreamingProcess() {
        return true;
    }
    
    @Override
    protected AbstractDataFlowElasticJob createDataFlowElasticJob(final JobCaller jobCaller) {
        return new FooStreamingBatchThroughputDataFlowElasticJob(jobCaller);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertExecuteWhenFetchDataIsNotEmpty() {
        when(getJobCaller().fetchData()).thenReturn(Arrays.<Object>asList(1, 2, 3), Collections.emptyList());
        when(getJobFacade().isEligibleForJobRunning()).thenReturn(true);
        when(getJobCaller().processData(1)).thenReturn(false);
        when(getJobCaller().processData(2)).thenReturn(true);
        when(getJobCaller().processData(3)).thenThrow(new IllegalStateException());
        getDataFlowElasticJob().execute();
        verify(getJobCaller(), times(2)).fetchData();
        verify(getJobCaller()).processData(1);
        verify(getJobCaller()).processData(2);
        ElasticJobAssert.verifyForIsNotMisfire(getJobFacade(), getShardingContext());
        ElasticJobAssert.assertProcessCountStatistics(1, 2);
    }

}
