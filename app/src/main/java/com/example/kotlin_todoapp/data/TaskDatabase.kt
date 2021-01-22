package com.example.kotlin_todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kotlin_todoapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task ::class ], version = 1)
abstract class TaskDatabase : RoomDatabase(){

    abstract fun taskDao() : TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope

        ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("매장 방문하기"))
                dao.insert(Task("다음주 3시 레스토랑 예약", important = true))
                dao.insert(Task("프린터 수리", completed = true))
                dao.insert(Task("Kotlin 앱 작업", completed = true))
            }
        }
    }
}