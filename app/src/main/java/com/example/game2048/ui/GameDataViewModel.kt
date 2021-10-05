package com.example.game2048.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import java.util.ArrayList
import java.util.Arrays
import java.util.Random
import java.util.function.IntUnaryOperator

/**
 * @brief  class used to manage all business logics
 */
class GameDataViewModel : ViewModel() {

    //represents the game console size
    internal var mGameConsoleSize = 4

    //matrix used for internal calculations
    internal var gameDataArray = Array(mGameConsoleSize) { IntArray(mGameConsoleSize) }

    //arraylist contains the game data
    internal var gameDataArrayList = ArrayList<Int>()

    //arraylist contains predefined data to be added in gameconsole. it may be 2 or 4
    internal var defaultValuesList = Arrays.asList(2, 4)

    //represents total score
    private val mTotalScoreLiveData = MutableLiveData<Int>()

    //represents the game data
    private val mGameDataLiveData = MutableLiveData<ArrayList<Int>>()

    //represents the winning status
    private val mGameWonStatus = MutableLiveData<Boolean>()

    //represents the game over status
    private val mGameOverStatus = MutableLiveData<Boolean>()


    val gameData: LiveData<ArrayList<Int>>
        get() = mGameDataLiveData

    val totalScore: LiveData<Int>
        get() = mTotalScoreLiveData

    val gameWonStatus: LiveData<Boolean>
        get() = mGameWonStatus

    val gameOverStatus: LiveData<Boolean>
        get() = mGameOverStatus

    init {
        initializeGameData()

    }

    /**
     * @brief  method used to convert matrix data to arraylist
     */
    private fun gameDataConversion() {
        gameDataArrayList = ArrayList()
        for (i in gameDataArray.indices) {
            for (j in 0 until gameDataArray[i].size) {
                gameDataArrayList.add(gameDataArray[i][j])
            }
        }
        mGameDataLiveData.setValue(gameDataArrayList)
    }

    /**
     * @brief  method used to add 2 default values into game console initially
     */
    private fun initializeGameData() {
        addNewGameData()
        addNewGameData()
    }

    /**
     * @brief  method used to trigger gamedata conversion and check the winning status
     */
    private fun updateGameData() {
        gameDataConversion()
        checkWinningStatus()
    }

    /**
     * @brief  method used check the winning status
     */
    private fun checkWinningStatus() {
        if (mTotalScoreLiveData.value != null && mTotalScoreLiveData.value!! >= 2048) {
            mGameWonStatus.setValue(true)
        }
    }

    /**
     * @brief : Method to add new random value 2 or 4 in the game console
     */
    internal fun addNewGameData() {
        //Arraylist to store the index of elements with value zero
        val stringArrayList = ArrayList<String>()
        for (i in gameDataArray.indices) {
            for (j in 0 until gameDataArray[i].size) {
                if (gameDataArray[i][j] == 0) {
                    val stringBuilder = i.toString() + j
                    stringArrayList.add(stringBuilder)
                }
            }
        }
        if (stringArrayList.size == 0) {
            mGameOverStatus.setValue(true)
        } else {
            val randomizer = Random()
            val randomIndex = stringArrayList[randomizer.nextInt(stringArrayList.size)]
            val intTab = randomIndex.chars().map(IntUnaryOperator { Character.getNumericValue(it) })
                .toArray()
            val x = intTab[0]
            val y = intTab[1]
            gameDataArray[x][y] = defaultValuesList[randomizer.nextInt(defaultValuesList.size)]
            updateGameData()
        }
    }

    /**
     * @brief : Method used to move tales in specific direction
     */
    internal fun moveTales(direction: Int) {
        for (k in 0 until mGameConsoleSize) {
            moveTale(k, direction)
        }
    }

