package org.hkfree.ospf.tools.load;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hkfree.ospf.model.ospf.OspfLink;
import org.hkfree.ospf.model.ospf.OspfModel;
import org.hkfree.ospf.model.ospfchange.OspfChangeModel;

/**
 * Třída sloužící k vytvoření OspfChangeModelu porovnáním více OspfModelů
 * @author Jakub Menzel
 */
public class OspfChangeLoader {

    private OspfChangeModel ospfChangeModel = null;
    private String inputDateFormat = "yyyy-MM-dd--HH-mm";


    /**
     * Konstruktor
     */
    public OspfChangeLoader() {}


    /**
     * Nastavuje OspfModel
     * @param ospfChangeModel
     */
    public void setOspfChangeModel(OspfChangeModel ospfChangeModel) {
	this.ospfChangeModel = ospfChangeModel;
    }


    /**
     * Vytvoří OspfChangeModel na základě OspfModelů
     * @param ospfModels
     */
    public void loadOspfChangeModel(List<OspfModel> ospfModels) throws Exception {
	SimpleDateFormat inputDateFormater = new SimpleDateFormat(inputDateFormat);
	Pattern datePattern = Pattern.compile("^([0-9]{4}-[0-9]{2}-[0-9]{2}--[0-9]{2}-[0-9]{2}).+$");
	Matcher dateMatcher = null;
	for (OspfModel oModel : ospfModels) {
	    dateMatcher = datePattern.matcher(oModel.getModelName());
	    dateMatcher.find();
	    if (dateMatcher.matches())
		ospfChangeModel.createNewOspfState(inputDateFormater.parse(dateMatcher.group(1)), oModel.getModelName());
	    else
		ospfChangeModel.createNewOspfState(new Date(), oModel.getModelName());
	    for (OspfLink ospfLink : oModel.getOspfLinks()) {
		ospfChangeModel.addOspfLink(ospfLink);
	    }
	}
    }
}
