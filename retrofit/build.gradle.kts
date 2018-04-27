import java.util.Properties

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        //用于上传maven包到jCenter中
        //https://github.com/bintray/gradle-bintray-plugin
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3")
    }
}

plugins {
    id("com.jfrog.bintray").version("1.7.3")
    java
    maven
}

java {
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
        }
    }
}

repositories {
    jcenter()
    google()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    
    compileOnly("com.google.android:android:4.1.1.4")
    compileOnly("org.codehaus.mojo:animal-sniffer-annotations:1.16")
}

val rootProjectName: String = project.name

// 这里是groupId,必须填写,一般填你唯一的包名
group = "com.fpliu"

//这个是版本号，必须填写
version = "2.4.0"

// 项目的主页,这个是说明，可随便填
val siteUrl = "https://github.com/leleliu008/$rootProjectName"

// GitHub仓库的URL,这个是说明，可随便填
val gitUrl = "https://github.com/leleliu008/$rootProjectName"


tasks {
    "install"(Upload::class) {
        repositories {
            withConvention(MavenRepositoryHandlerConvention::class) {
                mavenInstaller {
                    configuration = configurations.getByName("archives")
                    pom.project {
                        withGroovyBuilder {
                            "packaging"("jar")
                            "artifactId"(rootProjectName)
                            "name"(rootProjectName)
                            "url"(siteUrl)
                            "licenses" {
                                "license" {
                                    "name"("The Apache Software License, Version 2.0")
                                    "url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }
                            "developers" {
                                "developer" {
                                    "id"("fpliu")
                                    "name"("fpliu")
                                    "email"("leleliu008@gmail.com")
                                }
                            }
                            "scm" {
                                "connection"(gitUrl)
                                "developerConnection"(gitUrl)
                                "url"(siteUrl)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 生成jar包的task
val sourcesJarTask = task("sourcesJar", Jar::class) {
    from(java.sourceSets["main"].java.srcDirs)
    baseName = rootProjectName
    classifier = "sources"
}

// 生成jarDoc的task
val javadocTask = task("javadoc_", Javadoc::class) {
    source(java.sourceSets["main"].java.srcDirs)
//    classpath += project.files(java.)
    isFailOnError = false
}

// 生成javaDoc的jar
val javadocJarTask = task("javadocJar", Jar::class) {
    from(javadocTask.destinationDir)
    baseName = rootProjectName
    classifier = "javadoc"
}.dependsOn(javadocTask)

artifacts {
    add("archives", javadocJarTask)
    add("archives", sourcesJarTask)
}

val properties = Properties().apply { load(project.rootProject.file("local.properties").inputStream()) }
bintray {
    user = properties.getProperty("bintray.user")
    key = properties.getProperty("bintray.apikey")

    setConfigurations("archives")
    pkg = PackageConfig().apply {
        userOrg = "fpliu"
        repo = "newton"
        name = rootProjectName
        websiteUrl = siteUrl
        vcsUrl = gitUrl
        setLicenses("Apache-2.0")
        publish = true
    }
}
