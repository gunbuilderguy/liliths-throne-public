package com.lilithsthrone.game.inventory.portal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a single directed connection from one portal location to a
 * specific location on another portal item.
 *
 * @since 0.4.10
 */
public class PortalConnection {

	/** The portal ID (#XXXXXX) of the target item. */
	private final String targetPortalId;

	/** Index into the target portal item's location list. */
	private final int targetLocationIndex;

	public PortalConnection(String targetPortalId, int targetLocationIndex) {
		this.targetPortalId      = targetPortalId;
		this.targetLocationIndex = targetLocationIndex;
	}

	public String getTargetPortalId() {
		return targetPortalId;
	}

	public int getTargetLocationIndex() {
		return targetLocationIndex;
	}

	// ---- XML persistence ------------------------------------------------

	public Element saveAsXML(Element parentElement, Document doc) {
		Element el = doc.createElement("portalConnection");
		parentElement.appendChild(el);
		el.setAttribute("targetId",            targetPortalId);
		el.setAttribute("targetLocationIndex", String.valueOf(targetLocationIndex));
		return el;
	}

	public static PortalConnection loadFromXML(Element element) {
		String targetId = element.getAttribute("targetId");
		int    idx      = 0;
		try {
			idx = Integer.parseInt(element.getAttribute("targetLocationIndex"));
		} catch (NumberFormatException ignored) { }
		return new PortalConnection(targetId, idx);
	}
}
