package com.lilithsthrone.game.character.npc.dominion;

import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lilithsthrone.game.Game;
import com.lilithsthrone.game.character.CharacterImportSetting;
import com.lilithsthrone.game.character.EquipClothingSetting;
import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.Covering;
import com.lilithsthrone.game.character.body.types.HornType;
import com.lilithsthrone.game.character.body.types.LegType;
import com.lilithsthrone.game.character.body.types.PenisType;
import com.lilithsthrone.game.character.body.types.TailType;
import com.lilithsthrone.game.character.body.types.WingType;
import com.lilithsthrone.game.character.body.valueEnums.AreolaeSize;
import com.lilithsthrone.game.character.body.valueEnums.AssSize;
import com.lilithsthrone.game.character.body.valueEnums.BodyHair;
import com.lilithsthrone.game.character.body.valueEnums.BodySize;
import com.lilithsthrone.game.character.body.valueEnums.BreastShape;
import com.lilithsthrone.game.character.body.valueEnums.Capacity;
import com.lilithsthrone.game.character.body.valueEnums.ClitorisSize;
import com.lilithsthrone.game.character.body.valueEnums.CupSize;
import com.lilithsthrone.game.character.body.valueEnums.HairLength;
import com.lilithsthrone.game.character.body.valueEnums.HairStyle;
import com.lilithsthrone.game.character.body.valueEnums.HipSize;
import com.lilithsthrone.game.character.body.valueEnums.LabiaSize;
import com.lilithsthrone.game.character.body.valueEnums.LipSize;
import com.lilithsthrone.game.character.body.valueEnums.Muscle;
import com.lilithsthrone.game.character.body.valueEnums.NippleSize;
import com.lilithsthrone.game.character.body.valueEnums.OrificeElasticity;
import com.lilithsthrone.game.character.body.valueEnums.OrificePlasticity;
import com.lilithsthrone.game.character.body.valueEnums.PenetrationGirth;
import com.lilithsthrone.game.character.body.valueEnums.TesticleSize;
import com.lilithsthrone.game.character.body.valueEnums.TongueLength;
import com.lilithsthrone.game.character.body.valueEnums.Wetness;
import com.lilithsthrone.game.character.body.valueEnums.WingSize;
import com.lilithsthrone.game.character.effects.Perk;
import com.lilithsthrone.game.character.effects.PerkCategory;
import com.lilithsthrone.game.character.effects.PerkManager;
import com.lilithsthrone.game.character.effects.StatusEffect;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.gender.Gender;
import com.lilithsthrone.game.character.markings.Tattoo;
import com.lilithsthrone.game.character.markings.TattooCountType;
import com.lilithsthrone.game.character.markings.TattooCounter;
import com.lilithsthrone.game.character.markings.TattooCounterType;
import com.lilithsthrone.game.character.markings.TattooType;
import com.lilithsthrone.game.character.markings.TattooWriting;
import com.lilithsthrone.game.character.markings.TattooWritingStyle;
import com.lilithsthrone.game.character.npc.NPC;
import com.lilithsthrone.game.character.npc.dominion.Dogmeat;
import com.lilithsthrone.game.character.persona.NameTriplet;
import com.lilithsthrone.game.character.persona.Occupation;
import com.lilithsthrone.game.character.persona.PersonalityTrait;
import com.lilithsthrone.game.character.persona.SexualOrientation;
import com.lilithsthrone.game.character.race.RaceStage;
import com.lilithsthrone.game.character.race.Subspecies;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.game.inventory.AbstractCoreItem;
import com.lilithsthrone.game.inventory.CharacterInventory;
import com.lilithsthrone.game.inventory.InventorySlot;
import com.lilithsthrone.game.inventory.ItemTag;
import com.lilithsthrone.game.inventory.clothing.AbstractClothing;
import com.lilithsthrone.game.inventory.clothing.AbstractClothingType;
import com.lilithsthrone.game.inventory.clothing.ClothingType;
import com.lilithsthrone.game.inventory.enchanting.ItemEffect;
import com.lilithsthrone.game.inventory.enchanting.ItemEffectType;
import com.lilithsthrone.game.inventory.enchanting.TFModifier;
import com.lilithsthrone.game.inventory.enchanting.TFPotency;
import com.lilithsthrone.game.inventory.item.AbstractItem;
import com.lilithsthrone.game.inventory.item.AbstractItemType;
import com.lilithsthrone.game.inventory.item.ItemType;
import com.lilithsthrone.game.sex.GenericSexFlag;
import com.lilithsthrone.game.sex.SexAreaOrifice;
import com.lilithsthrone.game.sex.SexAreaPenetration;
import com.lilithsthrone.game.sex.SexParticipantType;
import com.lilithsthrone.game.sex.SexType;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.Util.Value;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * @since 0.1.66
 * @version 0.3.1
 * @author Innoxia
 */
