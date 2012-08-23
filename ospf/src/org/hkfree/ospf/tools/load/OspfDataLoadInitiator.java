package org.hkfree.ospf.tools.load;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.hkfree.ospf.model.Constants;
import org.hkfree.ospf.model.linkfault.LinkFaultModel;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospf.Router;
import org.hkfree.ospf.setting.AppSettings;
import org.hkfree.ospf.tools.rdns.FastReverseDNS;
import org.hkfree.ospf.tools.rdns.IPEnumeration;
import org.hkfree.ospf.tools.rdns.ReverseDNS;
import org.hkfree.ospf.tools.telnet.TelnetClient;

/**
 * Třída sloužící k poskytnutí vstupů na základě nastavení pro parsování vstupních dat
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class OspfDataLoadInitiator {

    private AppSettings settings;


    /**
     * Konstruktor
     * @param settings
     */
    public OspfDataLoadInitiator(AppSettings settings) {
	this.settings = settings;
    }


    /**
     * Rozcestník - soubory ze severu vs. lokálního umístění
     * @param ospfModel
     * @param sourcePath
     * @throws Exception
     */
    public void loadData(OspfModel ospfModel, String sourcePath) throws Exception {
	switch (settings.getDataSourceType()) {
	    case Constants.LOCAL:
		loadDataFromLocalFiles(ospfModel, sourcePath);
		break;
	    case Constants.REMOTE_SERVER:
		loadDataFromRemoteServerFiles(ospfModel, sourcePath);
		break;
	    case Constants.TELNET:
		loadDataViaTelnet(ospfModel, sourcePath);
		findAndSetSuffix(ospfModel);
		break;
	}
    }


    /**
     * Načte soubory z lokálního umístění
     * @param model
     * @param sourcePath
     * @throws Exception
     */
    private void loadDataFromLocalFiles(OspfModel model, String sourcePath) throws Exception {
	switch (settings.getDataType()) {
	    case Constants.FOLDER:
		if (!sourcePath.substring(sourcePath.length()).equals("/")
			&& !sourcePath.substring(sourcePath.length()).equals("\\")) {
		    sourcePath += "/";
		}
		loadTopologyDataFromLocalFile(model, sourcePath);
		loadNonTopologyDataFromLocalFile(model, sourcePath);
		break;
	    case Constants.ZIP:
		model.setModelName(new File(sourcePath).getName());
		ZipInputStream zipInStream = null;
		try {
		    zipInStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourcePath)));
		    loadTopologyDataFromZipFile(model, zipInStream);
		} finally {
		    if (zipInStream != null)
			zipInStream.close();
		}
		try {
		    zipInStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourcePath)));
		    loadNonTopologyDataFromZipFile(model, zipInStream);
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
     * @throws Exception
     */
    private void loadDataFromRemoteServerFiles(OspfModel model, String sourcePath) throws Exception {
	if (settings.getDataType() == Constants.ZIP) {
	    model.setModelName(sourcePath.substring(sourcePath.lastIndexOf("/") + 1));
	    URL adresa = null;
	    ZipInputStream zipInStream = null;
	    try {
		adresa = new URL(sourcePath);
		zipInStream = new ZipInputStream(new BufferedInputStream(adresa.openStream()));
		loadTopologyDataFromZipFile(model, zipInStream);
	    } finally {
		if (zipInStream != null)
		    zipInStream.close();
	    }
	    try {
		zipInStream = new ZipInputStream(new BufferedInputStream(adresa.openStream()));
		loadNonTopologyDataFromZipFile(model, zipInStream);
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
     * @throws Exception
     */
    private void loadTopologyDataFromZipFile(OspfModel model, ZipInputStream zipInputStream) throws Exception {
	OspfLoader dataLoader = new OspfLoader();
	InputStreamReader isr = null;
	BufferedReader inBfrdRdr = null;
	try {
	    isr = new InputStreamReader(zipInputStream);
	    inBfrdRdr = new BufferedReader(isr);
	    ZipEntry entry;
	    while ((entry = zipInputStream.getNextEntry()) != null) {
		if (entry.getName().equals(settings.fileNameTopology)) {
		    dataLoader.loadTopology(model, inBfrdRdr);
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
     * @throws Exception
     */
    private void loadNonTopologyDataFromZipFile(OspfModel model, ZipInputStream zipInputStream) throws Exception {
	OspfLoader dataLoader = new OspfLoader();
	InputStreamReader isr = null;
	BufferedReader inBfrdRdr = null;
	try {
	    isr = new InputStreamReader(zipInputStream);
	    inBfrdRdr = new BufferedReader(isr);
	    ZipEntry entry;
	    while ((entry = zipInputStream.getNextEntry()) != null) {
		if (!entry.getName().equals(settings.fileNameTopology)) {
		    if (entry.getName().equals(settings.fileNameRouterNames)) {
			dataLoader.loadRouterNames(model, inBfrdRdr);
		    } else if (entry.getName().equals(settings.fileNameGeoPositions)) {
			dataLoader.loadRouterGeoPositions(model, inBfrdRdr);
		    } else {
			dataLoader.loadCosts(model, entry.getName(), inBfrdRdr);
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
     * Zpracuje soubor topologie z lokálního umístění
     */
    private void loadTopologyDataFromLocalFile(OspfModel model, String sourcePath) throws Exception {
	OspfLoader dataLoader = new OspfLoader();
	FileReader frdr = null;
	BufferedReader inBfrdRdr = null;
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd--HH-mm");
	model.setModelName(formatter.format(date) + "_single");
	System.currentTimeMillis();
	try {
	    frdr = new FileReader(new File(sourcePath + settings.fileNameTopology));
	    inBfrdRdr = new BufferedReader(frdr);
	    dataLoader.loadTopology(model, inBfrdRdr);
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
     * @throws Exception
     */
    private void loadNonTopologyDataFromLocalFile(OspfModel model, String sourcePath) throws Exception {
	OspfLoader dataLoader = new OspfLoader();
	FileReader frdr = null;
	BufferedReader inBfrdRdr = null;
	for (Router r : model.getRouters()) {
	    String rIPfileName = r.getRouterID();
	    try {
		frdr = new FileReader(new File(sourcePath + rIPfileName));
		inBfrdRdr = new BufferedReader(frdr);
		dataLoader.loadCosts(model, rIPfileName, inBfrdRdr);
	    } finally {
		if (inBfrdRdr != null)
		    inBfrdRdr.close();
		if (frdr != null)
		    frdr.close();
	    }
	}
	try {
	    frdr = new FileReader(new File(sourcePath + settings.fileNameRouterNames));
	    inBfrdRdr = new BufferedReader(frdr);
	    dataLoader.loadRouterNames(model, inBfrdRdr);
	} finally {
	    if (inBfrdRdr != null)
		inBfrdRdr.close();
	    if (frdr != null)
		frdr.close();
	}
	try {
	    frdr = new FileReader(new File(sourcePath + settings.fileNameGeoPositions));
	    inBfrdRdr = new BufferedReader(frdr);
	    dataLoader.loadRouterGeoPositions(model, inBfrdRdr);
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
     * @throws Exception
     */
    public void loadLogsFromRemoteServerFiles(LinkFaultModel model, String sourcePath) throws Exception {
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
     */
    private void loadLogsDataFromZipFile(LinkFaultModel model, GZIPInputStream gzipInStream) throws Exception {
	OspfLoader dataLoader = new OspfLoader();
	InputStreamReader isr = null;
	BufferedReader inBfrdRdr = null;
	try {
	    isr = new InputStreamReader(gzipInStream);
	    inBfrdRdr = new BufferedReader(isr);
	    dataLoader.loadOSPFLog(model, inBfrdRdr);
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
     * @throws Exception
     */
    private void loadDataViaTelnet(OspfModel ospfModel, String sourcePath) throws Exception {
	TelnetClient tc = new TelnetClient(settings.telnetUrl, settings.telnetPort,
		settings.telnetPassword, settings.telnetTimeout);
	
	tc.initConnection();
	loadTopologyDataFromTelnet(ospfModel, tc);
	loadNonTopologyDataFromTelnet(ospfModel, tc);
	tc.close();
	
	// nazev noveho tabbu
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd--HH-mm");
	ospfModel.setModelName(formatter.format(date) + "_telnet");
    }


    /**
     * Nastaví do modelu topologická data
     * @param ospfModel OSPF model
     * @param tc TelnetKlient
     * @throws IOException
     * @throws InterruptedException
     */
    private void loadTopologyDataFromTelnet(OspfModel ospfModel, TelnetClient tc) throws IOException, InterruptedException {
	StringBuilder data = null;
	if (settings.isIPv4()) {
	    data = tc.getTopologyData();
	} else {
	    data = tc.getTopologyDataIPv6();
	}
	OspfLoader dataLoader = new OspfLoader();
	dataLoader.loadTopology(ospfModel, new BufferedReader(new StringReader(data.toString())));
    }


    /**
     * Nastaví do modelu netopologická data
     * @param ospfModel OSPF model
     * @param tc TelnetKlient
     * @throws IOException
     * @throws InterruptedException
     */
    private void loadNonTopologyDataFromTelnet(OspfModel ospfModel, TelnetClient tc) throws IOException,
	    InterruptedException {
	List<StringBuilder> nonTopData = null;
	if (settings.isIPv4()) {
	    nonTopData = tc.getNonTopologyData(ospfModel.getRouters());
	} else {
	    nonTopData = tc.getNonTopologyDataIPv6(ospfModel.getRouters());
	}
	List<String> ips = new ArrayList<String>();
	OspfLoader dataLoader = new OspfLoader();
	for (int i = 0; i < ospfModel.getRouters().size(); i++) {
	    Router r = ospfModel.getRouters().get(i);
	    // cena
	    dataLoader.loadCosts(ospfModel, r.getRouterID(), new BufferedReader(new StringReader(nonTopData.get(i)
		    .toString())));
	    // pridani IP routeru do seznamu pro dohledani nazvu
	    ips.add(r.getRouterID());
	}

	// nazev
	if (settings.rdnsServer != null && !settings.rdnsServer.isEmpty()) {
	    //pouziti zadaneho DNS serveru pro preklad ip na nazev
	    ReverseDNS rdns = new ReverseDNS(settings.rdnsServer);
	    IPEnumeration ipe = new IPEnumeration(ips);
	    Map<String, String> names = new HashMap<String, String>();
	    Thread threads[] = new Thread[60];
	    for (int i = 0; i < FastReverseDNS.DEFAULT_NUM_THREADS; i++) {
		threads[i] = new Thread(new FastReverseDNS(rdns, ipe, names));
		threads[i].start();
	    }
	    for (int i = 0; i < FastReverseDNS.DEFAULT_NUM_THREADS; i++) {
		threads[i].join();
	    }
	    for (Router r : ospfModel.getRouters()) {
		r.setRouterName(names.get(r.getRouterID()));
	    }
	} else {
	    //pouziti defaultniho DNS serveru pro preklad ip na nazev
	    for (Router r : ospfModel.getRouters()) {
		r.setRouterName(InetAddress.getByName(r.getRouterID()).getHostName());
	    }
	}
    }


    /**
     * Nalezne a nastavi suffix nazvu vsech routeru
     * @param ospfModel ospf model obsahujici seznam routeru
     */
    private void findAndSetSuffix(OspfModel ospfModel) {
	// nalezeni suffixu
	String suffix = findSuffix(ospfModel);
	// odebrani suffixu z nazvu routeru a nastaveni atributu pro suffix
	List<Router> routers = ospfModel.getRouters();
	for (Router r : routers) {
	    if (r.getRouterName() != null && r.getRouterName().endsWith(suffix)) {
		r.setRouterName(r.getRouterName().replace(suffix, ""));
		r.setNameSuffix(suffix);
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
		s = r.getRouterName().substring(i);
		if (s.length() > 0) {
		    if (map.containsKey(s)) {
			map.put(s, map.get(s).intValue() + 1);
		    } else {
			map.put(s, 1);
		    }
		}
	    } while ((i = r.getRouterName().indexOf('.', i + 1)) > 0);
	}
	// nalezeni nejcastejsiho suffixu
	String suffix = "";
	int count = 0;
	for (String suf : map.keySet()) {
	    if (map.get(suf).intValue() > count) {
		count = map.get(suf).intValue();
		suffix = suf;
	    }
	}
	return suffix;
    }
}
