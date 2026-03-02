package com.lilithsthrone.game.inventory.portal;

/**
 * Defines whether a portal location acts as an entry point or an exit point.
 * <p>
 * When two portal locations are connected, a penetrator entering an INPUT portal
 * emerges from the linked OUTPUT portal.
 *
 * @since 0.4.10
 */
public enum PortalMode {

	INPUT(
		"Input",
		"Penetrators insert <i>into</i> this portal and emerge from a connected output portal."),

	OUTPUT(
		"Output",
		"Penetrators emerge <i>from</i> this portal, originating from a connected input portal.");

	private final String displayName;
	private final String description;

	PortalMode(String displayName, String description) {
		this.displayName = displayName;
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}
}
