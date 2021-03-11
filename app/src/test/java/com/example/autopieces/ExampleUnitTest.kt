package com.example.autopieces

import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        runBlocking(Dispatchers.Main) {
            val result = getData()
            assertEquals(1,result)
        }

    }

    suspend fun getData():Int = withContext(Dispatchers.IO){
        1
    }
}