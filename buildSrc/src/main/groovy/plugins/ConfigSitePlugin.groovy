package plugins


import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

class ConfigSitePlugin implements Plugin<Project> {
  @Override
  void apply(Project target) {

    if (target != target.rootProject) throw new GradleException("Can only apply ConfigSitePlugin to root project")

    target.with {
      def dokkaDir = layout.buildDirectory.dir("dokka/html")
      def siteDir = layout.buildDirectory.dir("site")

      tasks.register("clearDokkaDir", Delete) {
        delete(dokkaDir)
        doLast { dokkaDir.get().asFile.mkdirs() }
      }

      tasks.register("clearSiteDir", Delete) {
        delete(siteDir)
        doLast { siteDir.get().asFile.mkdirs() }
      }

      tasks.named("dokkaGeneratePublicationHtml") {
        outputDirectory.set(dokkaDir)
      }

      tasks.register("copyReadmes", Copy) {
        from(file("docs/"))
        exclude(".gitignore", "_site/", "_config.yml")
        into(siteDir)
      }

      tasks.register("configSite") {
        dependsOn("copyReadmes")
        doLast {
          siteDir.get().file("_config.yml").asFile.write(Config.Site.generateJekyllConfig(target))
        }
      }

      tasks.register("chopChangelog") {
        doLast {
          def list = project.file("$rootDir/docs/CHANGELOG.md").readLines()
          def indices = list.findIndexValues { it.startsWith("###") }
          def newList = list.subList(indices.get(0).toInteger()+1, indices.get(1).toInteger())
          def newContent = "### Changelog\n" + newList.join("\n") + "\n\nFull changelog available at https://episode6.github.io/reflective-mockk/CHANGELOG.html"
          project.file(layout.buildDirectory.asFile.get().path).mkdirs()
          def choppedFile = project.file("${layout.buildDirectory.asFile.get().path}/VERSION_CHANGELOG.md")
          choppedFile.createNewFile()
          choppedFile.write(newContent)
        }
      }

      tasks.register("syncDocs") {
        dependsOn("dokkaGeneratePublicationHtml", "configSite", "chopChangelog")
      }
    }
  }
}