public class Kate extends NPC {

	/**
	 * Returns the relationship tier between Kate and Dogmeat based on off-screen visit count.
	 * <ul>
	 *   <li>0 — Not started</li>
	 *   <li>1 — New &amp; Nervous (1-3 visits)</li>
	 *   <li>2 — Comfortable (4-8 visits)</li>
	 *   <li>3 — Attached (9-15 visits)</li>
	 *   <li>4 — Devoted (16+ visits)</li>
	 * </ul>
	 */
	public static int getDogmeatRelationshipTier() {
		long count = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_offscreen_count");
		if (count >= 16) return 4;
		if (count >= 9)  return 3;
		if (count >= 4)  return 2;
		if (count >= 1)  return 1;
		return 0;
	}

	public Kate() {
		this(false);
	}
	
	public Kate(boolean isImported) {
		super(isImported, new NameTriplet("Kate"), "Lasiellemartu",
				"Kate is a demon who owns the beauty salon 'Succubi's Secrets'."
						+ " Despite being incredibly good at what she does, she's exceedingly lazy, and prefers to keep the exterior of her shop looking run-down so as to scare off potential customers.",
				361, Month.SEPTEMBER, 9,
				10, Gender.F_V_B_FEMALE, Subspecies.DEMON, RaceStage.GREATER,
				new CharacterInventory(false, 10), WorldType.SHOPPING_ARCADE, PlaceType.SHOPPING_ARCADE_KATES_SHOP, true);
		
		if(!isImported) {
			dailyUpdate();
		}
	}
	
	
	@Override
	public void loadFromXML(Element parentElement, Document doc, CharacterImportSetting... settings) {
		loadNPCVariablesFromXML(this, null, parentElement, doc, settings);

		if(Main.isVersionOlderThan(Game.loadingVersion, "0.2.10.5")) {
			resetBodyAfterVersion_2_10_5();
		}
		if(Main.isVersionOlderThan(Game.loadingVersion, "0.2.11")) {
			this.setAgeAppearanceAbsolute(28);
		}
		if(Main.isVersionOlderThan(Game.loadingVersion, "0.3.5.1")) {
			this.setPersonalityTraits(
					PersonalityTrait.SELFISH,
					PersonalityTrait.LEWD);
		}
		if(Main.isVersionOlderThan(Game.loadingVersion, "0.3.6")) {
			this.setTailGirth(PenetrationGirth.TWO_NARROW);
		}
		if(Main.isVersionOlderThan(Game.loadingVersion, "0.4.9.3")) {
			this.resetPerksMap(true);
		}
		if(Main.isVersionOlderThan(Game.loadingVersion, "0.4.9.8")) {
			this.setAge(361);
		}
		if(Main.isVersionOlderThan(Game.loadingVersion, "0.4.9.9")) {
			this.addTattoo(InventorySlot.GROIN, getKatesGroinTattoo());
		}
	}

	@Override
	public void setupPerks(boolean autoSelectPerks) {
		this.addSpecialPerk(Perk.SPECIAL_ARCANE_TATTOOIST);
		this.addSpecialPerk(Perk.SPECIAL_MEGA_SLUT);
		
		PerkManager.initialisePerks(this,
				Util.newArrayListOfValues(
						Perk.HEAVY_SLEEPER),
				Util.newHashMapOfValues(
						new Value<>(PerkCategory.PHYSICAL, 0),
						new Value<>(PerkCategory.LUST, 5),
						new Value<>(PerkCategory.ARCANE, 0)));
	}

