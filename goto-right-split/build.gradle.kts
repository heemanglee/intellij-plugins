plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "io.heemang"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // 설치된 IntelliJ를 그대로 컴파일 대상으로 사용해 SDK 다운로드를 피한다.
        local("/Applications/IntelliJ IDEA.app")
    }
}

intellijPlatform {
    buildSearchableOptions = false
    instrumentCode = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "261"
            untilBuild = provider { null }
        }
    }
}
