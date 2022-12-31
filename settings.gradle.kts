pluginManagement {
  repositories {
    gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
  versionCatalogs {
    create("libs") { from(files("libs.versions.toml")) }
    create("self") { from(files("self.versions.toml")) }
  }
}

rootProject.name = "reflective-mockk"
