package com.lilithsthrone.game.inventory.portal;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents any object that acts as a portal in the portal system.
 *
 * <h3>ID / name rules</h3>
 * <ul>
 *   <li>Auto-generated IDs use {@link #ID_CHARSET} and are {@link #ID_LENGTH} chars long.</li>
 *   <li>User-supplied names must match {@link #NAME_PATTERN}.</li>
 * </ul>
 * Implemented by {@link PortalClothing} and potentially other future types.
 *
 * <p>Each portal has a unique 6-character ID (e.g. {@code #AB3Z9F}), an optional
 * display name, and one or more portal locations that can be connected to other
 * {@code IPortalInterface} instances.
 *
 * @since 0.4.10
 */
public interface IPortalInterface {

	// ---- Constants -------------------------------------------------------

	/** Characters used when auto-generating a portal ID. */
	String ID_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	/** Length of an auto-generated portal ID. */
	int    ID_LENGTH  = 6;
	/** Regex that a user-supplied portal name must match. */
	String NAME_PATTERN = "[A-Za-z0-9 _']+";

	// ---- Identity --------------------------------------------------------

	/** The raw 6-character portal ID (no leading {@code #}). */
	String getPortalId();

	/** The portal ID formatted for display, e.g. {@code #AB3Z9F}. */
	String getFormattedId();

	/** User-visible name for this portal (defaults to the ID). */
	String getPortalName();

	/**
	 * Rename this portal.
	 *
	 * @param name New name; must match {@code [A-Za-z0-9 _']+}.
	 * @return {@code true} if the name was accepted.
	 */
	boolean setPortalName(String name);

	/**
	 * Regenerate the portal ID, updating the {@link PortalManager} registry.
	 * Connections in <em>other</em> portals pointing to the old ID will become stale.
	 */
	void regenerateId();

	// ---- Locations -------------------------------------------------------

	/** Number of portal locations defined for this item. */
	int getLocationCount();

	/** Type-level config for the portal location at {@code index}. */
	PortalLocationConfig getLocationConfig(int index);

	/** Mutable per-instance state for the portal location at {@code index}. */
	PortalLocationState getLocationState(int index);

	/** Convenience: body area for the location at {@code index}. */
	default PortalBodyArea getBodyArea(int index) {
		return getLocationConfig(index).getBodyArea();
	}

	/** Immutable view of all type-level location configs. */
	List<PortalLocationConfig> getLocationConfigs();

	/** Immutable view of all per-instance location states. */
	List<PortalLocationState> getLocationStates();

	// ---- XML persistence -------------------------------------------------

	/**
	 * Append portal-specific XML data to the clothing's existing element.
	 * Called by {@code AbstractClothing.saveAsXML()} when {@code this instanceof IPortalInterface}.
	 *
	 * @param clothingElement The already-created {@code <clothing>} element to append to.
	 * @param doc The owning document.
	 */
	void savePortalData(Element clothingElement, Document doc);

	/**
	 * Load portal-specific state from the clothing's XML element.
	 * Called by {@code AbstractClothing.loadFromXML()} when the created instance
	 * implements {@code IPortalInterface}.
	 *
	 * @param clothingElement The {@code <clothing>} element to read from.
	 */
	void loadPortalData(Element clothingElement);
}
