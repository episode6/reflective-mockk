package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConfigKmp implements Plugin<Project> {
  @Override
  void apply(Project target) {
    target.with {
      plugins.with {
        if (findPlugin(libs.plugins.kmp.get().getPluginId()) == null) {
          apply(libs.plugins.kmp.get().getPluginId())
        }
      }
      def skipTargets = getSkipTargets(project)

      kotlin {
        if (!skipTargets.contains("jvm")) {
          jvm {
            compilations.all {
              kotlinOptions {
                jvmTarget = Config.Jvm.name
                freeCompilerArgs += Config.Kotlin.compilerArgs
              }
            }
            java {
              sourceCompatibility = Config.Jvm.sourceCompat
              targetCompatibility = Config.Jvm.targetCompat
            }
            jvmTest {
              useJUnitPlatform()
              testLogging {
                events "passed", "skipped", "failed"
              }
            }
          }
        }
        if (!skipTargets.contains("js")) {
          js(IR) {
            nodejs()
            browser()
            binaries.library()
            compilations.all {
              kotlinOptions {
                freeCompilerArgs += Config.Kotlin.compilerArgs
              }
            }
          }
        }


        for (t in Config.KMPTargets.natives - skipTargets) {
          targets.add(presets.getByName(t).createTarget(t)) {
            compilations.all {
              kotlinOptions {
                freeCompilerArgs += Config.Kotlin.compilerArgs
              }
            }
          }
        }

        sourceSets {
          commonMain {}
          commonTest {
            dependencies {
              implementation(kotlin("test"))
            }
          }
          
          for (sourceSet in Config.KMPTargets.all - skipTargets) {
            getByName("${sourceSet}Main") {
              dependsOn(commonMain)
            }
          }
        }
      }
    }
  }

  private static String[] getSkipTargets(Project project) {
    def onlyTargets = project.findProperty("onlyTargets")?.split(",")
    if (onlyTargets == null) {
      return project.findProperty("skipTargets")?.split(",") ?: []
    }
    return Config.KMPTargets.all - onlyTargets
  }
}
