/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.launcher.daemon

class DaemonNativeServicesIntegrationTest extends DaemonIntegrationSpec {
    def "native services use daemon base dir" () {
        given:
        executer.requireOwnGradleUserHomeDir()
        def nativeDir = new File(executer.gradleUserHomeDir, "native")

        expect:
        !nativeDir.exists()

        when:
        executer.withArguments("-q").run()

        then:
        nativeDir.directory
    }
}
