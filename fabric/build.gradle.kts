plugins {
    kotlin("jvm")
    `multiloader-loader`
    id("dev.kikugie.loom-back-compat")
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
    modImplementation("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric_api")}+${commonMod.mc}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${commonMod.dep("fabric_language_kotlin")}")

    modImplementation("com.terraformersmc:modmenu:${commonMod.dep("modmenu")}")

    modImplementation("com.zigythebird.playeranim:PlayerAnimationLibFabric:${commonMod.dep("player_anim")}+mc.${commonMod.mc}")

    modImplementation(commonMod.modrinth("iris", commonMod.dep("iris") + "+${commonMod.mc}-fabric"))
    implementation("de.maxhenkel.voicechat:voicechat-api:${commonMod.dep("voicechat_api")}")

    if (sc.current.parsed >= "26") {
        modImplementation("eu.pb4:trinkets:${commonMod.dep("trinkets")}+${commonMod.mc}")
    } else {
        modImplementation("dev.emi:trinkets:${commonMod.dep("trinkets")}")
    }

    include("com.alphacephei:vosk:${commonMod.dep("vosk")}")
}

loom {
    // the widener uses 26.x (unobfuscated) names, so pre-26 versions skip it
    //    -- stupidest comment i've ever heard, use mixins instead!
    //                  - Theo
    if (stonecutter.current.parsed >= "26") {
        accessWidenerPath = rootProject.file("common/src/main/resources/${commonMod.id}.accesswidener")
    }

    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
        }

        getByName("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
        }
    }
}