    /**
     * @brief : Method used to move tale in specific direction
     */
    private fun moveTale(index: Int, direction: Int) {
        var needToupdatedTaleIndex = 0
        var count = 0
        while (count < mGameConsoleSize) {
            if (getValueFromGameData(count, index, direction) == 0) {
                count++
            } else {
                if (needToupdatedTaleIndex != count) {
                    val currentValue =
                        getValueFromGameData(needToupdatedTaleIndex, index, direction)
                    val moveTaleValue = getValueFromGameData(count, index, direction)

                    //current value is zero
                    if (currentValue == 0) {
                        //current value is zero and moveTale value also zero
                        if (moveTaleValue == 0) {
                            count++
                        } else {
                            updateGameData(needToupdatedTaleIndex, index, moveTaleValue, direction)
                            updateGameData(count, index, 0, direction)
                            count = needToupdatedTaleIndex
                        }//current value is zero and moveTale value is non zero


                    } //current value is non zero and moveTale value is zero
                    else if (moveTaleValue == 0) {
                        count++
                    } else if (currentValue == moveTaleValue) {
                        val updatedvalue = currentValue + moveTaleValue
                        val currentScore =
                            if (mTotalScoreLiveData.value != null) mTotalScoreLiveData.value else 0
                        mTotalScoreLiveData.setValue(currentScore!! + updatedvalue)
                        updateGameData(needToupdatedTaleIndex, index, updatedvalue, direction)
                        updateGameData(count, index, 0, direction)

                        needToupdatedTaleIndex++
                        count = needToupdatedTaleIndex
                    } else {
                        needToupdatedTaleIndex++
                        updateGameData(needToupdatedTaleIndex, index, moveTaleValue, direction)
                        if (needToupdatedTaleIndex != count) {
                            updateGameData(count, index, 0, direction)
                        }
                        count = needToupdatedTaleIndex
                    }//current value is  non zero and moveTale value also non zero and both are different
                    //current value is  non zero and moveTale value also non zero and both are same
                } else {
                    count++
                }
            }

        }


    }


    /**
     * @brief : Method used to fetch data from specific position in game data metrix
     * @param x : row position
     * @param y : column position
     * @direction : swipe direction
     * @return int : represents the value at specific position
     */
    private fun getValueFromGameData(x: Int, y: Int, direction: Int): Int {
        var result = 0
        when (direction) {
            1 -> result = gameDataArray[x][y]
            2 -> result = gameDataArray[y][x]
        }
        return result
    }

    /**
     * @brief : Method used to update data to specific position in game data metrix
     * @param x : row position
     * @param y : column position
     * @param result : new value for updation
     * @direction : swipe direction
     */
    private fun updateGameData(x: Int, y: Int, result: Int, direction: Int) {
        when (direction) {
            1 -> gameDataArray[x][y] = result
            2 -> gameDataArray[y][x] = result
        }
    }

    /**
     * @brief : Method used to reverse column in metrix
     */
    private fun reverseColumnsInMatrix(matrix: Array<IntArray>) {
        for (col in 0 until matrix[0].size) {
            for (row in 0 until matrix.size / 2) {
                val temp = matrix[row][col]
                matrix[row][col] = matrix[matrix.size - row - 1][col]
                matrix[matrix.size - row - 1][col] = temp
            }
        }
    }

    fun onSwipeTop() {
        moveTales(1)
        updateGameData()
        addNewGameData()

    }

    fun onSwipeRight() {
        reverseRowsInMatrix(gameDataArray)
        moveTales(2)
        reverseRowsInMatrix(gameDataArray)
        updateGameData()
        addNewGameData()
    }

    fun onSwipeLeft() {
        moveTales(2)
        updateGameData()
        addNewGameData()
    }

    fun onSwipeBottom() {
        reverseColumnsInMatrix(gameDataArray)
        moveTales(1)
        reverseColumnsInMatrix(gameDataArray)
        updateGameData()
        addNewGameData()
    }

    /**
     * @brief : Method used to reverse rows in metrix
     */
    fun reverseRowsInMatrix(matrix: Array<IntArray>) {
        for (row in matrix.indices) {
            for (col in 0 until matrix[row].size / 2) {
                val temp = matrix[row][col]
                matrix[row][col] = matrix[row][matrix[row].size - col - 1]
                matrix[row][matrix[row].size - col - 1] = temp
            }
        }
    }


}