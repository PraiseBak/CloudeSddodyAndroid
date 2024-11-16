package com.study.sddodyandroid.helper

enum class DeveloperEnum(override val info: String) : InfoEnum {
    Frontend("프론트엔드"),
    Backend("백엔드"),
    Android("안드로이드"),
    IOS("IOS"),
    Game("게임"),
    Database("데이터베이스"),
    AI("AI"),
    Graphics("디자이너"),
    ETC("기타"),
}

fun getDeveloperEnumFromString(developerString: String): DeveloperEnum {
    return when (developerString) {
        "Frontend" -> DeveloperEnum.Frontend
        "Backend" -> DeveloperEnum.Backend
        "Android" -> DeveloperEnum.Android
        "IOS" -> DeveloperEnum.IOS
        "Game" -> DeveloperEnum.Game
        "Database" -> DeveloperEnum.Database
        "AI" -> DeveloperEnum.AI
        "Graphics" -> DeveloperEnum.Graphics
        "ETC" -> DeveloperEnum.ETC
        else -> DeveloperEnum.ETC
    }
}