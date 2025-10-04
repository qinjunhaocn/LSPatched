import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import com.android.build.gradle.LibraryExtension

plugins {
    alias(libs.plugins.agp.lib) apply false
    alias(libs.plugins.agp.app) apply false
    alias(lspatch.plugins.compose.compiler) apply false
    alias(lspatch.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.eclipse.jgit:org.eclipse.jgit:7.3.0.202506031305-r")
    }
}

val commitCount = run {
    val repo = FileRepository(rootProject.file(".git"))
    val refId = repo.refDatabase.exactRef("refs/remotes/origin/master").objectId!!
    Git(repo).log().add(refId).call().count()
}

val (coreCommitCount, coreLatestTag) = FileRepositoryBuilder().setGitDir(rootProject.file(".git/modules/core"))
    .runCatching {
        build().use { repo ->
            val git = Git(repo)
            val coreCommitCount =
                git.log()
                    .add(repo.refDatabase.exactRef("HEAD").objectId)
                    .call().count() + 4200
            val ver = git.describe()
                .setTags(true)
                .setAbbrev(0).call().removePrefix("v")
            coreCommitCount to ver
        }
    }.getOrNull() ?: (1 to "1.0")

// sync from https://github.com/JingMatrix/LSPosed/blob/master/build.gradle.kts
val defaultManagerPackageName by extra("org.lsposed.lspatch")
val apiCode by extra(93)
val verCode by extra(commitCount)
val verName by extra("0.7")
val coreVerCode by extra(coreCommitCount)
val coreVerName by extra(coreLatestTag)
val androidMinSdkVersion by extra(28)
val androidTargetSdkVersion by extra(36)
val androidCompileSdkVersion by extra(36)
val androidCompileNdkVersion by extra("29.0.13113456")
val androidBuildToolsVersion by extra("36.0.0")
val androidSourceCompatibility by extra(JavaVersion.VERSION_21)
val androidTargetCompatibility by extra(JavaVersion.VERSION_21)

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}

listOf("Debug", "Release").forEach { variant ->
    tasks.register("build$variant") {
        description = "Build LSPatch with $variant"
        dependsOn(projects.jar.dependencyProject.tasks["build$variant"])
        dependsOn(projects.manager.dependencyProject.tasks["build$variant"])
    }
}

tasks.register("buildAll") {
    dependsOn("buildDebug", "buildRelease")
}

