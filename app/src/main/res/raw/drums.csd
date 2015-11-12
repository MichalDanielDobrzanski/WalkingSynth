<CsoundSynthesizer>
<CsOptions>
; for android:
-o dac -d -m3 -b512 -B2048
</CsOptions>
<CsInstruments>

;-------------------------------------------------------------------------
; globals
;-------------------------------------------------------------------------

sr = 44100
0dbfs = 1
ksmps = 128
nchnls = 2

;-------------------------------------------------------------------------
; global channels
;-------------------------------------------------------------------------

gaRevInL init 0
gaRevInR init 0


;-------------------------------------------------------------------------
; 1 - hihat closed
; p4 - amplitude
; p3 - duration
;-------------------------------------------------------------------------
instr 1
aamp      expon     p4,  p3,   0.01
arand     rand      aamp ; hi hat based on noise
outs arand, arand
endin

;-------------------------------------------------------------------------
; 2 - snare
; p4 - amplitude
;-------------------------------------------------------------------------
instr 2
aenv1  expon  p4 / 2, 0.03, 0.1
a1   oscili aenv1, 147, 1
aamp      expon     p4 / 2,  0.2,   0.01
arand     rand      aamp ; make noise to the snare
a1 = a1 + arand
; send snare to the reverb
gaRevInL = gaRevInL + a1
gaRevInR = gaRevInR + a1
outs a1, a1
endin

;-------------------------------------------------------------------------
; 3 - kick
; p4 - amplitude
; p3 - kick frequency
;-------------------------------------------------------------------------
instr 3
aenv expon p4, 0.25, 0.01
a1  poscil    1, p3, 1
outs a1*aenv, a1*aenv
endin


;-------------------------------------------------------------------------
; Simple bass FM synth
;-------------------------------------------------------------------------

instr 4
ifreq = p5 ; From p4 in the score or cps from MIDI note
kmodindex = p6 ; mod index [ 0 - 5 ]
kmodfactor = p7 ; mod factor [ 0 - 100]

kmodfreq = kmodfactor*ifreq
; Index = Am * fc/fm
kmodamp = kmodindex*kmodfactor*ifreq
; Modulator 2
amod poscil kmodamp, kmodfreq, 1

;Carrier amp envelope
aenv madsr i(p8), i(p9), i(p4), i(p10)

; Carrier
aout poscil aenv, ifreq+amod, 1

; Output
outs aout, aout
endin


;-------------------------------------------------------------------------
; Reverb
;-------------------------------------------------------------------------
instr 100
aInL = gaRevInL
aInR = gaRevInR
gaRevInL = 0
gaRevInR = 0
aInL = aInL / 3
aInR = aInR / 3
aoutL, aoutR reverbsc aInL, aInR, 0.8, 10000
iamp = 0.9
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
; reverb works for the whole time
i100 0 360000 0.6
</CsScore>
</CsoundSynthesizer>
