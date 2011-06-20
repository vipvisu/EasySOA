package org.easysoa.doctypes;

import java.util.HashMap;
import java.util.Map;

public class EasySOADoctype {

	public static final String PROP_TITLE = "title";
	public static final String PROP_DESCRIPTION = "description";

	public static final String SCHEMA_COMMON = "soacommon";
	public static final String SCHEMA_COMMON_PREFIX = "soa:";
	
	public static final String PROP_DTBROWSING = "discoveryTypeBrowsing";
	public static final String PROP_DTMONITORING = "discoveryTypeMonitoring";
	public static final String PROP_DTIMPORT = "discoveryTypeImport";

	private static Map<String, String> commonPropertyList = null;
	private static Map<String, String> dublinCorePropertyList = null;

	public static Map<String, String> getCommonPropertyList() {
		if (commonPropertyList == null) {
			commonPropertyList = new HashMap<String, String>();
			commonPropertyList.put(EasySOADoctype.PROP_DTBROWSING, "Notes about browsing-specific notifications. Informs the document of the notification source.");
			commonPropertyList.put(EasySOADoctype.PROP_DTMONITORING, "Notes about monitoring-specific notifications. Informs the document of the notification source.");
			commonPropertyList.put(EasySOADoctype.PROP_DTIMPORT, "Notes about import-specific notifications. Informs the document of the notification source.");
		}
		return commonPropertyList;
	}
	
	public static Map<String, String> getDublinCorePropertyList() {
		if (dublinCorePropertyList == null) {
			dublinCorePropertyList = new HashMap<String, String>();
			dublinCorePropertyList.put(EasySOADoctype.PROP_TITLE, "The name of the document.");
			dublinCorePropertyList.put(EasySOADoctype.PROP_DESCRIPTION, "A short description.");
		}
		return dublinCorePropertyList;
	}
	
	// TODO: Put url (+parentUrl ?) in a common schema, so that these
	// won't be needed anymore.
	
	public static String getSchema(String doctype) {
		if (doctype.equals(AppliImpl.DOCTYPE))
			return AppliImpl.SCHEMA;
		if (doctype.equals(ServiceAPI.DOCTYPE))
			return ServiceAPI.SCHEMA;
		if (doctype.equals(Service.DOCTYPE))
			return Service.SCHEMA;
		return null;
	}

	public static String getSchemaPrefix(String doctype) {
		if (doctype.equals(AppliImpl.DOCTYPE))
			return AppliImpl.SCHEMA_PREFIX;
		if (doctype.equals(ServiceAPI.DOCTYPE))
			return ServiceAPI.SCHEMA_PREFIX;
		if (doctype.equals(Service.DOCTYPE))
			return Service.SCHEMA_PREFIX;
		return null;
	}

}