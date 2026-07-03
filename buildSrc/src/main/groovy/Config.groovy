import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom

class Config {
  class Jvm {
    static String name = "17"
    static JavaVersion targetCompat = JavaVersion.VERSION_17
    static JavaVersion sourceCompat = JavaVersion.VERSION_17
  }

  class Kotlin {
    static String compilerArgs = "-opt-in=kotlin.RequiresOptIn"
  }

  class Site {
    static String generateJekyllConfig(Project project) {
      return """
        theme: jekyll-theme-cayman
        title: ${project.name}
        description: ${project.rootProject.description}
        version: ${project.version}
        docsDir: https://episode6.github.io/reflective-mockk/docs/${if (Maven.isReleaseBuild(project)) "v${project.version}" else "main"}
        kotlinVersion: ${project.libs.versions.kotlin.core.get()}
""".stripIndent()
    }
  }

  class Maven {
    static String projectGHUrl = "episode6/reflective-mockk"

    static void applyPomConfig(Project project, MavenPom pom) {
      pom.with {
        name = project.name
        url = "https://github.com/${projectGHUrl}"
        licenses {
          license {
            name = "The MIT License (MIT)"
            url = "https://github.com/${projectGHUrl}/blob/main/LICENSE"
            distribution = "repo"
          }
        }
        developers {
          developer {
            id = "episode6"
            name = "episode6, Inc."
          }
        }
        scm {
          url = "https://github.com/${projectGHUrl}"
          connection = "scm:git:https://github.com/${projectGHUrl}.git"
          developerConnection = "scm:git:ssh://github.com/${projectGHUrl}.git"
        }
      }
      project.afterEvaluate {
        pom.description = project.description ?: project.rootProject.description
      }
    }

    static boolean isReleaseBuild(Project project) {
      return project.version.contains("SNAPSHOT") == false
    }

    static String getRepoUrl(Project project) {
      if (isReleaseBuild(project)) {
        return "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
      } else {
        return "https://central.sonatype.com/repository/maven-snapshots/"
      }
    }
  }
}
