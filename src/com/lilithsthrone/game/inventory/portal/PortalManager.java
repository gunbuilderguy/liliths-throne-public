package com.lilithsthrone.game.inventory.portal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import com.lilithsthrone.game.inventory.clothing.AbstractClothing;

/**
 * Static registry that maps portal IDs to the {@link AbstractClothing} instances
 * that carry them.  All portal items register here when they are generated or
 * loaded from a save, allowing any part of the game to resolve a portal ID to
 * a concrete item.
 *
 * <p>Call {@link #clearAll()} when a new game is started or a save is loaded,
 * then let items re-register themselves as they are restored from XML.
 *
 * @since 0.4.10
 */
public final class PortalManager {

	private PortalManager() { }

	private static final Map<String, AbstractClothing> REGISTRY = new LinkedHashMap<>();
	private static final Random RNG = new Random();

	// ---- Registration ----------------------------------------------------

	/**
	 * Register a portal clothing item.  If the item has no portal data this
	 * is a no-op.
	 */
	public static void register(AbstractClothing clothing) {
		PortalItemData data = clothing.getPortalData();
		if (data != null) {
			REGISTRY.put(data.getPortalId(), clothing);
		}
	}

	/**
	 * Unregister a portal clothing item (e.g. when its ID is regenerated or
	 * the item is destroyed).
	 */
	public static void unregister(AbstractClothing clothing) {
		PortalItemData data = clothing.getPortalData();
		if (data != null) {
			REGISTRY.remove(data.getPortalId());
		}
	}

	/** Remove all registrations (call on game-load or new-game). */
	public static void clearAll() {
		REGISTRY.clear();
	}

	// ---- Lookup ----------------------------------------------------------

	/**
	 * Retrieve the clothing item associated with the given portal ID.
	 *
	 * @param id 6-character uppercase portal ID (without the leading {@code #}).
	 * @return The clothing item, or {@code null} if not found.
	 */
	public static AbstractClothing getById(String id) {
		if (id == null) return null;
		return REGISTRY.get(id.toUpperCase());
	}

	public static boolean isRegistered(String id) {
		return id != null && REGISTRY.containsKey(id.toUpperCase());
	}

	/** Unmodifiable view of the full registry. */
	public static Map<String, AbstractClothing> getAllPortals() {
		return Collections.unmodifiableMap(REGISTRY);
	}

	// ---- ID generation ---------------------------------------------------

	/**
	 * Generate a unique 6-character portal ID that is not currently in use.
	 * Characters are drawn from {@link PortalItemData#ID_CHARSET}.
	 */
	public static String generateUniqueId() {
		String id;
		do {
			StringBuilder sb = new StringBuilder(PortalItemData.ID_LENGTH);
			for (int i = 0; i < PortalItemData.ID_LENGTH; i++) {
				sb.append(PortalItemData.ID_CHARSET
						.charAt(RNG.nextInt(PortalItemData.ID_CHARSET.length())));
			}
			id = sb.toString();
		} while (REGISTRY.containsKey(id));
		return id;
	}

	// ---- Validation ------------------------------------------------------

	/**
	 * Check whether a user-supplied portal name is acceptable.
	 * Allowed characters: {@code [A-Za-z0-9 _']}.
	 */
	public static boolean isValidCustomName(String name) {
		return name != null && !name.isBlank()
				&& name.matches(PortalItemData.NAME_PATTERN);
	}

	/**
	 * Check whether a raw string is a valid registered portal ID
	 * (6 uppercase alphanumeric chars, currently in the registry).
	 */
	public static boolean isValidRegisteredId(String raw) {
		if (raw == null) return false;
		String upper = raw.toUpperCase().trim();
		return upper.matches("[A-Z0-9]{6}") && REGISTRY.containsKey(upper);
	}
}
