package VirtualGraffiti;

import java.util.Properties;

class P5Properties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean getBooleanProperty(String id, boolean defState) {
		return Boolean.parseBoolean(getProperty(id,""+defState));
	}

	int getIntProperty(String id, int defVal) {
		return Integer.parseInt(getProperty(id,""+defVal)); 
	}
	String getStringProperty( String id, String defVal ) {
		return (String) getProperty(id,""+defVal);
	}

	float getFloatProperty(String id, float defVal) {
		return Float.parseFloat(getProperty(id,""+defVal)); 
	}  
}