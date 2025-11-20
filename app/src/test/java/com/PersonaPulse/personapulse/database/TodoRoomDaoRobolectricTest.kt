package com.PersonaPulse.personapulse.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.PersonaPulse.personapulse.database.dao.TodoDao
import com.PersonaPulse.personapulse.database.entity.TodoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.*


@RunWith(RobolectricTestRunner::class)
class TodoRoomDaoRobolectricTest {

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: PersonaPulseDatabase
    private lateinit var dao: TodoDao

    @Before
    fun setUp(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PersonaPulseDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = db.todoDao()
    }

    @After
    fun tearDown(){
        db.close()
    }

    @Test
    fun insertAndReadTest() = runTest {
        val testTodo = TodoEntity(id = "1", title = "Test 1")
        dao.insertTodo(testTodo)
        val todos = dao.getAllTodos().first()

        assertEquals(1, todos.size)
        assertEquals("Test 1", todos[0].title)
    }

    @Test
    fun testDatabaseCreation() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val db = PersonaPulseDatabase.getDatabase(context)
        assertNotNull(db)
        assertEquals("personapulse_database", db.openHelper.databaseName)
    }
}