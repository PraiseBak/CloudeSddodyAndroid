package com.study.sddodyandroid.helper

enum class LanguageEnum(override val info: String) : InfoEnum {
    Java("Java"),
    Python("Python"),
    JavaScript("JavaScript"),
    Cpp("C++"),
    CSharp("C#"),
    Swift("Swift"),
    Kotlin("Kotlin"),
    Ruby("Ruby"),
    Go("Go"),
    Rust("Rust"),
    HTML_CSS("HTML/CSS"),
    ETC("기타"),
}

fun getLanguageEnumFromString(languageString: String): LanguageEnum {
    return when (languageString) {
        "Java" -> LanguageEnum.Java
        "Python" -> LanguageEnum.Python
        "JavaScript" -> LanguageEnum.JavaScript
        "Cpp" -> LanguageEnum.Cpp
        "CSharp" -> LanguageEnum.CSharp
        "Swift" -> LanguageEnum.Swift
        "Kotlin" -> LanguageEnum.Kotlin
        "Ruby" -> LanguageEnum.Ruby
        "Go" -> LanguageEnum.Go
        "Rust" -> LanguageEnum.Rust
        "HTML_CSS" -> LanguageEnum.HTML_CSS
        "ETC" -> LanguageEnum.ETC
        else -> LanguageEnum.ETC
    // 해당하는 문자열이 없을 경우 null 반환
    }
}