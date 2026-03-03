package com.lilithsthrone.game.inventory.portal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Static registry that maps portal IDs to any object implementing
 * {@link IPortalInterface}.  Currently only {@link PortalClothing} items
 * register here, but the interface allows future non-clothing portals.
 *
 * <p>Call {@link #clearAll()} when a new game is started or a save is loaded,
 * then let items re-register themselves as they are restored from XML.
 *
 * @since 0.4.10
 */
public final class PortalManager {

	private PortalManager() { }

	private static final Map<String, IPortalInterface> REGISTRY = new LinkedHashMap<>();
	private static final Random RNG = new Random();

	// ---- Registration ----------------------------------------------------

	/**
	 * Register a portal item.  If {@code portal} is {@code null} this is a no-op.
	 */
	public static void register(IPortalInterface portal) {
		if (portal != null) {
			REGISTRY.put(portal.getPortalId(), portal);
		}
	}

	/**
	 * Unregister a portal item (e.g. when its ID is regenerated or the item
	 * is destroyed).
	 */
	public static void unregister(IPortalInterface portal) {
		if (portal != null) {
			REGISTRY.remove(portal.getPortalId());
		}
	}

	/** Remove all registrations (call on game-load or new-game). */
	public static void clearAll() {
		REGISTRY.clear();
	}

	// ---- Lookup ----------------------------------------------------------

	/**
	 * Retrieve the portal associated with the given ID.
	 *
	 * @param id 6-character uppercase portal ID (without the leading {@code #}).
	 * @return The portal, or {@code null} if not found.
	 */
	public static IPortalInterface getById(String id) {
		if (id == null) return null;
		return REGISTRY.get(id.toUpperCase());
	}

	public static boolean isRegistered(String id) {
		return id != null && REGISTRY.containsKey(id.toUpperCase());
	}

	/** Unmodifiable view of the full registry. */
	public static Map<String, IPortalInterface> getAllPortals() {
		return Collections.unmodifiableMap(REGISTRY);
	}

	// ---- ID generation ---------------------------------------------------

	/**
	 * Generate a unique 6-character portal ID that is not currently in use.
	 * Characters are drawn from {@link IPortalInterface#ID_CHARSET}.
	 */
	public static String generateUniqueId() {
		String id;
		do {
			StringBuilder sb = new StringBuilder(IPortalInterface.ID_LENGTH);
			for (int i = 0; i < IPortalInterface.ID_LENGTH; i++) {
				sb.append(IPortalInterface.ID_CHARSET
						.charAt(RNG.nextInt(IPortalInterface.ID_CHARSET.length())));
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
				&& name.matches(IPortalInterface.NAME_PATTERN);
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
