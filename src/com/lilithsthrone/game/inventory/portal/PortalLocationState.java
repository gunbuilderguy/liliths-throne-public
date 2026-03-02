package com.lilithsthrone.game.inventory.portal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Per-instance runtime state for one portal location.
 * Tracks whether the location is active, which mode it is in,
 * and all outgoing connections.
 *
 * @since 0.4.10
 */
public class PortalLocationState {

	private boolean    enabled;
	private PortalMode currentMode;
	private final List<PortalConnection> connections;

	/** Construct a fresh state from the clothing-type config. */
	public PortalLocationState(PortalLocationConfig config) {
		this.enabled     = config.isEnabledByDefault();
		this.currentMode = config.getBodyArea().getDefaultMode();
		this.connections = new ArrayList<>();
	}

	/** Private constructor used by {@link #loadFromXML}. */
	private PortalLocationState(boolean enabled, PortalMode mode, List<PortalConnection> connections) {
		this.enabled     = enabled;
		this.currentMode = mode;
		this.connections = connections;
	}

	// ---- State accessors -------------------------------------------------

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public PortalMode getCurrentMode() {
		return currentMode;
	}

	/**
	 * Switch mode if allowed by the body area's config.
	 *
	 * @param bodyArea The area definition (used to validate the switch).
	 * @param newMode  Desired mode.
	 * @return {@code true} if the switch succeeded.
	 */
	public boolean setMode(PortalBodyArea bodyArea, PortalMode newMode) {
		if (!bodyArea.getAllowedModes().contains(newMode)) {
			return false;
		}
		currentMode = newMode;
		return true;
	}

	/** Unmodifiable view of all outgoing connections. */
	public List<PortalConnection> getConnections() {
		return Collections.unmodifiableList(connections);
	}

	/**
	 * Add a connection if not already present.
	 *
	 * @return {@code true} if the connection was added.
	 */
	public boolean addConnection(PortalConnection connection) {
		for (PortalConnection existing : connections) {
			if (existing.getTargetPortalId().equals(connection.getTargetPortalId())
					&& existing.getTargetLocationIndex() == connection.getTargetLocationIndex()) {
				return false; // duplicate
			}
		}
		connections.add(connection);
		return true;
	}

	/**
	 * Remove a connection matching the given target portal ID and location index.
	 *
	 * @return {@code true} if a connection was removed.
	 */
	public boolean removeConnection(String targetPortalId, int targetLocationIndex) {
		return connections.removeIf(c ->
				c.getTargetPortalId().equals(targetPortalId)
				&& c.getTargetLocationIndex() == targetLocationIndex);
	}

	/** Remove all connections to the given portal ID. */
	public void removeConnectionsTo(String targetPortalId) {
		connections.removeIf(c -> c.getTargetPortalId().equals(targetPortalId));
	}

	// ---- XML persistence -------------------------------------------------

	public Element saveAsXML(Element parentElement, Document doc) {
		Element el = doc.createElement("portalLocation");
		parentElement.appendChild(el);
		el.setAttribute("enabled", String.valueOf(enabled));
		el.setAttribute("mode",    currentMode.name());

		if (!connections.isEmpty()) {
			Element connEl = doc.createElement("connections");
			el.appendChild(connEl);
			for (PortalConnection conn : connections) {
				conn.saveAsXML(connEl, doc);
			}
		}
		return el;
	}

	public static PortalLocationState loadFromXML(Element element) {
		boolean enabled = Boolean.parseBoolean(element.getAttribute("enabled"));
		PortalMode mode;
		try {
			mode = PortalMode.valueOf(element.getAttribute("mode"));
		} catch (Exception e) {
			mode = PortalMode.INPUT;
		}

		List<PortalConnection> connections = new ArrayList<>();
		NodeList connParent = element.getElementsByTagName("connections");
		if (connParent.getLength() > 0) {
			NodeList connList = ((Element) connParent.item(0)).getElementsByTagName("portalConnection");
			for (int i = 0; i < connList.getLength(); i++) {
				connections.add(PortalConnection.loadFromXML((Element) connList.item(i)));
			}
		}

		return new PortalLocationState(enabled, mode, connections);
	}
}
