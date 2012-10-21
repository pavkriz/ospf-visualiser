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
     * @throws IOException
     * @throws FileNotFoundException
     */
    public SettingsManager(AppSettings settings) throws FileNotFoundException, IOException {
	this.settings = settings;
	pathFolder = System.getProperty("user.home") + "/.ospf-visualiser/";
	pathFile = pathFolder + Constants.SETTINGS_FILE;
	// overeni, ze existuje properties s nastavenim, pokud ne tak jej vytvoří
	File f = new File(pathFile);
	if (!f.exists()) {
	    createSettingsProperties();
	} else {
	    // uzivatelske nastaveni z domovskeho adresare
	    Properties propUser = new Properties();
	    propUser.load(new FileInputStream(f));
	    if (propUser.getProperty("appVersion") == null
		    || !propUser.getProperty("appVersion").equals(Constants.APP_VERSION)) {
		// atribut appVersion se nenasel (do verze 3.0.1 vcetne)
		// nacteni defaultniho nastaveni
		Properties propDef = new Properties();
		propDef.load(ClassLoader.getSystemResourceAsStream(Constants.SETTINGS_FILE));
		// update uzivatelskeho nastaveni na aktualni verzi
		updateSettings(propUser, propDef);
	    }
	}
    }


    /**
     * Aktualizuje uživatelské nastavení aplikace v domovském adresáři tak,
     * aby odpovídalo verzi aplikace
     * @param propUser uživatelské nastavení
     * @param propDef defaultní nastavení
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void updateSettings(Properties propUser, Properties propDef) throws FileNotFoundException, IOException {
	// zjistuje se pritomnost atributu v nastaveni, pokud neexistuje, prida se spolu s defaultni hodnotou
	if (propUser.getProperty("logsRemoteZipSourcePath") == null) {
	    propUser.setProperty("logsRemoteZipSourcePath", propDef.getProperty("logsRemoteZipSourcePath"));
	}
	if (propUser.getProperty("logsRemoteFolderIdentification") == null) {
	    propUser.setProperty("logsRemoteFolderIdentification", propDef.getProperty("logsRemoteFolderIdentification"));
	}
	if (propUser.getProperty("logsRemoteZipFileIdentification") == null) {
	    propUser.setProperty("logsRemoteZipFileIdentification", propDef.getProperty("logsRemoteZipFileIdentification"));
	}
	if (propUser.getProperty("loadDataTypIndex") == null) {
	    propUser.setProperty("loadDataTypIndex", propDef.getProperty("loadDataTypIndex"));
	}
	if (propUser.getProperty("modelZipRemotePath") == null) {
	    propUser.setProperty("modelZipRemotePath", propDef.getProperty("modelZipRemotePath"));
	}
	if (propUser.getProperty("remoteZipFileIdentification") == null) {
	    propUser.setProperty("remoteZipFileIdentification", propDef.getProperty("remoteZipFileIdentification"));
	}
	if (propUser.getProperty("modelSingleLocalPath") == null) {
	    propUser.setProperty("modelSingleLocalPath", propDef.getProperty("modelSingleLocalPath"));
	}
	if (propUser.getProperty("modelZipLocalPath") == null) {
	    propUser.setProperty("modelZipLocalPath", propDef.getProperty("modelZipLocalPath"));
	}
	if (propUser.getProperty("telnetUrl") == null) {
	    propUser.setProperty("telnetUrl", propDef.getProperty("telnetUrl"));
	}
	if (propUser.getProperty("telnetPortIPv4") == null) {
	    propUser.setProperty("telnetPortIPv4", propDef.getProperty("telnetPortIPv4"));
	}
	if (propUser.getProperty("telnetPortIPv6") == null) {
	    propUser.setProperty("telnetPortIPv6", propDef.getProperty("telnetPortIPv6"));
	}
	if (propUser.getProperty("telnetPassword") == null) {
	    propUser.setProperty("telnetPassword", propDef.getProperty("telnetPassword"));
	}
	if (propUser.getProperty("telnetTimeout") == null) {
	    propUser.setProperty("telnetTimeout", propDef.getProperty("telnetTimeout"));
	}
	if (propUser.getProperty("telnetRDNSServer") == null) {
	    propUser.setProperty("telnetRDNSServer", propDef.getProperty("telnetRDNSServer"));
	}
	if (propUser.getProperty("language") == null) {
	    propUser.setProperty("language", propDef.getProperty("language"));
	}
	if (propUser.getProperty("fileNameRouterNames") == null) {
	    propUser.setProperty("fileNameRouterNames", propDef.getProperty("fileNameRouterNames"));
	}
	if (propUser.getProperty("fileNameTopology") == null) {
	    propUser.setProperty("fileNameTopology", propDef.getProperty("fileNameTopology"));
	}
	if (propUser.getProperty("fileNameGeoPositions") == null) {
	    propUser.setProperty("fileNameGeoPositions", propDef.getProperty("fileNameGeoPositions"));
	}
	if (propUser.getProperty("remoteFileIdentification") == null) {
	    propUser.setProperty("remoteFileIdentification", propDef.getProperty("remoteFileIdentification"));
	}
	if (propUser.getProperty("modelZipRemotePathBetween") == null) {
	    propUser.setProperty("modelZipRemotePathBetween", propDef.getProperty("modelZipRemotePathBetween"));
	}
	if (propUser.getProperty("modelTimeBetween") == null) {
	    propUser.setProperty("modelTimeBetween", propDef.getProperty("modelTimeBetween"));
	}
	if (propUser.getProperty("countDaysBack") == null) {
	    propUser.setProperty("countDaysBack", propDef.getProperty("countDaysBack"));
	}
	if (propUser.getProperty("fromDateToDateLoadTo") == null) {
	    propUser.setProperty("fromDateToDateLoadTo", propDef.getProperty("fromDateToDateLoadTo"));
	}
	if (propUser.getProperty("closeLogDialog") == null) {
	    propUser.setProperty("closeLogDialog", propDef.getProperty("closeLogDialog"));
	}
	if (propUser.getProperty("cgiUrl") == null) {
	    propUser.setProperty("cgiUrl", propDef.getProperty("cgiUrl"));
	}
	if (propUser.getProperty("cgiRDNSServer") == null) {
	    propUser.setProperty("cgiRDNSServer", propDef.getProperty("cgiRDNSServer"));
	}
	propUser.setProperty("appVersion", Constants.APP_VERSION);
	propUser.store(new FileOutputStream(pathFile), Constants.SETTINGS_COMMENT);
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
	settings.telnetPortIPv4 = Integer.valueOf(prop.getProperty("telnetPortIPv4"));
	settings.telnetPortIPv6 = Integer.valueOf(prop.getProperty("telnetPortIPv6"));
	settings.telnetPassword = prop.getProperty("telnetPassword");
	settings.telnetTimeout = Integer.valueOf(prop.getProperty("telnetTimeout"));
	settings.telnetRDNSServer = prop.getProperty("telnetRDNSServer");
	settings.language = Constants.LANGUAGE.valueOf(prop.getProperty("language"));
	settings.fileNameRouterNames = prop.getProperty("fileNameRouterNames");
	settings.fileNameTopology = prop.getProperty("fileNameTopology");
	settings.fileNameGeoPositions = prop.getProperty("fileNameGeoPositions");
	settings.remoteFileIdentification = prop.getProperty("remoteFileIdentification");
	settings.modelZipRemotePathBetween = prop.getProperty("modelZipRemotePathBetween");
	settings.modelTimeBetween = prop.getProperty("modelTimeBetween");
	settings.countDaysBack = Integer.valueOf(prop.getProperty("countDaysBack"));
	settings.fromDateToDateLoadTo = prop.getProperty("fromDateToDateLoadTo");
	settings.closeLogDialog = Boolean.parseBoolean(prop.getProperty("closeLogDialog"));
	settings.cgiUrl = prop.getProperty("cgiUrl");
	settings.cgiRDNSServer = prop.getProperty("cgiRDNSServer");
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
	prop.setProperty("telnetPortIPv4", String.valueOf(settings.telnetPortIPv4));
	prop.setProperty("telnetPortIPv6", String.valueOf(settings.telnetPortIPv6));
	prop.setProperty("telnetPassword", settings.telnetPassword);
	prop.setProperty("telnetTimeout", String.valueOf(settings.telnetTimeout));
	prop.setProperty("telnetRDNSServer", settings.telnetRDNSServer);
	prop.setProperty("language", settings.language.toString());
	prop.setProperty("fileNameRouterNames", settings.fileNameRouterNames);
	prop.setProperty("fileNameTopology", settings.fileNameTopology);
	prop.setProperty("fileNameGeoPositions", settings.fileNameGeoPositions);
	prop.setProperty("remoteFileIdentification", settings.remoteFileIdentification);
	prop.setProperty("modelZipRemotePathBetween", settings.modelZipRemotePathBetween);
	prop.setProperty("modelTimeBetween", settings.modelTimeBetween);
	prop.setProperty("countDaysBack", String.valueOf(settings.countDaysBack));
	prop.setProperty("fromDateToDateLoadTo", settings.fromDateToDateLoadTo);
	prop.setProperty("closeLogDialog", String.valueOf(settings.closeLogDialog));
	prop.setProperty("cgiUrl", settings.cgiUrl);
	prop.setProperty("cgiRDNSServer", settings.cgiRDNSServer);
	prop.setProperty("appVersion", Constants.APP_VERSION);
	prop.store(new FileOutputStream(pathFile), Constants.SETTINGS_COMMENT);
    }
}
