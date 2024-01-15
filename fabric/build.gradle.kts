val xplat = project(":xplat")
val xplatMain = xplat.sourceSets.main.get()

dependencies {
	modImplementation(libs.bundles.fabric)
	compileOnly(xplat)
}

tasks {
	compileJava {
		source(xplatMain.allSource)
	}
	processResources {
		from(xplatMain.resources)
	}
}