fun Project.configureBaseExtension() {
    extensions.findByType(BaseExtension::class)?.run {
        compileSdkVersion(androidCompileSdkVersion)
        ndkVersion = androidCompileNdkVersion
        buildToolsVersion = androidBuildToolsVersion

        externalNativeBuild.cmake {
            version = "3.28.1+"
            buildStagingDirectory = layout.buildDirectory.get().asFile
        }

        defaultConfig {
            minSdk = androidMinSdkVersion
            targetSdk = androidTargetSdkVersion
            versionCode = verCode
            versionName = verName

            signingConfigs.create("config") {
                val androidStoreFile = project.findProperty("androidStoreFile") as String?
                if (!androidStoreFile.isNullOrEmpty()) {
                    storeFile = rootProject.file(androidStoreFile)
                    storePassword = project.property("androidStorePassword") as String
                    keyAlias = project.property("androidKeyAlias") as String
                    keyPassword = project.property("androidKeyPassword") as String
                }
            }

            externalNativeBuild {
                cmake {
                    arguments += "-DEXTERNAL_ROOT=${File(rootDir.absolutePath, "core/external")}"
                    arguments += "-DCORE_ROOT=${File(rootDir.absolutePath, "core/core/src/main/jni")}"
                    abiFilters("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    val flags = arrayOf(
                        "-Wall",
                        "-Qunused-arguments",
                        "-Wno-gnu-string-literal-operator-template",
                        "-fno-rtti",
                        "-fvisibility=hidden",
                        "-fvisibility-inlines-hidden",
                        "-fno-exceptions",
                        "-fno-stack-protector",
                        "-fomit-frame-pointer",
                        "-Wno-builtin-macro-redefined",
                        "-Wno-unused-value",
                        "-D__FILE__=__FILE_NAME__",
                    )
                    cppFlags("-std=c++20", *flags)
                    cFlags("-std=c18", *flags)
                    arguments(
                        "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON",
                        "-DVERSION_CODE=$verCode",
                        "-DVERSION_NAME=$verName",
                    )
                }
            }
        }

        compileOptions {
            targetCompatibility(androidTargetCompatibility)
            sourceCompatibility(androidSourceCompatibility)
        }

        buildTypes {
            all {
                signingConfig = if (signingConfigs["config"].storeFile != null) signingConfigs["config"] else signingConfigs["debug"]
            }
            named("debug") {
                externalNativeBuild {
                    cmake {
                        arguments.addAll(
                            arrayOf(
                                "-DCMAKE_CXX_FLAGS_DEBUG=-Og",
                                "-DCMAKE_C_FLAGS_DEBUG=-Og",
                            )
                        )
                    }
                }
            }
            named("release") {
                externalNativeBuild {
                    cmake {
                        val flags = arrayOf(
                            "-Wl,--exclude-libs,ALL",
                            "-ffunction-sections",
                            "-fdata-sections",
                            "-Wl,--gc-sections",
                            "-fno-unwind-tables",
                            "-fno-asynchronous-unwind-tables",
                            "-flto=thin",
                            "-Wl,--thinlto-cache-policy,cache_size_bytes=300m",
                            "-Wl,--thinlto-cache-dir=${buildDir.absolutePath}/.lto-cache",
                        )
                        cppFlags.addAll(flags)
                        cFlags.addAll(flags)
                        val configFlags = arrayOf(
                            "-Oz",
                            "-DNDEBUG"
                        ).joinToString(" ")
                        arguments.addAll(
                            arrayOf(
                                "-DCMAKE_CXX_FLAGS_RELEASE=$configFlags",
                                "-DCMAKE_CXX_FLAGS_RELWITHDEBINFO=$configFlags",
                                "-DCMAKE_C_FLAGS_RELEASE=$configFlags",
                                "-DCMAKE_C_FLAGS_RELWITHDEBINFO=$configFlags",
                                "-DDEBUG_SYMBOLS_PATH=${buildDir.absolutePath}/symbols",
                            )
                        )
                    }
                }
            }
        }
    }

    extensions.findByType(ApplicationExtension::class)?.lint {
        abortOnError = true
        checkReleaseBuilds = false
    }

    extensions.findByType(ApplicationAndroidComponentsExtension::class)?.let { androidComponents ->
        val optimizeReleaseRes = task("optimizeReleaseRes").doLast {
            val aapt2 = File(
                androidComponents.sdkComponents.sdkDirectory.get().asFile,
                "build-tools/${androidBuildToolsVersion}/aapt2"
            )
            val zip = java.nio.file.Paths.get(
                project.buildDir.path,
                "intermediates",
                "optimized_processed_res",
                "release",
                "optimizeReleaseResources",
                "resources-release-optimize.ap_"
            )
            val optimized = File("${zip}.opt")
            val cmd = exec {
                commandLine(
                    aapt2, "optimize",
                    "--collapse-resource-names",
                    "--enable-sparse-encoding",
                    "-o", optimized,
                    zip
                )
                isIgnoreExitValue = false
            }
            if (cmd.exitValue == 0) {
                delete(zip)
                optimized.renameTo(zip.toFile())
            }
        }

        tasks.configureEach {
            if (name == "optimizeReleaseResources") {
                finalizedBy(optimizeReleaseRes)
            }
        }
    }
}

subprojects {
    plugins.withId("com.android.application") {
        configureBaseExtension()
    }
    plugins.withId("com.android.library") {
        configureBaseExtension()
    }
}


project(":core") {
    afterEvaluate {
        if (property("android") is LibraryExtension) {
            val android = property("android") as LibraryExtension
            android.run {
                buildTypes {
                    release { proguardFiles(rootProject.file("share/lspatch-rules.pro")) }
                }
            }
        }
    }
}
