package org.fossasia.susi.ai.chat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.util.Locale
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_sttframe.*
import kotlinx.android.synthetic.main.fragment_sttframe.view.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.recycleradapters.VoiceCommandsAdapter
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.helper.PrefManager
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

/**
 * Created by meeera on 17/8/17.
 */
class STTFragment : Fragment() {
    lateinit var recognizer: SpeechRecognizer
    private val chatPresenter: IChatPresenter by inject { parametersOf(this) }
    private val thisActivity = activity
    private var textToSpeech: TextToSpeech? = null
    private val mainHandler: Handler = Handler()
    private val subHandler: Handler = Handler()

    @NonNull
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_sttframe, container, false)
        if (thisActivity is ChatActivity)
            thisActivity.fabsetting.hide()
        setupCommands(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recognizer = SpeechRecognizer
                .createSpeechRecognizer(activity?.applicationContext)

        mainHandler.post(runnable)
        if (!PrefManager.getBoolean(R.string.used_voice, true)) {
            subHandler.postDelayed(delayRunnable, 1500)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private val runnable: Runnable = Runnable {
        textToSpeech = TextToSpeech(requireContext(), TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                val locale = textToSpeech?.language
                textToSpeech?.language = locale
                if (!PrefManager.getBoolean(R.string.used_voice, false)) {
                    textToSpeech?.speak(getString(R.string.voice_welcome), TextToSpeech.QUEUE_FLUSH, null)
                    PrefManager.putBoolean(R.string.used_voice, true)
                } else {
                    promptSpeechInput()
                }
            }
        })
    }

    private val delayRunnable: Runnable = Runnable {
        promptSpeechInput()
    }

    private fun setupCommands(rootView: View) {
        var voiceCommand = getResources().getStringArray(R.array.voiceCommands)
        var voiceCommandsList = voiceCommand.toCollection(ArrayList())
        rootView.clickableCommands.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rootView.clickableCommands.adapter = VoiceCommandsAdapter(voiceCommandsList, activity)
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.domain.app")
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000)

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
                val thisActivity = activity
                if (thisActivity is ChatActivity) thisActivity.setText(voiceResults[0])
                recognizer.destroy()
                if ((activity as ChatActivity).recordingThread != null) {
                    chatPresenter.startHotwordDetection()
                }
                (activity as ChatActivity).fabsetting.show()
                activity?.searchChat?.show()
                activity?.voiceSearchChat?.show()
                activity?.btnSpeak?.isEnabled = true
                activity?.supportFragmentManager?.popBackStackImmediate()
            }

            override fun onReadyForSpeech(params: Bundle) {
                Timber.d("Ready for speech")
            }

            override fun onError(error: Int) {
                Timber.d("Error listening for speech: %s", error)
                Toast.makeText(activity?.applicationContext, "Could not recognize speech, try again.", Toast.LENGTH_SHORT).show()
                speechProgress?.onResultOrOnError()
                recognizer.destroy()
                activity?.fabsetting?.show()
                activity?.searchChat?.show()
                activity?.voiceSearchChat?.show()
                activity?.btnSpeak?.isEnabled = true
                activity?.supportFragmentManager?.popBackStackImmediate()
            }

            override fun onBeginningOfSpeech() {
                Timber.d("Speech starting")
                speechProgress?.onBeginningOfSpeech()
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // This method is intentionally empty
            }

            override fun onEndOfSpeech() {
                speechProgress?.onEndOfSpeech()
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
                speechProgress?.onRmsChanged(rmsdB)
            }
        }
        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)
    }

    override fun onPause() {
        super.onPause()
        if (thisActivity is ChatActivity) {
            thisActivity.enableVoiceInput()
            thisActivity.fabsetting.show()
        }
        if (textToSpeech != null) {
            textToSpeech?.stop()
        }
        recognizer.cancel()
        recognizer.destroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainHandler.removeCallbacks(runnable)
        subHandler.removeCallbacks(delayRunnable)
    }
}
