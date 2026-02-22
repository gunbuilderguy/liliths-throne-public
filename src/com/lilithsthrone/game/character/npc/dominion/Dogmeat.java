package com.lilithsthrone.game.character.npc.dominion;

import java.time.Month;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lilithsthrone.game.character.CharacterImportSetting;
import com.lilithsthrone.game.character.EquipClothingSetting;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.Covering;
import com.lilithsthrone.game.character.attributes.Attribute;
import com.lilithsthrone.game.character.body.types.PenisType;
import com.lilithsthrone.game.character.body.valueEnums.BodySize;
import com.lilithsthrone.game.character.body.valueEnums.Muscle;
import com.lilithsthrone.game.character.body.valueEnums.PenetrationGirth;
import com.lilithsthrone.game.character.body.valueEnums.PenetrationModifier;
import com.lilithsthrone.game.character.body.valueEnums.TesticleSize;
import com.lilithsthrone.game.character.effects.PerkCategory;
import com.lilithsthrone.game.character.effects.PerkManager;
import com.lilithsthrone.game.character.gender.Gender;
import com.lilithsthrone.game.character.npc.NPC;
import com.lilithsthrone.game.character.persona.NameTriplet;
import com.lilithsthrone.game.character.persona.Occupation;
import com.lilithsthrone.game.character.persona.PersonalityTrait;
import com.lilithsthrone.game.character.persona.SexualOrientation;
import com.lilithsthrone.game.character.race.RaceStage;
import com.lilithsthrone.game.character.race.Subspecies;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.npcDialogue.dominion.DogmeatDialogue;
import com.lilithsthrone.game.inventory.CharacterInventory;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.Util.Value;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * Dogmeat - a loyal stray dog found in Dominion's alleyways.
 * A subtle nod to the faithful canine companion from the Fallout series.
 *
 * @since 0.4.11.3
 * @version 0.4.11.3
 */
public class Dogmeat extends NPC {

	public Dogmeat() {
		this(false);
	}

	public Dogmeat(boolean isImported) {
		super(isImported,
				new NameTriplet("Dogmeat"),
				"",
				"A scruffy but powerfully-built stray dog roaming Dominion's alleyways."
						+ " Despite the hardships of street life, there's a fierce loyalty in those amber eyes.",
				5, Month.JUNE, 1,
				165, Gender.M_P_MALE, Subspecies.DOG_MORPH_GERMAN_SHEPHERD, RaceStage.FERAL,
				new CharacterInventory(false, 0),
				WorldType.EMPTY, PlaceType.GENERIC_HOLDING_CELL, true);
	}

	@Override
	public void loadFromXML(Element parentElement, Document doc, CharacterImportSetting... settings) {
		loadNPCVariablesFromXML(this, null, parentElement, doc, settings);
	}

	@Override
	public void setupPerks(boolean autoSelectPerks) {
		PerkManager.initialisePerks(this,
				Util.newArrayListOfValues(),
				Util.newHashMapOfValues(
						new Value<>(PerkCategory.PHYSICAL, 2),
						new Value<>(PerkCategory.LUST, 0),
						new Value<>(PerkCategory.ARCANE, 0)));
	}

	@Override
	public void setStartingBody(boolean setPersona) {
		// Persona:
		if (setPersona) {
			this.setPersonalityTraits(PersonalityTrait.BRAVE);
			this.setSexualOrientation(SexualOrientation.AMBIPHILIC);
			this.setHistory(Occupation.NPC_MUGGER); // closest generic NPC occupation
		}

		// Body:
		this.setFemininity(10);
		this.setMuscle(Muscle.THREE_MUSCULAR.getMedianValue());
		this.setBodySize(BodySize.TWO_AVERAGE.getMedianValue());

		// Fur - tan/black saddle pattern like a German Shepherd:
		this.setSkinCovering(new Covering(BodyCoveringType.CANINE_FUR, PresetColour.COVERING_TAN), true);
		this.setHairCovering(new Covering(BodyCoveringType.CANINE_FUR, PresetColour.COVERING_BLACK), true);

		// Genitalia:
		this.setPenisType(PenisType.DOG_MORPH);
		this.addPenisModifier(PenetrationModifier.KNOTTED);
		this.addPenisModifier(PenetrationModifier.TAPERED);
		this.setPenisSize(22);
		this.setPenisGirth(PenetrationGirth.THREE_AVERAGE);
		this.setTesticleSize(TesticleSize.FOUR_HUGE);
		this.setInternalTesticles(false);
		this.setPenisCumStorage(60);
		this.fillCumToMaxStorage();
		this.setAttribute(Attribute.VIRILITY, 75);

		// Actual quadrupedal dog, not an anthro morph:
		this.setFeral(Subspecies.DOG_MORPH_GERMAN_SHEPHERD);
	}

	@Override
	public void equipClothing(List<EquipClothingSetting> settings) {
		// No starting clothing — leave whatever the player has equipped untouched
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	@Override
	public String getSpeechColour() {
		return "#C68B3A";
	}

	@Override
	public boolean isAbleToBeImpregnated() {
		return false;
	}

	@Override
	public void changeFurryLevel() {
	}

	@Override
	public void turnUpdate() {
		if (!Main.game.getPlayer().getCompanions().contains(this)) {
			// Not a companion - stay in the holding cell
			if (!this.getWorldLocation().equals(WorldType.EMPTY)) {
				this.setLocation(WorldType.EMPTY, PlaceType.GENERIC_HOLDING_CELL, false);
			}
		}
		// When a companion, the standard companion following logic handles movement
	}

	@Override
	public DialogueNode getEncounterDialogue() {
		return DogmeatDialogue.DOGMEAT_ENCOUNTER;
	}

}
