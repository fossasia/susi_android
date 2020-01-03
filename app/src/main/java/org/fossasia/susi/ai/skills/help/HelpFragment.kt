package org.fossasia.susi.ai.skills.help

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.TextView
import org.fossasia.susi.ai.R

class HelpFragment : Fragment(), View.OnClickListener {

    lateinit var questionsArray: ArrayList<TextView>
    lateinit var answerArray: ArrayList<TextView>
    lateinit var imageArray: ArrayList<ImageView>

    override fun onClick(v: View) {
        if (answerArray[questionsArray.indexOf(v)].visibility == View.GONE) {
            rotate(imageArray[questionsArray.indexOf(v)])
            expand(answerArray[questionsArray.indexOf(v)])
        } else {
            rotateBack(imageArray[questionsArray.indexOf(v)])
            collapse(answerArray[questionsArray.indexOf(v)])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val thisActivity = activity
        thisActivity?.title = getString(R.string.action_help)
        val rootView = inflater.inflate(R.layout.fragment_help, container, false)
        questionsArray = ArrayList()
        answerArray = ArrayList()
        imageArray = ArrayList()
        for (i in 1..8) {
            val question = "question$i"
            val questionID = resources.getIdentifier(question, "id", activity?.getPackageName())
            questionsArray.add(rootView.findViewById(questionID))
            val answers = "answer$i"
            val answerId = resources.getIdentifier(answers, "id", activity?.getPackageName())
            answerArray.add(rootView.findViewById(answerId))
            questionsArray[i - 1].setOnClickListener(this)
            var imageIdString = "img$i"
            val imageId = resources.getIdentifier(imageIdString, "id", activity?.getPackageName())
            imageArray.add(rootView.findViewById(imageId))
        }
        return rootView
    }

    fun collapse(v: View) {
        val initialHeight = v.getMeasuredHeight()

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.setVisibility(View.GONE)
                } else {
                    v.getLayoutParams().height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = ((initialHeight / v.getContext().getResources().getDisplayMetrics().density).toInt()).toLong()
        v.startAnimation(a)
    }

    fun expand(v: View) {
        val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec((v.getParent() as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.getMeasuredHeight()
        v.getLayoutParams().height = 1
        v.setVisibility(View.VISIBLE)
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.getLayoutParams().height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = ((targetHeight / v.getContext().getResources().getDisplayMetrics().density).toInt()).toLong()
        v.startAnimation(a)
    }

    fun rotate(v: View) {
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(v)
                .rotation(180f)
                .withLayer()
                .setDuration(300)
                .setInterpolator(interpolator)
                .start()
    }

    fun rotateBack(v: View) {
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(v)
                .rotation(0f)
                .withLayer()
                .setDuration(300)
                .setInterpolator(interpolator)
                .start()
    }
}
