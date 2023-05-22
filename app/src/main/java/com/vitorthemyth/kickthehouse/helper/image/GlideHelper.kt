package com.vitorthemyth.kickthehouse.helper.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition


fun loadGifIntoImageView(context: Context, imageView: ImageView, gifResourceId: Int) {
    imageView.apply {
        alpha = 0f // set initial alpha to 0
        visibility = View.VISIBLE
        animate().alpha(1f).setDuration(1000).start() // animate alpha to 1 over 500ms
    }

    val requestBuilder = Glide.with(context)
        .asGif()
        .load(gifResourceId)
        .diskCacheStrategy(DiskCacheStrategy.NONE)


    val customAnimationFactory = DrawableCrossFadeFactory.Builder()
        .setCrossFadeEnabled(false)
        .build()

    requestBuilder.transition(DrawableTransitionOptions.withCrossFade(customAnimationFactory))
        .into(imageView)
}