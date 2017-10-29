package com.dobi.walkingsynth;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.dobi.walkingsynth.model.musicgeneration.utils.Note;
import com.dobi.walkingsynth.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_NAME;
import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_VALUES_BASENOTE_KEY;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void restoringMainActivityFromPreferences() {

        SharedPreferences sharedPreferences =  mActivityRule.getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        String baseNote = sharedPreferences.getString(PREFERENCES_VALUES_BASENOTE_KEY, Note.C.note);

        // TODO FIX IT
        onView(withId(R.id.note_parameter_view)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(baseNote))).perform(click());
        onView(withId(R.id.note_parameter_view)).check(matches(withSpinnerText(containsString(baseNote))));

    }
}
