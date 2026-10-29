plugins {
    kotlin("jvm")
    `multiloader-loader`
    id("net.neoforged.moddev") version "2.0.148"
    id("versioning")
//    id("com.google.devtools.ksp") version "2.3.12" // TODO(template): review
//    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.13" // TODO(template): review
}

neoForge {
    enable {
        version = commonMod.dep("neoforge")
    }
}

dependencies {
    compileOnly("de.maxhenkel.voicechat:voicechat-api:${commonMod.dep("voicechat_api")}")

    // bundled; JNA comes from minecraft
    implementation("com.alphacephei:vosk:${commonMod.dep("vosk")}") { exclude(group = "net.java.dev.jna") }
    // 26+ puts implementation libs on the game classpath directly
    if (stonecutter.eval(stonecutter.current.version, "<26")) {
        "additionalRuntimeClasspath"("com.alphacephei:vosk:${commonMod.dep("vosk")}") { exclude(group = "net.java.dev.jna") }
    }
    jarJar("com.alphacephei:vosk:${commonMod.dep("vosk")}") { exclude(group = "net.java.dev.jna") }
}

neoForge {
    runs {
        register("client") {
            client()
            ideName = "NeoForge Client (${project.path})"
        }
        register("server") {
            server()
            ideName = "NeoForge Server (${project.path})"
        }
    }

    parchment {
        commonMod.depOrNull("parchment")?.let {
            mappingsVersion = it
            minecraftVersion = commonMod.mc
        }
    }

    mods {
        register(commonMod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}