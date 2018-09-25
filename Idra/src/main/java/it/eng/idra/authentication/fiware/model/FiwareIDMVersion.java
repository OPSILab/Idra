package it.eng.idra.authentication.fiware.model;

public enum FiwareIDMVersion {

	FIWARE_IDM_VERSION_6("6"), FIWARE_IDM_VERSION_7("7");

	private final String text;

	private FiwareIDMVersion(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public static FiwareIDMVersion fromString(String text) {
		for (FiwareIDMVersion b : FiwareIDMVersion.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}

}
