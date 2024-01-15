val xplat = project(":xplat")
val xplatMain = xplat.sourceSets.main.get()

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
}
