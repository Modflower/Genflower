val xplat = project(":xplat")
val xplatMain = xplat.sourceSets.main.get()

val modrinthId: String by project

dependencies {
	modImplementation(libs.bundles.fabric)
	compileOnly(xplat)

	// Probably will be replaced at some point with something better
	val fapi = libs.versions.fabric.api.get()
	include(fabricApi.module("fabric-api-base", fapi))
	include(fabricApi.module("fabric-registry-sync-v0", fapi))
	include(fabricApi.module("fabric-networking-api-v1", fapi))
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
	versionName.set("${meta.projectVersion} - Fabric ${libs.versions.minecraft.version.get()}")
	versionNumber.set("${project.version}-fabric")
	changelog.set(meta.changelog)
	uploadFile.set(tasks.remapJar)
	gameVersions.set(meta.minecraftCompatible)
	loaders.addAll("fabric", "quilt")
}
