<CsoundSynthesizer>
<CsOptions>
-odac -d -m3 -B512 -b256
</CsOptions>
<CsInstruments>
sr = 44100
nchnls = 2

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
</CsScore>
</CsoundSynthesizer>