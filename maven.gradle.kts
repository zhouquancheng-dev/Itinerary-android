fun RepositoryHandler.addCommonMaven() {
    maven {
        url = uri("https://maven.aliyun.com/nexus/content/repositories/releases/")
    }
    maven {
        url = uri("https://jitpack.io")
    }

    // 阿里云效 制品私有仓库
    maven {
        credentials {
            username = "60d431933c458713dd87bbdf"
            password = "DzLfKyVmGSC["
        }
        url = uri("https://packages.aliyun.com/6564880f3e469c2f35349933/maven/repo-dktbp")
    }
}

dependencyResolutionManagement {
    repositories {
        addCommonMaven()
    }
}