plugins {
  alias(libs.plugins.kmp)
  alias(libs.plugins.dokka)
  id("config-site")
  id("config-kmp-deploy")
}

description = "Stub mockks programmatically using kotlin-reflect."
version = self.versions.name.get()
group = "com.episode6.reflectivemockk"

tasks.wrapper {
  gradleVersion = libs.versions.gradle.get()
  distributionType = Wrapper.DistributionType.ALL
}

kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        api(libs.mockk)
        api(kotlin("reflect"))
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.assertk)
        implementation(libs.kotlinx.coroutines.test)
      }
    }
  }
}
