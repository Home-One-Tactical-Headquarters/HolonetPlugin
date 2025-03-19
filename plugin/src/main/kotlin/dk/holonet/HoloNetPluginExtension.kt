package dk.holonet

import org.gradle.api.provider.Property
import java.io.File

abstract class HoloNetPluginExtension {
    abstract val pluginId: Property<String>
    abstract val pluginClass: Property<String>
    abstract val pluginProvider: Property<String>
    abstract val pluginsDir: Property<File>
}