package ru.ll.minesweeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    var indexRandom = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("рекурсия $indexRandom раз")
//        println(
//            "печать ${
//                addMines(createEmptyField(10, 10), 100)
//                    .joinToString("\n")
//            }"
//        )
        println("печать ${createNotEmptyField(10, 10, 18).joinToString("\n")}")
//        println("печать ${createFieldWithMines(10, 10, 18).joinToString("\n")}")
    }

    fun createEmptyField(width: Int, height: Int): List<MutableList<Boolean>> {
//        TODO размеры и количество мин запихать в условия
        val field = mutableListOf<MutableList<Boolean>>()
        (0 until width).forEach {
            val line = mutableListOf<Boolean>()
            (0 until height).forEach {
                line.add(false)
            }
            field.add(line)
        }
        return field
    }

    fun createNotEmptyField(width: Int, height: Int, mines: Int): List<MutableList<Boolean>> {
//        TODO размеры и количество мин запихать в условия
        var i = 0
        val field = mutableListOf<MutableList<Boolean>>()
        (0 until width).forEach {
            val line = mutableListOf<Boolean>()
            (0 until height).forEach {
                line.add(i < mines)
                i++
            }
            field.add(line)
        }
//        TODO перемешать мины
        var j = 0
        (0 until width).forEach br@{ column ->
            (0 until height).forEach { tile ->
                if (j > mines) {
                    return@br
                }
                val widthRandom = Random.nextInt(field.size)
                val heightRandom = Random.nextInt(field[0].size)
                val randomTile = field[widthRandom][heightRandom]
                field[widthRandom][heightRandom] = field[column][tile]
                field[column][tile] = randomTile
                j++
            }
        }
        println("счетчик $j")
        return field
    }

    fun addMines(field: List<MutableList<Boolean>>, mines: Int): List<List<Boolean>> {
        (0 until mines).forEach {
            placeMine(field)

        }

        return field
    }

    fun placeMine(field: List<MutableList<Boolean>>) {
        val random = Random(System.currentTimeMillis())
        val widthRandom = random.nextInt(field.size)
        val heightRandom = random.nextInt(field[0].size)
        val mine = field[widthRandom][heightRandom]
        if (mine) {
            placeMine(field)
        } else {
            field[widthRandom][heightRandom] = true
        }
        indexRandom++
    }

    fun createFieldWithMines(width: Int, height: Int, mines: Int): List<List<Boolean>> {
        val fieldShuffled = (0..(width * height) - 1).shuffled()
        var index = 0
        val field = mutableListOf<MutableList<Boolean>>()
        (0 until width).forEach {
            val line = mutableListOf<Boolean>()
            (0 until height).forEach {
                line.add(fieldShuffled.get(index) < mines)
                index++
            }
            field.add(line)
        }
        return field
    }
}

