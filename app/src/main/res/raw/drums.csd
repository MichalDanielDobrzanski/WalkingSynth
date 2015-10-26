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
aamp      expon     1000,  0.1,   p4
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
a1  poscil    10000, k1, 1
outs a1*aenv, a1*aenv
endin

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

i1 0      0.25 10
i1 0.25   0.25 10
i1 0.5    0.25 10
i1 0.75   0.25 10
i1 1      0.25 10
i1 1.25   0.25 10
i1 1.5    0.25 10
i1 1.75   0.25 10
i1 2      0.25 10
i1 2.25   0.25 10
i1 2.5    0.25 10
i1 2.75   0.25 10
i1 3      0.25 10
i1 3.25   0.25 10
i1 3.5    0.25 10
i1 3.75   0.25 10

i2 0.5 1 10000
i2 1.5 1 10000
i2 2.5 1 10000
i2 3.5 1 10000

i3 0      0.25  50
i3 0.375  0.25
i3 0.75   0.25
i3 1.25   0.25
i3 2      0.25
i3 2.375  0.25
i3 2.75   0.25
i3 3.25   0.25
i3 3.75   0.25 

i100 0 5 0.6

</CsScore>
</CsoundSynthesizer> 
