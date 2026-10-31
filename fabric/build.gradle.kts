plugins {
    kotlin("jvm")
    `multiloader-loader`
    id("dev.kikugie.loom-back-compat")
    id("versioning")
//    id("com.google.devtools.ksp") version "2.4.12" // TODO(template): review
//    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.14" // TODO(template): review
}

stonecutter {

}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mc}")

    commonMod.depOrNull("parchment")?.let { parchmentVersion ->
        mappings(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${commonMod.mc}:$parchmentVersion@zip")
        })
    }

    modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric_api")}+${commonMod.mc}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${commonMod.dep("fabric_language_kotlin")}")

    modImplementation("com.terraformersmc:modmenu:${commonMod.dep("modmenu")}")

    commonMod.depOrNull("player_anim")?.let { modImplementation("com.zigythebird.playeranim:PlayerAnimationLibFabric:$it+mc.${commonMod.mc}") }

    commonMod.depOrNull("iris")?.let { compileOnly(commonMod.modrinth("iris", it)) { isTransitive = false } }
    compileOnly("de.maxhenkel.voicechat:voicechat-api:${commonMod.dep("voicechat_api")}")

    implementation("com.alphacephei:vosk:${commonMod.dep("vosk")}") { exclude(group = "net.java.dev.jna") }
    include("com.alphacephei:vosk:${commonMod.dep("vosk")}")
}

loom {
    // the widener uses 26.x (unobfuscated) names, so pre-26 versions skip it
    if (stonecutter.eval(stonecutter.current.version, ">=26")) {
        accessWidenerPath = rootProject.file("common/src/main/resources/${commonMod.id}.accesswidener")
    }

    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            environmentVariable("SDL_VIDEO_DRIVER", "wayland")
            environmentVariable("__EGL_VENDOR_LIBRARY_FILENAMES", "/run/opengl-driver/share/glvnd/egl_vendor.d/50_mesa.json")
            environmentVariable("MESA_LOADER_DRIVER_OVERRIDE", "zink")
            programArgs("--username", "Loqor")
            ideConfigGenerated(true)
        }
        getByName("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
        }
    }
}