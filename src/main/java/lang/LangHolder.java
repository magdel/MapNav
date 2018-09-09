/*
 * LangHolder.java
 *                  
 * Created on 16 январь 2007 г., 14:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lang;


import RPMap.MapCanvas;
import java.io.DataInputStream;


public class LangHolder {
  
  // Common prefix of all resource packages
  //public static final String PREFIX = "lang.Lang";
  
  
  // List of available language packs
  public static String[] LANG_AVAILABLE(){
     return  new String[]{"EN","RU","DE","PR","FI","UA","BG","SK","LT","ES","PL","NO","TR","AR","HU","FR","CZ","IT"};
  };
  
  // List of loaded languages
  //private static String[][] LANGS={null,null,null,null,null,null,null,null,null,null};
  
  // Current language
  private static String currUiLanguage = LangHolder.LANG_AVAILABLE()[0];
  private static int currUiLanguageInd = 0;
  
  public static String nm="nm";
  public static String mi="mi";
  public static String mph="mph";
  public static String kn="kn";
  public static String ft="ft";
  public static String fpm="ft/min";
  
  // Get user interface language/localization for current session
  public final static String getCurrUiLanguage() {
    return LangHolder.currUiLanguage;
  }
  
  
  // Set user interface language/localization for current session
  public static void setCurrUiLanguage(String currUiLanguage) {
     String[] LANG_AVAILABLE = LangHolder.LANG_AVAILABLE();
      for (int i = 0; i < LANG_AVAILABLE.length; i++) {
      if (LANG_AVAILABLE[i].equals(currUiLanguage)) {
        LangHolder.currUiLanguage = currUiLanguage;
        LangHolder.currUiLanguageInd=i;
        loadCurrLang();
        return;
      }
    }
  }
  
  private static void loadEnglishLang(){
//    if (LangHolder.LANGS[0]==null) {
      DataInputStream dis = new DataInputStream(MapCanvas.map.getClass().getResourceAsStream("/lang/"+LangHolder.LANG_AVAILABLE()[0]+".lng"));
      try {
        try{
          int sc;
          int osc = dis.readShort();
          words=null;
          sc=osc;
          words = new String[sc];
          for (int i=0;i<osc;i++)
            words[i]=dis.readUTF();
        //  LangHolder.LANGS[0]=words;
        }finally{
          dis.close();
        }
      }catch(Throwable t){
        //   currUiLanguageInd=currUiLanguageInd;
      }
    //}
   // words=LangHolder.LANGS[currUiLanguageInd];
  }
  
  private static void loadCurrLang(){
    loadEnglishLang();
    if (currUiLanguageInd==0)return;
    
    String[] wordsn=null;
    //if (LangHolder.LANGS[currUiLanguageInd]==null) {
      try {
      DataInputStream dis = new DataInputStream(MapCanvas.map.getClass().getResourceAsStream("/lang/"+LangHolder.LANG_AVAILABLE()[LangHolder.currUiLanguageInd]+".lng"));
        //int sc;
try{       
 int osc = dis.readShort();
        //words=null;
//        sc=osc;
//        if (currUiLanguageInd!=0) {
//          sc=words.length;
//        }
        //wordsn = new String[sc];
        for (int i=0;i<osc;i++)
          words[i]=dis.readUTF();
      }finally{
        dis.close();
      }
//        if (currUiLanguageInd!=0) {
//          if (osc<words.length) {
//            for (int i=osc;i<words.length;i++)
//              wordsn[i]=words[i];
//          }
//        }
      }catch(Throwable t){
        //   currUiLanguageInd=currUiLanguageInd;
      }
   // }
   // words=wordsn;
  }
  
  private static String nr = "NetRadar";
  // Get string from active language pack
  public static synchronized String getString(int key) {
    if (key<0){
      if (key==-1) return nr;
    } else
    if (key<LangHolder.words.length) {
      return LangHolder.words[key];
    }
    return String.valueOf(key);
//    // Get/load resource bundle
//    LangHolder holder;
//    synchronized (groups) {
//      holder = (LangHolder) groups.get(LangHolder.currUiLanguage);
//      if (holder == null) {
//        holder = LangHolder.loadBundle(LangHolder.currUiLanguage);
//      }
//    }
    
    // Return value from resource bundle
    // return holder._getString(key,true);
    
  }
  
//  static String getString(int key, boolean first) {
//
//    // Get/load resource bundle
//    LangHolder holder;
//    synchronized (groups) {
//      holder = (LangHolder) groups.get(LangHolder.currUiLanguage);
//      if (holder == null) {
//        holder = LangHolder.loadBundle(LangHolder.currUiLanguage);
//      }
//    }
//    // Return value from resource bundle
//    return holder._getString(key,first);
//
//  }
  
  
  
//  // Initialize ResourceBundle
//  public static LangHolder loadBundle(String name) {
//
//    // Load
//    LangHolder holder = null;
//    try {
//      holder = (LangHolder) Class.forName(LangHolder.PREFIX + "_" + name).newInstance();
//    } catch (Exception e) {
//      // Do nothing
//    }
//
//    // Put into hashtable
//    LangHolder.groups.put(name, holder);
//
//    // Return ResourceBundle
//    return (holder);
//
//  }
  
  
  // Resource hashtable
  protected static String[] words;// = new Hashtable();
  
//
//  // Return value form hashtable
//  protected String _getString(int key, boolean first) {
//    if (key<words.length) {
//    return words[key];
//    }
//    return String.valueOf(key);
////    if (value != null) {
////      return (value);
////    } else
////      if (first) {
////      return Lang_EN.getString(key,false);
////      } else return (key);
//
//  }
  
  
  
}

