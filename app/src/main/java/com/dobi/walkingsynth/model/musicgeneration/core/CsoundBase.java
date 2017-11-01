package com.dobi.walkingsynth.model.musicgeneration.core;

import android.content.res.Resources;
import android.os.Handler;

import com.csounds.CsoundObj;
import com.csounds.CsoundObjListener;
import com.csounds.bindings.CsoundBinding;
import com.dobi.walkingsynth.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Csound initialization and configuration
 */
class CsoundBase implements CsoundObjListener, CsoundBinding {

    private static final String TAG = CsoundBase.class.getSimpleName();

    protected CsoundObj csoundObj = new CsoundObj(false, true);
    protected Handler handler = new Handler();

    private Resources resources;
    private File cacheDir;
    private File tempFile;

    public CsoundBase(Resources res, File cDir) {
        resources = res;
        cacheDir = cDir;

        String csd = getResourceFileAsString(R.raw.csound_part);
        tempFile = createTempFile(csd);

        csoundObj.setMessageLoggingEnabled(true);
        csoundObj.addBinding(this);
    }

    public void startCSound() {
        if (csoundObj.isStopped())
            csoundObj.startCsound(tempFile);
        else if(csoundObj.isPaused())
            csoundObj.play();
    }

    public void destroyCSound() {
        csoundObj.stop();
    }

    private String getResourceFileAsString(int resId) {
        StringBuilder str = new StringBuilder();

        InputStream is = resources.openRawResource(resId);
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String line;

        try {
            while ((line = r.readLine()) != null) {
                str.append(line).append("\n");
            }
        } catch (IOException ignored) {

        }

        return str.toString();
    }

    private File createTempFile(String csd) {
        File f = null;

        try {
            f = File.createTempFile("temp", ".csd", cacheDir);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(csd.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return f;
    }

    @Override
    public void setup(CsoundObj csoundObj) {

    }

    @Override
    public void updateValuesToCsound() {

    }

    @Override
    public void updateValuesFromCsound() {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void csoundObjStarted(CsoundObj csoundObj) {

    }

    @Override
    public void csoundObjCompleted(CsoundObj csoundObj) {

    }
}
