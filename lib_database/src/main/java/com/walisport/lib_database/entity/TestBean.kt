package com.walisport.lib_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO 測試用，隨時可移除
@Entity(tableName = "test_bean")
data class TestBean(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)
