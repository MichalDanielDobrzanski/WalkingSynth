<CsoundSynthesizer>
<CsOptions>
; for android:
-o dac -d -m3 -b512 -B2048
</CsOptions>
<CsInstruments>
;-------------------------------------------------------------------------
; MAIN INFO:
; - reverb works all the time
; - snare uses reverb
; - 1st synth uses delay and reverb
; - delay works only when invoked as an event!
;-------------------------------------------------------------------------



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

giSine ftgen 0,0,8192,10,1

gaRevSendL init 0
gaRevSendR init 0

gaDelSendL init 0
gaDelSendR init 0


;-------------------------------------------------------------------------
; 1 - hihat closed
; instr stime dur amplitude
; i1    0     0.25  0.6
;
; p4 - amplitude
;-------------------------------------------------------------------------
instr 1
aamp      expon     p4,  p3,   0.01
arand     rand      aamp ; hi hat based on noise
outs arand, arand
endin

;-------------------------------------------------------------------------
; 2 - snare
; instr stime dur amplitude
; i2    0     0.26  0.6
;
; p4 - amplitude
;-------------------------------------------------------------------------
instr 2
aenv1  expon  p4 / 2, 0.03, 0.1
a1   oscili aenv1, 147, 1
aamp      expon     p4 / 2,  0.2,   0.01
arand     rand      aamp ; make noise to the snare
a1 = a1 + arand
; send snare to the reverb
gaRevSendL = gaRevSendL + a1
gaRevSendR = gaRevSendR + a1
outs a1, a1
endin

;-------------------------------------------------------------------------
; 3 - kick
; instr stime freq amplitude
; i3    0     100  0.6
;
; p3 - kick frequency
; p4 - amplitude
;
;-------------------------------------------------------------------------
instr 3
aenv expon p4, 0.25, 0.01
a1  poscil    1, p3, 1
outs a1*aenv, a1*aenv
endin

;-------------------------------------------------------------------------
; 1st synth (i4)
; goes to the reverb and delay
; p5 - Frequency is passed as PITCH.
; p6 - mod_idx [0 - 5]
; p7 - mod_factor [-100, 100]
; p8-11 - ADSR later on
;
; instr start  dur  amp freq     mod_idx  mod_fact att dec sus rel
; i4      0    0.2  0.4 10.00     1       1        0.6 0.8 0.6 0.1
;-------------------------------------------------------------------------
instr 4
ifreq = cpspch(p5)
kmodindex = p6 ; mod index [ 0 - 5 ]
kmodfactor = p7 ; mod factor [ 0 - 100]
kmodfreq = kmodfactor*ifreq
kmodamp = kmodindex*kmodfactor*ifreq
; Modulator 2
amod poscil kmodamp, kmodfreq, 1
;Carrier amp envelope
aenv madsr i(p8), i(p9), i(p10), i(p11) ; [0 - 1]
; Carrier
aout poscil aenv, ifreq+amod, 1
aout = aout * p4
; Reverb output
gaRevSendL = aout
gaRevSendR = aout
; Delay output
gaDelSendL = aout
gaDelSendR = aout
; Output
iamp = 0.6
kdeclick linseg iamp, 366, iamp, 0.05, 0
outs aout * iamp, aout * iamp
endin


;-------------------------------------------------------------------------
; 2nd synth (i5)
; goes only to the reverb
; Made from Csound Power! book
;instr start dur amp freq
; i5     0    2  0.6 8.04
;-------------------------------------------------------------------------
instr 5
; constants
iamp = p4 * 0.5
index = 3
ifreq = cpspch(p5)
idetune = 0.7
; envelopes
kindexenv linseg index, 0.05, (index * 0.5), (p3 - 0.05), 0
kampenv linseg 0, 0.01, iamp, p3 - 0.01, 0
; tone generators
aout foscil kampenv, ifreq - idetune, 1, 1, kindexenv, giSine
aout2 foscil kampenv, ifreq + idetune, 1, 1, kindexenv, giSine
;aout = aout + ao ut2
; panning - later on!
;aL, aR pan2 aout, p6
; send signal to the reverb
gaRevSendL = gaRevSendL + aout
gaRevSendR = gaRevSendR + aout2
outs aout, aout2
endin

;-------------------------------------------------------------------------
; Delay
; p4 - amplitude
; p5 - feedback
; p6 - left delay
; p7 - right delay
; instr  dur end amp feed ldel rdel
;  i99   0   30  0.7 0.3  0.5  0.75
;-------------------------------------------------------------------------
instr 99
ilevel = p4
ifeedback = p5
itimeL = p6
itimeR = p7
aInL = gaDelSendL
aInR = gaDelSendR
gaDelSendL = 0
gaDelSendR = 0
adeloutR init 0
adeloutL delayr itimeL
	delayw aInL + (adeloutR * ifeedback)
adeloutL dcblock2 adeloutL
adeloutR delayr itimeR
	delayw aInR + (adeloutL * ifeedback)
adeloutR dcblock2 adeloutR
adeloutL = adeloutL * ilevel
adeloutR = adeloutR * ilevel
outs adeloutL,adeloutR
endin

;-------------------------------------------------------------------------
; Reverb
;-------------------------------------------------------------------------
instr 100
aInL = gaRevSendL
aInR = gaRevSendR
gaRevSendL = 0
gaRevSendR = 0
aInL = aInL / 3
aInR = aInR / 3
aoutL, aoutR reverbsc aInL, aInR, 0.9, 10000
iamp = 0.45
kdeclick linseg iamp, 366, iamp, 0.05, 0
aoutL = aoutL * kdeclick
aoutR = aoutR * kdeclick
outs aoutL, aoutR
endin

</CsInstruments>
<CsScore>
f1 0 1024 10 1

; reverb works for the whole time
i100 0 360000

; always active delay line
;	       dur out feed ldel rdel
i99  0 360000  0.7 0.3  0.5  0.75

; listen to events for 1hour * 100
e 360000

</CsScore>
</CsoundSynthesizer>
