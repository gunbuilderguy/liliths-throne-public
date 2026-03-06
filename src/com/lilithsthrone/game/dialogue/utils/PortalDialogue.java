package com.lilithsthrone.game.dialogue.utils;

import java.util.Map;

import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.DialogueNodeType;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.game.inventory.clothing.AbstractClothing;
import com.lilithsthrone.game.inventory.portal.IPortalInterface;
import com.lilithsthrone.game.inventory.portal.PortalBodyArea;
import com.lilithsthrone.game.inventory.portal.PortalClothing;
import com.lilithsthrone.game.inventory.portal.PortalConnection;
import com.lilithsthrone.game.inventory.portal.PortalLocationConfig;
import com.lilithsthrone.game.inventory.portal.PortalLocationState;
import com.lilithsthrone.game.inventory.portal.PortalManager;
import com.lilithsthrone.game.inventory.portal.PortalMode;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.colours.PresetColour;

/**
 * UI dialogue for managing portal item connections, names, and modes.
 *
 * <p>Displayed when the player clicks "Manage Portals" on a portal clothing item
 * in their inventory.  Inline HTML buttons are handled by
 * {@link com.lilithsthrone.controller.MiscController#initPortalListeners()}.
 *
 * @since 0.4.10
 */
public class PortalDialogue {

	// ---- Static state ----------------------------------------------------

	/** The portal item currently open in the manager.  Set before opening. */
	public static PortalClothing managedClothing = null;

	/**
	 * The raw ID string typed into the search box.  Read by the controller
	 * after the player confirms the search.
	 */
	public static String searchedPortalId = "";

	// ---- Button ID constants ---------------------------------------------

	public static final String BTN_RENAME_CONFIRM = "PORTAL_RENAME_CONFIRM";
	public static final String BTN_REGEN_ID        = "PORTAL_REGEN_ID";
	public static final String BTN_SEARCH_CONFIRM  = "PORTAL_SEARCH_CONFIRM";
	public static final String INPUT_NAME          = "portalNameInput";
	public static final String INPUT_SEARCH_ID     = "portalSearchIdInput";
	public static final String HIDDEN_FIELD        = "portalHiddenField";

	public static String btnToggleLoc(int locIdx)         { return "PORTAL_TOGGLE_LOC_"  + locIdx; }
	public static String btnToggleMode(int locIdx)        { return "PORTAL_TOGGLE_MODE_" + locIdx; }
	public static String btnConnect(int locIdx, int tLocIdx, String tId) {
		return "PORTAL_CONNECT_" + locIdx + "_" + tLocIdx + "_" + tId;
	}
	public static String btnDisconnect(int locIdx, int tLocIdx, String tId) {
		return "PORTAL_DISCONNECT_" + locIdx + "_" + tLocIdx + "_" + tId;
	}

	// ---- DialogueNodes ---------------------------------------------------

	/** Main portal management dialogue. */
	public static final DialogueNode PORTAL_MANAGE = new DialogueNode(
			"Portal Manager", "Manage this portal item's connections and settings.", true) {

		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.INVENTORY;
		}

