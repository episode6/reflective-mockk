package plugins


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

class ConfigSite implements Plugin<Project> {
  @Override
  void apply(Project target) {

    if (target != target.rootProject) throw GradleException("Can only apply ConfigSitePlugin to root project")

    target.with {
      def dokkaDir = "${rootProject.buildDir}/dokka/html"
      def siteDir = "${rootProject.buildDir}/site"

      tasks.create("clearDokkaDir", Delete) {
        delete(dokkaDir)
        doLast { file(dokkaDir).mkdirs() }
      }

      tasks.create("clearSiteDir", Delete) {
        delete(siteDir)
        doLast { file(siteDir).mkdirs() }
      }

      tasks.dokkaHtmlMultiModule {
        outputDirectory.set(file(dokkaDir))
      }

      tasks.create("copyReadmes", Copy) {
        from(file("docs/"))
        exclude(".gitignore", "_site/", "_config.yml")
        into(file(siteDir))
      }

      tasks.create("configSite") {
        dependsOn("copyReadmes")
        doLast {
          file("$siteDir/_config.yml").write(Config.Site.generateJekyllConfig(target))
        }
      }

      tasks.create("chopChangelog") {
        doLast {
          def list = project.file("$rootDir/docs/CHANGELOG.md").readLines()
          def indices = list.findIndexValues { it.startsWith("###") }
          def newList = list.subList(indices.get(0).toInteger()+1, indices.get(1).toInteger())
          def newContent = "### Changelog\n" + newList.join("\n")
          project.file(rootProject.buildDir).mkdirs()
          def choppedFile = project.file("$rootProject.buildDir/VERSION_CHANGELOG.md")
          choppedFile.createNewFile()
          choppedFile.write(newContent)
        }
      }

      tasks.create("syncDocs") {
        dependsOn("dokkaHtmlMultiModule", "configSite", "chopChangelog")
      }
    }
  }
}
