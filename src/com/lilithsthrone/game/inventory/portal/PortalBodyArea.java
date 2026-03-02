package com.lilithsthrone.game.inventory.portal;

import java.util.EnumSet;
import java.util.Set;

/**
 * Defines all body areas that can host a portal location, along with the
 * modes each area supports and whether the portal has a restrictive effect
 * (large/huge rings that cause "portal amputation").
 *
 * @since 0.4.10
 */
public enum PortalBodyArea {

	// ---- Standard orifice / penetrator areas ----

	VAGINA(
		"vagina",
		"The vaginal opening.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		false),

	VAGINA_URETHRA(
		"vaginal urethra",
		"The urethral opening of the vagina.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		false),

	ANUS(
		"anus",
		"The anal opening.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		false),

	PENIS(
		"penis",
		"The penile shaft. Defaults to input (urethral), but can be switched to output (as a penetrator).",
		PortalMode.INPUT,
		EnumSet.of(PortalMode.INPUT, PortalMode.OUTPUT),
		false),

	MOUTH(
		"mouth",
		"The oral opening. Can receive or emit penetrators, and allows the tongue to emerge when set to output.",
		PortalMode.INPUT,
		EnumSet.of(PortalMode.INPUT, PortalMode.OUTPUT),
		false),

	NIPPLE(
		"nipple",
		"A nipple. Used in pairs.",
		PortalMode.INPUT,
		EnumSet.of(PortalMode.INPUT, PortalMode.OUTPUT),
		false),

	CROTCH_NIPPLE(
		"crotch-nipple",
		"A crotch-nipple. Used in pairs.",
		PortalMode.INPUT,
		EnumSet.of(PortalMode.INPUT, PortalMode.OUTPUT),
		false),

	FINGER(
		"finger",
		"A finger. Used in pairs.",
		PortalMode.INPUT,
		EnumSet.of(PortalMode.INPUT, PortalMode.OUTPUT),
		false),

	// ---- Restrictive areas (large rings) ----

	ARM(
		"arm",
		"Encircles an arm. The limb appears to vanish through the portal, restricting arm-based actions.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		true),

	LEG(
		"leg",
		"Encircles a leg. The limb appears to vanish through the portal, restricting leg-based actions.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		true),

	HEAD(
		"head",
		"Encircles the neck/head. The head appears to vanish through the portal, preventing speech and sight.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		true),

	TAIL(
		"tail",
		"Encircles a tail. The tail appears to vanish through the portal.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		true),

	// ---- Restrictive areas (huge rings) ----

	TORSO(
		"torso",
		"Encircles the torso. Depending on orientation, either the upper or lower half appears to vanish through the portal.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		true),

	CHEST(
		"chest",
		"Encircles the chest. The breasts appear to vanish through the portal, preventing their use.",
		PortalMode.OUTPUT,
		EnumSet.of(PortalMode.OUTPUT),
		true);

	// -----------------------------------------------------------------------

	private final String displayName;
	private final String description;
	private final PortalMode defaultMode;
	private final Set<PortalMode> allowedModes;
	private final boolean restrictive;

	PortalBodyArea(String displayName, String description, PortalMode defaultMode,
			Set<PortalMode> allowedModes, boolean restrictive) {
		this.displayName  = displayName;
		this.description  = description;
		this.defaultMode  = defaultMode;
		this.allowedModes = allowedModes;
		this.restrictive  = restrictive;
	}

	/** Human-readable name shown in the UI. */
	public String getDisplayName() {
		return displayName;
	}

	/** Tooltip description for this area. */
	public String getDescription() {
		return description;
	}

	/** The mode this area defaults to when first enabled. */
	public PortalMode getDefaultMode() {
		return defaultMode;
	}

	/** All modes this area is allowed to operate in. */
	public Set<PortalMode> getAllowedModes() {
		return allowedModes;
	}

	/** Whether this area causes a restrictive "portal amputation" effect. */
	public boolean isRestrictive() {
		return restrictive;
	}

	/** True if the player can switch this location between INPUT and OUTPUT. */
	public boolean isModeSwitchable() {
		return allowedModes.size() > 1;
	}
}
