package com.example.noteapp.presentation

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.noteapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var fabButtonClick: FabButtonClick? = null
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab = findViewById<FloatingActionButton>(R.id.fabButton)
        fab.setOnClickListener {
            fabButtonClick?.onFabClicked()
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
         navController = navHostFragment.navController
        navController.addOnDestinationChangedListener{_, destination, _ ->
            when (destination.id) {
                R.id.notesScreenFragment -> {
                    fab.show()
                }
                else -> {
                    fab.hide()
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val ret = super.dispatchTouchEvent(ev)
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_UP) {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        val touchCoordinates = IntArray(2)
                        view.getLocationOnScreen(touchCoordinates)
                        val x: Float = event.rawX + view.getLeft() - touchCoordinates[0]
                        val y: Float = event.rawY + view.getTop() - touchCoordinates[1]
                        if (x < view.getLeft() || x >= view.getRight() || y < view.getTop() || y > view.getBottom()) {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                            view.clearFocus()
                        }
                    }
                }
            }
        }
        return ret
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setFabListener(listener: FabButtonClick) {
        fabButtonClick = listener
    }

    interface FabButtonClick{
        fun onFabClicked()
    }
}