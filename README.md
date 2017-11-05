# Walking synth #

*Updated as for 05.11.2017* - the WalkingSynth has undergone several architectural changes in order to facilitate the latest Android design patterns and architecture suggestions. This means that the project uses:
* Dagger2 for Dependency Injection
* Butterknife
* MVP pattern for sepratation
* RxJava2 for handling accelerometer values
* Fantastic 3d praty Horizontal Wheel View for displaying current note
* Custom Picker Views created with the help of recycler view

### Walking Synthesizer ###

This is an Android app for playing and generating music while walking.
It keeps track of the leg movement pace and plays music based on that.
It counts the steps quite precisely, so this could also serve as pedometer.
It comes with particular version of compiled Csound library as *.so file .

## Tools ##
* Mobile device's accelerometer
* Csound library - [Csound GitHub](https://csound.github.io/)
* Achartengine

## App features ##
* Customizable base note of the synthesizer melody
* Scales to play (Pentatonic / Flamenco)
* The amount of steps after the user will be notified and the synth would play different variation of the current scale
* Adjustable threshold for different leg pace and force
* Step counter (pedometer)
* Current tempo in bpm
* Elapsed time
* Accelerometer data (the blue line in the chart)
* Save/restore musical parameters
* Save threshold for step detection

## Info ##
* Written in Android Studio 3.0
* Tested on Google Nexus 7 2013 API 22 (5.1.1), OnePLus5 (7.1.1)

## Owner and purpose ##
* Software under MIT License
* Michał Dobrzański
* dobrzanski.michal.daniel@gmail.com
* This is my personal software project created with passion to the music. 
* I explored the depths of csound language, which seems to be very poweful.


![Screen1](https://github.com/MichalDanielDobrzanski/WalkingSynth/blob/master/Screenshot_2015-12-23-21-21-49.png)
![Screen2](https://github.com/MichalDanielDobrzanski/WalkingSynth/blob/master/Screenshot_2015-12-23-21-22-04.png)
