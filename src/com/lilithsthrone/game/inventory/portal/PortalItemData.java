package com.lilithsthrone.game.inventory.portal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * All portal-related data attached to a single {@code AbstractClothing} instance.
 * <p>
 * Each portal item has a unique 6-character ID (e.g. {@code #AB3Z9F}) that is
 * used to look it up in the {@link PortalManager} registry.  The player may
 * rename the portal to any string matching {@code [A-Za-z0-9 _']+}.
 *
 * @since 0.4.10
 */
public class PortalItemData {

	/** Characters used when auto-generating a portal ID. */
	public static final String ID_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final int    ID_LENGTH  = 6;

	/** Regex that a user-supplied portal name must match. */
	public static final String NAME_PATTERN = "[A-Za-z0-9 _']+";

	// -----------------------------------------------------------------------

	private String portalId;
	private String portalName;

	/** Type-level configs – one per portal location defined in the XML. */
	private final List<PortalLocationConfig> locationConfigs;

	/** Per-instance state – parallel list to locationConfigs. */
	private final List<PortalLocationState>  locationStates;

	// ---- Construction -----------------------------------------------------

	/**
	 * Create a brand-new {@code PortalItemData} for a freshly generated item.
	 * A random unique ID is assigned via {@link PortalManager#generateUniqueId()}.
	 */
	public PortalItemData(List<PortalLocationConfig> configs) {
		this.portalId      = PortalManager.generateUniqueId();
		this.portalName    = portalId;
		this.locationConfigs = new ArrayList<>(configs);
		this.locationStates  = new ArrayList<>();
		for (PortalLocationConfig cfg : configs) {
			locationStates.add(new PortalLocationState(cfg));
		}
	}

	/** Private constructor used by {@link #loadFromXML}. */
	private PortalItemData(String id, String name,
			List<PortalLocationConfig> configs, List<PortalLocationState> states) {
		this.portalId        = id;
		this.portalName      = name;
		this.locationConfigs = configs;
		this.locationStates  = states;
	}

	// ---- ID / name management ---------------------------------------------

	public String getPortalId() {
		return portalId;
	}

	/** Returns the ID formatted for display, e.g. {@code #AB3Z9F}. */
	public String getFormattedId() {
		return "#" + portalId;
	}

	public String getPortalName() {
		return portalName;
	}

	/**
	 * Rename this portal.
	 *
	 * @param name New name; must match {@link #NAME_PATTERN}.
	 * @return {@code true} if the name was accepted.
	 */
	public boolean setPortalName(String name) {
		if (name == null || name.isBlank() || !name.matches(NAME_PATTERN)) {
			return false;
		}
		this.portalName = name;
		return true;
	}

	/**
	 * Regenerate the portal ID.  All existing connections <em>from</em> this
	 * item are preserved; connections <em>in other items pointing to this item</em>
	 * will become stale (they will simply show as disconnected in the UI).
	 * The new ID is registered with {@link PortalManager}.
	 *
	 * @param ownerClothing The clothing item that owns this data.
	 */
	public void regenerateId(com.lilithsthrone.game.inventory.clothing.AbstractClothing ownerClothing) {
		PortalManager.unregister(ownerClothing);
		this.portalId   = PortalManager.generateUniqueId();
		this.portalName = portalId;
		PortalManager.register(ownerClothing);
	}

	// ---- Location accessors -----------------------------------------------

	public int getLocationCount() {
		return locationConfigs.size();
	}

	public PortalLocationConfig getLocationConfig(int index) {
		return locationConfigs.get(index);
	}

	public PortalLocationState getLocationState(int index) {
		return locationStates.get(index);
	}

	public List<PortalLocationConfig> getLocationConfigs() {
		return Collections.unmodifiableList(locationConfigs);
	}

	public List<PortalLocationState> getLocationStates() {
		return Collections.unmodifiableList(locationStates);
	}

	// ---- XML persistence -------------------------------------------------

	public Element saveAsXML(Element parentElement, Document doc) {
		Element el = doc.createElement("portalData");
		parentElement.appendChild(el);
		el.setAttribute("id",   portalId);
		el.setAttribute("name", portalName);

		Element locsEl = doc.createElement("portalLocations");
		el.appendChild(locsEl);
		for (PortalLocationState state : locationStates) {
			state.saveAsXML(locsEl, doc);
		}
		return el;
	}

	/**
	 * Restore portal data from save XML.
	 *
	 * @param element The {@code <portalData>} element.
	 * @param configs The type-level configs (from the clothing type definition).
	 */
	public static PortalItemData loadFromXML(Element element,
			List<PortalLocationConfig> configs) {
		String id   = element.getAttribute("id");
		String name = element.getAttribute("name");
		if (name == null || name.isBlank()) {
			name = id;
		}

		List<PortalLocationState> states = new ArrayList<>();
		NodeList locParent = element.getElementsByTagName("portalLocations");
		if (locParent.getLength() > 0) {
			NodeList locs = ((Element) locParent.item(0))
					.getElementsByTagName("portalLocation");
			for (int i = 0; i < locs.getLength() && i < configs.size(); i++) {
				states.add(PortalLocationState.loadFromXML((Element) locs.item(i)));
			}
		}
		// Fill any missing states with fresh defaults
		while (states.size() < configs.size()) {
			states.add(new PortalLocationState(configs.get(states.size())));
		}

		return new PortalItemData(id, name, new ArrayList<>(configs), states);
	}
}
