package com.lilithsthrone.game.dialogue.places.dominion.shoppingArcade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lilithsthrone.game.PropertyValue;
import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.body.Antenna;
import com.lilithsthrone.game.character.body.Arm;
import com.lilithsthrone.game.character.body.Ass;
import com.lilithsthrone.game.character.body.BodyPartInterface;
import com.lilithsthrone.game.character.body.Breast;
import com.lilithsthrone.game.character.body.BreastCrotch;
import com.lilithsthrone.game.character.body.CoverableArea;
import com.lilithsthrone.game.character.body.Eye;
import com.lilithsthrone.game.character.body.Face;
import com.lilithsthrone.game.character.body.Hair;
import com.lilithsthrone.game.character.body.Horn;
import com.lilithsthrone.game.character.body.Penis;
import com.lilithsthrone.game.character.body.Tail;
import com.lilithsthrone.game.character.body.Tentacle;
import com.lilithsthrone.game.character.body.Torso;
import com.lilithsthrone.game.character.body.Vagina;
import com.lilithsthrone.game.character.body.Wing;
import com.lilithsthrone.game.character.body.coverings.AbstractBodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringCategory;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.game.character.body.tags.BodyPartTag;
import com.lilithsthrone.game.character.body.types.TailType;
import com.lilithsthrone.game.character.body.valueEnums.BodyHair;
import com.lilithsthrone.game.character.body.valueEnums.BodyMaterial;
import com.lilithsthrone.game.character.body.valueEnums.PiercingType;
import com.lilithsthrone.game.character.effects.StatusEffect;
import com.lilithsthrone.game.character.markings.TattooCounterType;
import com.lilithsthrone.game.character.markings.TattooType;
import com.lilithsthrone.game.character.npc.dominion.Kate;
import com.lilithsthrone.game.character.quests.Quest;
import com.lilithsthrone.game.character.quests.QuestLine;
import com.lilithsthrone.game.character.race.AbstractRace;
import com.lilithsthrone.game.character.race.Race;
import com.lilithsthrone.game.dialogue.DialogueFlagValue;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.game.dialogue.responses.ResponseSex;
import com.lilithsthrone.game.dialogue.responses.ResponseTrade;
import com.lilithsthrone.game.dialogue.utils.BodyChanging;
import com.lilithsthrone.game.dialogue.utils.CharacterModificationUtils;
import com.lilithsthrone.game.dialogue.utils.CosmeticsDialogue;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.game.inventory.InventorySlot;
import com.lilithsthrone.game.inventory.item.ItemType;
import com.lilithsthrone.game.sex.ImmobilisationType;
import com.lilithsthrone.game.sex.SexPace;
import com.lilithsthrone.game.sex.managers.universal.SMSitting;
import com.lilithsthrone.game.sex.positions.slots.SexSlotSitting;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.Util.Value;
import com.lilithsthrone.utils.colours.PresetColour;

/**
 * @since 0.1.66
 * @version 0.4
 * @author Innoxia
 */
public class SuccubisSecrets {

	public static InventorySlot invSlotTattooToRemove = null;
	
	public static Map<AbstractBodyCoveringType, Value<AbstractRace, List<String>>> coveringsNamesMap;
	
	private static StringBuilder descriptionSB;
	
	public static final int BASE_COSMETICS_COST = 200;
	public static final int BASE_PIERCINGS_COST = 25;
	public static final int BASE_HAIR_LENGTH_COST = 25;
	public static final int BASE_HAIR_STYLE_COST = 50;
	public static final int BASE_ANAL_BLEACHING_COST = 100;
	public static final int BASE_BODY_HAIR_COST = 50;
	
	public static final HashMap<AbstractBodyCoveringType, Integer> cosmeticCostsMap = Util.newHashMapOfValues(
			new Value<>(BodyCoveringType.MAKEUP_BLUSHER, 25),
			new Value<>(BodyCoveringType.MAKEUP_EYE_LINER, 25),
			new Value<>(BodyCoveringType.MAKEUP_EYE_SHADOW, 25),
			new Value<>(BodyCoveringType.MAKEUP_LIPSTICK, 25),
			new Value<>(BodyCoveringType.MAKEUP_NAIL_POLISH_FEET, 25),
			new Value<>(BodyCoveringType.MAKEUP_NAIL_POLISH_HANDS, 25));

	public static final HashMap<PiercingType, Integer> piercingCostsMap = Util.newHashMapOfValues(
			new Value<>(PiercingType.EAR, 10),
			new Value<>(PiercingType.LIP, 25),
			new Value<>(PiercingType.NAVEL, 25),
			new Value<>(PiercingType.NIPPLE, 50),
			new Value<>(PiercingType.NOSE, 25),
			new Value<>(PiercingType.PENIS, 100),
			new Value<>(PiercingType.TONGUE, 50),
			new Value<>(PiercingType.VAGINA, 100));
	
