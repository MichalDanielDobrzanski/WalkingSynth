package com.dobi.walkingsynth.csound;

import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import com.csounds.CsoundObj;
import com.csounds.CsoundObjListener;
import com.csounds.bindings.CsoundBinding;
import com.dobi.walkingsynth.OnStepCountChangeListener;
import com.dobi.walkingsynth.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Creating music, anaylizing tempo.
 */
public class CsoundMusician implements CsoundObjListener, CsoundBinding {

    private static final String TAG = CsoundMusician.class.getSimpleName();

    protected CsoundObj csoundObj = new CsoundObj(false,true);
    protected Handler handler = new Handler();

    private Resources resources;
    private File cacheDir;

    private int mStepCount = 0;

    public CsoundMusician(Resources res, File cDir) {
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

    /**
     * step has been detected
     * @param v eventTime value
     */
    public void onStep(long eventTime) {
        ++mStepCount;
        Log.d(TAG,"onStep");
        csoundObj.sendScore(String.format(
                "i3 0 0.25 100",mStepCount));
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
