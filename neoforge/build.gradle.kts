plugins {
    kotlin("jvm")
    `multiloader-loader`
    id("net.neoforged.moddev") version "2.0.148"
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

    // FIXME only for neoforge?
    implementation("com.zigythebird.playeranim:PlayerAnimationLibNeo:${commonMod.dep("player_anim")}+mc.${commonMod.mc}") {
        exclude("io.netty")
        exclude("org.slf4j")
        exclude("it.unimi.dsi")
        exclude("com.google.code.gson")
        exclude("org.joml")
    }

    // bundled; JNA comes from minecraft
    jarJar("com.alphacephei:vosk:${commonMod.dep("vosk")}") { exclude(group = "net.java.dev.jna") }

    compileOnly("top.theillusivec4.curios:curios-neoforge:${commonMod.dep("curios")}+${commonMod.mc}:api")
    runtimeOnly("top.theillusivec4.curios:curios-neoforge:${commonMod.dep("curios")}+${commonMod.mc}")
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