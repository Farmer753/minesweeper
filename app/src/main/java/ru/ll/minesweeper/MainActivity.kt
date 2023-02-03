package ru.ll.minesweeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    var indexRandom = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Executors.newCachedThreadPool().execute {
            testMinesweeper(10, 10, 18, 1000, ::createNotEmptyField)
            testMinesweeper(10, 10, 18, 1000, ::createFieldWithMines)
            testMinesweeper(10, 10, 18, 1000, ::createFieldWithRecursion)
        }
//        println(
//            "поле рекурсивного метода ${
//                createFieldWithRecursionRandom(10, 10, 18)
//                    .joinToString("\n")
//            }"
//        )
//        println("рекурсивная функция метода createFieldWithRecursionRandom $indexRandom раз")
//
//        indexRandom = 0
//
//        println(
//            "поле рекурсивного метода ${
//                createFieldWithRecursion(10, 10, 18)
//                    .joinToString("\n")
//            }"
//        )
//        println("рекурсивная функция метода createFieldWithRecursion $indexRandom раз")
//        println("печать ${createNotEmptyField(10, 10, 18).joinToString("\n")}")
    }

    fun testMinesweeper(
        width: Int,
        height: Int,
        mines: Int,
        iterations: Int,
        fieldGenerator: (Int, Int, Int) -> List<List<Boolean>>
    ) {
        //        TODO размеры и количество мин запихать в условия

        var timeBefore = System.currentTimeMillis()
        var forAll = 0L
        for (i in 0 until iterations) {
            fieldGenerator(
                width, height, mines
            )
            val currentTime = System.currentTimeMillis()
            val timeFor = currentTime - timeBefore
            timeBefore = currentTime
            forAll += timeFor
        }
        println("переменная forAll $forAll")
        println(
            "метод выполнился $iterations раз в среднем за ${forAll / iterations.toDouble()} миллисекунд"
        )

    }

    fun createFieldWithRecursionRandom(width: Int, height: Int, mines: Int): List<List<Boolean>> {
//        TODO размеры и количество мин запихать в условия
        val field = mutableListOf<MutableList<Boolean>>()
        (0 until width).forEach {
            val line = mutableListOf<Boolean>()
            (0 until height).forEach {
                line.add(false)
            }
            field.add(line)
        }
        (0 until mines).forEach {
            val random = Random(System.currentTimeMillis())
            placeMine(field, random::nextInt)
        }
        return field
    }

    fun createFieldWithRecursion(width: Int, height: Int, mines: Int): List<List<Boolean>> {
        val field = mutableListOf<MutableList<Boolean>>()
        (0 until width).forEach {
            val line = mutableListOf<Boolean>()
            (0 until height).forEach {
                line.add(false)
            }
            field.add(line)
        }
        (0 until mines).forEach {
            val random = Random(System.currentTimeMillis())
            placeMine(field, Random::nextInt)
        }
        return field
    }

    fun createNotEmptyField(width: Int, height: Int, mines: Int): List<MutableList<Boolean>> {
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
        return field
    }

    fun placeMine(field: List<MutableList<Boolean>>, intGenerator: (Int) -> Int) {
        val widthRandom = intGenerator(field.size)
        val heightRandom = intGenerator(field[0].size)
        val mine = field[widthRandom][heightRandom]
        if (mine) {
            placeMine(field, intGenerator)
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