	public static void initCoveringsMap(GameCharacter target) {
		coveringsNamesMap = new LinkedHashMap<>();
		
		for(BodyPartInterface bp : target.getAllBodyParts()){
			if(bp.getBodyCoveringType(target)!=null
					&& !(bp instanceof Hair)
					&& !(bp instanceof Eye)) {
				
				String name = bp.getName(target);
				if(bp instanceof Torso) {
					name = "torso";
				} else if(bp instanceof Vagina) {
					name = "vagina";
				}
				
				boolean addBpi = true;
				// Check for parts not owned:
				if((bp instanceof Antenna && !target.hasAntennae())
						|| (bp instanceof Arm && !target.hasArms())
						|| (bp instanceof Breast && !target.hasNipples())
						|| (bp instanceof BreastCrotch && !target.hasBreastsCrotch())
						|| (bp instanceof Hair && !target.hasHair())
						|| (bp instanceof Horn && !target.hasHorns())
						|| (bp instanceof Penis && !target.hasPenisIgnoreDildo())
						|| (bp instanceof Tail && !target.hasTail())
						|| (bp instanceof Tentacle && !target.hasTentacle())
						|| (bp instanceof Vagina && !target.hasVagina())
						|| (bp instanceof Wing && !target.hasWings())) {
					addBpi = false;
				}
				AbstractRace race = bp.getType().getRace();
				if(addBpi) {
					AbstractBodyCoveringType coveringType = bp.getBodyCoveringType(target);
					if(bp instanceof Ass) {
						coveringType = BodyCoveringType.ANUS;
					} else if(bp instanceof Breast) {
						coveringType = BodyCoveringType.NIPPLES;
					} else if(bp instanceof BreastCrotch) {
						coveringType = BodyCoveringType.NIPPLES_CROTCH;
					}
					if(coveringsNamesMap.containsKey(coveringType)) {
						coveringsNamesMap.get(coveringType).getValue().add(name);
					} else {
						coveringsNamesMap.put(coveringType, new Value<>(race, Util.newArrayListOfValues(name)));
					}
					
					if(bp instanceof Face) {
						coveringType = BodyCoveringType.MOUTH;
						if(coveringsNamesMap.containsKey(coveringType)) {
							coveringsNamesMap.get(coveringType).getValue().add(name);
						} else {
							coveringsNamesMap.put(coveringType, new Value<>(race, Util.newArrayListOfValues(name)));
						}
						coveringType = BodyCoveringType.TONGUE;
						if(coveringsNamesMap.containsKey(coveringType)) {
							coveringsNamesMap.get(coveringType).getValue().add(name);
						} else {
							coveringsNamesMap.put(coveringType, new Value<>(race, Util.newArrayListOfValues(name)));
						}
					}
				}
			}
		}
		
		if(target.getTailType()==TailType.DEMON_HAIR_TIP && !coveringsNamesMap.containsKey(BodyCoveringType.HAIR_DEMON)) {
			coveringsNamesMap.put(BodyCoveringType.HAIR_DEMON, new Value<>(Race.DEMON, Util.newArrayListOfValues(BodyCoveringType.HAIR_DEMON.getName(target))));
		}
		
		if(target.hasNipples()) {
			coveringsNamesMap.putIfAbsent(BodyCoveringType.MILK, new Value<>(Race.NONE, Util.newArrayListOfValues("milk")));
		}
		if(target.hasPenisIgnoreDildo()) {
			coveringsNamesMap.putIfAbsent(BodyCoveringType.CUM, new Value<>(Race.NONE, Util.newArrayListOfValues("cum")));
		}
		if(target.hasVagina()) {
			coveringsNamesMap.putIfAbsent(BodyCoveringType.GIRL_CUM, new Value<>(Race.NONE, Util.newArrayListOfValues("girlcum")));
		}
		
		
		if(Main.getProperties().hasValue(PropertyValue.pubicHairContent) && target.getPubicHair()!=BodyHair.ZERO_NONE) {
			coveringsNamesMap.putIfAbsent(target.getPubicHairType().getType(), new Value<>(Race.NONE, new ArrayList<>()));
			coveringsNamesMap.get(target.getPubicHairType().getType()).getValue().add(UtilText.parse(target, "growing around [npc.namePos] pubic region"));
		}
		if(Main.getProperties().hasValue(PropertyValue.facialHairContent) && target.getFacialHair()!=BodyHair.ZERO_NONE) {
			coveringsNamesMap.putIfAbsent(target.getFacialHairType().getType(), new Value<>(Race.NONE, new ArrayList<>()));
			coveringsNamesMap.get(target.getFacialHairType().getType()).getValue().add(UtilText.parse(target, "covering [npc.namePos] face"));
		}
		if(Main.getProperties().hasValue(PropertyValue.bodyHairContent) && target.getUnderarmHair()!=BodyHair.ZERO_NONE) {
			coveringsNamesMap.putIfAbsent(target.getBodyHairCoveringType(), new Value<>(Race.NONE, new ArrayList<>()));
			coveringsNamesMap.get(target.getBodyHairCoveringType()).getValue().add(UtilText.parse(target, "growing in [npc.namePos] underarms"));
		}
		if(Main.getProperties().hasValue(PropertyValue.assHairContent) && target.getAssHair()!=BodyHair.ZERO_NONE) {
			coveringsNamesMap.putIfAbsent(target.getAssHairType().getType(), new Value<>(Race.NONE, new ArrayList<>()));
			coveringsNamesMap.get(target.getAssHairType().getType()).getValue().add(UtilText.parse(target, "growing around [npc.namePos] anus"));
		}
		
		// Alter the map for if the target's body is not made of flesh:
		if(BodyChanging.getTarget().getBodyMaterial()!=BodyMaterial.FLESH) {
			Map<AbstractBodyCoveringType, Value<AbstractRace, List<String>>> altMaterialCoveringsNamesMap = new LinkedHashMap<>();
			for(Entry<AbstractBodyCoveringType, Value<AbstractRace, List<String>>> entry : coveringsNamesMap.entrySet()) {
				if(entry.getKey().getCategory().isInfluencedByMaterialType()) {
					altMaterialCoveringsNamesMap.put(BodyCoveringType.getMaterialBodyCoveringType(BodyChanging.getTarget().getBodyMaterial(), entry.getKey().getCategory()), entry.getValue());
				} else {
					altMaterialCoveringsNamesMap.put(entry.getKey(), entry.getValue());
				}
			}
			coveringsNamesMap = altMaterialCoveringsNamesMap;
		}

		for(Entry<AbstractBodyCoveringType, Value<AbstractRace, List<String>>> entry : coveringsNamesMap.entrySet()) {
			if(entry.getKey().getCategory()==BodyCoveringCategory.ANUS) {
				entry.getValue().getValue().clear();
				entry.getValue().getValue().add("anus");
			} else if(entry.getKey().getCategory()==BodyCoveringCategory.MOUTH) {
				entry.getValue().getValue().clear();
				entry.getValue().getValue().add("mouth");
			} else if(entry.getKey().getCategory()==BodyCoveringCategory.NIPPLE) {
				entry.getValue().getValue().clear();
				entry.getValue().getValue().add("nipples");
			} else if(entry.getKey().getCategory()==BodyCoveringCategory.NIPPLE_CROTCH) {
				entry.getValue().getValue().clear();
				entry.getValue().getValue().add("crotch nipples");
			} else if(entry.getKey().getCategory()==BodyCoveringCategory.TONGUE) {
				entry.getValue().getValue().clear();
				entry.getValue().getValue().add("tongue");
			}
		}
	}
	
	public static Value<String, String> getCoveringTitleDescription(GameCharacter target, AbstractBodyCoveringType coveringType, List<String> areasList) {
		String title = Util.capitaliseSentence(coveringType.getNameTransformation(target));
		
		String description = "This is the "+coveringType.getName(target)+" that's currently covering [npc.namePos] "+Util.stringsToStringList(areasList, false)+".";
		
		if(coveringType.getCategory()==BodyCoveringCategory.FLUID) {
			description = "As its name would suggest, this is simply [npc.namePos] "+Util.stringsToStringList(areasList, false)+".";
			
		} else if(coveringType.getCategory()==BodyCoveringCategory.ANUS) {
			title = "Anus";
			description = "This is the skin that's currently covering [npc.namePos] anal rim. The secondary colour determines what [npc.her] anus's inner-walls look like.";
			
		} else if(coveringType.getCategory()==BodyCoveringCategory.VAGINA) {
			title = "Vagina";
			description = "This is the skin that's currently covering [npc.namePos] labia. The secondary colour determines what [npc.her] vagina's inner-walls look like.";
			
		} else if(coveringType.getCategory()==BodyCoveringCategory.PENIS) {
			title = "Penis";
			description = "This is the skin that's currently covering [npc.namePos] penis. The secondary colour determines what the inside of [npc.her] urethra looks like (if it's fuckable).";
			
		} else if(coveringType.getCategory()==BodyCoveringCategory.NIPPLE) {
			title = "Nipples";
			description = "This is the skin that's currently covering [npc.namePos] nipples and areolae. The secondary colour determines what [npc.her] nipples' inner-walls look like (if they are fuckable).";
			
		} else if(coveringType.getCategory()==BodyCoveringCategory.NIPPLE_CROTCH) {
			title = "Crotch Nipples";
			description = "This is the skin that's currently covering the nipples and areolae on [npc.namePos] [npc.crotchBoobs]. The secondary colour determines what [npc.her] nipples' inner-walls look like (if they are fuckable).";
			
		} else if(coveringType.getCategory()==BodyCoveringCategory.MOUTH) {
			title = "Lips & Throat";
			if(target.getFaceType().getTags().contains(BodyPartTag.FACE_BEAK)) {
				description = "This is the colour of [npc.namePos] beak. The secondary colour determines what the insides of [npc.her] mouth and throat look like.";
			} else {
				description = "This is the skin that's currently covering [npc.namePos] lips. The secondary colour determines what the insides of [npc.her] mouth and throat look like.";
			}
			
		} else if(coveringType.getCategory()==BodyCoveringCategory.TONGUE) {
			title = "Tongue";
			description = "This is the skin that's currently covering [npc.namePos] tongue.";
		
		} else if(Main.getProperties().hasValue(PropertyValue.pubicHairContent) && coveringType == target.getPubicHairType().getType()) {
			title = "Pubic "+coveringType.getName(target);
			description = "This is the "+coveringType.getName(target)+" that's currently "+Util.stringsToStringList(areasList, false)+".";
			
		} else if(Main.getProperties().hasValue(PropertyValue.facialHairContent) && coveringType == target.getFacialHairType().getType()) {
			title = "Facial "+coveringType.getName(target);
			description = "This is the "+coveringType.getName(target)+" that's currently "+Util.stringsToStringList(areasList, false)+".";
			
		} else if(Main.getProperties().hasValue(PropertyValue.bodyHairContent) && coveringType == target.getBodyHairCoveringType()) {
			title = "Body "+coveringType.getName(target);
			description = "This is the "+coveringType.getName(target)+" that's currently "+Util.stringsToStringList(areasList, false)+".";
		}
		
		return new Value<>(title, description);
	}
	
