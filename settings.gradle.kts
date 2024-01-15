dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			from(files("libs.versions.toml"))
		}
	}
}

pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.architectury.dev/") { name = "Architectury" }
		maven("https://files.minecraftforge.net/maven/") { name = "Forge" }
		gradlePluginPortal()
	}
}

plugins {
	// apply the foojay-resolver plugin to allow automatic download of jdks
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "genflower"
include("xplat")
include("forge")
include("fabric")
