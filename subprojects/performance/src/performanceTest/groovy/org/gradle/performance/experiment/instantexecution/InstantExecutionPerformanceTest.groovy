/*
 * Copyright 2019 the original author or authors.
 *
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
 */

package org.gradle.performance.experiment.instantexecution

import org.gradle.performance.AbstractCrossBuildPerformanceTest
import spock.lang.Unroll


class InstantExecutionPerformanceTest extends AbstractCrossBuildPerformanceTest {

    @Unroll
    def "help on #testProject with classic and instant execution"() {
        given:
        runner.testGroup = "instant execution"
        runner.baseline {
            warmUpCount = warmUpRuns
            invocationCount = runs
            projectName(testProject).displayName("classic").invocation {
                tasksToRun("help").args("-PbuildSrcCheck=false")
            }
        }
        runner.buildSpec {
            warmUpCount = warmUpRuns
            invocationCount = runs
            projectName(testProject).displayName("instant").invocation {
                tasksToRun("help").args("-PbuildSrcCheck=false", "-Dorg.gradle.unsafe.instant-execution")
            }
        }

        when:
        def results = runner.run()

        then:
        results

        where:
        testProject             | warmUpRuns | runs
        "largeJavaBuildBuilder" | 5          | 5
    }

}