	@Override
	public void setStartingBody(boolean setPersona) {
		
		// Persona:

		if(setPersona) {
			this.setPersonalityTraits(
					PersonalityTrait.SELFISH,
					PersonalityTrait.LEWD);
			
			this.setSexualOrientation(SexualOrientation.AMBIPHILIC);
			
			this.setHistory(Occupation.NPC_BEAUTICIAN);
			
			this.addFetish(Fetish.FETISH_SUBMISSIVE);
			this.addFetish(Fetish.FETISH_PREGNANCY);
		}
		
		// Body:
		this.setLegType(LegType.DEMON_COMMON);
		this.setHornType(HornType.CURLED);
		this.setWingType(WingType.DEMON_COMMON);
		this.setWingSize(WingSize.ONE_SMALL.getValue());
		this.setTailType(TailType.DEMON_COMMON);
		this.setTailGirth(PenetrationGirth.TWO_NARROW);

		if(this.getTattooInSlot(InventorySlot.GROIN)==null) {
			try {
				this.addTattoo(InventorySlot.GROIN, getKatesGroinTattoo());
				
				this.addTattoo(InventorySlot.TORSO_OVER,
						new Tattoo(
							"innoxia_animal_butterflies",
							PresetColour.CLOTHING_PURPLE,
							PresetColour.CLOTHING_PINK,
							PresetColour.CLOTHING_PINK_LIGHT,
							false,
							null,
							null));
				
				this.addTattoo(InventorySlot.TORSO_UNDER,
						new Tattoo(
							"innoxia_symbol_tribal",
							PresetColour.CLOTHING_BLACK,
							null,
							null,
							false,
							new TattooWriting(
									"Don't pull out!",
									PresetColour.CLOTHING_BLACK,
									false),
							null));
				
			} catch(Exception ex) {
			}
		}

		// Core:
		this.setAgeAppearanceAbsolute(28);
		this.setHeight(180);
		this.setFemininity(85);
		this.setMuscle(Muscle.THREE_MUSCULAR.getMedianValue());
		this.setBodySize(BodySize.TWO_AVERAGE.getMedianValue());

		// Coverings:
		this.setEyeCovering(new Covering(BodyCoveringType.EYE_DEMON_COMMON, PresetColour.EYE_GREEN));
		this.setSkinCovering(new Covering(BodyCoveringType.DEMON_COMMON, PresetColour.SKIN_PINK), true);
		
		this.setSkinCovering(new Covering(BodyCoveringType.HORN, PresetColour.COVERING_DARK_GREY), false);

		this.setHairCovering(new Covering(BodyCoveringType.HAIR_DEMON, PresetColour.COVERING_RED), true);
		this.setHairLength(HairLength.THREE_SHOULDER_LENGTH.getMedianValue());
		this.setHairStyle(HairStyle.SIDECUT);

		this.setHairCovering(new Covering(BodyCoveringType.BODY_HAIR_DEMON, PresetColour.COVERING_BLACK), false);
		this.setUnderarmHair(BodyHair.ZERO_NONE);
		this.setAssHair(BodyHair.ZERO_NONE);
		this.setPubicHair(BodyHair.ZERO_NONE);
		this.setFacialHair(BodyHair.ZERO_NONE);

		this.setHandNailPolish(new Covering(BodyCoveringType.MAKEUP_NAIL_POLISH_HANDS, PresetColour.COVERING_PINK));
		this.setFootNailPolish(new Covering(BodyCoveringType.MAKEUP_NAIL_POLISH_FEET, PresetColour.COVERING_PINK));
//		this.setBlusher(new Covering(BodyCoveringType.MAKEUP_BLUSHER, PresetColour.COVERING_RED));
		this.setLipstick(new Covering(BodyCoveringType.MAKEUP_LIPSTICK, PresetColour.COVERING_RED));
		this.setEyeLiner(new Covering(BodyCoveringType.MAKEUP_EYE_LINER, PresetColour.COVERING_BLACK));
		this.setEyeShadow(new Covering(BodyCoveringType.MAKEUP_EYE_SHADOW, PresetColour.COVERING_RED));
		
		// Face:
		this.setFaceVirgin(false);
		this.setLipSize(LipSize.TWO_FULL);
		this.setFaceCapacity(Capacity.FIVE_ROOMY, true);
		// Throat settings and modifiers
		this.setTongueLength(TongueLength.ZERO_NORMAL.getMedianValue());
		// Tongue modifiers
		
		// Chest:
		this.setNippleVirgin(false);
		this.setBreastSize(CupSize.F.getMeasurement());
		this.setBreastShape(BreastShape.ROUND);
		this.setNippleSize(NippleSize.TWO_BIG);
		this.setAreolaeSize(AreolaeSize.TWO_BIG);
		// Nipple settings and modifiers
		
		// Ass:
		this.setAssVirgin(false);
		this.setAssBleached(true);
		this.setAssSize(AssSize.FOUR_LARGE);
		this.setHipSize(HipSize.THREE_GIRLY);
		this.setAssCapacity(Capacity.TWO_TIGHT, true);
		this.setAssWetness(Wetness.ZERO_DRY);
		this.setAssElasticity(OrificeElasticity.SEVEN_ELASTIC.getValue());
		this.setAssPlasticity(OrificePlasticity.ONE_SPRINGY.getValue());
		// Anus modifiers
		
		// Penis:
		// (For when she grows one)
		this.setPenisVirgin(false);
		this.setPenisGirth(PenetrationGirth.FOUR_GIRTHY);
		this.setPenisSize(15);
//		this.setInternalTesticles(true); Use player preferences
		this.setTesticleSize(TesticleSize.THREE_LARGE);
		this.setPenisCumStorage(150);
		this.fillCumToMaxStorage();
		
		// Vagina:
		this.setVaginaVirgin(false);
		this.setVaginaClitorisSize(ClitorisSize.ZERO_AVERAGE);
		this.setVaginaLabiaSize(LabiaSize.TWO_AVERAGE);
		this.setVaginaSquirter(true);
		this.setVaginaCapacity(Capacity.ONE_EXTREMELY_TIGHT, true);
		this.setVaginaWetness(Wetness.SIX_SOPPING_WET);
		this.setVaginaElasticity(OrificeElasticity.SEVEN_ELASTIC.getValue());
		this.setVaginaPlasticity(OrificePlasticity.ONE_SPRINGY.getValue());
		
		// Feet:
		// Foot shape
	}
	
