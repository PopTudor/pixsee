package com.marked.vifo.adapter.viewholders

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.marked.vifo.R

/**
 * Created by Tudor Pop on 02-Jan-16.
 * This makes the animation dots happens
 */
public class TypingHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    private val dot1 = itemView.findViewById(R.id.dot1) as TextView
    private val dot2 = itemView.findViewById(R.id.dot2) as TextView
    private val dot3 = itemView.findViewById(R.id.dot3) as TextView

    companion object {
        private var animatorSet = AnimatorSet()
    }

    val dot1Typing = AnimatorInflater.loadAnimator(context, R.animator.typing)
    val dot2Typing = AnimatorInflater.loadAnimator(context, R.animator.typing)
    val dot3Typing = AnimatorInflater.loadAnimator(context, R.animator.typing)

    fun animate() {
        dot1Typing.setTarget(dot1)
        dot2Typing.setTarget(dot2)
        dot3Typing.setTarget(dot3)

        dot2Typing.startDelay = 200
        dot3Typing.startDelay = 400

        animatorSet.playTogether(dot1Typing, dot2Typing, dot3Typing)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                animatorSet.start()
            }
        })
        animatorSet.start()
    }

    fun stop(): Unit {

    }
}
