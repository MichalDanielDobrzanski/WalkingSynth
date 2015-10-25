<CsoundSynthesizer>
<CsOptions>
-o dac -d -m3 -b512 -B2048
</CsOptions>
<CsInstruments>
nchnls = 2
0dbfs = 10000
sr = 44100
ksmps = 32

instr 1; hihat closed
aamp      expon     1000,  0.1,   p4
arand     rand      aamp
outs arand, arand
endin

instr 2;snare
aenv1  expon  p4, 0.03, 0.5
a1   oscili aenv1, 147, 1
aamp      expon     1000,  0.2,   10
arand     rand      aamp
outs a1+arand, a1+arand
endin

instr 3; kick
k1  expon    p4, .2, 50
aenv expon 1, p3, 0.01
a1  poscil    10000, k1, 1
outs a1*aenv, a1*aenv
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

i3 0      0.25  100
i3 0.375  0.25  100
i3 0.75   0.25  100
i3 1.25   0.25  100
i3 2      0.25  100
i3 2.375  0.25  100
i3 2.75   0.25  100
i3 3.25   0.25  100
i3 3.75   0.25  100
</CsScore>
</CsoundSynthesizer>