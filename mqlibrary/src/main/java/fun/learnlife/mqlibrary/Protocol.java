package fun.learnlife.mqlibrary;

public interface Protocol {
    String wake = "voice.wake";
    String vad_start = "voice.vad.start";
    String vad_end = "voice.vad.end";
    String asr = "voice.asr";
    String nlu = "voice.nlu";
    String tts_start = "voice.tts.start";
    String tts_end = "voice.tts.end";
    String session_end = "voice.session.end";
}
