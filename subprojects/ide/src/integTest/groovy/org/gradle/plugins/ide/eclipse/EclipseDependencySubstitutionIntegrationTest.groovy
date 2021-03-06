/*
 * Copyright 2015 the original author or authors.
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
package org.gradle.plugins.ide.eclipse

import org.gradle.integtests.fixtures.TestResources
import org.junit.Rule
import org.junit.Test

class EclipseDependencySubstitutionIntegrationTest extends AbstractEclipseIntegrationTest {
    @Rule
    public final TestResources testResources = new TestResources(testDirectoryProvider)

    @Test
    void "external dependency substituted with project dependency"() {
        runEclipseTask("include 'project1', 'project2'", """
allprojects {
    apply plugin: "java"
    apply plugin: "eclipse"
}

project(":project2") {
    dependencies {
        compile group: "junit", name: "junit", version: "4.7"
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution.withModule("junit:junit") {
            it.useTarget project(":project1")
        }
    }
}
""")

        def classpath = classpath("project2")
        assert classpath.projects == ["/project1"]
        assert classpath.libs == []
    }

    @Test
    void "transitive external dependency substituted with project dependency"() {
        mavenRepo.module("org.gradle", "module1").dependsOn("module2").publish()
        mavenRepo.module("org.gradle", "module2").publish()

        runEclipseTask("include 'project1', 'project2'", """
allprojects {
    apply plugin: "java"
    apply plugin: "eclipse"
}

project(":project2") {
    repositories {
        maven { url "${mavenRepo.uri}" }
    }

    dependencies {
        compile "org.gradle:module1:1.0"
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution.withModule("org.gradle:module2") {
            it.useTarget project(":project1")
        }
    }
}
""")

        def classpath = classpath("project2")
        assert classpath.libs*.jarName == ["module1-1.0.jar"]
        assert classpath.projects == ["/project1"]
    }


    @Test
    void "project dependency substituted with external dependency"() {
        runEclipseTask("include 'project1', 'project2'", """
 allprojects {
    apply plugin: "java"
    apply plugin: "eclipse"
}

project(":project2") {
    repositories {
        mavenCentral()
    }

    dependencies {
        compile project(":project1")
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution.withProject(":project1") {
            it.useTarget "junit:junit:4.7"
        }
    }
}
""")

        def classpath = classpath("project2")
        assert classpath.projects == []
        assert classpath.libs*.jarName == ["junit-4.7.jar"]
    }
}