	@Override
	public void equipClothing(List<EquipClothingSetting> settings) {

		this.unequipAllClothingIntoVoid(true, true);

		this.setMoney(10);

		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_groin_vstring", PresetColour.CLOTHING_PINK, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_leg_micro_skirt_belted", PresetColour.CLOTHING_BLACK, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_torso_cami_straps", PresetColour.CLOTHING_PINK, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_torsoOver_womens_leather_jacket", PresetColour.CLOTHING_BLACK, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_sock_fishnets", PresetColour.CLOTHING_BLACK, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_foot_heels", PresetColour.CLOTHING_BLACK, false), true, this);

		this.setPiercedEar(true);
		this.setPiercedNavel(true);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_piercing_ear_ring", PresetColour.CLOTHING_GOLD, false), true, this);
		this.equipClothingFromNowhere(Main.game.getItemGen().generateClothing("innoxia_piercing_gemstone_barbell", PresetColour.CLOTHING_GOLD, false), InventorySlot.PIERCING_STOMACH, true, this);

	}
	
	@Override
	public boolean isUnique() {
		return true;
	}
	
	@Override
	public void dailyUpdate() {
		clearNonEquippedInventory(false);

		for(AbstractItemType item : ItemType.getAllItems()) {
			if(item.getItemTags().contains(ItemTag.SOLD_BY_KATE)
					&& (!item.getItemTags().contains(ItemTag.SILLY_MODE) || Main.game.isSillyMode())) {
				this.addItem(Main.game.getItemGen().generateItem(item), !item.isConsumedOnUse()?1:(6+Util.random.nextInt(12)), false, false);
			}
		}
		
		List<AbstractClothing> clothingToSell = new ArrayList<>();
		
		for(AbstractClothingType clothing : ClothingType.getAllClothing()) {
			if(clothing.getDefaultItemTags().contains(ItemTag.SOLD_BY_KATE)
					&& (!clothing.getDefaultItemTags().contains(ItemTag.SILLY_MODE) || Main.game.isSillyMode())) {
				clothingToSell.add(Main.game.getItemGen().generateClothing(clothing, false));
			}
		}

		for(AbstractClothing c : clothingToSell) {
			this.addClothing(c, 2+Util.random.nextInt(5), false, false);
		}
		
		for(AbstractClothing c : Main.game.getCharacterUtils().generateEnchantedClothingForTrader(this, clothingToSell, 6, 2)) {
			this.addClothing(c, false);
		}
	}
	
	@Override
	public void turnUpdate() {
		// If the player has removed the tracking enchant from the collar, update state.
		if (Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_tracking_active") == 1) {
			AbstractClothing collar = Main.game.getPlayer().getClothingInSlot(InventorySlot.NECK);
			boolean trackingPresent = collar != null
					&& collar.getClothingType().getId().equals("innoxia_neck_dogmeat_collar_engraved")
					&& collar.getEffects().stream().anyMatch(
							e -> e.getSecondaryModifier() == TFModifier.CLOTHING_TRACKING);
			if (!trackingPresent) {
				Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_tracking_active", 0);
				Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_tracking_removed", 1);
			}
		}

		// Evening home visit: if invited and it's between 19:00–22:00, join the player at home.
		if (Main.game.getDialogueFlags().getSavedLong("kate_home_visit_active") == 1) {
			int visitHour = Main.game.getHourOfDay();
			if (visitHour >= 19 && visitHour < 22) {
				boolean playerHome = Main.game.getPlayer().getWorldLocation().equals(WorldType.LILAYAS_HOUSE_FIRST_FLOOR)
						|| Main.game.getPlayer().getWorldLocation().equals(WorldType.LILAYAS_HOUSE_GROUND_FLOOR);
				boolean kateAlreadyThere = this.getWorldLocation().equals(WorldType.LILAYAS_HOUSE_FIRST_FLOOR)
						|| this.getWorldLocation().equals(WorldType.LILAYAS_HOUSE_GROUND_FLOOR);
				if (playerHome && !kateAlreadyThere) {
					this.setLocation(
							Main.game.getPlayer().getWorldLocation(),
							Main.game.getPlayer().getLocationPlace().getPlaceType(),
							false);
				}
			}
		}

		if (Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_schedule_active") == 1) {
			Dogmeat dogmeat = (Dogmeat) Main.game.getNpc(Dogmeat.class);
			// If Dogmeat is now a companion, skip the apartment visit logic entirely.
			if (dogmeat != null && Main.game.getPlayer().getCompanions().contains(dogmeat)) {
				// fall through to normal shop-presence logic below
			} else if (dogmeat != null
					&& this.getWorldLocation().equals(WorldType.getWorldTypeFromId("innoxia_dominion_kate_apartment"))) {

				long arrivalMinute = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_arrival_minute");
				long now           = Main.game.getMinutesPassed();

				if (arrivalMinute > 0 && now >= arrivalMinute) {
					if (now < arrivalMinute + 120) {
						// Within the visit window: fire any acts whose scheduled minute has passed.
						long actsRemaining = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_acts_remaining");
						long interval      = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_act_interval");
						long nextActMinute = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_next_act_minute");
						while (actsRemaining > 0 && now >= nextActMinute) {
							simulateOffscreenActs(dogmeat, 1);
							actsRemaining--;
							nextActMinute += interval;
						}
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_acts_remaining", actsRemaining);
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_next_act_minute", nextActMinute);
					} else {
						// Visit window expired: simulate remaining acts, then send both home.
						long actsRemaining = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_acts_remaining");
						simulateOffscreenActs(dogmeat, (int) actsRemaining);
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_acts_remaining", 0);
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_arrival_minute", 0);

						long offscreenCount = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_offscreen_count");
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_offscreen_count", offscreenCount + 1);

						this.setLocation(WorldType.SHOPPING_ARCADE, PlaceType.SHOPPING_ARCADE_KATES_SHOP, false);
						dogmeat.setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
					return; // Skip normal shop presence logic during/after visit this turn.
				}
			}
		}
		// Normal shop presence logic — skip if Kate is currently on a home visit or at her apartment.
		boolean onHomeVisit = Main.game.getDialogueFlags().getSavedLong("kate_home_visit_active") == 1
				&& (this.getWorldLocation().equals(WorldType.LILAYAS_HOUSE_FIRST_FLOOR)
						|| this.getWorldLocation().equals(WorldType.LILAYAS_HOUSE_GROUND_FLOOR));
		boolean atApartment = this.getWorldLocation().equals(WorldType.getWorldTypeFromId("innoxia_dominion_kate_apartment"));
		if (!onHomeVisit && !atApartment && !Main.game.getCharactersPresent().contains(this)) {
			if (Main.game.isExtendedWorkTime()) {
				this.returnToHome();
			} else {
				this.setLocation(WorldType.EMPTY, PlaceType.GENERIC_HOLDING_CELL, false);
			}
		}
	}
	
	@Override
	public void changeFurryLevel(){
	}
	
	@Override
	public DialogueNode getEncounterDialogue() {
		return null;
	}

	@Override
	public String getTraderDescription() {
		return "";
	}

	@Override
	public boolean isTrader() {
		return true;
	}

	@Override
	public boolean willBuy(AbstractCoreItem item) {
		return (item instanceof AbstractClothing)
				&& !item.getItemTags().contains(ItemTag.CONTRABAND_LIGHT)
				&& !item.getItemTags().contains(ItemTag.CONTRABAND_MEDIUM)
				&& !item.getItemTags().contains(ItemTag.CONTRABAND_HEAVY);
	}

	@Override
	public void endSex() {
		setPenisType(PenisType.NONE);
	}
	
	@Override
	public boolean isAbleToBeImpregnated() {
		return true;
	}
	
	@Override
	public boolean isAffectedBySleepingStatusEffect() {
		return true;
	}
	
	@Override
	public boolean isSleepingAtHour(int hour) {
		return this.isAtHome(); // Always sleeping when on home tile
	}

	@Override
	public void hourlyUpdate(int hour) {
		super.hourlyUpdate(hour);

		// Hour-22 departure for home visit.
		if (hour == 22
				&& Main.game.getDialogueFlags().getSavedLong("kate_home_visit_active") == 1) {
			this.setLocation(WorldType.SHOPPING_ARCADE, PlaceType.SHOPPING_ARCADE_KATES_SHOP, false);
			Main.game.getDialogueFlags().setSavedLong("kate_home_visit_active", 0);
		}

		// Hour-13 safety fallback: return both NPCs from apartment if still there.
		if (hour == 13) {
			Dogmeat dogmeatFallback = (Dogmeat) Main.game.getNpc(Dogmeat.class);
			if (this.getWorldLocation().equals(WorldType.getWorldTypeFromId("innoxia_dominion_kate_apartment"))) {
				this.setLocation(WorldType.SHOPPING_ARCADE, PlaceType.SHOPPING_ARCADE_KATES_SHOP, false);
			}
			if (dogmeatFallback != null
					&& dogmeatFallback.getWorldLocation().equals(WorldType.getWorldTypeFromId("innoxia_dominion_kate_apartment"))
					&& !Main.game.getPlayer().getCompanions().contains(dogmeatFallback)) {
				dogmeatFallback.setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
			}
		}

		if (Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_schedule_active") == 1) {
			Dogmeat dogmeat = (Dogmeat) Main.game.getNpc(Dogmeat.class);
			// If Dogmeat is a companion, skip the apartment visit schedule.
			if (dogmeat != null && !Main.game.getPlayer().getCompanions().contains(dogmeat)
					&& !dogmeat.getWorldLocation().equals(WorldType.EMPTY)) {
				if (hour == 11) {
					// Both head to Kate's apartment for the visit.
					this.setLocation(WorldType.getWorldTypeFromId("innoxia_dominion_kate_apartment"),
							PlaceType.getPlaceTypeFromId("innoxia_dominion_kate_apartment_bedroom"), false);
					dogmeat.setLocation(WorldType.getWorldTypeFromId("innoxia_dominion_kate_apartment"),
							PlaceType.getPlaceTypeFromId("innoxia_dominion_kate_apartment_bedroom"), false);

					long offscreenCount = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_offscreen_count");
					boolean hasBestiality = this.hasFetish(Fetish.FETISH_BESTIALITY);
					int actCount = Math.max(1, this.getOrgasmsBeforeSatisfied()
							+ Math.min((int)(offscreenCount / 4), 3)
							+ (hasBestiality                                    ? 1 : 0)
							+ (this.hasFetish(Fetish.FETISH_CUM_ADDICT)         ? 1 : 0)
							+ (this.hasFetish(Fetish.FETISH_SUBMISSIVE)         ? 1 : 0)
							+ (this.hasTraitActivated(Perk.NYMPHOMANIAC)        ? 1 : 0));

					// Divide the 2-hour window into (actCount+1) equal slots so acts are spaced evenly.
					long arrivalMinute = Main.game.getMinutesPassed();
					long interval      = 120L / (actCount + 1);
					Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_arrival_minute",  arrivalMinute);
					Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_act_interval",    interval);
					Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_acts_remaining",  actCount);
					Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_next_act_minute", arrivalMinute + interval);
				}
			}
		}
	}

	/**
	 * Simulate {@code actCount} off-screen sex acts between Kate and Dogmeat,
	 * applying all mechanical effects. Grants FETISH_BESTIALITY on vaginal acts.
	 */
	private void simulateOffscreenActs(Dogmeat dogmeat, int actCount) {
		if (actCount <= 0) {
			return;
		}

		long offscreenCount = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_offscreen_count");
		boolean hasBestiality = this.hasFetish(Fetish.FETISH_BESTIALITY);
		int vaginaThreshold = hasBestiality ? 1 : 2;
		int analThreshold   = hasBestiality ? 3 : 5;

		Map<SexAreaOrifice, Integer> orificeWeights = new LinkedHashMap<>();
		orificeWeights.put(SexAreaOrifice.MOUTH,
				4 + (this.hasFetish(Fetish.FETISH_ORAL_RECEIVING) ? 3 : 0)
				  + (this.hasFetish(Fetish.FETISH_CUM_ADDICT)     ? 2 : 0));
		if (offscreenCount >= vaginaThreshold) {
			orificeWeights.put(SexAreaOrifice.VAGINA,
					5 + (this.hasFetish(Fetish.FETISH_VAGINAL_RECEIVING) ? 3 : 0)
					  + (this.hasFetish(Fetish.FETISH_PREGNANCY)         ? 2 : 0)
					  + (this.hasFetish(Fetish.FETISH_IMPREGNATION)      ? 2 : 0)
					  + (this.hasFetish(Fetish.FETISH_BREEDER)           ? 3 : 0));
		}
		if (offscreenCount >= analThreshold) {
			orificeWeights.put(SexAreaOrifice.ANUS,
					3 + (this.hasFetish(Fetish.FETISH_ANAL_RECEIVING) ? 4 : 0));
		}

		for (int i = 0; i < actCount; i++) {
			SexAreaOrifice orifice = Util.getRandomObjectFromWeightedMap(orificeWeights);
			this.calculateGenericSexEffects(
					true, true, dogmeat,
					Subspecies.DOG_MORPH_GERMAN_SHEPHERD,
					Subspecies.DOG_MORPH_GERMAN_SHEPHERD,
					new SexType(SexParticipantType.NORMAL, orifice, SexAreaPenetration.PENIS),
					GenericSexFlag.NO_DESCRIPTION_NEEDED);
			this.ingestFluid(dogmeat, dogmeat.getCum(), orifice, dogmeat.getPenisRawOrgasmCumQuantity());

			// Grant bestiality fetish the first time a vaginal act occurs.
			if (orifice == SexAreaOrifice.VAGINA && !this.hasFetish(Fetish.FETISH_BESTIALITY)) {
				this.addFetish(Fetish.FETISH_BESTIALITY);
			}
		}
	}
	
	@Override
	public Value<Boolean, String> getItemUseEffects(AbstractItem item,  GameCharacter itemOwner, GameCharacter user, GameCharacter target) {
		if(user.isPlayer() && !target.isPlayer() && !target.isAsleep()) {
			if(item.isTypeOneOf("innoxia_pills_fertility", "innoxia_pills_broodmother")) {
				String useDesc = itemOwner.useItem(item, target, false, true);
				return new Value<>(true,
						"<p>"
							+ "Producing a "+item.getName(false, false)+" from your inventory, you pop it out of its plastic wrapper before pushing it into Kate's mouth."
							+ " She giggles as she happily swallows the little "+item.getColour(0).getName()+" pill, knowing that it's going to make her womb far more fertile."
						+ "</p>"
						+ useDesc);
				
			} else if(item.getItemType()==ItemType.PREGNANCY_TEST) {
				String useDesc = itemOwner.useItem(item, target, false, true);
				if(this.isPregnant()) {
					this.setCharacterReactedToPregnancy(user, true);
					String litterCount = Util.intToString(this.getPregnantLitter().getTotalLitterCount());
					return new Value<>(true,
							"<p>"
								+ "Producing "+item.getName(true, false)+" from your inventory, you pass it over Kate's tummy and take a look at the results..."
							+ "</p>"
							+ useDesc
							+"<p>"
								+ (this.isVisiblyPregnant()
									?"[kate.speechNoEffects(What were you expecting?)] Kate laughs, before rubbing her pregnant belly and winking at you. [kate.speech(You can already see I'm knocked up, can't you?)]"
									:"[kate.speechNoEffects(~Ooh!~ I'm pregnant!)]"
											+ (this.getPregnantLitter().getTotalLitterCount()>2
													?" Kate exclaims, before rubbing her belly and biting her lip. [kate.speechNoEffects(I've got "+litterCount+" kids in here? ~Mmm!~ My tummy's gonna be so big...)]"
													:" Kate moans, before rubbing her belly and winking at you. [kate.speech(I'm gonna have a big, round tummy soon!)]"))
							+ "</p>");
					
				} else {
					return new Value<>(true,
							"<p>"
									+ "Producing "+item.getName(true, false)+" from your inventory, you pass it over Kate's tummy and take a look at the results..."
							+ "</p>"
							+ useDesc
							+"<p>"
								+ (this.hasStatusEffect(StatusEffect.PREGNANT_0)
									?"[kate.speechNoEffects(~Aww!~ I'm not pregnant!)] Kate whines, before rubbing her belly and pouting at you. [kate.speech(Come on, [pc.name], there's plenty of time for you to change that!)]"
									:"[kate.speechNoEffects(What were you expecting? Of course I'm not going to be pregnant!)] Kate laughs, before rubbing her belly and biting her lip."
											+ " [kate.speech(Although there's plenty of time for you to change that...)]")
							+ "</p>");
				}
				
			} else {
				return new Value<>(false,
						"<p>"
							+ "You start to pull "+item.getItemType().getDeterminer()+" "+item.getName()+" out from your inventory, but Kate quickly kicks your hand away and frowns at you."
						+ "</p>");
			}
		}
		return super.getItemUseEffects(item, itemOwner, user, target);
	}
	
	@Override
	public String getCondomEquipEffects(AbstractClothingType condomClothingType, GameCharacter equipper, GameCharacter target, boolean rough) {
		if(Main.game.isInSex() && !target.isAsleep()) {
			if(!target.equals(equipper) && !target.isPlayer()) {
				if(condomClothingType.equals(ClothingType.getClothingTypeFromId("innoxia_penis_condom_webbing"))) {
					return null;
				}
				return "<p>"
							+ "Holding out a condom to [kate.name], you force [kate.herHim] to take it and put it on."
							+ " Quickly ripping it out of its little foil wrapper, [kate.she] rolls it down the length of [kate.her] [kate.cock+] as [kate.she] whines at you,"
							+ " [kate.speech(Do I really have to? It feels so much better without one...)]"
						+ "</p>";
			}
			if(target.equals(equipper) && target.isPlayer() && !this.isAsleep()) {
				AbstractClothing clothing = target.getClothingInSlot(InventorySlot.PENIS);
				if(clothing!=null && clothing.isCondom()) {
					target.unequipClothingIntoVoid(clothing, true, equipper);
					target.getInventory().resetEquipDescription();
				}
				if(condomClothingType.equals(ClothingType.getClothingTypeFromId("innoxia_penis_condom_webbing"))) {
					return UtilText.parse(equipper, target,
							"You direct your spinneret at your own [npc.cock], with the intention of weaving a silky web condom around it, but as Kate sees what it is you're about to do, she firmly slaps it away and giggles,"
							+ " [kate.speech(Don't do that! It's no fun if I don't get any cum!)]");
				}
				return "<p>"
							+ "As you pull out a condom, a worried frown flashes across Kate's face, "
							+ "[kate.speech(Oh! Erm, let me put that on for you!)]"
							+"<br/>"
							+ "Before you can react, Kate snatches the condom out of your hands, and with a devious smile, uses her sharp little canines to [style.colourBad(tear a big hole in it)]."
							+ " She laughs at your shocked reaction and declares, "
							+ "[kate.speech(It's no fun if I don't get any cum!)]"
						+ "</p>";
			}
		}
		return null;
	}
	
	
	// Dirty talk:

	/**
	 * @return A <b>non-formatted</b> String of this NPCs speech related to no ongoing penetration.
	 */
	@Override
	public String getDirtyTalkNoPenetration(GameCharacter target, boolean isPlayerDom){
		List<String> speech = new ArrayList<>();

		speech.add("Come on! Fuck me already!");
		speech.add("Please, let's get started!");
		speech.add("My little pussy needs you so bad!");

		String returnedLine = speech.get(Util.random.nextInt(speech.size()));
		return UtilText.parse(this, target, "[npc.speech("+returnedLine+")]");
	}
	
	private Tattoo getKatesGroinTattoo() {
		Tattoo tat = new Tattoo(
				TattooType.getTattooTypeFromId("innoxia_heartWomb_heart_womb"),
				PresetColour.CLOTHING_PINK,
				PresetColour.CLOTHING_PINK_LIGHT,
				PresetColour.CLOTHING_PURPLE,
				true,
				new TattooWriting(
						"Breed me!",
						PresetColour.CLOTHING_PINK_LIGHT,
						true,
						TattooWritingStyle.ITALICISED),
				new TattooCounter(
						TattooCounterType.CURRENT_PREGNANCY,
						TattooCountType.NUMBERS,
						PresetColour.CLOTHING_PINK_LIGHT,
						true,
						0));
		
		for(int i=0; i<10; i++) {
			tat.addEffect(new ItemEffect(ItemEffectType.TATTOO, TFModifier.CLOTHING_ATTRIBUTE, TFModifier.FERTILITY, TFPotency.MAJOR_BOOST, 0));
		}
		return tat;
	}
}
