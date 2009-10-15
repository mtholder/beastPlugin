package beastplugin.thmm;
import java.util.*;

import beastplugin.thmm.*;
import dr.app.plugin.*;
import dr.xml.XMLObjectParser;
public class THMMPlugin implements Plugin {

	public Set<XMLObjectParser> getParsers() {
		Set<XMLObjectParser> parsers = new HashSet<XMLObjectParser>();
		THMMParser thmmParser = new THMMParser();
		parsers.add(thmmParser);
		return parsers;
	}
	
}
