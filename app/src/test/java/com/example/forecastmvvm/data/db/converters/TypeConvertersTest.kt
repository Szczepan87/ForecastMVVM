package com.example.forecastmvvm.data.db.converters

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TypeConvertersTest {

    private var typeConverters: TypeConverters? = null

    @Before
    fun setup() {
        typeConverters = TypeConverters()
    }

    @After
    fun tearDown() {
        typeConverters = null
    }

    @Test
    fun `should return empty list when receive square brackets`() {
        val result = typeConverters?.restoreList("[]")
        val expected = listOf<String>()
        assertEquals(expected, result)
    }

    @Test
    fun `should return list when receive json`() {
        val result = typeConverters?.restoreList("[a,b,c]")
        val expected = listOf("a", "b", "c")
        assertEquals(expected, result)
    }

    @Test
    fun `should return square brackets when receive empty list`() {
        val result = "[]"
        val expected = typeConverters?.saveList(listOf<String>())
        assertEquals(expected, result)
    }

    @Test
    fun `should return json when receive list`() {
        val result = "[\"a\",\"b\",\"c\"]"
        val expected = typeConverters?.saveList(listOf("a", "b", "c"))
        assertEquals(expected, result)
    }
}