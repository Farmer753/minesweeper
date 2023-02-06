package ru.ll.minesweeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import ru.ll.minesweeper.databinding.ActivityMainBinding
import java.util.concurrent.Executors
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var indexRecursion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.buttonGo.setOnClickListener {
            val width = binding.editTextWidth.text.toString().toInt()
            val height = binding.editTextHeight.text.toString().toInt()
            val mines = binding.editTextMines.text.toString().toInt()
            val iterations = binding.editTextIterations.text.toString().toInt()

            Executors.newCachedThreadPool().execute {
                testMinesweeper(width, height, mines, iterations, ::createNotEmptyField)
                testMinesweeper(width, height, mines, iterations, ::createFieldWithMines)
                indexRecursion = 0
                testMinesweeper(width, height, mines, iterations, ::createFieldWithRecursion)
                println("Среднее число рекурсии ${indexRecursion / iterations}")
                indexRecursion = 0
                testMinesweeper(width, height, mines, iterations, ::createFieldWithRecursionRandom)
                println("Среднее число рекурсии ${indexRecursion / iterations}")
                println("поток ${Thread.currentThread().name}")
            }
        }
        binding.editTextHeight.addTextChangedListener { checkForm() }
        binding.editTextWidth.addTextChangedListener { checkForm() }
        binding.editTextMines.addTextChangedListener { checkForm() }
        binding.editTextIterations.addTextChangedListener { checkForm() }

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

    fun checkForm() {
        binding.buttonGo.isEnabled = (binding.editTextHeight.text.isNotEmpty()
                && binding.editTextWidth.text.isNotEmpty()
                && binding.editTextMines.text.isNotEmpty()
                && binding.editTextIterations.text.isNotEmpty())
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
        indexRecursion++
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

