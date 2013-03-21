package org.hkfree.ospf.tools.load;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.hkfree.ospf.gui.ospfwin.OspfWinManager;
import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.linkfault.LinkFaultModel;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.setting.AppSettings;
import org.hkfree.ospf.tools.Factory;
import org.hkfree.ospf.tools.rdns.FastReverseDNS;
import org.hkfree.ospf.tools.rdns.IPEnumeration;
import org.hkfree.ospf.tools.rdns.ReverseDNS;
import org.hkfree.ospf.tools.telnet.TelnetClient;
import org.hkfree.ospf.tools.telnet.TelnetException;

/**
 * Třída sloužící k poskytnutí vstupů na základě nastavení pro parsování vstupních dat
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfDataLoadInitiator {

    private ResourceBundle rb = Factory.getRb();
    private AppSettings settings;
    private OspfWinManager winManager;


    /**
     * Konstruktor
     * @param settings
     */
    public OspfDataLoadInitiator(AppSettings settings, OspfWinManager winManager) {
	this.settings = settings;
	this.winManager = winManager;
    }


    /**
     * Rozcestník - soubory ze severu vs. lokálního umístění
     * @param ospfModel
     * @param sourcePath
     * @throws IOException
     * @throws InterruptedException
     * @throws TelnetException
     */
    public void loadData(OspfModel ospfModel, String sourcePath) throws IOException, TelnetException, InterruptedException {
	switch (settings.getDataSourceType()) {
	    case Constants.LOCAL:
		loadDataFromLocalFiles(ospfModel, sourcePath);
		break;
	    case Constants.REMOTE_SERVER:
		loadDataFromRemoteServerFiles(ospfModel, sourcePath);
		break;
	    case Constants.TELNET:
		loadDataViaTelnet(ospfModel, sourcePath);
		break;
	    case Constants.CGI:
		loadDataFromCgiScript(ospfModel, sourcePath);
		break;
	}
    }


    /**
     * Načte soubory z lokálního umístění
     * @param model
     * @param sourcePath
     * @throws IOException
     */
    private void loadDataFromLocalFiles(OspfModel model, String sourcePath) throws IOException {
	InputStreamReader isr = null;
	BufferedReader inBufRd = null;
	switch (settings.getDataType()) {
	    case Constants.FOLDER:
		if (!sourcePath.substring(sourcePath.length()).equals("/")
			&& !sourcePath.substring(sourcePath.length()).equals("\\")) {
		    sourcePath += "/";
		}
		File f = new File(sourcePath + Constants.FILENAME_OSPF_DUMP);
		if (f.exists()) {
		    FileReader frdr = new FileReader(f);
		    inBufRd = new BufferedReader(frdr);
		    winManager.getOwner().getStateDialog().addText(rb.getString("stated.7"));
		    OspfLoader.getTopologyFromData(model, inBufRd);
		    winManager.getOwner().getStateDialog().operationSucceeded();
		} else {
		    winManager.getOwner().getStateDialog().addText(rb.getString("stated.7"));
		    loadTopologyDataFromLocalFile(model, sourcePath);
		    winManager.getOwner().getStateDialog().operationSucceeded();
		    winManager.getOwner().getStateDialog().addText(rb.getString("stated.8"));
		    loadNonTopologyDataFromLocalFile(model, sourcePath);
		    winManager.getOwner().getStateDialog().operationSucceeded();
		}
		winManager.getOwner().getStateDialog().addText(rb.getString("stated.10"));
		loadNamesGeoDataFromLocalFile(model, sourcePath);
		winManager.getOwner().getStateDialog().operationSucceeded();
		break;
	    case Constants.ZIP:
		model.setModelName(new File(sourcePath).getName());
		ZipInputStream zipInStream = null;
		try {
		    zipInStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourcePath)));
		    isr = new InputStreamReader(zipInStream);
		    inBufRd = new BufferedReader(isr);
		    ZipEntry entry;
		    while ((entry = zipInStream.getNextEntry()) != null) {
			if (entry.getName().equals(Constants.FILENAME_OSPF_DUMP)) {
			    winManager.getOwner().getStateDialog().addText(rb.getString("stated.7"));
			    OspfLoader.getTopologyFromData(model, inBufRd);
			    winManager.getOwner().getStateDialog().operationSucceeded();
			}
		    }
		    if (model.getRouters().isEmpty()) {
			winManager.getOwner().getStateDialog().addText(rb.getString("stated.7"));
			zipInStream = new ZipInputStream(new BufferedInputStream(new BufferedInputStream(
				new FileInputStream(sourcePath))));
			loadTopologyDataFromZipFile(model, zipInStream);
			winManager.getOwner().getStateDialog().operationSucceeded();
			winManager.getOwner().getStateDialog().addText(rb.getString("stated.8"));
			zipInStream = new ZipInputStream(new BufferedInputStream(new BufferedInputStream(
				new FileInputStream(sourcePath))));
			loadNonTopologyDataFromZipFile(model, zipInStream);
			winManager.getOwner().getStateDialog().operationSucceeded();
		    }
		    winManager.getOwner().getStateDialog().addText(rb.getString("stated.10"));
		    zipInStream = new ZipInputStream(new BufferedInputStream(new BufferedInputStream(new FileInputStream(
			    sourcePath))));
		    loadNamesGeoDataFromZipFile(model, zipInStream);
		    winManager.getOwner().getStateDialog().operationSucceeded();
		} finally {
		    if (zipInStream != null)
			zipInStream.close();
		}
		break;
	}
    }


    /**
     * Načte soubory ze serveru
     * @param model
     * @param sourcePath
     * @throws IOException
     */
    private void loadDataFromRemoteServerFiles(OspfModel model, String sourcePath) throws IOException {
	if (settings.getDataType() == Constants.ZIP) {
	    model.setModelName(sourcePath.substring(sourcePath.lastIndexOf("/") + 1));
	    URL url = null;
	    ZipInputStream zipInStream = null;
	    try {
		url = new URL(sourcePath);
		zipInStream = new ZipInputStream(new BufferedInputStream(url.openStream()));
		InputStreamReader isr = new InputStreamReader(zipInStream);
		BufferedReader inBfrdRdr = new BufferedReader(isr);
		ZipEntry entry;
		while ((entry = zipInStream.getNextEntry()) != null) {
		    if (entry.getName().equals(Constants.FILENAME_OSPF_DUMP)) {
			winManager.getOwner().getStateDialog().addText(rb.getString("stated.7"));
			OspfLoader.getTopologyFromData(model, inBfrdRdr);
			winManager.getOwner().getStateDialog().operationSucceeded();
		    }
		}
		if (model.getRouters().isEmpty()) {
		    winManager.getOwner().getStateDialog().addText(rb.getString("stated.7"));
		    zipInStream = new ZipInputStream(new BufferedInputStream(url.openStream()));
		    loadTopologyDataFromZipFile(model, zipInStream);
		    winManager.getOwner().getStateDialog().operationSucceeded();
		    winManager.getOwner().getStateDialog().addText(rb.getString("stated.8"));
		    zipInStream = new ZipInputStream(new BufferedInputStream(url.openStream()));
		    loadNonTopologyDataFromZipFile(model, zipInStream);
		    winManager.getOwner().getStateDialog().operationSucceeded();
		}
		winManager.getOwner().getStateDialog().addText(rb.getString("stated.10"));
		zipInStream = new ZipInputStream(new BufferedInputStream(url.openStream()));
		loadNamesGeoDataFromZipFile(model, zipInStream);
		winManager.getOwner().getStateDialog().operationSucceeded();
	    } finally {
		if (zipInStream != null)
		    zipInStream.close();
	    }
	} else {
	    // vyhodit MyException - nemělo by dojít (z HTTP to bude vždy jen ze ZIPů)
	}
    }


    /**
     * Zpracuje soubor s topologií sítě ze ZIP souboru
     * @param model
     * @param zipInputStream
     * @throws IOException
     */
    private void loadTopologyDataFromZipFile(OspfModel model, ZipInputStream zipInputStream) throws IOException {
	InputStreamReader isr = null;
	BufferedReader inBfrdRdr = null;
	try {
	    isr = new InputStreamReader(zipInputStream);
	    inBfrdRdr = new BufferedReader(isr);
	    ZipEntry entry;
	    while ((entry = zipInputStream.getNextEntry()) != null) {
		if (entry.getName().equals(Constants.FILENAME_TOPOLOGY)) {
		    OspfLoader.loadTopology(model, inBfrdRdr);
		}
	    }
	} finally {
	    if (inBfrdRdr != null)
		inBfrdRdr.close();
	    if (isr != null)
		isr.close();
	}
    }


    /**
     * Zpracuje soubory s cenami ,jmény a pozicemi routerů ze ZIP souboru
     * @param model
     * @param zipInputStream
     * @throws IOException
     */
    private void loadNonTopologyDataFromZipFile(OspfModel model, ZipInputStream zipInputStream) throws IOException {
	InputStreamReader isr = null;
	BufferedReader inBfrdRdr = null;
	try {
	    isr = new InputStreamReader(zipInputStream);
	    inBfrdRdr = new BufferedReader(isr);
	    ZipEntry entry;
	    while ((entry = zipInputStream.getNextEntry()) != null) {
		if (!entry.getName().equals(Constants.FILENAME_TOPOLOGY)) {
		    if (entry.getName().equals(Constants.FILENAME_ROUTER_NAMES)) {
			// nic
		    } else if (entry.getName().equals(Constants.FILENAME_GEO_POSITIONS)) {
			// nic
		    } else {
			OspfLoader.loadCosts(model, entry.getName(), inBfrdRdr);
		    }
		}
	    }
	} finally {
	    if (inBfrdRdr != null)
		inBfrdRdr.close();
	    if (isr != null)
		isr.close();
	}
    }


    /**
     * Zpracuje soubory s cenami ,jmény a pozicemi routerů ze ZIP souboru
     * @param model
     * @param zipInputStream
     * @throws IOException
     */
    private void loadNamesGeoDataFromZipFile(OspfModel model, ZipInputStream zipInputStream) throws IOException {
	InputStreamReader isr = null;
	BufferedReader inBfrdRdr = null;
	try {
	    isr = new InputStreamReader(zipInputStream);
	    inBfrdRdr = new BufferedReader(isr);
	    ZipEntry entry;
	    while ((entry = zipInputStream.getNextEntry()) != null) {
		if (entry.getName().equals(Constants.FILENAME_ROUTER_NAMES)) {
		    OspfLoader.loadRouterNames(model, inBfrdRdr);
		} else if (entry.getName().equals(Constants.FILENAME_GEO_POSITIONS)) {
		    OspfLoader.loadRouterGeoPositions(model, inBfrdRdr);
		} else {
		    // nic
		}
	    }
	} finally {
	    if (inBfrdRdr != null)
		inBfrdRdr.close();
	    if (isr != null)
		isr.close();
	}
    }


    /**
     * Zpracuje soubor topologie z lokálního umístění
     * @throws IOException
     */
    private void loadTopologyDataFromLocalFile(OspfModel model, String sourcePath) throws IOException {
	FileReader frdr = null;
	BufferedReader inBfrdRdr = null;
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd--HH-mm");
	model.setModelName(formatter.format(date) + "_single");
	System.currentTimeMillis();
	try {
	    frdr = new FileReader(new File(sourcePath + Constants.FILENAME_TOPOLOGY));
	    inBfrdRdr = new BufferedReader(frdr);
	    OspfLoader.loadTopology(model, inBfrdRdr);
	} finally {
	    if (inBfrdRdr != null)
		inBfrdRdr.close();
	    if (frdr != null)
		frdr.close();
	}
    }


    /**
     * Zpracuje soubory s cenami, názvy a pozicemi routerů z lokálního umístění
     * @param model
     * @param sourcePath
     * @throws IOException
     */
    private void loadNonTopologyDataFromLocalFile(OspfModel model, String sourcePath) throws IOException {
	FileReader frdr = null;
	BufferedReader inBfrdRdr = null;
	for (Router r : model.getRouters()) {
	    String rIPfileName = r.getId();
	    try {
		frdr = new FileReader(new File(sourcePath + rIPfileName));
		inBfrdRdr = new BufferedReader(frdr);
		OspfLoader.loadCosts(model, rIPfileName, inBfrdRdr);
	    } finally {
		if (inBfrdRdr != null)
		    inBfrdRdr.close();
		if (frdr != null)
		    frdr.close();
	    }
	}
    }


    /**
     * Zpracuje soubory s cenami, názvy a pozicemi routerů z lokálního umístění
     * @param model
     * @param sourcePath
     * @throws IOException
     */
    private void loadNamesGeoDataFromLocalFile(OspfModel model, String sourcePath) throws IOException {
	FileReader frdr = null;
	BufferedReader inBfrdRdr = null;
	try {
	    frdr = new FileReader(new File(sourcePath + Constants.FILENAME_ROUTER_NAMES));
	    inBfrdRdr = new BufferedReader(frdr);
	    OspfLoader.loadRouterNames(model, inBfrdRdr);
	} finally {
	    if (inBfrdRdr != null)
		inBfrdRdr.close();
	    if (frdr != null)
		frdr.close();
	}
	try {
	    frdr = new FileReader(new File(sourcePath + Constants.FILENAME_GEO_POSITIONS));
	    inBfrdRdr = new BufferedReader(frdr);
	    OspfLoader.loadRouterGeoPositions(model, inBfrdRdr);
	} finally {
	    if (inBfrdRdr != null)
		inBfrdRdr.close();
	    if (frdr != null)
		frdr.close();
	}
    }


    /**
     * Načte soubory s logy výpadků ze serveru
     * @param model
     * @param sourcePath
     * @throws IOException
     * @throws ParseException
     */
    public void loadLogsFromRemoteServerFiles(LinkFaultModel model, String sourcePath) throws IOException, ParseException {
	if (settings.getDataType() == Constants.ZIP) {
	    URL adresa = null;
	    GZIPInputStream gzipInStream = null;
	    try {
		adresa = new URL(sourcePath);
		gzipInStream = new GZIPInputStream(new BufferedInputStream(adresa.openStream()));
		loadLogsDataFromZipFile(model, gzipInStream);
	    } finally {
		if (gzipInStream != null)
		    gzipInStream.close();
	    }
	} else {
	    // nemělo by k tomu dojít (z HTTP to bude vždy jen ze ZIPů)
	}
    }


    /**
     * Zpracuje soubor s logy výpadků ze ZIP
     * @param model
     * @param gzipInStream
     * @throws IOException
     * @throws ParseException
     */
    private void loadLogsDataFromZipFile(LinkFaultModel model, GZIPInputStream gzipInStream) throws IOException,
	    ParseException {
	InputStreamReader isr = null;
	BufferedReader inBfrdRdr = null;
	try {
	    isr = new InputStreamReader(gzipInStream);
	    inBfrdRdr = new BufferedReader(isr);
	    OspfLoader.loadOSPFLog(model, inBfrdRdr);
	} finally {
	    if (inBfrdRdr != null)
		inBfrdRdr.close();
	    if (isr != null)
		isr.close();
	}
    }


    /**
     * Stáhne data přes telnet
     * @param ospfModel
     * @param sourcePath
     * @throws TelnetException
     * @throws IOException
     * @throws InterruptedException
     */
    private void loadDataViaTelnet(OspfModel ospfModel, String sourcePath) throws IOException, TelnetException,
	    InterruptedException {
	StringBuilder data = new StringBuilder();
	TelnetClient tc = null;
	winManager.getOwner().getStateDialog().addText(rb.getString("stated.0"));
	tc = new TelnetClient(settings.telnetUrl, settings.telnetPortIPv4, settings.telnetPassword,
		settings.telnetTimeout);
	tc.initConnection();
	data.append(tc.getDataIPv4());
	tc.close();
	// nacteni dat pro IPv6 pouze v pripade zadani portu
	if (settings.telnetPortIPv6 != null) {
	    try {
		tc = new TelnetClient(settings.telnetUrl, settings.telnetPortIPv6, settings.telnetPassword,
			settings.telnetTimeout);
		tc.initConnection();
		data.append(tc.getDataIPv6());
		tc.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	winManager.getOwner().getStateDialog().operationSucceeded();
	// nacteni modelu z prijatych dat
	winManager.getOwner().getStateDialog().addText(rb.getString("stated.1"));
	OspfLoader.getTopologyFromData(ospfModel, new BufferedReader(new StringReader(data.toString())));
	winManager.getOwner().getStateDialog().operationSucceeded();
	// nacteni nazvu routeru
	winManager.getOwner().getStateDialog().addText(rb.getString("stated.3"));
	loadNames(ospfModel);
	winManager.getOwner().getStateDialog().operationSucceeded();
	// nalezeni a prirazeni suffixu z nazvu routeru
	winManager.getOwner().getStateDialog().addText(rb.getString("stated.9"));
	findAndSetSuffix(ospfModel);
	winManager.getOwner().getStateDialog().operationSucceeded();
	// nazev modelu
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd--HH-mm");
	ospfModel.setModelName(formatter.format(date) + "_telnet");
    }


    /**
     * Stáhne data ze výstupu CGI skriptu
     * @param ospfModel
     * @param sourcePath
     * @throws InterruptedException
     */
    private void loadDataFromCgiScript(OspfModel model, String sourcePath) throws InterruptedException {
	try {
	    // stazeni dat
	    winManager.getOwner().getStateDialog().addText(rb.getString("stated.0"));
	    URL adresa = new URL(settings.cgiUrl);
	    // InputStream is = adresa.openStream();
	    // StringBuilder sb = new StringBuilder();
	    // byte[] buff = new byte[8096];
	    // int receiveLength = 0; // počet znaků přijatého řetězce
	    // while ((receiveLength = is.read(buff)) != -1) {
	    // sb.append(new String(buff, 0, receiveLength));
	    // }
	    winManager.getOwner().getStateDialog().operationSucceeded();
	    // nacteni dat
	    winManager.getOwner().getStateDialog().addText(rb.getString("stated.1"));
	    // OspfLoader.getTopologyFromData(model, new BufferedReader(new StringReader(sb.toString())));
	    OspfLoader.getTopologyFromData(model, new BufferedReader(new InputStreamReader(adresa.openStream())));
	    winManager.getOwner().getStateDialog().operationSucceeded();
	    // nacteni nazvu routeru
	    winManager.getOwner().getStateDialog().addText(rb.getString("stated.3"));
	    loadNames(model);
	    winManager.getOwner().getStateDialog().operationSucceeded();
	    // nalezeni a prirazeni suffixu z nazvu routeru
	    winManager.getOwner().getStateDialog().addText(rb.getString("stated.9"));
	    findAndSetSuffix(model);
	    winManager.getOwner().getStateDialog().operationSucceeded();
	    // nazev modelu
	    Date date = new Date(System.currentTimeMillis());
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd--HH-mm");
	    model.setModelName(sdf.format(date) + "_" + settings.cgiUrl.substring(settings.cgiUrl.lastIndexOf("/") + 1));
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }


    /**
     * Nastaví do modelu netopologická data
     * @param ospfModel OSPF model
     * @param tc TelnetKlient
     * @throws IOException
     * @throws InterruptedException
     */
    private void loadNames(OspfModel ospfModel) throws IOException, InterruptedException {
	List<String> ips = new ArrayList<String>();
	for (Router r : ospfModel.getRouters()) {
	    ips.add(r.getId());
	}
	ReverseDNS rdns = null;
	switch (settings.getDataSourceType()) {
	    case Constants.TELNET:
		rdns = new ReverseDNS(settings.telnetRDNSServer);
		break;
	    case Constants.CGI:
		rdns = new ReverseDNS(settings.cgiRDNSServer);
		break;
	}
	IPEnumeration ipe = new IPEnumeration(ips);
	Map<String, String> names = new HashMap<String, String>();
	Thread threads[] = new Thread[60];
	for (int i = 0; i < FastReverseDNS.DEFAULT_NUM_THREADS; i++) {
	    threads[i] = new Thread(new FastReverseDNS(rdns, ipe, names), "Thread" + i);
	    threads[i].start();
	}
	for (int i = 0; i < FastReverseDNS.DEFAULT_NUM_THREADS; i++) {
	    threads[i].join();
	}
	for (Router r : ospfModel.getRouters()) {
	    String s = names.get(r.getId());
	    if (s != null && !s.equals(r.getId())) {
		r.setName(s);
	    }
	}
    }


    /**
     * Nalezne a nastavi suffix nazvu vsech routeru
     * @param ospfModel ospf model obsahujici seznam routeru
     */
    private void findAndSetSuffix(OspfModel ospfModel) {
	String suffix = findSuffix(ospfModel);
	// odebrani suffixu z nazvu routeru a nastaveni atributu pro suffix
	List<Router> routers = ospfModel.getRouters();
	for (Router r : routers) {
	    // pokud se obdrzelo neco jineho nez je id routeru, vlozi se to do nazvu routeru
	    if (r.getName() != null && r.getName().endsWith(suffix)) {
		r.setName(r.getName().replace(suffix, ""));
		r.setSuffix(suffix);
	    }
	}
    }


    /**
     * Vraci suffixu nazvu vsech routeru z modelu
     * @param ospfModel ospf model obsahujici routery
     * @return suffix nazvu
     */
    private String findSuffix(OspfModel ospfModel) {
	List<Router> routers = ospfModel.getRouters();
	Map<String, Integer> map = new HashMap<String, Integer>();
	String s;
	int i;
	// spocitani vyskytu vsech suffixu
	for (Router r : routers) {
	    i = 0;
	    do {
		s = r.getName().substring(i);
		if (s.length() > 0) {
		    if (map.containsKey(s)) {
			map.put(s, map.get(s).intValue() + 1);
		    } else {
			map.put(s, 1);
		    }
		}
	    } while ((i = r.getName().indexOf('.', i + 1)) > 0);
	}
	// nalezeni nejcastejsiho suffixu
	String suffix = "";
	int count = 0;
	for (String suf : map.keySet()) {
	    if (suf.trim().equals(".") || suf.isEmpty()) {
		continue;
	    }
	    if (map.get(suf).intValue() > count) {
		count = map.get(suf).intValue();
		suffix = suf;
	    }
	    if (map.get(suf).intValue() >= (count - 5) && suf.length() > suffix.length()) {
		count = map.get(suf).intValue();
		suffix = suf;
	    }
	}
	return suffix;
    }
}
