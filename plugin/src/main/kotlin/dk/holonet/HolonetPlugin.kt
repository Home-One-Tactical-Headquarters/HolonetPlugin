package dk.holonet

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.kotlin.dsl.create
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import java.io.File

class HolonetPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Apply required plugins
        project.plugins.apply("org.jetbrains.kotlin.jvm")

        // Add the extension for configuration
        val extension = project.extensions.create<HoloNetPluginExtension>("holoNetPlugin")

        project.afterEvaluate {
            // Add dependencies
            project.dependencies {
                "compileOnly"("dk.holonet:core:0.0.1")
                "compileOnly"("org.pf4j:pf4j:3.6.0")
                "compileOnly"("io.insert-koin:koin-core:4.0.2")
                "compileOnly"("io.insert-koin:koin-compose:4.0.2")
                "compileOnly"("io.ktor:ktor-client-core:3.1.0")
                "compileOnly"("io.ktor:ktor-client-cio:3.1.0")
                "kapt"("org.pf4j:pf4j:3.6.0")
            }

            // Configure jar task with manifest attributes
            project.tasks.named<Jar>("jar") {
                manifest {
                    attributes["Plugin-Class"] = extension.pluginClass.get()
                    attributes["Plugin-Id"] = extension.pluginId.get()
                    attributes["Plugin-Version"] = project.version
                    attributes["Plugin-Provider"] = extension.pluginProvider.get()
                }
            }

            // Create plugin packaging task
            project.tasks.register<Jar>("plugin") {
                archiveBaseName.set("plugin-${extension.pluginId.get()}")

                into("classes") {
                    with(project.tasks.named<Jar>("jar").get())
                }

                dependsOn(project.configurations.named("runtimeClasspath"))
                into("lib") {
                    from({
                        project.configurations.named("runtimeClasspath").get().filter { file ->
                            // Exclude Kotlin stdlib and annotations
                            !file.name.startsWith("kotlin-stdlib") &&
                                    !file.name.startsWith("kotlin-annotations") &&
                                    !file.name.contains("annotations") &&
                            file.name.endsWith("jar")
                        }
                    })
                }

                archiveExtension.set("zip")
            }

            // Copy plugin to plugins directory (configurable)
            val pluginsDir = extension.pluginsDir.getOrElse(File(project.rootProject.projectDir, "plugins"))
            project.tasks.register<Copy>("assemblePlugin") {
                from(project.tasks.named("plugin"))
                into(pluginsDir)
            }

            // Update build task
            project.tasks.named("build") {
                dependsOn(project.tasks.named("plugin"))
                finalizedBy(project.tasks.named("assemblePlugin"))
            }
        }
    }
}
