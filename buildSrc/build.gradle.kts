plugins {
  `java-gradle-plugin`
}

gradlePlugin {
  plugins {
    create("ConfigSite") {
      id = "config-site"
      implementationClass = "plugins.ConfigSite"
    }
    create("ConfigKmp") {
      id = "config-kmp"
      implementationClass = "plugins.ConfigKmp"
    }
    create("ConfigKmpAllDeployable") {
      id = "config-kmp-deploy"
      implementationClass = "plugins.ConfigKmpDeployable"
    }
  }
}
