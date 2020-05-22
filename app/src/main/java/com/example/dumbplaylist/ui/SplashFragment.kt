package com.example.dumbplaylist.ui

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.example.dumbplaylist.R

const val GAME_LENGTH_MILLISECONDS = 1000L

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {
    private var mCountDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createTimer(GAME_LENGTH_MILLISECONDS)
        mCountDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDownTimer = null
    }


    private fun createTimer(millisecond: Long) {
        mCountDownTimer?.cancel()

        mCountDownTimer = object : CountDownTimer(millisecond, 500) {
            override fun onFinish() {
                //showInterstitial(context!!)
                val direction = SplashFragmentDirections.actionSplashFragmentToAdMobFragment()
                findNavController().navigate(direction)
                Toast.makeText(context, "Timer is finished", Toast.LENGTH_SHORT).show()
            }

            override fun onTick(millisUntilFinished: Long) {
                // Do nothing
            }
        }

    }
}
