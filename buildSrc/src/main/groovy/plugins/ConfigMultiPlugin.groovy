package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConfigMultiPlugin implements Plugin<Project> {
  @Override
  void apply(Project target) {
    target.with {
      plugins.with {
        apply("org.jetbrains.kotlin.multiplatform")
      }
      kotlin {
        jvm  {
          def jvmTargetClass = it.class.classLoader.loadClass("org.jetbrains.kotlin.gradle.dsl.JvmTarget")
          compilerOptions {
            jvmTarget.set(jvmTargetClass.fromTarget(Config.Jvm.name))
            freeCompilerArgs.add(Config.Kotlin.compilerArgs)
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

        sourceSets {
          commonMain {}
          commonTest {
            dependencies {
              implementation(kotlin("test"))
              implementation(libs.assertk.core)
            }
          }
          jvmMain {}
          jvmTest {
            dependencies {
              implementation(libs.junit5.core)
              runtimeOnly(libs.junit5.launcher)
              implementation(libs.assertk.jvm)
            }
          }
        }

      }
    }
  }
}
