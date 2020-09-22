package fr.velinfo.webapp;

public class PatternBreakingCharacterRemover {

    public static String strip(String string){
        return string.replaceAll("[\n|\r|\t]", "_");
    }
}