		@Override
		public String getContent() {
			if (managedClothing == null) {
				return "<p>No portal item selected.</p>";
			}
			return buildManagementHTML(managedClothing);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 0) {
				return new Response("Back", "Return to the item.", InventoryDialogue.CLOTHING_INVENTORY);
			}
			return null;
		}
	};

	// ---- HTML builder ----------------------------------------------------

	private static String buildManagementHTML(PortalClothing clothing) {
		StringBuilder sb = new StringBuilder();

		String accentColour = PresetColour.GENERIC_ARCANE.toWebHexString();
		String dimColour    = PresetColour.TEXT_GREY.toWebHexString();
		String goodColour   = PresetColour.GENERIC_GOOD.toWebHexString();
		String badColour    = PresetColour.GENERIC_BAD.toWebHexString();

		// ---- Header ----
		sb.append("<div class='container-full-width' style='padding:8px 16px; border-bottom:1px solid ").append(accentColour).append(";'>");
		sb.append("<h5 style='margin:0; color:").append(accentColour).append(";'>")
				.append(clothing.getName()).append(" — Portal Manager</h5>");
		sb.append("</div>");

		// ---- ID + Name panel ----
		sb.append("<div class='container-full-width' style='padding:8px 16px;'>");

		// Hidden field for JS→Java text transfer
		sb.append("<p id='").append(HIDDEN_FIELD).append("' style='display:none;'></p>");

		sb.append("<div style='float:left; width:20%; font-weight:bold;'>Portal ID:</div>");
		sb.append("<div style='float:left; width:20%; color:").append(accentColour).append(";'>")
				.append(clothing.getFormattedId()).append("</div>");
		sb.append("<div class='normal-button' id='").append(BTN_REGEN_ID)
				.append("' style='float:left; width:12%; margin:0 0 0 1%; text-align:center;' "
						+ "title='Generate a new random ID (existing connections to this ID will break)'>")
				.append("&#8635; Regen ID</div>");
		sb.append("<div style='clear:both;'></div>");

		sb.append("<div style='float:left; width:20%; font-weight:bold; margin-top:4px;'>Name:</div>");
		sb.append("<form style='float:left; width:35%; margin:0; padding:0;'>"
				+ "<input type='text' id='").append(INPUT_NAME)
				.append("' value='").append(escapeHtml(clothing.getPortalName()))
				.append("' style='width:100%; margin:0; padding:2px;' maxlength='32' "
						+ "title=\"Allowed characters: A-Z a-z 0-9 space _ &#39;\"></form>");
		sb.append("<div class='normal-button' id='").append(BTN_RENAME_CONFIRM)
				.append("' style='float:left; width:6%; margin:0 0 0 1%; text-align:center;' title='Confirm rename'>")
				.append("&#10003;</div>");
		sb.append("<div style='clear:both; margin-bottom:8px;'></div>");
		sb.append("</div>");

		// ---- Portal locations ----
		sb.append("<div class='container-full-width' style='padding:4px 16px;'>");
		sb.append("<h6 style='color:").append(accentColour).append("; margin:0 0 4px 0;'>Portal Locations</h6>");

		for (int i = 0; i < clothing.getLocationCount(); i++) {
			PortalLocationConfig cfg   = clothing.getLocationConfig(i);
			PortalLocationState  state = clothing.getLocationState(i);
			PortalBodyArea       area  = cfg.getBodyArea();

			sb.append("<div style='border:1px solid ").append(dimColour)
					.append("; margin:4px 0; padding:6px; border-radius:4px;'>");

			// Area name + enable toggle
			String enabledStr = state.isEnabled() ? "Enabled" : "Disabled";
			String toggleColor = state.isEnabled() ? goodColour : badColour;
			sb.append("<div style='float:left; width:28%; font-weight:bold; color:")
					.append(toggleColor).append(";'>")
					.append(capitalise(area.getDisplayName())).append("</div>");
			sb.append("<div class='normal-button' id='").append(btnToggleLoc(i))
					.append("' style='float:left; width:10%; text-align:center; font-size:0.85em; color:")
					.append(toggleColor).append(";' title='Toggle this location on/off'>")
					.append(enabledStr).append("</div>");

			if (state.isEnabled()) {
				// Mode indicator + toggle (if switchable)
				String modeLabel = state.getCurrentMode() == PortalMode.INPUT ? "&#8595; Input" : "&#8593; Output";
				String modeColour = state.getCurrentMode() == PortalMode.INPUT ? "#7eb8d4" : "#d47e7e";
				sb.append("<div style='float:left; width:2%; margin:0 1%;'></div>");
				if (area.isModeSwitchable()) {
					sb.append("<div class='normal-button' id='").append(btnToggleMode(i))
							.append("' style='float:left; width:14%; text-align:center; font-size:0.85em; color:")
							.append(modeColour).append(";' title='")
							.append(state.getCurrentMode().getDescription()).append("'>")
							.append(modeLabel).append("</div>");
				} else {
					sb.append("<div style='float:left; width:14%; text-align:center; font-size:0.85em; color:")
							.append(modeColour).append(";'>").append(modeLabel).append("</div>");
				}

				// Connection description
				sb.append("<div style='float:left; width:44%; font-size:0.8em; color:").append(dimColour)
						.append("; padding-left:8px; line-height:1.6em;'>")
						.append(area.getDescription()).append("</div>");
				sb.append("<div style='clear:both;'></div>");

				// Current connections list
				if (!state.getConnections().isEmpty()) {
					sb.append("<div style='margin-top:4px; font-size:0.85em; color:").append(dimColour)
							.append(";'>Connected to:</div>");
					for (PortalConnection conn : state.getConnections()) {
						IPortalInterface target = PortalManager.getById(conn.getTargetPortalId());
						String targetName;
						int tLoc = conn.getTargetLocationIndex();
						if (target != null && tLoc < target.getLocationCount()) {
							String tArea = target.getLocationConfig(tLoc).getBodyArea().getDisplayName();
							targetName = target.getPortalName() + " (" + target.getFormattedId() + ") — " + tArea;
						} else {
							targetName = "<span style='color:" + badColour + ";'>#"
									+ conn.getTargetPortalId() + " (not found)</span>";
						}
						sb.append("<div style='margin:2px 0 2px 8px;'>&#8594; ").append(targetName)
								.append(" <span class='normal-button' id='")
								.append(btnDisconnect(i, tLoc, conn.getTargetPortalId()))
								.append("' style='font-size:0.8em; padding:1px 6px; color:")
								.append(badColour).append(";'>&#10007;</span></div>");
					}
				} else {
					sb.append("<div style='margin-top:2px; font-size:0.8em; color:").append(dimColour)
							.append(";font-style:italic;'>No connections.</div>");
				}

				// ---- Add connection section ----
				sb.append("<div style='margin-top:6px;'>");
				sb.append("<span style='font-size:0.85em; font-weight:bold;'>Add connection:</span> ");
				sb.append("<form style='display:inline; margin:0; padding:0;'>"
						+ "<input type='text' id='").append(INPUT_SEARCH_ID).append("_").append(i)
						.append("' placeholder='Portal ID (e.g. AB3Z9F)' maxlength='6' "
								+ "style='width:120px; margin:0 4px; padding:2px; font-family:monospace;'></form>");
				sb.append("<span class='normal-button' id='").append(BTN_SEARCH_CONFIRM).append("_").append(i)
						.append("' style='font-size:0.85em;' title='Find portal by ID'>Search</span>");

				// If a search result is active for this location, show available target locations
				if (!searchedPortalId.isEmpty()) {
					IPortalInterface found = PortalManager.getById(searchedPortalId);
					if (found != null && !(found instanceof AbstractClothing
							&& ((AbstractClothing) found).equals(clothing))) {
						sb.append("<div style='margin-top:4px; font-size:0.85em;'>")
								.append("Found: <b>").append(escapeHtml(found.getPortalName()))
								.append("</b> (").append(found.getFormattedId()).append(")<br/>");
						sb.append("Select a location to connect to:</div>");
						for (int j = 0; j < found.getLocationCount(); j++) {
							PortalLocationState tState = found.getLocationState(j);
							if (!tState.isEnabled()) continue;
							String tArea = found.getLocationConfig(j).getBodyArea().getDisplayName();
							String tMode = tState.getCurrentMode() == PortalMode.INPUT ? "Input" : "Output";
							sb.append("<div class='normal-button' id='")
									.append(btnConnect(i, j, searchedPortalId))
									.append("' style='margin:2px 8px; font-size:0.85em;'>")
									.append(capitalise(tArea)).append(" (").append(tMode).append(")</div>");
						}
					} else if (found == null) {
						sb.append("<div style='color:").append(badColour).append("; font-size:0.85em; margin-top:2px;'>")
								.append("No portal found with ID #").append(searchedPortalId).append("</div>");
					} else {
						sb.append("<div style='color:").append(badColour).append("; font-size:0.85em; margin-top:2px;'>")
								.append("Cannot connect a portal to itself.</div>");
					}
				}

				sb.append("</div>"); // end add-connection section
			}

			sb.append("</div>"); // end location card
		}

		sb.append("</div>"); // end locations panel

		// ---- Available portals list ----
		sb.append("<div class='container-full-width' style='padding:4px 16px;'>");
		sb.append("<h6 style='color:").append(dimColour).append("; margin:4px 0 2px 0;'>")
				.append("All registered portals:</h6>");
		for (Map.Entry<String, IPortalInterface> entry : PortalManager.getAllPortals().entrySet()) {
			IPortalInterface p = entry.getValue();
			if (p instanceof AbstractClothing && ((AbstractClothing) p).equals(clothing)) continue;
			sb.append("<div style='font-size:0.8em; color:").append(dimColour).append(";'>")
					.append(p.getFormattedId()).append(" — ").append(escapeHtml(p.getPortalName()));
			if (p instanceof AbstractClothing) {
				sb.append(" (").append(((AbstractClothing) p).getName()).append(")");
			}
			sb.append("</div>");
		}
		sb.append("</div>");

		return sb.toString();
	}

	// ---- Effect methods called by MiscController -------------------------

	/** Apply rename from text input. */
	public static void applyRename(String newName) {
		if (managedClothing == null) return;
		managedClothing.setPortalName(newName);
		Main.game.setContent(new Response("Rename Portal", "", PORTAL_MANAGE) {});
	}

	/** Regenerate portal ID. */
	public static void applyRegenId() {
		if (managedClothing == null) return;
		managedClothing.regenerateId();
		searchedPortalId = "";
		Main.game.setContent(new Response("Regenerate ID", "", PORTAL_MANAGE) {});
	}

	/** Toggle enabled/disabled for location at index. */
	public static void applyToggleLocation(int locIndex) {
		if (managedClothing == null) return;
		PortalLocationState state = managedClothing.getLocationState(locIndex);
		state.setEnabled(!state.isEnabled());
		Main.game.setContent(new Response("Toggle Location", "", PORTAL_MANAGE) {});
	}

	/** Toggle mode for location at index. */
	public static void applyToggleMode(int locIndex) {
		if (managedClothing == null) return;
		PortalLocationConfig cfg   = managedClothing.getLocationConfig(locIndex);
		PortalLocationState  state = managedClothing.getLocationState(locIndex);
		PortalMode newMode = (state.getCurrentMode() == PortalMode.INPUT)
				? PortalMode.OUTPUT : PortalMode.INPUT;
		state.setMode(cfg.getBodyArea(), newMode);
		Main.game.setContent(new Response("Toggle Mode", "", PORTAL_MANAGE) {});
	}

	/** Set the searched portal ID from the text input and refresh. */
	public static void applySearch(String rawId) {
		searchedPortalId = rawId == null ? "" : rawId.toUpperCase().trim();
		Main.game.setContent(new Response("Search Portal", "", PORTAL_MANAGE) {});
	}

	/** Add a connection from this item's location to the target location. */
	public static void applyConnect(int locIndex, String targetPortalId, int targetLocIndex) {
		if (managedClothing == null) return;
		managedClothing.getLocationState(locIndex)
				.addConnection(new PortalConnection(targetPortalId, targetLocIndex));
		searchedPortalId = "";
		Main.game.setContent(new Response("Connect Portal", "", PORTAL_MANAGE) {});
	}

	/** Remove a connection. */
	public static void applyDisconnect(int locIndex, String targetPortalId, int targetLocIndex) {
		if (managedClothing == null) return;
		managedClothing.getLocationState(locIndex)
				.removeConnection(targetPortalId, targetLocIndex);
		Main.game.setContent(new Response("Disconnect Portal", "", PORTAL_MANAGE) {});
	}

	// ---- Helpers ---------------------------------------------------------

	private static String capitalise(String s) {
		if (s == null || s.isEmpty()) return s;
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private static String escapeHtml(String s) {
		if (s == null) return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}
}
