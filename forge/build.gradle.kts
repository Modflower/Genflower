val xplat = project(":xplat")
val xplatMain = xplat.sourceSets.main.get()

val modrinthId: String by project

dependencies {
	forge(libs.forge.loader)
	compileOnly(xplat)
}

tasks {
	compileJava {
		source(xplatMain.allSource)
	}
	processResources {
		from(xplatMain.resources)
	}
	publish {
		dependsOn(modrinth)
	}
}

modrinth {
	token.set(System.getenv("MODRINTH_TOKEN"))
	projectId.set(modrinthId)
	versionType.set(meta.releaseType)
	versionName.set("${meta.projectVersion} - Forge ${libs.versions.minecraft.version.get()}")
	versionNumber.set("${project.version}-forge")
	changelog.set(meta.changelog)
	uploadFile.set(tasks.remapJar)
	gameVersions.set(meta.minecraftCompatible)
	loaders.addAll("forge", "neoforge")
}
