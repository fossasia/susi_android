package org.fossasia.susi.ai.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_sttframe.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import timber.log.Timber

/**
 * Created by meeera on 17/8/17.
 */
class STTfragment : Fragment() {
    lateinit var recognizer: SpeechRecognizer
    lateinit var chatPresenter: IChatPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        chatPresenter = ChatPresenter(activity as ChatActivity)
    }

    @NonNull
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_sttframe, container, false)
        (activity as ChatActivity).fabsetting.hide()
        promptSpeechInput()
        return rootView
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.domain.app")
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        recognizer = SpeechRecognizer
                .createSpeechRecognizer(activity?.applicationContext)
        val listener = object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (voiceResults == null) {
                    Timber.e("No voice results")
                } else {
                    Timber.d("Printing matches: ")
                    for (match in voiceResults) {
                        Timber.d(match)
                    }
                }
                if (speechProgress != null)
                    speechProgress.onResultOrOnError()
                (activity as ChatActivity).setText(voiceResults[0])
                recognizer.destroy()
                if ((activity as ChatActivity).recordingThread != null) {
                    chatPresenter.startHotwordDetection()
                }
                (activity as ChatActivity).fabsetting.show()
                activity?.supportFragmentManager?.popBackStackImmediate()
            }

            override fun onReadyForSpeech(params: Bundle) {
                Timber.d("Ready for speech")
            }

            override fun onError(error: Int) {
                Timber.d("Error listening for speech: %s", error)
                Toast.makeText(activity?.applicationContext, "Could not recognize speech, try again.", Toast.LENGTH_SHORT).show()
                if (speechProgress != null)
                    speechProgress.onResultOrOnError()
                recognizer.destroy()
                activity?.fabsetting?.show()
                activity?.supportFragmentManager?.popBackStackImmediate()
            }

            override fun onBeginningOfSpeech() {
                Timber.d("Speech starting")
                if (speechProgress != null)
                    speechProgress.onBeginningOfSpeech()
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // This method is intentionally empty
            }

            override fun onEndOfSpeech() {
                if (speechProgress != null)
                    speechProgress.onEndOfSpeech()
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // This method is intentionally empty
            }

            override fun onPartialResults(partialResults: Bundle) {
                val partial = partialResults
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                txtChat?.text = partial[0]
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (speechProgress != null)
                    speechProgress.onRmsChanged(rmsdB)
            }
        }
        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)
    }

    override fun onPause() {
        super.onPause()
        (activity as ChatActivity).fabsetting.show()
        recognizer.cancel()
        recognizer.destroy()
    }
}