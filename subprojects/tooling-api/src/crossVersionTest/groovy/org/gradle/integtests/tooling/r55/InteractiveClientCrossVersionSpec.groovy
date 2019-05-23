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

package org.gradle.integtests.tooling.r55

import org.apache.commons.io.output.TeeOutputStream
import org.gradle.api.internal.tasks.userinput.UserInputHandler
import org.gradle.integtests.tooling.fixture.TargetGradleVersion
import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.integtests.tooling.fixture.ToolingApiVersion

import javax.inject.Inject

@ToolingApiVersion(">=5.5")
class InteractiveClientCrossVersionSpec extends ToolingApiSpecification {
    @TargetGradleVersion(">=5.5")
    def "client can use internal API to enable interactive input"() {
        given:
        buildFile << """
            // Use internal, version specific API
            import ${UserInputHandler.name}
            import ${Inject.name}
            
            abstract class PromptTask extends DefaultTask {
                @Inject
                abstract UserInputHandler getInputHandler()

                @TaskAction
                def run() {
                    def answer = inputHandler.selectOption("thing", ["one", "two", "three"], "two") 
                    println("answer = " + answer)
                }       
            }
            
            task prompt(type: PromptTask)
        """

        def input = new ByteArrayInputStream("1\n".bytes)
        def output = new ByteArrayOutputStream()

        when:
        withConnection { connection ->
            def build = connection.newBuild()
            build.colorOutput = true
            build.interactiveInput = true
            build.standardInput = input
            build.standardOutput = new TeeOutputStream(output, System.out)
            build.standardError = System.err
            build.forTasks("prompt")
            build.run()
        }

        then:
        output.toString().contains("answer = one")
    }

    @TargetGradleVersion("<5.5")
    def "cannot set interactive input for older Gradle version"() {
        expect: false
    }
}
