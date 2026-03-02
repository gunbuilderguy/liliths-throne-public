package com.lilithsthrone.game.inventory.portal;

/**
 * Immutable type-level definition of a single portal location on a clothing type.
 * Loaded from the clothing XML at startup; shared by all instances of that type.
 *
 * @since 0.4.10
 */
public class PortalLocationConfig {

	private final PortalBodyArea bodyArea;
	private final boolean enabledByDefault;

	public PortalLocationConfig(PortalBodyArea bodyArea, boolean enabledByDefault) {
		this.bodyArea       = bodyArea;
		this.enabledByDefault = enabledByDefault;
	}

	/** The body area this portal location covers. */
	public PortalBodyArea getBodyArea() {
		return bodyArea;
	}

	/**
	 * Whether this location starts enabled when the item is first generated.
	 * Some items have locations that must be explicitly activated.
	 */
	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}
}
