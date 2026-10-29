plugins {
    kotlin("jvm")
    `multiloader-common`
    id("dev.kikugie.loom-back-compat")
    id("versioning")
//    id("com.google.devtools.ksp") version "2.3.12" // TODO(template): review
//    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.14" // TODO(template): review
}

stonecutter {

}

//fletchingTable {
//     TODO(template): review
//    j52j.register("main") {
//        extension("json", "**/*.json5")
//    }
//}

loom {
    // the widener uses 26.x (unobfuscated) names, so pre-26 versions skip it
    if (stonecutter.eval(stonecutter.current.version, ">=26")) {
        accessWidenerPath = rootProject.file("common/src/main/resources/${commonMod.id}.accesswidener")
    }

    mixin {
        useLegacyMixinAp = false
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mc}")

    commonMod.depOrNull("parchment")?.let { parchmentVersion ->
        mappings(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${commonMod.mc}:$parchmentVersion@zip")
        })
    }

    compileOnly("org.spongepowered:mixin:0.8.5")
    modCompileOnly("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")

    commonMod.depOrNull("iris")?.let { compileOnly(commonMod.modrinth("iris", it)) { isTransitive = false } }

    // voice chat + oath recognition; JNA comes from minecraft
    compileOnly("de.maxhenkel.voicechat:voicechat-api:${commonMod.dep("voicechat_api")}")
    compileOnly("com.alphacephei:vosk:${commonMod.dep("vosk")}") { exclude(group = "net.java.dev.jna") }
}

// TODO(template): same as buildSrc/.../multiloader-loader.gradle.kts
val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonKotlin: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    afterEvaluate {
        val mainSourceSet = sourceSets.main.get()
        mainSourceSet.kotlin.sourceDirectories.files.forEach {
            add(commonKotlin.name, it)
        }
        mainSourceSet.java.sourceDirectories.files.forEach {
            add(commonJava.name, it)
        }
        mainSourceSet.resources.sourceDirectories.files.forEach {
            add(commonResources.name, it)
        }
    }
}
