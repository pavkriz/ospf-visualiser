package org.hkfree.ospf.setting;

import java.util.ArrayList;
import java.util.List;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.Constants.LANGUAGE;

/**
 * Třída představující vstupní nastavení aplikace pro načítání dat
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class AppSettings {

    // konstanty
    public static String MAP_PANEL = "MAP_PANEL";
    public static String NET_STATE_WINDOW = "NET_STATE_WINDOW";
    public static String IP_V4 = "IPV4";
    public static String IP_V6 = "IPV6";
    private List<String> filePaths = new ArrayList<String>();
    private int dataSourceType = Constants.LOCAL;
    private int dataType = Constants.FOLDER;
    // nastaveni z properties souboru v domovskem adresari
    // komentar informuje o defaultni hodnote
    public String logsRemoteZipSourcePath;// =http://lab.hkfree.org/ospfmap/ospf-data/logs
    public String logsRemoteFolderIdentification;// =folder
    public String logsRemoteZipFileIdentification;// =compressed
    public int loadDataTypIndex; // =0
    public String modelZipRemotePath;// =http://lab.hkfree.org/ospfmap/ospf-data/data-archive/2012-03-03/
    public String modelZipRemotePathBetween;// =http://lab.hkfree.org/ospfmap/ospf-data/data-archive/
    public String remoteZipFileIdentification;// =/files.txt.gz
    public String modelSingleLocalPath;// =/home/schovjan/Desktop/2012-03-18--14-20
    public String modelZipLocalPath;// =d:/
    public String telnetUrl;// =89.248.243.69
    public int telnetPortIPv4;// =2604
    public int telnetPortIPv6;// =2606
    public String telnetPassword;// =free
    public int telnetTimeout;// =5000
    public String telnetRDNSServer;// =lab.hkfree.org
    public LANGUAGE language;
    public String fileNameRouterNames;// =router_names
    public String remoteFileIdentification;// =file
    public String fileNameTopology;// =ospf_database_network
    public String fileNameGeoPositions;// =geo
    public String modelTimeBetween;// =20-00
    public int countDaysBack; // =7
    public String fromDateToDateLoadTo; // =MAP_PANEL
    public boolean closeLogDialog; // =true
    public String cgiUrl; // =
    public String cgiRDNSServer; // =lab.hkfree.org


    /**
     * Konstruktor - vytvoří instanci třídy
     */
    public AppSettings() {}


    /**
     * Metoda, která vrátí cestu k načítání dat
     * @return paths
     */
    public List<String> getFilePaths() {
	return this.filePaths;
    }


    /**
     * Metoda, která nastaví cestu k načítání dat
     * @param paths
     */
    public void setFilePaths(List<String> paths) {
	this.filePaths.clear();
	this.filePaths.addAll(paths);
    }


    /**
     * Vrací typ zdroje dat
     * @return int
     */
    public int getDataSourceType() {
	return this.dataSourceType;
    }


    /**
     * Nastavuje typ zdroje dat
     * @param type
     */
    public void setDataSourceType(int type) {
	this.dataSourceType = type;
    }


    /**
     * Vrací typ vstupních dat
     * @return int
     */
    public int getDataType() {
	return dataType;
    }


    /**
     * Nastavuje typ vstupních dat
     * @param dataType
     */
    public void setDataType(int dataType) {
	this.dataType = dataType;
    }


    /**
     * Smaže seznam cest ke vstupním datům
     */
    public void clearFilePaths() {
	filePaths.clear();
    }


    /**
     * Přidá cestu k souboru vstupních dat
     * @param path
     */
    public void addFilePath(String path) {
	filePaths.add(path);
    }
}