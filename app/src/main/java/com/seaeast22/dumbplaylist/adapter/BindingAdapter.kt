package com.seaeast22.dumbplaylist.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("thumbnailUrl")
fun loadImage(view : ImageView, url: String?) {
    url?.let {
        //val requestOption : RequestOptions
        Glide.with(view.context).load(it)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
            .into(view)
    }
}