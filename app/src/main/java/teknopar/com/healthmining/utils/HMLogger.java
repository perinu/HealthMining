package teknopar.com.healthmining.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import teknopar.com.healthmining.core.Constants;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 2/5/15
 * <br/>
 * <h3>Description</h3>
 *
 * Logger class for the application. It may also dispatch log data to
 * a file via the method {@link HMLogger#attachFileHandler(String, int)}
 */
public final class HMLogger {
    
    private static int     APP_LOG_LEVEL          = Log.VERBOSE;
    
    private static char    APP_LOG_LEVEL_CHAR_REP = 'V';
    
    private static String  APP_TAG                = Constants.DEFAULT_APP_TAG;
    
    private static boolean isFileHandlerAttached  = false;
    
    private static Process logcatProc             = null;
    
    
    public static void initializeLogging(int logLevel, String appTag) {
    
        if (logLevel >= Log.VERBOSE && logLevel <= Log.ERROR) {
            APP_LOG_LEVEL          = logLevel;
            APP_LOG_LEVEL_CHAR_REP = findLogLevelCharRep(logLevel);
        }
        if (appTag != null && appTag.length() > 0)
            APP_TAG = appTag;
    }
    
    public static char findLogLevelCharRep(int level) {
    
        switch (level) {
            case Log.VERBOSE:
                return 'V';
            case Log.DEBUG:
                return 'D';
            case Log.INFO:
                return 'I';
            case Log.WARN:
                return 'W';
            case Log.ERROR:
                return 'E';
            case Log.ASSERT:
                return 'A';
            default:
                return '\0';
        }
    }
    
    public static void attachFileHandler(String rootFolder, int rotationSize) {
    
        File logFolder;
        boolean isFolderCreated;
        
        if (isFileHandlerAttached)
            return;
        
        try {
            // Check Whether Folder For Log Files Already Exists; If Not, Create
            // One...
            logFolder = new File(rootFolder
                            + (rootFolder.charAt(rootFolder.length() - 1) != '/' ? File.separator
                                            : "") + Constants.LOG_FOLDER);
            if (!logFolder.exists())
                isFolderCreated = logFolder.mkdir();
            else
                isFolderCreated = true;
            if (!isFolderCreated)
                throw new RuntimeException("Log Folder Cannot Be Created "
                                + "With Respect To The Given Root Path : " + rootFolder);
            attachMMLibFileLogger(logFolder,
                                  rotationSize);
            isFileHandlerAttached = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void attachMMLibFileLogger(File logFolder, int rotationSize) throws IOException {
    
        File curLogFile;
        String command;
        
        curLogFile = new File(logFolder,
                              Constants.LOG_FILE_NAME);
        curLogFile.createNewFile();
        command = "logcat"
                        + " -f "
                        + curLogFile.getAbsolutePath()
                        + " -r "
                        + Integer.toString(rotationSize)
                        + " "
                        + APP_TAG
                        + ":"
                        + Character.toString(APP_LOG_LEVEL_CHAR_REP)
                        + (APP_TAG.equals(Constants.DEFAULT_APP_TAG) ? ""
                                        : (" " + Constants.DEFAULT_APP_TAG + ":" + Character.toString(APP_LOG_LEVEL_CHAR_REP)))
                        + " AndroidRuntime:E" + " dalvikvm:E" + " *:S";
        logcatProc = Runtime.getRuntime().exec(command);
        // When VM Process For This App Dies; So Does Logcat Native Process...
        Runtime.getRuntime()
               .addShutdownHook(new Thread(new Runnable() {
                   
                   @Override
                   public void run() {
                   
                       logcatProc.destroy();
                   }
               }));
    }
    
    public static final void destroyMMLogger() { 
        
        if(logcatProc != null)
            logcatProc.destroy();
        APP_LOG_LEVEL = Log.VERBOSE;
        findLogLevelCharRep(APP_LOG_LEVEL);
        isFileHandlerAttached = false;
    }
    
    /**
     * Generate log for.
     * 
     * @param clazz
     *            the clazz
     * @param logLevel
     *            the log level
     * @param msg
     *            the msg
     * @param args
     *            the args
     */
    public static void generateLogFor(Class<?> clazz, int logLevel, String msg, Object... args) {
    
        if (clazz != null) {
            generateLog(clazz.getName(),
                        logLevel,
                        msg,
                        args);
        } else
            generateLog(APP_TAG,
                        logLevel,
                        msg,
                        args);
    }
    
    /**
     * Generate log.
     * 
     * @param tag
     *            the tag
     * @param logLevel
     *            the log level
     * @param msg
     *            the msg
     * @param args
     *            the args
     */
    public static void generateLog(String tag, int logLevel, String msg, Object... args) {
    
        String formattedMsg = msg;
        
        if (logLevel >= APP_LOG_LEVEL) {
            if (args != null && args.length > 0)
                formattedMsg = MessageFormat.format(msg,
                                                    args);
            formattedMsg = "{" + tag + "}:" + formattedMsg;
            switch (logLevel) {
                case Log.VERBOSE:
                    Log.v(APP_TAG,
                          formattedMsg);
                    break;
                
                case Log.DEBUG:
                    Log.d(APP_TAG,
                          formattedMsg);
                    break;
                
                case Log.INFO:
                    Log.i(APP_TAG,
                          formattedMsg);
                    break;
                
                case Log.WARN:
                    Log.w(APP_TAG,
                          formattedMsg);
                    break;
                
                case Log.ERROR:
                    Log.e(APP_TAG,
                          formattedMsg);
                    break;
                
                case Log.ASSERT:
                    Log.e(APP_TAG,
                          formattedMsg);
                    
                default:
                    break;
            }
        }// End Of Outer-most-if-Block...
    }
}
