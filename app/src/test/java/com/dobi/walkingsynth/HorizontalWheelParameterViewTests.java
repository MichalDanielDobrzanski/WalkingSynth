package com.dobi.walkingsynth;

import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.view.impl.HorizontalWheelParameterView;
import com.dobi.walkingsynth.view.ParameterView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Objects;

import static org.junit.Assert.assertTrue;

// TODO: View testing as Unit Testing: http://jordifierro.com/android-view-unit-testing
public class HorizontalWheelParameterViewTests {

    String[] data;

    ParameterView parameterView;

    @Before
    public void setup() {
        data = Note.toStringArray();

        parameterView = Mockito.mock(HorizontalWheelParameterView.class);
    }

    @Test
    public void initialize_isCorrect() {
        parameterView.initialize(data, Note.C.note);

        System.out.println(parameterView.getCurrentValue());

        assertTrue(Objects.equals(parameterView.getCurrentValue(), Note.C.note));
    }

    @Test
    public void getCurrentValue_isCorrect() {
        parameterView.initialize(data, Note.C.name());

        parameterView.setValue(Note.FSharp.name());

        assertTrue(Objects.equals(parameterView.getCurrentValue(), Note.FSharp.note));
    }

}
