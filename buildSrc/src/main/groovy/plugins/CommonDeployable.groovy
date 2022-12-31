package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

class CommonDeployable implements Plugin<Project> {
  @Override void apply(Project target) {
    target.with {
      plugins.with {
        apply(libs.plugins.dokka.get().getPluginId())
        apply("maven-publish")
        apply("signing")
      }

      kotlin {
        explicitApi()
      }

      task("deploy", dependsOn: tasks.publish)
      task("install", dependsOn: tasks.publishToMavenLocal)

      task("javadocJar", type: Jar, dependsOn: tasks.dokkaHtml) {
        archiveClassifier.set('javadoc')
        from tasks.dokkaHtml
      }

      if (Config.Maven.isReleaseBuild(target)) {
        signing {
          def signingKey = findProperty("signingKey")
          def signingPassword = findProperty("signingPassword")
          if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
          }
          sign publishing.publications
        }
      }

      publishing {
        repositories {
          maven {
            url Config.Maven.getRepoUrl(target)
            credentials {
              username findProperty("nexusUsername")
              password findProperty("nexusPassword")
            }
          }
        }
      }
    }
  }
}
