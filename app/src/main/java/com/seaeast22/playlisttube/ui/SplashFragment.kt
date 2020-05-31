package com.seaeast22.playlisttube.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.seaeast22.playlisttube.R
import com.seaeast22.playlisttube.databinding.FragmentSplashBinding

const val GAME_LENGTH_MILLISECONDS = 1000L

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {
    private var mCountDownTimer: CountDownTimer? = null
    private lateinit var mFragmentBinding: FragmentSplashBinding
    private var mIsConnected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mFragmentBinding = FragmentSplashBinding.inflate(inflater, container, false)
        context ?: return mFragmentBinding.root


        mFragmentBinding.networkStatus.text = (getString(R.string.check_network_status) + getString(R.string.checking))

        return mFragmentBinding.root
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
                if (mIsConnected) {
                    val direction = SplashFragmentDirections.actionSplashFragmentToAdMobFragment()
                    findNavController().navigate(direction)
                }
                else {
                    activity?.finish()
                    Toast.makeText(requireContext(), getString(R.string.toast_network_connection_fail),Toast.LENGTH_LONG)
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                mIsConnected = isNetworkConnected()
                if (mIsConnected) {
                    mFragmentBinding.networkStatus.text = (getString(R.string.check_network_status) + getString(R.string.connected))
                }
                else {
                    mFragmentBinding.networkStatus.text = (getString(R.string.check_network_status) + getString(R.string.disconnected))
                }
            }
        }

    }

    private fun isNetworkConnected(): Boolean {
        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.let { cmng ->
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cmng.activeNetworkInfo
                ni?.let { ninfo ->
                    return@isNetworkConnected ninfo.isConnected && (ninfo.type == ConnectivityManager.TYPE_WIFI || ninfo.type == ConnectivityManager.TYPE_MOBILE)
                }
            }
            else {
                val n = cmng.activeNetwork
                n?.let { nw ->
                    val nc = cmng.getNetworkCapabilities(nw)
                    nc?.let {ncs ->
                        return@isNetworkConnected ncs.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || ncs.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    }
                }
            }
        }

        return false
    }
}
