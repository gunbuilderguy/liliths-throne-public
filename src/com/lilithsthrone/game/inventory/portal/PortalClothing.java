package com.lilithsthrone.game.inventory.portal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.lilithsthrone.game.inventory.clothing.AbstractClothing;
import com.lilithsthrone.game.inventory.clothing.AbstractClothingType;
import com.lilithsthrone.game.inventory.enchanting.ItemEffect;
import com.lilithsthrone.utils.colours.Colour;

/**
 * A clothing item that carries one or more portal locations.
 *
 * <p>All portal-specific state (ID, name, location configs and states) is stored
 * directly in this class rather than in a separate data object.  The class
 * also implements {@link IPortalInterface} so it can be stored in
 * {@link PortalManager} alongside any future non-clothing portal types.
 *
 * <p>Instances are created by {@link AbstractClothingType#createInstance} and
 * {@link AbstractClothingType#createLoadedInstance}, which check whether the
 * type defines portal location configs and return a {@code PortalClothing}
 * rather than a plain {@code AbstractClothing}.
 *
 * @since 0.4.10
 */
public class PortalClothing extends AbstractClothing implements IPortalInterface {

	// -----------------------------------------------------------------------
	// Portal state (stored directly – no PortalItemData wrapper)
	// -----------------------------------------------------------------------

	private String portalId;
	private String portalName;

	/** Type-level configs – parallel to locationStates. */
	private final List<PortalLocationConfig> locationConfigs;

	/** Per-instance runtime state – parallel to locationConfigs. */
	private final List<PortalLocationState> locationStates;

	// -----------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------

	/**
	 * New-generation constructor (used by
	 * {@link AbstractClothingType#createInstance(List, boolean)}).
	 */
	public PortalClothing(AbstractClothingType clothingType,
			List<Colour> colours, boolean allowRandomEnchantment) {
		super(clothingType, colours, allowRandomEnchantment);
		this.locationConfigs = new ArrayList<>(
				clothingType.getPortalLocationConfigs());
		this.locationStates  = buildDefaultStates(locationConfigs);
		this.portalId        = PortalManager.generateUniqueId();
		this.portalName      = portalId;
		PortalManager.register(this);
	}

	/**
	 * Load-from-save constructor (used by
	 * {@link AbstractClothingType#createLoadedInstance(List, List)}).
	 * Portal state is populated later by {@link #loadPortalData(Element)}.
	 */
	public PortalClothing(AbstractClothingType clothingType,
			List<Colour> colours, List<ItemEffect> effects) {
		super(clothingType, colours, effects);
		this.locationConfigs = new ArrayList<>(
				clothingType.getPortalLocationConfigs());
		this.locationStates  = buildDefaultStates(locationConfigs);
		this.portalId        = PortalManager.generateUniqueId(); // temporary; overwritten by loadPortalData
		this.portalName      = portalId;
		// Do NOT register yet – loadPortalData will set the real ID and register.
	}

	/**
	 * Copy constructor — used by {@link #createCopy()} to support
	 * {@code setDirty} / {@code setEnchantmentKnown} in
	 * {@link AbstractClothing}.
	 */
	public PortalClothing(PortalClothing original) {
		super(original); // copies all AbstractClothing state
		this.locationConfigs = original.locationConfigs; // immutable after construction
		// Shallow-copy states: the original is being discarded, so sharing is safe
		this.locationStates = new ArrayList<>(original.locationStates);
		this.portalId   = original.portalId;
		this.portalName = original.portalName;
		PortalManager.register(this); // new instance takes over the registry slot
	}

	/** Builds default {@link PortalLocationState} objects for a config list. */
	private static List<PortalLocationState> buildDefaultStates(
			List<PortalLocationConfig> configs) {
		List<PortalLocationState> states = new ArrayList<>(configs.size());
		for (PortalLocationConfig cfg : configs) {
			states.add(new PortalLocationState(cfg));
		}
		return states;
	}

	// -----------------------------------------------------------------------
	// AbstractClothing extension hooks
	// -----------------------------------------------------------------------

	/**
	 * Returns a {@link PortalClothing} copy so that portal data is preserved
	 * when {@code setDirty} / {@code setEnchantmentKnown} replaces the item.
	 */
	@Override
	protected AbstractClothing createCopy() {
		return new PortalClothing(this);
	}

	// -----------------------------------------------------------------------
	// XML persistence (IPortalInterface)
	// -----------------------------------------------------------------------

	@Override
	public void savePortalData(Element clothingElement, Document doc) {
		Element el = doc.createElement("portalData");
		clothingElement.appendChild(el);
		el.setAttribute("id",   portalId);
		el.setAttribute("name", portalName);

		Element locsEl = doc.createElement("portalLocations");
		el.appendChild(locsEl);
		for (PortalLocationState state : locationStates) {
			state.saveAsXML(locsEl, doc);
		}
	}

	@Override
	public void loadPortalData(Element clothingElement) {
		// Unregister the temporary ID assigned in the load constructor
		PortalManager.unregister(this);

		Element portalEl = (Element) clothingElement
				.getElementsByTagName("portalData").item(0);

		if (portalEl != null) {
			String savedId   = portalEl.getAttribute("id");
			String savedName = portalEl.getAttribute("name");

			this.portalId   = (savedId   != null && !savedId.isBlank())   ? savedId   : PortalManager.generateUniqueId();
			this.portalName = (savedName != null && !savedName.isBlank()) ? savedName : this.portalId;

			NodeList locParent = portalEl.getElementsByTagName("portalLocations");
			if (locParent.getLength() > 0) {
				NodeList locs = ((Element) locParent.item(0))
						.getElementsByTagName("portalLocation");
				int count = Math.min(locs.getLength(), locationConfigs.size());
				for (int i = 0; i < count; i++) {
					locationStates.set(i,
							PortalLocationState.loadFromXML((Element) locs.item(i)));
				}
				// Fill any missing states with fresh defaults
				for (int i = locs.getLength(); i < locationConfigs.size(); i++) {
					locationStates.set(i, new PortalLocationState(locationConfigs.get(i)));
				}
			}
		}
		// else: keep the temporary ID and default states (item pre-dates portal system)

		PortalManager.register(this);
	}

	// -----------------------------------------------------------------------
	// IPortalInterface – identity
	// -----------------------------------------------------------------------

	@Override
	public String getPortalId() {
		return portalId;
	}

	@Override
	public String getFormattedId() {
		return "#" + portalId;
	}

	@Override
	public String getPortalName() {
		return portalName;
	}

	@Override
	public boolean setPortalName(String name) {
		if (name == null || name.isBlank() || !name.matches(IPortalInterface.NAME_PATTERN)) {
			return false;
		}
		this.portalName = name;
		return true;
	}

	@Override
	public void regenerateId() {
		PortalManager.unregister(this);
		this.portalId   = PortalManager.generateUniqueId();
		this.portalName = portalId;
		PortalManager.register(this);
	}

	// -----------------------------------------------------------------------
	// IPortalInterface – locations
	// -----------------------------------------------------------------------

	@Override
	public int getLocationCount() {
		return locationConfigs.size();
	}

	@Override
	public PortalLocationConfig getLocationConfig(int index) {
		return locationConfigs.get(index);
	}

	@Override
	public PortalLocationState getLocationState(int index) {
		return locationStates.get(index);
	}

	@Override
	public List<PortalLocationConfig> getLocationConfigs() {
		return Collections.unmodifiableList(locationConfigs);
	}

	@Override
	public List<PortalLocationState> getLocationStates() {
		return Collections.unmodifiableList(locationStates);
	}
}
