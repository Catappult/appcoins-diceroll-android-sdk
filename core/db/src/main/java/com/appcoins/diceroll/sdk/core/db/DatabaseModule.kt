package com.appcoins.diceroll.sdk.core.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideDiceRollDatabase(@ApplicationContext context: Context): DiceRollDatabase {
    return Room.databaseBuilder(context, DiceRollDatabase::class.java, "dice_roll_database")
      .build()
  }

  @Provides
  @Singleton
  fun provideDiceRollDao(diceRollDatabase: DiceRollDatabase) = diceRollDatabase.diceRollDao()
}