package com.dobi.walkingsynth.model.musicgeneration.synths;
import android.util.Log;

import com.csounds.CsoundObj;
import com.dobi.walkingsynth.model.musicgeneration.core.CsoundPlayer;
import com.dobi.walkingsynth.model.musicgeneration.utils.Note;
import com.dobi.walkingsynth.model.musicgeneration.utils.Scale;

import java.util.Locale;
import java.util.Random;

/**
 * Synthesizer player. Needs to now about the current key and scale and step and beat count.
 * Provides the SynthesizerSequencer with necessary information.
 */
public class SynthesizerPlayer extends CsoundPlayer {

    private static final String TAG = SynthesizerPlayer.class.getSimpleName();

    private final Locale locale;

    private int lastStep;

    private int[] rhythmSequence;

    private Random random = new Random(System.currentTimeMillis());

    private SynthesizerSequencer synthesizerSequencer;

    public SynthesizerPlayer(CsoundObj csoundObj, int interval, SynthesizerSequencer synthesizerSequencer) {
        super(csoundObj, interval);

        locale = Locale.getDefault();
        lastStep = 0;

        this.synthesizerSequencer = synthesizerSequencer;

        rhythmSequence = this.synthesizerSequencer.getRandomScore();
    }

    /**
        Parsing the current sequence and playing it at right time.
     */
    private void playRhythmScore(int bc) {
        if (bc % 4 == 0) {
            playCsoundRhythmNote(rhythmSequence[0]);
            playCsoundLeadNote(rhythmSequence[0]);
        }
        if ((bc + 1) % 4 == 0) {
            playCsoundRhythmNote(rhythmSequence[1]);
            playCsoundLeadNote(rhythmSequence[1]);
        }
        if ((bc + 2) % 4 == 0) {
            playCsoundRhythmNote(rhythmSequence[2]);
            playCsoundLeadNote(rhythmSequence[2]);
        }
        if ((bc + 3) % 4 == 0) {
            playCsoundRhythmNote(rhythmSequence[3]);
            playCsoundLeadNote(rhythmSequence[3]);
        }
    }

    /**
        instrument number | amplitude | note to play
     */
    private void playCsoundRhythmNote(int note) {
        csoundObj.sendScore(String.format(locale, "i%d 0 1 0.%d 6.%02d", 5, 5, note));
    }

    /**
     *  instrument number | amplitude | note to play | mod_index | mod_factor
     */
    private void playCsoundLeadNote(int note) {
        csoundObj.sendScore(String.format(locale, "i%d 0 0.1 0.%d 8.%02d %d %d 0.6 0.8 0.6 0.1",
                4, 4, note, random.nextInt(5), random.nextInt(5)));
    }

    /**
     *  when to startCSound | note to play | mod_index | mod_factor
     */
    private void playCsoundArpNotes() {
        csoundObj.sendScore(String.format(locale, "i4 0.%d 0.11 0.42 9.%02d %d %d 0.2 0.8 0.3 0.1",
                0, rhythmSequence[0], random.nextInt(5), random.nextInt(5)));
        csoundObj.sendScore(String.format(locale, "i4 0.%d 0.15 0.41 9.%02d %d %d 0.2 0.8 0.3 0.1",
                2, rhythmSequence[1], random.nextInt(5), random.nextInt(5)));
        csoundObj.sendScore(String.format(locale, "i4 0.%d 0.13 0.42 9.%02d %d %d 0.2 0.8 0.3 0.1",
                3, rhythmSequence[2], random.nextInt(5), random.nextInt(5)));
        csoundObj.sendScore(String.format(locale, "i4 0.%d 0.12 0.35 9.%02d %d %d 0.2 0.8 0.3 0.1",
                4, rhythmSequence[3], random.nextInt(5), random.nextInt(5)));
    }

    private void randomize() {
        rhythmSequence = synthesizerSequencer.getRandomScore();
    }

    @Override
    public void onStep(int step) {
        Log.d(TAG, "onStep(): step: " + step + " interval:  " + interval);

        if (step > 0 && lastStep != step && step % interval == 0) {
            Log.d(TAG, "Playing random score.");
            rhythmSequence = synthesizerSequencer.getRandomScore();
            playCsoundArpNotes();
            lastStep = step;
        }
    }

    @Override
    public void invalidate(int position) {
        Log.d(TAG, "invalidateTempo(): " + position);
        if ((position + 8) % 8 == 0) {
            playRhythmScore(position);
        }
    }

    @Override
    public void invalidateNote(Note note) {
        synthesizerSequencer.setNote(note);
        randomize();
    }

    @Override
    public void invalidateScale(Scale scale) {
        randomize();
    }

    @Override
    public void invalidateInterval(int interval) {
        this.interval = interval;
    }
}
