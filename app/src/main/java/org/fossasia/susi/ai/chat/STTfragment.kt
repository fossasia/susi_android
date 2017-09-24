package org.fossasia.susi.ai.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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

/**
 * Created by meeera on 17/8/17.
 */
class STTfragment : Fragment(){
    lateinit var recognizer: SpeechRecognizer
    lateinit var chatPresenter: IChatPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        chatPresenter = ChatPresenter(activity as ChatActivity)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater?.inflate(R.layout.fragment_sttframe, container, false)
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
                .createSpeechRecognizer(activity.applicationContext)
        val listener = object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (voiceResults == null) {
                    Log.e("fragment", "No voice results")
                } else {
                    Log.d("fragment", "Printing matches: ")
                    for (match in voiceResults) {
                        Log.d("fragment", match)
                    }
                }
                if (speechprogress != null)
                    speechprogress.onResultOrOnError()
                (activity as ChatActivity).setText(voiceResults[0])
                recognizer.destroy()
                if ( (activity as ChatActivity).recordingThread != null ) {
                    chatPresenter.startHotwordDetection()
                }
                (activity as ChatActivity).fabsetting.show()
                activity.supportFragmentManager.popBackStackImmediate()
            }

            override fun onReadyForSpeech(params: Bundle) {
                Log.d("fragment", "Ready for speech")
            }

            override fun onError(error: Int) {
                Log.d("fragment",
                        "Error listening for speech: " + error)
                Toast.makeText(activity.applicationContext, "Could not recognize speech, try again.", Toast.LENGTH_SHORT).show()
                if (speechprogress != null)
                    speechprogress.onResultOrOnError()
                recognizer.destroy()
                (activity as ChatActivity).fabsetting.show()
                activity.supportFragmentManager.popBackStackImmediate()
            }

            override fun onBeginningOfSpeech() {
                Log.d("fragment", "Speech starting")
                if (speechprogress != null)
                    speechprogress.onBeginningOfSpeech()
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // This method is intentionally empty
            }

            override fun onEndOfSpeech() {
                if (speechprogress != null)
                    speechprogress.onEndOfSpeech()
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // This method is intentionally empty
            }

            override fun onPartialResults(partialResults: Bundle) {
                val partial = partialResults
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                txtchat?.text = partial[0]
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (speechprogress != null)
                    speechprogress.onRmsChanged(rmsdB)
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