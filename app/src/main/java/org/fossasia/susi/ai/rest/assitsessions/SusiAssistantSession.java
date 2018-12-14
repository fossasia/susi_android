package org.fossasia.susi.ai.rest.assitsessions;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.service.voice.VoiceInteractionSession;

@TargetApi(21)
public class SusiAssistantSession extends VoiceInteractionSession {

        public SusiAssistantSession(Context context) {

            super(context);
        }

}
