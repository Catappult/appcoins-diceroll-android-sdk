package com.appcoins.diceroll.sdk.feature.settings.data.model

data class UserPrefs(
  val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
  val cacheStrategy: CacheStrategy = CacheStrategy.NEVER
)

enum class ThemeConfig {
  FOLLOW_SYSTEM, LIGHT, DARK
}

enum class CacheStrategy {
  NEVER, ALWAYS
}