plugins {
  `java-gradle-plugin`
}

gradlePlugin {
  plugins {
    create("ConfigSite") {
      id = "config-site"
      implementationClass = "plugins.ConfigSite"
    }
    create("ConfigJvm") {
      id = "config-jvm"
      implementationClass = "plugins.ConfigJvm"
    }
    create("ConfigJvmDeployable") {
      id = "config-jvm-deploy"
      implementationClass = "plugins.ConfigJvmDeployable"
    }
    create("ConfigKmp") {
      id = "config-kmp"
      implementationClass = "plugins.ConfigKmp"
    }
    create("ConfigKmpAllDeployable") {
      id = "config-kmp-deploy"
      implementationClass = "plugins.ConfigKmpDeployable"
    }
    create("ConfigAndroidCompose") {
      id = "config-android-compose"
      implementationClass = "plugins.ConfigAndroidCompose"
    }
    create("ConfigAndroidApp") {
      id = "config-android-app"
      implementationClass = "plugins.ConfigAndroidApp"
    }
    create("ConfigAndroidLib") {
      id = "config-android-lib"
      implementationClass = "plugins.ConfigAndroidLib"
    }
    create("ConfigAndroidLibDeployable") {
      id = "config-android-lib-deploy"
      implementationClass = "plugins.ConfigAndroidLibDeployable"
    }
  }
}
