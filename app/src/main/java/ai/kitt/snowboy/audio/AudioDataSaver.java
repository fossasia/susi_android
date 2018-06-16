package ai.kitt.snowboy.audio;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ai.kitt.snowboy.Constants;
import timber.log.Timber;

public class AudioDataSaver implements AudioDataReceivedListener {

    private File saveFile;
    private DataOutputStream dataOutputStreamInstance = null;

    public AudioDataSaver() {
        saveFile = new File(Constants.SAVE_AUDIO);
        Timber.e(Constants.SAVE_AUDIO);
    }

    @Override
    public void start() {
        if (null != saveFile) {
            if (saveFile.exists()) {
                saveFile.delete();
            }
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                Timber.e(e, "IO Exception on creating audio file %s", saveFile);
            }

            try {
                BufferedOutputStream bufferedStreamInstance = new BufferedOutputStream(
                        new FileOutputStream(this.saveFile));
                dataOutputStreamInstance = new DataOutputStream(bufferedStreamInstance);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Cannot Open File", e);
            }
        }
    }

    @Override
    public void onAudioDataReceived(byte[] data, int length) {
        try {
            if (null != dataOutputStreamInstance) {
                dataOutputStreamInstance.write(data, 0, length);
            }
        } catch (IOException e) {
            Timber.e(e, "IO Exception on saving audio file %s", saveFile);
        }
    }

    @Override
    public void stop() {
        if (null != dataOutputStreamInstance) {
            try {
                dataOutputStreamInstance.close();
            } catch (IOException e) {
                Timber.e(e, "IO Exception on finishing saving audio file %s", saveFile.toString());
            }
            Timber.e("Recording saved to " + saveFile.toString());
        }
    }
}
