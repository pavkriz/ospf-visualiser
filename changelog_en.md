# Installation and running #
For running application is needed Java JRE >= 1.6.
How to:
  * download file ospfVERZE.jar and run it
  * running from command line: ```
java -jar ospfVERZE.jar```

# Version #

## ospf3.0.5 (30.5.2013) ##
  * shorkey CTRL+L for loading models
  * JS layout icon added on toolbar
  * fixes BUGs
    * layouting from central router
    * stop first layouting after change type of layouting
    * first time running new version NULL pointer


## ospf3.0.4 (16.4.2013) ##
  * SVG export with CSS
  * modify labels and tooltips in map
  * display last added model after loading (in settings)
  * load data by date interval
    * fixes delete, it deletes all marked now
  * loading via CGI
    * remove address of rDNS server
    * loading names and geo coordinates from CGI
  * LLTD
    * application can display data from subnet via LLTD protocol
    * LLTD models added into router which propagets subnet
    * you can display LLTD model after click on routers in map

## ospf3.0.3 (5.1.2013) ##
<font color='red'><b>ATTENTION:</b> If an error ocurcurs when you first start new version, delete folder ".ospf-visualiser" in your home dir.</font>

## ospf3.0.2 (20.9.2012) ##
  * completely changed GUI - one window plus net state window
  * edited searching in map (now by ID or name of router - non-case sensitive)
  * dialog for information about operation in progress
  * dialog for XGMML export fixed
  * export SVG
  * highlighting of links in map
  * info panel containing:
    * loaded models - select box for selecting model
    * list of routers in selected model
    * info about clicked router/link
  * výběr dat dle časového intervalu v zadaném čase
    * možnost načtení modelů do seznamu
    * nebo do okna pro sledování stavů
  * choose data from zip file containing all models
  * faster telnet
  * IPv6 via telnet support
  * used default DNS server in case of empty value
  * fix of title and lables
  * statusbar changed
  * tips for working with application in menu help
  * fix bugs - net state window:
    * the shortest path mode
    * added option for location by GPS
    * u vypadlých spojů zůstává zobrazení poslední známé ceny

## ospf3.0.1 (26.6.2012) ##
  * layout correction (failure results of links/routers)
  * label correction
  * correction of loading data via telnet
  * correction of open/save NML file dialog
  * correction of side pannel in state map
  * possibility of select all/none in load logs dialo
  * FR layout for map of routers
    * faster, lower CPU requirements
  * searching for suffix of routers name (the most occurence of string is used) - only for data loaded via telnet

## ospf3.0.0 (20.6.2012) ##
  * cs/en translation completed
  * searching in map of routers
    * CTRL+f set focus on the input field for searched string
    * use symbol | for separate more searched strings
    * looking for the name of the router which contains the search string
    * found routers are highlighted by green color and its label by blue color

## ospf3.0.0rc (9.6.2012) ##
  * language settings
  * cs/en partial translation
  * connect to router via telnet, options:
    * router address, port, password
    * timeout (how long will application try to connect to router)
    * rDNS server - address of server for reverse DNS (determination of a domain name that is associated with a IP address)
  * export to xgmml file for import to Cytoscape and work with it
  * redesign to use dynamic layout in application GUI - correct display in Linux/Windows
  * save file with application settings into home folder
    * when you first start application the settings.properties file is created in your home folder in .ospf-visualiser folder (language settings is set by system)
    * when you confirm dialog for loading logs/model data the values you typed are saved
    * when you confirm dialog for loading model data the way of loading data is saved

Screenshot from Cytoscape with loaded model:
<img src='http://ospf-visualiser.googlecode.com/svn/wiki/scn_cytoscape.png' />

## older versions ##
Older versions were described here: http://lab.hkfree.org/ospfmap/