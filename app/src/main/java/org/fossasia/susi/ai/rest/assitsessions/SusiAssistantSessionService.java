package org.fossasia.susi.ai.rest.assitsessions;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;

@TargetApi(21)
public class SusiAssistantSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(Bundle bundle) {
        return new SusiAssistantSession(this);
    }
}