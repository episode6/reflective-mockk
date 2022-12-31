plugins {
  alias(libs.plugins.kmp)
  alias(libs.plugins.dokka)
  id("config-site")
  id("config-kmp-deploy")
}

description = "Stub mockks programmatically using kotlin-reflect."
version = self.versions.name.get()
allprojects {
  group = "com.episode6.reflectivemock"
  version = rootProject.version
}

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
    val jvmTest by getting {
      dependencies {
        implementation(libs.junit5)
        implementation(libs.assertk)
      }
    }
  }
}
