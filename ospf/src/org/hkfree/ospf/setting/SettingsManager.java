package org.hkfree.ospf.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.hkfree.ospf.model.Constants;

/**
 * Manažer pro načítání a ukládání nastavení aplikace a nastavení načítaných dat
 * @author Jan Schovánek
 */
public class SettingsManager {

    private AppSettings settings = null;
    private String pathFile;
    private String pathFolder;


    /**
     * Konstruktor
     * @param settings nastavení
     */
    public SettingsManager(AppSettings settings) {
	this.settings = settings;
	pathFolder = System.getProperty("user.home") + "/.ospf-visualiser/";
	pathFile = pathFolder + Constants.SETTINGS_FILE;

	// overeni, ze existuje properties s nastavenim, pokud ne tak jej vytvoří
	File f = new File(pathFile);
	if (!f.exists()) {
	    createSettingsProperties();
	}
    }


    /**
     * Vytvoří defaultní properties soubor s nastavením aplikace
     */
    private void createSettingsProperties() {
	try {
	    // vytvoření skryté složky
	    File f = new File(System.getProperty("user.home") + "/.ospf-visualiser/");
	    f.mkdir();
	    
	    // vytvoření properties s nastavením
	    f = new File(pathFile);
	    f.createNewFile();
	    
	    // uložení defaultního nastavení aplikace
	    Properties p = new Properties();
	    p.load(ClassLoader.getSystemResourceAsStream(Constants.SETTINGS_FILE));
	    Constants.LANGUAGE lng = Constants.LANGUAGE.en_EN;
	    if (Locale.getDefault().toString().substring(0, 2).startsWith("cs")) {
		lng = Constants.LANGUAGE.cs_CZ;
	    }
	    p.setProperty("language", lng.toString());
	    p.store(new FileOutputStream(pathFile), Constants.SETTINGS_COMMENT);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }


    /**
     * Načte nastavenní ze souboru
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void loadSettings() throws FileNotFoundException, IOException {
	Properties prop = new Properties();
	prop.load(new FileInputStream(new File(pathFile)));
	settings.logsRemoteZipSourcePath = prop.getProperty("logsRemoteZipSourcePath");
	settings.logsRemoteFolderIdentification = prop.getProperty("logsRemoteFolderIdentification");
	settings.logsRemoteZipFileIdentification = prop.getProperty("logsRemoteZipFileIdentification");
	settings.loadDataTypIndex = Integer.valueOf(prop.getProperty("loadDataTypIndex"));
	settings.modelZipRemotePath = prop.getProperty("modelZipRemotePath");
	settings.remoteZipFileIdentification = prop.getProperty("remoteZipFileIdentification");
	settings.modelSingleLocalPath = prop.getProperty("modelSingleLocalPath");
	settings.modelZipLocalPath = prop.getProperty("modelZipLocalPath");
	settings.telnetUrl = prop.getProperty("telnetUrl");
	settings.telnetPort = Integer.valueOf(prop.getProperty("telnetPort"));
	settings.telnetPassword = prop.getProperty("telnetPassword");
	settings.telnetTimeout = Integer.valueOf(prop.getProperty("telnetTimeout"));
	settings.rdnsServer = prop.getProperty("rdnsServer");
	settings.lng = Constants.LANGUAGE.valueOf(prop.getProperty("language"));
	settings.fileNameRouterNames = prop.getProperty("fileNameRouterNames");
	settings.fileNameTopology = prop.getProperty("fileNameTopology");
	settings.fileNameGeoPositions = prop.getProperty("fileNameGeoPositions");
	settings.remoteFileIdentification = prop.getProperty("remoteFileIdentification");
	settings.modelZipRemotePathBetween = prop.getProperty("modelZipRemotePathBetween");
	settings.modelTimeBetween = prop.getProperty("modelTimeBetween");
	settings.countDaysBack = Integer.valueOf(prop.getProperty("countDaysBack"));
    }


    /**
     * Uloží nastavení aplikace do souboru properties
     * @param sett nastavení aplikace
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveSettings() throws FileNotFoundException, IOException {
	Properties prop = new Properties();
	prop.setProperty("logsRemoteZipSourcePath", settings.logsRemoteZipSourcePath);
	prop.setProperty("logsRemoteFolderIdentification", settings.logsRemoteFolderIdentification);
	prop.setProperty("logsRemoteZipFileIdentification", settings.logsRemoteZipFileIdentification);
	prop.setProperty("loadDataTypIndex", String.valueOf(settings.loadDataTypIndex));
	prop.setProperty("modelZipRemotePath", settings.modelZipRemotePath);
	prop.setProperty("remoteZipFileIdentification", settings.remoteZipFileIdentification);
	prop.setProperty("modelSingleLocalPath", settings.modelSingleLocalPath);
	prop.setProperty("modelZipLocalPath", settings.modelZipLocalPath);
	prop.setProperty("telnetUrl", settings.telnetUrl);
	prop.setProperty("telnetPort", String.valueOf(settings.telnetPort));
	prop.setProperty("telnetPassword", settings.telnetPassword);
	prop.setProperty("telnetTimeout", String.valueOf(settings.telnetTimeout));
	prop.setProperty("rdnsServer", settings.rdnsServer);
	prop.setProperty("language", settings.lng.toString());
	prop.setProperty("fileNameRouterNames", settings.fileNameRouterNames);
	prop.setProperty("fileNameTopology", settings.fileNameTopology);
	prop.setProperty("fileNameGeoPositions", settings.fileNameGeoPositions);
	prop.setProperty("remoteFileIdentification", settings.remoteFileIdentification);
	prop.setProperty("modelZipRemotePathBetween", settings.modelZipRemotePathBetween);
	prop.setProperty("modelTimeBetween", settings.modelTimeBetween);
	prop.setProperty("countDaysBack", String.valueOf(settings.countDaysBack));
	prop.store(new FileOutputStream(pathFile), Constants.SETTINGS_COMMENT);
    }
}