	public static int getBodyCoveringTypeCost(AbstractBodyCoveringType type) {
		if(cosmeticCostsMap.containsKey(type)) {
			return cosmeticCostsMap.get(type);
		}
		
		return BASE_COSMETICS_COST;
	}

	public static int getPiercingCost(PiercingType type) {
		if(piercingCostsMap.containsKey(type)) {
			return piercingCostsMap.get(type);
		}
		
		return BASE_PIERCINGS_COST;
	}
	
	private static Kate getKate() {
		return (Kate) Main.game.getNpc(Kate.class);
	}
	
	public static final DialogueNode EXTERIOR = new DialogueNode("Succubi's Secrets (Exterior)", "-", false) {

		@Override
		public String getContent() {
			return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "EXTERIOR");
		}

		@Override
		public String getResponseTabTitle(int index) {
			return ShoppingArcadeDialogue.getCoreResponseTab(index);
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			if(responseTab==0) {
				if (index == 1) {
					if(!Main.game.isExtendedWorkTime()) {
						return new Response("Enter", "'Succubi's Secrets' is currently closed, so you'll have to come back during opening hours if you wanted to take a look inside.", null);
						
					} else if(Main.game.getDialogueFlags().values.contains(DialogueFlagValue.kateIntroduced)) {
						return new Response("Enter", "Step inside Succubi's Secrets.", SHOP_BEAUTY_SALON_ENTER) {
							@Override
							public void effects() {
								BodyChanging.setTarget(Main.game.getPlayer());
							}
						};
						
					} else {
						return new Response("Enter", "Step inside Succubi's Secrets.", SHOP_BEAUTY_SALON) {
							@Override
							public void effects() {
								BodyChanging.setTarget(Main.game.getPlayer());
							}
						};
					}
				}
			}
			return ShoppingArcadeDialogue.getFastTravelResponses(responseTab, index);
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON");
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Wake her", "Wake the sleeping demon.", SHOP_BEAUTY_SALON_WAKE);
				
			} else if (index == 2) {
				return new Response("Watch", "Wait for the sleeping demon to wake up.", SHOP_BEAUTY_SALON_WATCH);

			} else if (index == 0) {
				return new Response("Leave", "Head back out to the Shopping Arcade.", EXTERIOR) {
					@Override
					public void effects() {
						Main.game.setResponseTab(0);
					}
				};
			}
			return null;
		}
	};
	public static final DialogueNode SHOP_BEAUTY_SALON_WAKE = new DialogueNode("Succubi's Secrets", "-", true, true) {
		@Override
		public void applyPreParsingEffects() {
			getKate().wakeUp();
		}
		@Override
		public String getContent() {
			return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_WAKE");
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("No thanks", "Tell her that you're not the sort of person who just has sex with random shopkeepers.", SHOP_BEAUTY_SALON_NO_THANKS);
				
			} else if (index == 2) {
				return new ResponseSex("Sex", "You can't resist the horny succubus's request...",
						true, true,
						new SMSitting(
								Util.newHashMapOfValues(new Value<>(Main.game.getPlayer(), SexSlotSitting.SITTING_BETWEEN_LEGS)),
								Util.newHashMapOfValues(new Value<>(getKate(), SexSlotSitting.SITTING))) {
							@Override
							public Map<GameCharacter, List<CoverableArea>> exposeAtStartOfSexMap() {
								return Util.newHashMapOfValues(new Value<>(getKate(), Util.newArrayListOfValues(CoverableArea.VAGINA)));
							}
						},
						null,
						null,
						AFTER_SEX,
						UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_WAKE_START_SEX"));
			}
			return null;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_WATCH = new DialogueNode("Succubi's Secrets", "-", true, true) {
		@Override
		public void applyPreParsingEffects() {
			getKate().wakeUp();
		}
		@Override
		public String getContent() {
			return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_WATCH");
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("No thanks", "Tell her that you're not the sort of person who just has sex with random shopkeepers.", SHOP_BEAUTY_SALON_NO_THANKS);
				
			} else if (index == 2) {
				return new ResponseSex("Fuck her", "Do as she says and start having sex with her.",
						true, true,
						new SMSitting(
								Util.newHashMapOfValues(new Value<>(Main.game.getPlayer(), SexSlotSitting.SITTING_BETWEEN_LEGS)),
								Util.newHashMapOfValues(new Value<>(getKate(), SexSlotSitting.SITTING))) {
							@Override
							public Map<GameCharacter, List<CoverableArea>> exposeAtStartOfSexMap() {
								return Util.newHashMapOfValues(new Value<>(getKate(), Util.newArrayListOfValues(CoverableArea.VAGINA)));
							}
						},
						null,
						null,
						AFTER_SEX,
						UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_WATCH_START_SEX"));
			}
			return null;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_NO_THANKS = new DialogueNode("Succubi's Secrets", "-", true, true) {

		@Override
		public String getContent() {
			return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_NO_THANKS");
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Services", "Read the brochure that Kate just handed to you.", SHOP_BEAUTY_SALON_MAIN){
					@Override
					public void effects() {
						Main.game.getDialogueFlags().values.add(DialogueFlagValue.kateIntroduced);
					}
				};
			}
			return null;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_ENTER = new DialogueNode("Succubi's Secrets", "-", true) {
		@Override
		public void applyPreParsingEffects() {
			getKate().wakeUp();
			Main.game.getTextStartStringBuilder().append(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_ENTER"));
			if(Main.game.getPlayer().isVisiblyPregnant()) {
				Main.game.getPlayer().setCharacterReactedToPregnancy(getKate(), true);
			}
			if(getKate().isVisiblyPregnant()) {
				getKate().setCharacterReactedToPregnancy(Main.game.getPlayer(), true);
			}
		}
		@Override
		public String getContent() {
			return "";//UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_ENTER");
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_MAIN = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_MAIN");
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}
	};
	
	private static Response getMainResponse(int index) {
		if(index == 1){
			return new ResponseTrade("Trade with Kate", "There's a separate leaflet tucked into the back of the brochure. It informs you that Kate is a registered distributor for a large jewellery firm.", getKate());
			
		} else if (index == 2) {
			if(!Main.game.getPlayer().isAbleToWearMakeup()) {
				return new Response("Makeup", "As your body is made of "+Main.game.getPlayer().getBodyMaterial().getName()+", Kate is unable to apply any makeup!", null);
				
			} else {
				return new Response("Makeup",
						"Kate offers a wide range of different cosmetic services, and several pages of the brochure are devoted to images displaying different styles and colours of lipstick, nail polish, and other forms of makeup.",
						SHOP_BEAUTY_SALON_COSMETICS);
			}

		} else if (index == 3) {
			return new Response("Hair",
					"There's a double-page spread of all the different dyes, styles, and lengths of hair that Kate's able to work with.",
					SHOP_BEAUTY_SALON_HAIR);

		} else if (index == 4) {
				return new Response("Piercings",
						"Kate offers a wide range of different piercings.",
						SHOP_BEAUTY_SALON_PIERCINGS);

		}  else if (index == 5) {
				return new Response("Eyes",
						"There's a special page near the front of the brochure, advertising Kate's ability to recolour a person's eyes."
						+ " Just like skin recolourings, this is quite demanding on her aura, and is therefore very expensive.", SHOP_BEAUTY_SALON_EYES);

		} else if (index == 6) {
			return new Response("Coverings",
					"There's a special page in the middle of the brochure, advertising Kate's special ability to harness the arcane in order to recolour a person's skin or fur."
					+ " Apparently, this is quite demanding on her aura, and is therefore very expensive.",
					SHOP_BEAUTY_SALON_SKIN_COLOUR){
				@Override
				public void effects() {
					initCoveringsMap(Main.game.getPlayer());
				}
			};

		} else if (index == 7) {
			return new Response("Other", "Kate can offer other miscellaneous services, such as anal bleaching.", SHOP_BEAUTY_SALON_OTHER);

		} else if (index == 8) {
			return new Response("Tattoos", "Most of the brochure is taken up with drawings and photographs displaying Kate's considerable artistic talents."
					+ " She's even able to apply arcane-enchanted tattoos, but they look to be very expensive...", SHOP_BEAUTY_SALON_TATTOOS);

		} else if (index == 9) {
			return new ResponseSex("Sex",
					"At the end of the brochure, there's an extremely lewd collection of pictures of Kate inserting her tail into her various orifices, with the suggestive caption 'Don't make me do it myself...'",
					true, true,
					new SMSitting(
							Util.newHashMapOfValues(new Value<>(Main.game.getPlayer(), SexSlotSitting.SITTING_BETWEEN_LEGS)),
							Util.newHashMapOfValues(new Value<>(getKate(), SexSlotSitting.SITTING))) {
						@Override
						public Map<GameCharacter, List<CoverableArea>> exposeAtStartOfSexMap() {
							return Util.newHashMapOfValues(new Value<>(getKate(), Util.newArrayListOfValues(CoverableArea.VAGINA)));
						}
					},
					null,
					null,
					AFTER_SEX_REPEATED,
					UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_MAIN_SEX"));
			
		} else if(index==10) {
			return new ResponseSex("Sleep sex",
					"As she's such a deep sleeper, you're confident that you'd be able to fuck Kate without waking her up."
							+ " All you'd need to do is wait for her to fall asleep and be gentle with her...",
					true, false,
					new SMSitting(
							Util.newHashMapOfValues(new Value<>(Main.game.getPlayer(), SexSlotSitting.SITTING_BETWEEN_LEGS)),
							Util.newHashMapOfValues(new Value<>(getKate(), SexSlotSitting.SITTING))) {
						@Override
						public Map<GameCharacter, List<CoverableArea>> exposeAtStartOfSexMap() {
							return Util.newHashMapOfValues(new Value<>(getKate(), Util.newArrayListOfValues(CoverableArea.VAGINA)));
						}
						@Override
						public SexPace getStartingSexPaceModifier(GameCharacter character) {
							if(character.isPlayer()) {
								return SexPace.DOM_GENTLE;
							}
							return super.getStartingSexPaceModifier(character);
						}
						@Override
						public Map<ImmobilisationType, Map<GameCharacter, Set<GameCharacter>>> getStartingCharactersImmobilised() {
							Map<ImmobilisationType, Map<GameCharacter, Set<GameCharacter>>> map = new HashMap<>();
							map.put(ImmobilisationType.SLEEP, new HashMap<>());
							map.get(ImmobilisationType.SLEEP).put(Main.game.getPlayer(), Util.newHashSetOfValues(getKate()));
							return map;
						}
					},
					null,
					null,
					AFTER_SEX_REPEATED,
					UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_MAIN_SEX_SLEEP")){
				@Override
				public void effects() {
					getKate().addStatusEffect(StatusEffect.SLEEPING_HEAVY, -1);
				}
			};
			
		} else if (index == 11
				&& Main.game.getPlayer().hasQuest(QuestLine.SIDE_BUYING_BRAX)
				&& Main.game.getPlayer().getQuest(QuestLine.SIDE_BUYING_BRAX)==Quest.BUYING_BRAX_START
				&& !Main.game.getPlayer().hasItemType(ItemType.CANDI_PERFUMES)) {
			if(Main.game.getPlayer().getMoney()<500) {
				return new Response("Candi's perfume", "You need at least 500 flames in order to pay for Candi's perfume!", null);
			}
			return new Response("Candi's perfume", "Tell Kate that you're here to collect Candi's order of perfume.", SHOP_BEAUTY_SALON_CANDI_PERFUME) {
				@Override
				public void effects() {
					Main.game.getTextEndStringBuilder().append(Main.game.getPlayer().incrementMoney(-500));
					Main.game.getTextEndStringBuilder().append(Main.game.getPlayer().addItem(Main.game.getItemGen().generateItem(ItemType.CANDI_PERFUMES), false));
					Main.game.getTextEndStringBuilder().append(Main.game.getPlayer().setQuestProgress(QuestLine.SIDE_BUYING_BRAX, Quest.BUYING_BRAX_DELIVER_PERFUME));
				}
			};
			
		} else if (index == 12
				&& Main.game.getDialogueFlags().getSavedLong("dogmeat_collar_state") == 1) {
			if (Main.game.getPlayer().getMoney() < 200) {
				return new Response("Re-engrave collar",
						"You pull out the worn leather collar. Kate looks it over and names her price — 200 flames."
								+ " You don't have enough money right now.",
						null);
			}
			return new Response("Re-engrave collar",
					"Show Kate the worn collar and ask her to re-engrave the tag:"
							+ " your name on the front, 'Property of: Dogmeat' on the back."
							+ "<br/>[style.italicsMoney(This will cost 200 flames.)]",
					DOGMEAT_COLLAR_DIALOGUE);

		} else if (index == 13
				&& Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_tracking_active") == 1) {
			if (Main.game.getPlayer().getMoney() < 100) {
				return new Response("Remove extra enchantment",
						"There's a second line worked into the inner band of the collar &mdash; an enchantment Kate didn't mention."
								+ " She'd remove it for 100 flames. You don't have enough right now.",
						null);
			}
			return new Response("Remove extra enchantment",
					"There's a second line worked into the inner band of the collar &mdash; an enchantment Kate didn't mention."
							+ " Ask her to take it off."
							+ "<br/>[style.italicsMoney(Removal fee: 100 flames.)]",
					DOGMEAT_COLLAR_REMOVE_TRACKING) {
				@Override
				public void effects() {
					Main.game.getTextEndStringBuilder().append(Main.game.getPlayer().incrementMoney(-100));
					Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_tracking_active", 0);
					Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_tracking_removed", 1);
				}
			};

		} else if (index == 14
				&& Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_tracking_removed") == 1
				&& Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_informed") != 1) {
			return new Response("Tell her about Dogmeat",
					"You removed the enchantment she hid on the collar."
							+ " If you want her to know about Dogmeat, you'll have to tell her yourself.",
					DOGMEAT_COLLAR_TELL_VOLUNTARILY);

		} else if (index == 15
				&& Main.game.getDialogueFlags().getSavedLong("kate_collar_state") == 1
				&& Main.game.getDialogueFlags().getSavedLong("kate_bring_active") != 1) {
			return new Response("Offer to take her to meet Dogmeat",
					"Kate said she wanted to meet him. Offer to take her there now.",
					DOGMEAT_COLLAR_ENGRAVING_WANTS_IN_AFTER);

		} else if (index == 0) {
			return new Response("Leave", "Leave Kate's shop, heading back out into the Shopping Arcade.", EXTERIOR){
				@Override
				public void effects() {
					Main.game.setResponseTab(0);
				}
			};
		}

		return null;
	}

	// =========================================================================
	// DOGMEAT COLLAR DIALOGUE CHAIN
	// =========================================================================

	/**
	 * Entry point: Kate sees the collar on the counter and asks about it.
	 * Branches into TELL (player is honest) or EVADE (player stays vague).
	 */
	public static final DialogueNode DOGMEAT_COLLAR_DIALOGUE = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return "<p>"
					+ "You pull out the worn collar and set it on the counter."
					+ " Kate opens one eye &mdash; she had apparently been dozing."
					+ " She stares at the collar. Then at you. Then at the collar again."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(That's a dog collar.)]")
					+ "</p>"
					+ "<p>"
					+ "You tell her what you need: your name on the front, <i>'Property of: Dogmeat'</i> on the back."
					+ "</p>"
					+ "<p>"
					+ "There is a pause. Kate picks up the collar and holds it close, squinting at the scratched-out tag."
					+ " Her thumb runs along the inside of the band &mdash; once, slowly."
					+ " She puts it back down. Her tail sways, once."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Y'know, in three hundred and sixty-one years, I've had some weird requests...)]")
					+ " She looks up at you."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Is this seriously who's been keeping you busy? A dog?)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Tell her the whole story",
						"Tell Kate that the collar belongs to a dog named Dogmeat, and what he means to you.",
						DOGMEAT_COLLAR_TELL);
			}
			if (index == 2) {
				return new Response("Keep it vague",
						"Let Kate assume what she wants. Don't give details.",
						DOGMEAT_COLLAR_EVADE);
			}
			return null;
		}
	};

	/**
	 * Player tells Kate the truth. She gets flustered, curious, and a little too interested.
	 * Sets kate_dogmeat_informed and kate_dogmeat_schedule_active on confirm.
	 */
	public static final DialogueNode DOGMEAT_COLLAR_TELL = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return "<p>"
					+ "You tell her. The size of him, where you found him, what he is. What the collar means."
					+ "</p>"
					+ "<p>"
					+ "Kate listens. For once she isn't half-asleep &mdash; she's very still, the collar still in her hand."
					+ " When you finish, she's quiet for a moment."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(...Like, an actual dog.)]")
					+ "</p>"
					+ "<p>"
					+ "Not a question. She's just saying it out loud."
					+ "</p>"
					+ "<p>"
					+ "She picks up the collar again. Brings it close. You watch her breathe in, just once, and see"
					+ " the precise moment she realises she's doing it because she can still smell him on the leather."
					+ " Her expression goes somewhere complicated &mdash; a little caught-out, a little flushed &mdash;"
					+ " and she sets it back down with slightly more care than necessary."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Okay.)]")
					+ " A pause."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Okay, that's... Y'know what, I don't judge.)]")
					+ "</p>"
					+ "<p>"
					+ "She sits up properly for the first time since you walked in and reaches for her engraving tools,"
					+ " and it's very obvious she's thinking about something else and failing to pretend otherwise."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Is he... good?)]")
					+ " She says it without looking at you, already setting the needle to the steel."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Like, is he actually...)]")
					+ " She waves the stylus vaguely."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Y'know.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Yes. Very.",
						"Let her draw her own conclusions.",
						DOGMEAT_COLLAR_ENGRAVING_HAIR_REACTION) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_informed", 1);
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_schedule_active", 1);
					}
				};
			}
			return null;
		}
	};

	/**
	 * Player stays vague. Kate quietly adds a hidden tracking enchantment during engraving.
	 * Sets kate_dogmeat_tracking_active. Schedule activates when player next visits Dogmeat.
	 */
	public static final DialogueNode DOGMEAT_COLLAR_EVADE = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return "<p>"
					+ "You give her the short version: it's personal, it's for someone who matters to you,"
					+ " that's all she needs to know."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Sure. Whatever.)]")
					+ "</p>"
					+ "<p>"
					+ "Kate picks up the collar and reaches for her engraving tools, and you're fairly sure that's the end of it."
					+ "</p>"
					+ "<p>"
					+ "It isn't quite."
					+ "</p>"
					+ "<p>"
					+ "She works quickly, efficiently &mdash; the main line, the names, clean and competent. Standard."
					+ " Then, so briefly you almost miss it, the needle makes a second pass along the inner edge of the band."
					+ " The line it traces is barely a hairsbreadth, following the curve of the first like a shadow,"
					+ " worked into the grain of the leather where you'd have to know what you were looking for to find it."
					+ "</p>"
					+ "<p>"
					+ "Kate knows what she's looking for."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(There.)]")
					+ " She holds the collar out."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Keyed to you, so additions are cheaper if you come back.)]")
					+ "</p>"
					+ "<p>"
					+ "[style.italicsBad(You notice a faint second line etched into the inner band of the collar."
					+ " It looks like some kind of enchantment. Kate doesn't mention it.)]"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Take it",
						"Pay Kate and take the engraved collar.",
						DOGMEAT_COLLAR_ENGRAVING_HAIR_REACTION) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_tracking_active", 1);
					}
				};
			}
			return null;
		}
	};

	/**
	 * Collar engraving complete. Prefixed with Kate's reaction to the hair discovery
	 * (stored in kate_hair_choice), then shows the engraved collar going on.
	 * Leads into the "wants in" beat.
	 */
	public static final DialogueNode DOGMEAT_COLLAR_ENGRAVING = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			String playerName = Main.game.getPlayer().getName();
			boolean kateTold = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_informed") == 1;
			long hairChoice = Main.game.getDialogueFlags().getSavedLong("kate_hair_choice");

			String hairReact;
			if (hairChoice == 1) {
				hairReact = "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(You have a dog.)]")
						+ " Kate stares. A pause."
						+ " "
						+ UtilText.parse(getKate(), "[npc.speech(And he &mdash;)]")
						+ " She stops. Her tail flicks once, sharply."
						+ " "
						+ UtilText.parse(getKate(), "[npc.speech(Okay.)]")
						+ "</p>"
						+ "<p>"
						+ "She picks the tools back up. The subject appears closed."
						+ " Her ears, however, have rotated slightly."
						+ "</p>";
			} else if (hairChoice == 2) {
				hairReact = "<p>"
						+ "Kate holds the silence for a long moment. Then: "
						+ UtilText.parse(getKate(), "[npc.speech(Huh.)]")
						+ "</p>"
						+ "<p>"
						+ "She picks the tools back up. Her expression is unreadable."
						+ " Her tail is doing something complicated."
						+ "</p>";
			} else {
				hairReact = "";
			}

			return hairReact
					+ "<p>"
					+ "When she slides it back across the counter, you hold it up to the light:"
					+ "</p>"
					+ "<p style='text-align:center;'>"
					+ "<i>Front: " + playerName + "</i>"
					+ "<br/>"
					+ "<i>Back: Property of: Dogmeat</i>"
					+ "</p>"
					+ "<p>"
					+ "You clasp it around your neck."
					+ "</p>"
					+ "<p>"
					+ (kateTold
						? UtilText.parse(getKate(), "[npc.speech(Huh.)]")
								+ " Kate watches you fasten it, and there's that expression again &mdash;"
								+ " the one doing a lot of work."
								+ " Her tail sways once, slow and thoughtful."
						: UtilText.parse(getKate(), "[npc.speech(Huh.)]")
								+ " She settles back into her chair, watching you."
								+ " Her tail has gone still.")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue",
						"",
						DOGMEAT_COLLAR_ENGRAVING_WANTS_IN);
			}
			return null;
		}
	};

	/**
	 * Kate notices dog hair on the collar during engraving — a beat before the work is done.
	 * Player can explain or stay silent; either way the engraving continues.
	 * kate_hair_choice: 1 = explained, 2 = said nothing.
	 */
	public static final DialogueNode DOGMEAT_COLLAR_ENGRAVING_HAIR_REACTION = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			boolean kateTold = Main.game.getDialogueFlags().getSavedLong("kate_dogmeat_informed") == 1;
			return "<p>"
					+ "Kate picks up the collar with practiced indifference. She's seen collars like this."
					+ " She already has a read on what it is. Her tools are out before she's fully looked at it."
					+ "</p>"
					+ "<p>"
					+ "Then she stops."
					+ "</p>"
					+ "<p>"
					+ "She squints at the leather. Holds it up. Rolls something between her fingers &mdash;"
					+ " a short, coarse strand still caught in the seam of the old tag."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(...This is actual dog hair.)]")
					+ "</p>"
					+ "<p>"
					+ "She puts the collar back down. Looks at you differently &mdash; not the look of someone who has seen everything,"
					+ " but someone who has just found the edge of it."
					+ "</p>"
					+ (kateTold
						? "<p>"
								+ "She already knows, of course. You told her. But knowing it in the abstract and holding"
								+ " a strand of his fur between her fingers are apparently different things."
								+ "</p>"
						: "");
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("\"His name is Dogmeat. He found me in the alley.\"",
						"Matter-of-fact. Give her the short version.",
						DOGMEAT_COLLAR_ENGRAVING) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_hair_choice", 1);
						Main.game.getTextEndStringBuilder().append(Main.game.getPlayer().incrementMoney(-200));
						Main.game.getDialogueFlags().setSavedLong("dogmeat_collar_state", 2);
						Main.game.getTextEndStringBuilder().append(
								Main.game.getPlayer().equipClothingFromNowhere(
										Main.game.getItemGen().generateClothing(
												"innoxia_neck_dogmeat_collar_engraved",
												PresetColour.CLOTHING_BLACK, false),
										true, Main.game.getPlayer()));
					}
				};
			}
			if (index == 2) {
				return new Response("(Say nothing. Just hold her gaze.)",
						"Let the silence answer for you.",
						DOGMEAT_COLLAR_ENGRAVING) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_hair_choice", 2);
						Main.game.getTextEndStringBuilder().append(Main.game.getPlayer().incrementMoney(-200));
						Main.game.getDialogueFlags().setSavedLong("dogmeat_collar_state", 2);
						Main.game.getTextEndStringBuilder().append(
								Main.game.getPlayer().equipClothingFromNowhere(
										Main.game.getItemGen().generateClothing(
												"innoxia_neck_dogmeat_collar_engraved",
												PresetColour.CLOTHING_BLACK, false),
										true, Main.game.getPlayer()));
					}
				};
			}
			return null;
		}
	};

	/**
	 * Kate says "He put that on you himself?" — player chooses how to answer.
	 * kate_wants_choice: 1 = "He shook it off", 2 = "More or less".
	 */
	public static final DialogueNode DOGMEAT_COLLAR_ENGRAVING_WANTS_IN = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(...So.)]")
					+ "</p>"
					+ "<p>"
					+ "She's looking at the collar, not at you."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(He put that on you himself?)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("\"He shook it off and offered it to me.\"",
						"",
						DOGMEAT_COLLAR_ENGRAVING_WANTS_IN_AFTER) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_wants_choice", 1);
						Main.game.getDialogueFlags().setSavedLong("kate_particular_response", 0);
					}
				};
			}
			if (index == 2) {
				return new Response("\"More or less.\"",
						"",
						DOGMEAT_COLLAR_ENGRAVING_WANTS_IN_AFTER) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_wants_choice", 2);
						Main.game.getDialogueFlags().setSavedLong("kate_particular_response", 0);
					}
				};
			}
			return null;
		}
	};

	/**
	 * Kate says "I want to meet him." Content varies based on prior choice and "particular" loop.
	 * Three choices: bring her now, defer, or the "he's particular" loop.
	 */
	public static final DialogueNode DOGMEAT_COLLAR_ENGRAVING_WANTS_IN_AFTER = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			long wantsChoice = Main.game.getDialogueFlags().getSavedLong("kate_wants_choice");
			long particular = Main.game.getDialogueFlags().getSavedLong("kate_particular_response");

			String react;
			if (particular == 1) {
				react = "<p>"
						+ UtilText.parse(getKate(), "[npc.speech(Good.)]")
						+ " Her tail sways once."
						+ " "
						+ UtilText.parse(getKate(), "[npc.speech(So am I.)]")
						+ " She waits."
						+ "</p>";
			} else if (wantsChoice == 1) {
				react = "<p>"
						+ "Kate absorbs this. Something in her expression shifts &mdash; not softness exactly,"
						+ " but attention. Real attention, the kind she doesn't bother with for most things."
						+ "</p>";
			} else {
				react = "<p>"
						+ "Kate's eyes move from the collar to your face. She seems to be deciding something."
						+ "</p>";
			}

			return react
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(I want to meet him.)]")
					+ "</p>"
					+ "<p>"
					+ "Not a question. Her voice has the same flat certainty she used to quote 200 flames."
					+ " This is just how things are going to be."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("\"I can take you there.\"",
						"Offer to bring her to Dogmeat's alley right now.",
						DOGMEAT_COLLAR_ENGRAVING_CLOSING_EARLY) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_collar_state", 1);
						Main.game.getDialogueFlags().setSavedLong("kate_bring_active", 1);
					}
				};
			}
			if (index == 2) {
				return new Response("\"Maybe another time.\"",
						"Not today. But you know where to find each other.",
						DOGMEAT_COLLAR_ENGRAVING_DEFERRED) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_collar_state", 1);
					}
				};
			}
			if (index == 3) {
				return new Response("\"He's... particular about people.\"",
						"Warn her gently.",
						DOGMEAT_COLLAR_ENGRAVING_WANTS_IN_AFTER) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_particular_response", 1);
					}
				};
			}
			return null;
		}
	};

	/**
	 * Kate doesn't push when the player defers. "You know where I am."
	 */
	public static final DialogueNode DOGMEAT_COLLAR_ENGRAVING_DEFERRED = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Mm.)]")
					+ " She doesn't push. She does, however, look at you for a beat longer than usual"
					+ " before settling back into her chair."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(You know where I am.)]")
					+ "</p>"
					+ "<p>"
					+ "[style.italicsQuest(You can bring Kate to meet Dogmeat at any time by talking to her in the shop.)]"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue",
						"",
						SHOP_BEAUTY_SALON_MAIN);
			}
			return null;
		}
	};

	/**
	 * Kate decides to close the shop early. Player heads to the alley.
	 */
	public static final DialogueNode DOGMEAT_COLLAR_ENGRAVING_CLOSING_EARLY = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return "<p>"
					+ "She gets up. One slow, unhurried movement that manages to be both catlike and deliberate."
					+ " She produces a coat from somewhere that looks indifferent and put-together in equal measure."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(I'm closing early.)]")
					+ "</p>"
					+ "<p>"
					+ "She has, apparently, never closed early. She doesn't comment on this."
					+ "</p>"
					+ "<p>"
					+ "[style.italicsQuest(Head to Dogmeat's alley in the back streets of Dominion.)]"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Go to the alley",
						"Lead the way to Dogmeat.",
						Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};

	/**
	 * Player asks Kate to remove the hidden tracking enchantment she added.
	 * Costs 100 flames (standard removal fee). Sets kate_dogmeat_tracking_removed.
	 */
	public static final DialogueNode DOGMEAT_COLLAR_REMOVE_TRACKING = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return "<p>"
					+ "You set the collar on the counter and point to the second line &mdash; the faint one worked into the inner"
					+ " edge of the band, barely visible unless you knew to look."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(...Hm.)]")
					+ "</p>"
					+ "<p>"
					+ "Kate doesn't deny it. She picks up the stylus &mdash; she had it in her pocket, which says something"
					+ " &mdash; and without any particular ceremony runs a quick counter-line along the enchantment until it dissolves."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Y'know, in my defense, you were being very cagey.)]")
					+ "</p>"
					+ "<p>"
					+ "She slides the collar back."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(There. Clean. And before you ask &mdash; I haven't followed it anywhere yet. You got here first.)]")
					+ "</p>"
					+ "<p>"
					+ "[style.italicsGood(The hidden enchantment has been removed from the collar.)]"
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}
	};

	/**
	 * Player voluntarily tells Kate about Dogmeat after having removed her tracking enchantment.
	 * Sets kate_dogmeat_informed and kate_dogmeat_schedule_active.
	 */
	public static final DialogueNode DOGMEAT_COLLAR_TELL_VOLUNTARILY = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return "<p>"
					+ "You tell her about Dogmeat. The size of him. Where he is."
					+ "</p>"
					+ "<p>"
					+ "Kate is quiet for a moment."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(Huh.)]")
					+ " A pause."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(So you knew about the tracking thing.)]")
					+ "</p>"
					+ "<p>"
					+ "Not a question. She's processing."
					+ "</p>"
					+ "<p>"
					+ UtilText.parse(getKate(), "[npc.speech(And you're telling me anyway.)]")
					+ "</p>"
					+ "<p>"
					+ "Her tail sways once. She looks away &mdash; then back."
					+ " "
					+ UtilText.parse(getKate(), "[npc.speech(Okay. Y'know, that's actually kind of &mdash; anyway. Thanks.)]")
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("You're welcome.",
						"",
						SHOP_BEAUTY_SALON_MAIN) {
					@Override
					public void effects() {
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_informed", 1);
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_schedule_active", 1);
						Main.game.getDialogueFlags().setSavedLong("kate_dogmeat_tracking_removed", 0);
					}
				};
			}
			return null;
		}
	};

	public static final DialogueNode SHOP_BEAUTY_SALON_CANDI_PERFUME = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getContent() {
			return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_CANDI_PERFUME");
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}
	};
	
	private static String getMoneyRemainingString() {
		return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_MONEY_REMAINING");
	}
	
	public static final DialogueNode SHOP_BEAUTY_SALON_COSMETICS = new DialogueNode("", "", true) {
		
		@Override
		public String getLabel() {
			return "Cosmetics";
		}

		@Override
		public String getHeaderContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_COSMETICS"));
			
			UtilText.nodeContentSB.append(getMoneyRemainingString());
			
			UtilText.nodeContentSB.append(
					CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE, BodyCoveringType.MAKEUP_BLUSHER, "Blusher", "Blusher (also called rouge) is used to colour the cheeks so as to provide a more youthful appearance, and to emphasise the cheekbones.", true, true)
							
					+CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE, BodyCoveringType.MAKEUP_LIPSTICK, "Lipstick", "Lipstick is used to provide colour, texture, and protection to the wearer's lips.", true, true)
		
					+CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE, BodyCoveringType.MAKEUP_EYE_LINER, "Eyeliner", "Eyeliner is applied around the contours of the eyes to help to define shape or highlight different features.", true, true)
		
					+CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE, BodyCoveringType.MAKEUP_EYE_SHADOW, "Eye shadow", "Eye shadow is used to make the wearer's eyes stand out or look more attractive.", true, true)
		
					+CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE, BodyCoveringType.MAKEUP_NAIL_POLISH_HANDS, "Nail polish", "Nail polish is used to colour and protect the nails on your [pc.hands].", true, true)
		
					+CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE, BodyCoveringType.MAKEUP_NAIL_POLISH_FEET, "Toenail polish", "Toenail polish is used to colour and protect the nails on your [pc.feet].", true, true));
			
			return UtilText.nodeContentSB.toString();
			
		}

		@Override
		public String getContent() {
			return "";
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}

		@Override
		public boolean reloadOnRestore() {
			return true;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_HAIR = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getHeaderContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_HAIR"));
			
			UtilText.nodeContentSB.append(getMoneyRemainingString());
			
			UtilText.nodeContentSB.append(
				CharacterModificationUtils.getKatesDivHairLengths(true, "Hair Length", "Hair length determines what hair styles you're able to have. The longer the hair, the more styles are available.")

				+CharacterModificationUtils.getKatesDivHairStyles(true, "Hair Style", "Hair style availability is determined by your hair length.")
				
				+CharacterModificationUtils.getKatesDivCoveringsNew(true, Main.game.getPlayer().getHairType().getRace(), Main.game.getPlayer().getCovering(Main.game.getPlayer().getHairCovering()).getType(),
						"[pc.Hair] Colour", "All hair recolourings are permanent, so if you want to change your colour again at a later time, you'll have to visit Kate again.", true, true));
			
			return UtilText.nodeContentSB.toString();
		}
		
		@Override
		public String getContent() {
			return "";
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}
		
		@Override
		public boolean reloadOnRestore() {
			return true;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_SKIN_COLOUR = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getHeaderContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_SKIN_COLOUR"));
			
			UtilText.nodeContentSB.append(getMoneyRemainingString());
			
			for(Entry<AbstractBodyCoveringType, Value<AbstractRace, List<String>>> entry : coveringsNamesMap.entrySet()){
				AbstractBodyCoveringType bct = entry.getKey();
				AbstractRace race = entry.getValue().getKey();
				GameCharacter target = Main.game.getPlayer();
				
				Value<String, String> titleDescription = getCoveringTitleDescription(target, bct, entry.getValue().getValue());
				
				UtilText.nodeContentSB.append(CharacterModificationUtils.getKatesDivCoveringsNew(
						true,
						race,
						bct,
						titleDescription.getKey(),
						UtilText.parse(target, titleDescription.getValue()),
						true,
						true));
			}
			
			return UtilText.nodeContentSB.toString();
		}
		
		@Override
		public String getContent() {
			return "";
		}
		
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}
		
		@Override
		public boolean reloadOnRestore() {
			return true;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_EYES = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getHeaderContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_EYES"));
			
			UtilText.nodeContentSB.append(getMoneyRemainingString());

			UtilText.nodeContentSB.append(
					CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Main.game.getPlayer().getEyeType().getRace(), Main.game.getPlayer().getEyeCovering(),
							"Irises", "The iris is the coloured part of the eye that's responsible for controlling the diameter and size of the pupil.", true, true)
		
					+CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE,
							Main.game.getPlayer().getBodyMaterial()!=BodyMaterial.FLESH
								?BodyCoveringType.getMaterialBodyCoveringType(Main.game.getPlayer().getBodyMaterial(), BodyCoveringCategory.EYE_PUPIL)
								:BodyCoveringType.EYE_PUPILS,
							"Pupils", "The pupil is a hole located in the centre of the iris that allows light to strike the retina.", true, true)
		
					+CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE,
							Main.game.getPlayer().getBodyMaterial()!=BodyMaterial.FLESH
								?BodyCoveringType.getMaterialBodyCoveringType(Main.game.getPlayer().getBodyMaterial(), BodyCoveringCategory.EYE_SCLERA)
								:BodyCoveringType.EYE_SCLERA,
							"Sclerae", "The sclera is the (typically white) part of the eye that surrounds the iris.", true, true));
			
			return UtilText.nodeContentSB.toString();
		}
		
		@Override
		public String getContent() {
			return "";
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}
		
		@Override
		public boolean reloadOnRestore() {
			return true;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_PIERCINGS = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getHeaderContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_PIERCINGS"));
			
			UtilText.nodeContentSB.append(getMoneyRemainingString());
			
			UtilText.nodeContentSB.append(CharacterModificationUtils.getKatesDivPiercings(false));
			
			return UtilText.nodeContentSB.toString();
		
		}

		@Override
		public String getContent() {
			return "";
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}

		@Override
		public boolean reloadOnRestore() {
			return true;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_OTHER = new DialogueNode("", "", true) {
		
		@Override
		public String getLabel() {
			return "Cosmetics";
		}

		@Override
		public String getHeaderContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_OTHER"));
			
			UtilText.nodeContentSB.append(getMoneyRemainingString());
			
			UtilText.nodeContentSB.append(
					CharacterModificationUtils.getKatesDivAnalBleaching()

//					+(Main.game.isFacialHairEnabled() || Main.game.isBodyHairEnabled() || Main.game.isPubicHairEnabled()
//							?CharacterModificationUtils.getKatesDivCoveringsNew(
//									true, Main.game.getPlayer().getBodyHairCoveringType(), "Body hair", "This is the hair that covers all areas other than the head.", true, true)
//							:"")
					
					+(Main.game.isFacialHairEnabled()
							? CharacterModificationUtils.getKatesDivFacialHair(true, "Facial hair", "The body hair found on your face." 
									+ (Main.game.isFemaleFacialHairEnabled() ? "" : " Feminine characters cannot grow facial hair."))
							:"")
					
					+(Main.game.isPubicHairEnabled()
							?CharacterModificationUtils.getKatesDivPubicHair(true, "Pubic hair", "The body hair found in the genital area; located on and around your sex organs and crotch.")
							:"")
					
					+(Main.game.isBodyHairEnabled()
							?CharacterModificationUtils.getKatesDivUnderarmHair(true, "Underarm hair", "The body hair found in your armpits.")
							:"")
					
					+(Main.game.isAssHairEnabled()
							?CharacterModificationUtils.getKatesDivAssHair(true, "Ass hair", "The body hair found around your asshole.")
							:"")
					);
			
			for(AbstractBodyCoveringType bct : BodyCoveringType.getAllBodyCoveringTypes()) {
				if((Main.game.isFacialHairEnabled() && Main.game.getPlayer().getFacialHairType().getType()==bct)
						|| (Main.game.isBodyHairEnabled() && Main.game.getPlayer().getUnderarmHairType().getType()==bct)
						|| (Main.game.isAssHairEnabled() &&  Main.game.getPlayer().getAssHairType().getType()==bct)
						|| (Main.game.isPubicHairEnabled() && Main.game.getPlayer().getPubicHairType().getType()==bct)) {
					UtilText.nodeContentSB.append(CharacterModificationUtils.getKatesDivCoveringsNew(
							true, Race.NONE, bct, "Body hair", "Your body hair.", true, true));
					
				}
			}
			
			return UtilText.nodeContentSB.toString();
		}

		@Override
		public String getContent() {
			return "";
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return getMainResponse(index);
		}

		@Override
		public boolean reloadOnRestore() {
			return true;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_TATTOOS = new DialogueNode("Succubi's Secrets", "-", true) {
		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "SHOP_BEAUTY_SALON_TATTOOS"));
			
			UtilText.nodeContentSB.append(getMoneyRemainingString());
			
			UtilText.nodeContentSB.append(CharacterModificationUtils.getKatesDivTattoos());
			
			return UtilText.nodeContentSB.toString();
		}
		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 8) {
				return new Response("Tattoos", "You are already looking at the tattoos available...", null);
				
			} else if(index==11) {
				return new Response("Confirmations: ",
						"Toggle tattoo removal confirmations."
							+ " When turned on, it will take two clicks to remove tattoos."
							+ " When turned off, it will only take one click.",
							SHOP_BEAUTY_SALON_TATTOOS) {
					@Override
					public String getTitle() {
						return "Confirmations: "+(Main.getProperties().hasValue(PropertyValue.tattooRemovalConfirmations)
									?"<span style='color:"+PresetColour.GENERIC_GOOD.toWebHexString()+";'>ON</span>"
									:"<span style='color:"+PresetColour.GENERIC_BAD.toWebHexString()+";'>OFF</span>");
					}
					
					@Override
					public void effects() {
						Main.getProperties().setValue(PropertyValue.tattooRemovalConfirmations, !Main.getProperties().hasValue(PropertyValue.tattooRemovalConfirmations));
						Main.getProperties().savePropertiesAsXML();
					}
				};
			}
			
			return getMainResponse(index);
		}

		@Override
		public boolean reloadOnRestore() {
			return true;
		}
	};
	
	public static final DialogueNode SHOP_BEAUTY_SALON_TATTOOS_ADD = new DialogueNode("Succubi's Secrets", "-", true) {

		@Override
		public String getLabel() {
			return "Succubi's Secrets - "+Util.capitaliseSentence(CharacterModificationUtils.tattooInventorySlot.getTattooSlotName()) +" Tattoo";
		}
		
		@Override
		public String getContent() {
			descriptionSB = new StringBuilder();
			
			descriptionSB.append(CharacterModificationUtils.getKatesDivTattoosAdd());
			
			return descriptionSB.toString();
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			int value = CharacterModificationUtils.tattoo.getValue();
			if (index == 1) {
				if(Main.game.getPlayer().getMoney()<value) {
					return new Response("Apply ("+UtilText.formatAsMoneyUncoloured(value, "span")+")", "You don't have enough money to get a tattoo!", null);
					
				} else if(CharacterModificationUtils.tattoo.getType().equals(TattooType.getTattooTypeFromId("innoxia_misc_none"))
						&& CharacterModificationUtils.tattoo.getWriting().getText().isEmpty()
						&& CharacterModificationUtils.tattoo.getCounter().getType()==TattooCounterType.NONE) {
					return new Response("Apply ("+UtilText.formatAsMoneyUncoloured(value, "span")+")", "You need to select a tattoo type, add some writing, or add a counter in order to make a tattoo!", null);
					
				} else {
					return new Response("Apply ("+UtilText.formatAsMoney(value, "span")+")", "Tell Kate that you'd like her to give you this tattoo.", SHOP_BEAUTY_SALON_TATTOOS) {
						@Override
						public void effects() {
							Main.game.getTextStartStringBuilder().append(Main.game.getPlayer().incrementMoney(-value));

							Main.mainController.getWebEngine().executeScript("document.getElementById('hiddenPField').innerHTML=document.getElementById('tattoo_name').value;");
							CharacterModificationUtils.tattoo.getWriting().setText(Main.mainController.getWebEngine().getDocument().getElementById("hiddenPField").getTextContent());
							CharacterModificationUtils.tattoo.setName(CharacterModificationUtils.tattoo.getType().getName());
							Main.game.getPlayer().addTattoo(CharacterModificationUtils.tattooInventorySlot, CharacterModificationUtils.tattoo);
						}
					};
				}
			
			} else if(index==2) {
				return new Response("Save/Load", "Save/Load tattoo presets.", CosmeticsDialogue.TATTOO_SAVE_LOAD) {
					@Override
					public void effects() {
						CosmeticsDialogue.initTattooSaveLoadDialogue(SHOP_BEAUTY_SALON_TATTOOS_ADD);
					}
				};
			
			} else if(index==0) {
				return new Response("Back", "Decide not to get this tattoo and return to the main selection screen.", SHOP_BEAUTY_SALON_TATTOOS);
			}
			
			return null;
		}

		@Override
		public boolean reloadOnRestore() {
			return true;
		}
	};
	
	// Sex:
	
	public static final DialogueNode AFTER_SEX = new DialogueNode("Finished", "", true, false) {
		@Override
		public String getDescription() {
			return "[pc.Step] back and allow Kate to recover.";
		}
		@Override
		public String getContent() {
			return UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "AFTER_SEX");
		}
		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Services", "Read the brochure that Kate just handed to you.", SuccubisSecrets.SHOP_BEAUTY_SALON_MAIN){
					@Override
					public void effects() {
						Main.game.getDialogueFlags().values.add(DialogueFlagValue.kateIntroduced);
					}
				};
			}
			return null;
		}
	};
	
	public static final DialogueNode AFTER_SEX_REPEATED = new DialogueNode("Finished", "", true, false) {
		@Override
		public String getDescription() {
			if(getKate().isAsleep()) {
				return "[pc.Step] back and leave Kate to continue sleeping.";
			}
			return "[pc.Step] back and allow Kate to recover.";
		}
		public void applyPreParsingEffects() {
			Main.game.appendToTextStartStringBuilder(UtilText.parseFromXMLFile("places/dominion/shoppingArcade/succubisSecrets", "AFTER_SEX_REPEATED"));
			getKate().wakeUp();
		}
		@Override
		public String getContent() {
			return "";
		}
		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Services", "Turn your attention back to the brochure.", SuccubisSecrets.SHOP_BEAUTY_SALON_MAIN);
			}
			return null;
		}
	};
	
}
