package com.seaeast22.playlisttube.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seaeast22.playlisttube.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd


class AdMobFragment : Fragment() {
    private var interstitialAd1: InterstitialAd? = null

    private var adsLoadFailCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ad_mob, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        interstitialAd1 = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val appContext = activity?.applicationContext ?: return

        interstitialAd1 = newInterstitialAd(appContext)
        loadInterstitial()
    }


    private fun newInterstitialAd(context: Context): InterstitialAd {
        val interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = getString(R.string.interstitial_ad_unit_id)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                showInterstitial(context)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                adsLoadFailCount++
                // try to load ads 3 times
                if (adsLoadFailCount > 3)
                    navigateToHome(context)
                else
                    loadInterstitial()
            }

            override fun onAdClosed() {
                // Proceed to the next level.
                navigateToHome(context)
            }
        }
        return interstitialAd
    }

    private fun showInterstitial(context: Context) {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        val interstitialAd = interstitialAd1 ?: return
        if (interstitialAd.isLoaded) {
            interstitialAd.show()
        } else {
            navigateToHome(context)
        }
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        interstitialAd1?.loadAd(adRequest)
    }

    private fun navigateToHome(context: Context) {
        // Show the next level and reload the ad to prepare for the level after.
        //levelTextView?.text = context.getString(R.string.level_text, ++level)
        //interstitialAd1 = newInterstitialAd(context)
        //loadInterstitial()
        val direction = AdMobFragmentDirections.actionAdMobFragmentToViewPager2Fragment()
        findNavController().navigate(direction)
    }



    companion object {
        // Remove the below line after defining your own ad unit ID.
        private const val TOAST_TEXT =
            "Test ads are being shown. " + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID."
        private const val START_LEVEL = 1
    }
}
