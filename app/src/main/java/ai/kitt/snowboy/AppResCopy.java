package ai.kitt.snowboy;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import timber.log.Timber;

public class AppResCopy {
    private static String envWorkSpace = Constants.DEFAULT_WORK_SPACE;

    private static void copyFilesFromAssets(Context context, String assetsSrcDir, String sdcardDstDir, boolean override) {
        try {
            String fileNames[] = context.getAssets().list(assetsSrcDir);
            if (fileNames.length > 0) {
                Timber.i(" directory has %s files.\n", fileNames.length);
                File dir = new File(sdcardDstDir);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Timber.e("mkdir failed: %s", sdcardDstDir);
                        return;
                    } else {
                        Timber.i("mkdir ok: %s", sdcardDstDir);
                    }
                } else {
                    Timber.w("%s already exists! ", sdcardDstDir);
                }
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, assetsSrcDir + "/" + fileName, sdcardDstDir + "/" + fileName, override);
                }
            } else {
                Timber.i("%s is file\n", assetsSrcDir);
                File outFile = new File(sdcardDstDir);
                if (outFile.exists()) {
                    if (override) {
                        outFile.delete();
                        Timber.e("overriding file %s\n", sdcardDstDir);
                    } else {
                        Timber.e("file %s already exists. No override.\n", sdcardDstDir);
                        return;
                    }
                }
                InputStream is = context.getAssets().open(assetsSrcDir);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
                Timber.i("copy to %s ok!", sdcardDstDir);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void copyResFromAssetsToSD(Context context) {
        copyFilesFromAssets(context, Constants.ASSETS_RES_DIR, envWorkSpace + "/", true);
    }
}
