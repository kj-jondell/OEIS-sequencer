s.boot;
s.reboot;
s.options.device = "Soundflower (64ch)"

NetAddr.localAddr()

//VARIABLES
(
    ~length_pattern = 8;
    //HELPER FUNCTION (TODO: investiage if possible to do with native function?)
    ~getDurations = {
        var list = Array.fill(~length_pattern, {0});
        var durs = Pwrand([1/4,1/8,1/3,1/2],[5,1,4,1].normalizeSum, ~length_pattern).asStream;
        durs.do({arg val,i; list[i] = val/*+0.05.rand*/;});
        list;
    };
    ~degrees = PatternProxy(Pseq([1,5,2,6,3], inf));
    ~durations = PatternProxy(Pseq(~getDurations.value(), inf));
    ~scale = PatternProxy(Scale.majorPentatonic);
)

//SCALE DEFINITION
(
    ~scale.source = Scale.neapolitanMajor;
    ~octaves = 2;
)

//OSC DEF
(
    OSCdef.new('listen', { 
        arg msg;
        var incoming_msg = msg[1..~length_pattern], pattern;

        if(incoming_msg.size<~length_pattern,{
            incoming_msg = incoming_msg.wrapExtend(~length_pattern)});
        
        pattern = Array.fill(~length_pattern, {
            arg index;
            incoming_msg[index] % (~scale.asStream.next.degrees.size*~octaves);
        });
        
        ~degrees.source = Pseq(pattern, inf);
        ~durations.source = Pseq(~getDurations.value(), inf);

    }, "/degree");
)

//SYNTH AND PATTERN DEF
//TODO: change synth!
(
    SynthDef.new(\oeissynth, {
        arg freq = 440, rel = 1.2, pan = 0;
        var signal = SinOsc.ar(SinOsc.ar(Rand(0.25)*800, mul:Rand()*60, add: freq), mul: 0.35)*EnvGen.ar(Env.perc(Rand(0.01,0.28),rel), doneAction: Done.freeSelf);
        Out.ar(pan, signal);
    }).add;
)

(
    Pbindef.new(\oeissequencer,
        \detune, Pwhite(-5,5,inf),
        \instrument, \oeissynth,
        \rel, Pwhite(0.8,1.2,inf),
        \scale, ~scale,
        \degree, ~degrees,
        \root, 4,
        \pan, Pxrand(Array.fill(9,{arg i; i;}), inf),
        \octave, 4,
        \dur, Pwhite(-0.08,0.08,inf)+~durations
    ).play(quant:4);
)

s.queryAllNodes();
s.meter
