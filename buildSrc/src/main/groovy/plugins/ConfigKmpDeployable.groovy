package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign

class ConfigKmpDeployable implements Plugin<Project> {
  @Override
  void apply(Project target) {
    target.with {
      plugins.with {
        apply(ConfigKmp)
        apply(CommonDeployable)
      }

      // mitigate gradle warnings by ensuring all pub tasks depend on all sign tasks
      def signTasks = tasks.withType(Sign)
      tasks.withType(AbstractPublishToMaven).forEach { pubTask ->
        signTasks.forEach {
          pubTask.dependsOn(it)
        }
      }

      afterEvaluate { project ->
        publishing {
          publications.withType(MavenPublication) {
            Config.Maven.applyPomConfig(project, pom)
            artifact javadocJar
          }
        }
      }
    }
  }
}
