import java.io.File;
import java.util.*;
import edu.duke.*;

public class VigenereBreaker {
    public static void main(String[] args) {
        VigenereBreaker vb = new VigenereBreaker();
        vb.breakVigenere();

    }
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder sliced = new StringBuilder();
        for (int i = whichSlice; i < message.length(); i+=totalSlices) {
            sliced.append(message.charAt(i));
        }

        return sliced.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        CaesarCracker cc = new CaesarCracker(mostCommon);
        for (int i = 0; i < klength; i++) {
            String charsSliced = sliceString(encrypted, i, klength);
            int k = cc.getKey(charsSliced);
            key[i] = k;
        }
        return key;
    }

    public void breakVigenere () {
        FileResource fr = new FileResource();
        String message = fr.asString().toLowerCase();
        DirectoryResource dr = new DirectoryResource();

        HashMap<String, HashSet<String>> dictionaryMap = new HashMap<>();
        for(File f : dr.selectedFiles()){
            FileResource frd = new FileResource(f);
            HashSet<String> dictionary = readDictionary(frd);
            dictionaryMap.put(f.getName(), dictionary);
        }
        breakForAllLangs(message, dictionaryMap);
    }

    public HashSet<String> readDictionary(FileResource fr){
        HashSet<String> dictionary = new HashSet<>();
        for(String line : fr.lines()){
            dictionary.add(line.toLowerCase());
        }
        return dictionary;
    }
    public int countWords(String message, HashSet<String> dictionary){
        String[] messageArray = message.split("\\W+");
        int count = 0;

        for(String word: messageArray){
            if(dictionary.contains(word)){
                count++;
            }
        }
        return count;
    }
    public String breakForLanguage(String encrypted, HashSet<String> dictionary){
        int max = 0;
        String newDictionary = "";
        char mostCommon = mostCommonCharIn(dictionary);

        for (int i = 1 ; i < 100; i++) {
            int[] key;
            key = tryKeyLength(encrypted,i,mostCommon);
            VigenereCipher vc = new VigenereCipher(key);
            String decrypted = vc.decrypt(encrypted);
            if (countWords(decrypted, dictionary) > max) {
                max = countWords(decrypted, dictionary);
                newDictionary = decrypted;
            }
        }
        System.out.println("The number of valid words is: " + max);
        return newDictionary;
    }
    public char mostCommonCharIn(HashSet<String> dictionary){
        HashMap<Character, Integer> count = new HashMap<>();
        char mostCommonChar = 'a';
        int max = 0;
        for(String character : dictionary) {
            for (int i = 0; i < character.length(); i++) {
                if(count.containsKey(character.charAt(i))) {
                    count.put(character.charAt(i), count.get(character.charAt(i)) + 1);
                }
                else {
                    count.put(character.charAt(i), 1);
                }
            }
        }
        for(Character character : count.keySet()){
            if(count.get(character) > max) {
                max = count.get(character);
                mostCommonChar = character;
            }
        }
        System.out.println("The most common char is: " + mostCommonChar);
        return mostCommonChar;
    }
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages){
        for(String languageName : languages.keySet()){
            System.out.println("This is: " + languageName);
            String decrpyted = breakForLanguage(encrypted, languages.get(languageName));
            System.out.println(decrpyted);
        }
    }

}
