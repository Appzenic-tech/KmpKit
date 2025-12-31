plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
//    `maven-publish`
}

kotlin {

    androidLibrary {
        namespace = "com.appzenic.camera_ui_kmp"
        compileSdk = 36
        minSdk = 26

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "camera-ui-kmpKits"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.bundles.camerax)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
            }
        }
    }
}

//publishing {
//    publications {
//        withType<MavenPublication> {
//            groupId = "com.github.appzenic"
////            artifactId = "camera-ui-kmp"
//            version = "0.0.10"
////            artifact("${buildDir}/outputs/aar/camera-ui-kmp.aar")
//
//            pom {
//                name.set("Camera UI KMP")
//                description.set("Camera UI components for Compose Multiplatform")
//                url.set("https://github.com/Appzenic-tech/camera-ui-kmp")
//            }
//        }
//    }
//
//    repositories {
//        maven {
//            name = "GithubPackages"
//            url = uri("https://maven.pkg.github.com/Appzenic-tech/camera-ui-kmp")
//            credentials {
//                username = project.findProperty("gpr.user") as String?
//                password = project.findProperty("gpr.key") as String?
////                username = System.getenv("GITHUB_USER") ?:""
////                password = System.getenv("GITHUB_TOKEN")?:""
//            }
//        }
//    }
//}
