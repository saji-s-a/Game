package com.example.game2048.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game2048.R
import com.general.game.Utils.OnSwipeTouchListener

/**
 * @brief class used to manage game UI
 */
class GameActivity : AppCompatActivity() {
    lateinit var gameConsoleRV: RecyclerView
    lateinit var gameListAdapter: GameListAdapter
    lateinit var scoreHeaderTv: TextView
    private var gameDataViewModel: GameDataViewModel? = null

    /**
     * @brief Overridden method to handle activity create event
     * @param savedInstanceState : Bundle instance
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //game view model initialization
        initViewModel()
        //ui components initialization
        initViews()
        //observing game data changes
        listenGameDataChanges()
        //observing game total score changes
        listenGameScoreChanges()
        //observing game over status changes
        listenGameOverStatus()
        //observing game winning status changes
        listenGameWinningStatus()
    }

    /**
     * @brief  method to observe game over status. once it triggered with true updating user about the same
     */
    private fun listenGameOverStatus() {
        gameDataViewModel!!.gameOverStatus.observe(this, Observer {
            if (it) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Game Over ......")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, id -> finish() }
                val alert = builder.create()
                alert.show()
            }
        })
    }

    /**
     * @brief  method to observe game winning status. once it triggered with true updating user about the same
     */
    private fun listenGameWinningStatus() {
        gameDataViewModel!!.gameWonStatus.observe(this, Observer {
            if (it) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Congtrats you won the game..")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, id -> finish() }
                val alert = builder.create()
                alert.show()
            }
        })
    }

    /**
     * @brief  method to observe game score changes. Once score changes update the score into UI
     */
    private fun listenGameScoreChanges() {
        gameDataViewModel!!.totalScore.observe(this, Observer {
            scoreHeaderTv.text = "Score : $it"
        })
    }

    /**
     * @brief  method to observe game data changes. Once changes occured directly pass
     * the data to adapter class for updating ui
     */
    private fun listenGameDataChanges() {
        gameDataViewModel!!.gameData.observe(this, Observer {
            gameListAdapter.updateGameData(it)
            gameListAdapter.notifyDataSetChanged()
        })

    }

    /**
     * @brief  method to initialize the viewmodel
     */
    private fun initViewModel() {
        gameDataViewModel = ViewModelProvider(this).get(GameDataViewModel::class.java)
    }


    /**
     * @brief  method to initialize ui components
     */
    private fun initViews() {
        scoreHeaderTv = findViewById(R.id.scoreHeaderTv)
        scoreHeaderTv.text = "Score : " + 0
        gameConsoleRV = findViewById(R.id.gameConsoleRV)
        gameConsoleRV.layoutManager = GridLayoutManager(this, 4)

        //listener added for swipe events
        gameConsoleRV.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeTop() {
                gameDataViewModel!!.onSwipeTop()
            }

            override fun onSwipeRight() {
                gameDataViewModel!!.onSwipeRight()
            }

            override fun onSwipeLeft() {
                gameDataViewModel!!.onSwipeLeft()
            }

            override fun onSwipeBottom() {
                gameDataViewModel!!.onSwipeBottom()
            }

        })

        gameListAdapter = GameListAdapter(gameDataViewModel!!.gameData.value, this)
        gameConsoleRV.adapter = gameListAdapter
    }

    companion object {
        private val TAG = "MyActivity"
    }


}


