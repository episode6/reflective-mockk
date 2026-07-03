plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.dokka)
  id("config-site")
  id("config-multi-deploy")
}

description = "Stub mockks programmatically using kotlin-reflect."
version = self.versions.name.get()
group = "com.episode6.reflectivemockk"

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}

kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        api(libs.mockk.core)
        api(kotlin("reflect"))
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(libs.kotlinx.coroutines.test)
      }
    }
  }
}
