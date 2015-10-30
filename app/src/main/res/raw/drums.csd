<CsoundSynthesizer>
<CsOptions>
; for android:
-o dac -d -m3 -b512 -B2048
</CsOptions>
<CsInstruments>
sr = 44100
0dbfs = 10000
ksmps = 32
nchnls = 2

; init bus
gaRevInL init 0
gaRevInR init 0

instr 1; hihat closed
aamp      expon     600,  0.1,   p4
arand     rand      aamp
outs arand, arand
endin

instr 2;snare
aenv1  expon  p4 , 0.03, 0.5
a1   oscili aenv1, 147, 1
aamp      expon     1000,  0.2,   10
arand     rand      aamp
a1 = a1 + arand
; send signal to the reverb
gaRevInL = gaRevInL + a1
gaRevInR = gaRevInR + a1
outs a1, a1
endin

instr 3; kick
k1  expon    p4, .2, 50
aenv expon 1, p3, 0.01
a1  poscil    8000, k1, 1
outs a1*aenv, a1*aenv
endin

; reverb
instr 100
aInL = gaRevInL
aInR = gaRevInR
gaRevInL = 0
gaRevInR = 0
aInL = aInL / 3
aInR = aInR / 3
aoutL, aoutR reverbsc aInL, aInR, 0.8, 10000
iamp = 0.6
kdeclick linseg iamp, (p3 - 0.05), iamp, 0.05, 0
aoutL = aoutL * kdeclick
aoutR = aoutR * kdeclick
outs aoutL, aoutR
endin

</CsInstruments>
<CsScore>
f1 0 1024 10 1

; listen to events for 1hour * 100
e 360000
</CsScore>
</CsoundSynthesizer> 
