package com.dobi.walkingsynth.music;

import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

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
public class CsoundBaseSetup implements CsoundObjListener, CsoundBinding {

    private static final String TAG = CsoundBaseSetup.class.getSimpleName();

    protected CsoundObj csoundObj = new CsoundObj(false,true);
    protected Handler handler = new Handler();

    private Resources resources;
    private File cacheDir;

    public CsoundBaseSetup(Resources res, File cDir) {
        resources = res;
        cacheDir = cDir;

        String csd = getResourceFileAsString(R.raw.drums);
        File f = createTempFile(csd);

        csoundObj.setMessageLoggingEnabled(true);
        csoundObj.addBinding(this);
        csoundObj.startCsound(f);
    }

    public void destroy() {
        csoundObj.stop();
    }

    protected String getResourceFileAsString(int resId) {
        StringBuilder str = new StringBuilder();

        InputStream is = resources.openRawResource(resId);
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String line;

        try {
            while ((line = r.readLine()) != null) {
                str.append(line).append("\n");
            }
        } catch (IOException ios) {

        }

        return str.toString();
    }

    protected File createTempFile(String csd) {
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
