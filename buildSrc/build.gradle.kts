plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
}

gradlePlugin {
    plugins {
        register("course.conventions") {
            id = "course.conventions"
            implementationClass = "CourseConventionsPlugin"
        }
    }
}
