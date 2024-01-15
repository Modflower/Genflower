plugins {
	java
	alias(libs.plugins.loom)
	id("gay.ampflower.BuildPlugin")
}

val github: String by project
val modrinthId: String by project

allprojects {
	apply(plugin = "java")
	apply(plugin = "gay.ampflower.BuildPlugin")
	apply(plugin = rootProject.libs.plugins.loom.get().pluginId)

	version = meta.globalVersion

	base {
		if (project != rootProject) {
			archivesName.set(rootProject.name + '-' + project.name)
		}
	}

	java {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
		withSourcesJar()
		withJavadocJar()
	}

	repositories {
		mavenLocal()
		mavenCentral()
		maven("https://api.modrinth.com/maven") { name = "Modrinth" }
	}

	dependencies {
		minecraft(rootProject.libs.minecraft)
		mappings(variantOf(rootProject.libs.yarn) { classifier("v2") })
	}

	tasks {
		withType<JavaCompile> {
			options.release.set(17)
			options.encoding = "UTF-8"
			options.isDeprecation = true
			options.isWarnings = true
		}
		withType<Jar> {
			from("LICENSE*") {
				rename { "${it}_${rootProject.name}" }
			}
		}

		processResources {
			val map =
				mapOf(
					"id" to project.name,
					"java" to java.targetCompatibility.majorVersion,
					"version" to project.version,
					"sources" to github,
					"issues" to "$github/issues",
					"description" to project.description,
					"projectVersion" to meta.projectVersion,
					"modrinthId" to modrinthId,
					"forgeRequired" to libs.versions.forge.loader.get().let {
						val s = it.indexOf('-') + 1
						it.substring(s, it.indexOf('.', s))
					},
					"minecraftVersion" to libs.versions.minecraft.version.get(),
					"minecraftRequired" to libs.versions.minecraft.required.get()
				)
			inputs.properties(map)

			filesMatching(listOf("fabric.mod.json", "quilt.mod.json", "META-INF/mods.toml")) {
				expand(map)
			}
		}
		javadoc {
			(options as StandardJavadocDocletOptions).tags("reason:a:Reason")
		}
	}
}
