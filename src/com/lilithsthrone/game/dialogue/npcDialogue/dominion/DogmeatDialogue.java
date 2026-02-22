package com.lilithsthrone.game.dialogue.npcDialogue.dominion;

import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.attributes.CorruptionLevel;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.npc.dominion.Dogmeat;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.game.dialogue.responses.ResponseSex;
import com.lilithsthrone.game.sex.SexControl;
import com.lilithsthrone.game.sex.managers.universal.SMGeneric;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * Dialogue for discovering and adopting Dogmeat in the alleyways of Dominion.
 *
 * @since 0.4.11.3
 * @version 0.4.11.3
 */
public class DogmeatDialogue {

	private static Dogmeat getDogmeat() {
		return Main.game.getNpc(Dogmeat.class);
	}

	/** True if this encounter is the player's very first meeting with Dogmeat. */
	private static boolean isFirstMeeting;

	public static final DialogueNode DOGMEAT_ENCOUNTER = new DialogueNode("A stray dog", ".", true) {

		@Override
		public int getSecondsPassed() {
			return 2 * 60;
		}

		@Override
		public void applyPreParsingEffects() {
			isFirstMeeting = !Main.game.getDialogueFlags().hasSavedLong("dogmeat_found");
			Main.game.getDialogueFlags().setSavedLong("dogmeat_found", Main.game.getMinutesPassed());
		}

		@Override
		public String getContent() {
			int count = getDogmeat().getPlayerSurrenderCount();
			if (isFirstMeeting) {
				return "<p>"
						+ "Rounding a corner deeper into the alley, your eye is caught by a large dog sitting in the shadows between two rubbish bins."
						+ " It's a powerfully-built animal — broad shoulders, tan and black fur matted with the dust of Dominion's streets — watching you with steady, amber eyes."
						+ "</p>"
						+ "<p>"
						+ "The dog doesn't growl or cower. It simply holds your gaze, tail giving one slow, measured wag, as if it has been waiting for precisely <i>you</i> to come along."
						+ " A worn collar hangs loose around its neck, the tag too scratched to read."
						+ "</p>"
						+ "<p>"
						+ "Stray dogs aren't uncommon in Dominion, but something about this one feels different — calm, intelligent, and utterly unafraid."
						+ "</p>";

			} else if (count >= 3) {
				return "<p>"
						+ "He's waiting. That's the impression you get as you round the corner — he was already watching the entrance of the alley,"
						+ " already knew you'd come. He rises before you've fully stopped walking, stretching once with languid confidence, then fixes you with those amber eyes."
						+ "</p>"
						+ "<p>"
						+ "His tail doesn't wag. He simply waits, with the patience of something that knows it will be obeyed."
						+ "</p>";

			} else {
				return "<p>"
						+ "The dog is there again, sitting in the same spot between the rubbish bins."
						+ " He spots you before you spot him — his head turns, ears pricking forward, amber eyes finding yours across the alley."
						+ " His tail begins a slow, deliberate sweep."
						+ "</p>"
						+ "<p>"
						+ "He remembers you."
						+ "</p>";
			}
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			int count = getDogmeat().getPlayerSurrenderCount();

			if (index == 1) {
				if (!Main.game.getPlayer().canHaveMoreCompanions()) {
					return new Response("Take him",
							"You'd love to bring this dog along, but your party is already full.",
							null);
				}
				return new Response("Take him",
						"Crouch down and call the dog over. Invite him to travel with you.",
						DOGMEAT_ADOPTED) {
					@Override
					public void effects() {
						Dogmeat dogmeat = getDogmeat();
						dogmeat.setLocation(Main.game.getPlayer(), true);
						dogmeat.setPlayerKnowsName(true);
						Main.game.getPlayer().addCompanion(dogmeat);
					}
				};
			}

			if (index == 2) {
				return new Response("Leave him",
						"Leave the dog where he is and continue through the alley.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}

			if (index == 3) {
				String title = count >= 3 ? "Kneel (his bitch)" : "Kneel before him";
				String tooltip;
				if (count >= 3) {
					tooltip = "Lower yourself before your master, as you've come to know is your place."
							+ "<br/>[style.italicsSex(You are his. You both know it.)]";
				} else if (count == 2) {
					tooltip = "Lower yourself before the powerful stray, offering your submission."
							+ "<br/>[style.italicsSubmissive(You kneel before the dog, showing him he is dominant over you.)]"
							+ "<br/>[style.italicsSex(A third act of submission will confirm your place as his.)]";
				} else {
					tooltip = "Lower yourself before the powerful stray, offering your submission."
							+ "<br/>[style.italicsSubmissive(You kneel before the dog, showing him he is dominant over you.)]";
				}
				return new Response(title, tooltip, DOGMEAT_KNEEL,
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						CorruptionLevel.TWO_MESSY,
						null, null, null) {
					@Override
					public void effects() {
						getDogmeat().incrementPlayerSurrenderCount(1);
						if (getDogmeat().getPlayerSurrenderCount() >= 3) {
							Main.game.getDialogueFlags().setSavedLong("dogmeat_is_bitch", 1);
						}
						getDogmeat().setLocation(Main.game.getPlayer(), true);
					}
				};
			}

			if (index == 4 && count >= 3) {
				return new ResponseSex("Offer yourself",
						"Present yourself to your master, letting him take you as he pleases."
								+ "<br/>[style.italicsSex(You offer your body to the powerful dog, submitting to him completely...)]",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.THREE_DIRTY,
						null, null, null,
						true, false,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues(Main.game.getPlayer()),
								null,
								null) {
							@Override
							public SexControl getSexControl(GameCharacter character) {
								if (character.isPlayer()) {
									return SexControl.ONGOING_PLUS_LIMITED_PENETRATIONS;
								}
								return super.getSexControl(character);
							}
						},
						DOGMEAT_AFTER_SEX,
						"<p>"
								+ "The dog's nostrils flare as you lower yourself before him, his amber eyes sharpening with sudden, possessive interest."
								+ " He moves to you without hesitation, nosing along your neck — deliberate, claiming, entirely assured of his right to you."
								+ "</p>"
								+ "<p>"
								+ "This is what you are to him now."
								+ "</p>") {
					@Override
					public void effects() {
						getDogmeat().setLocation(Main.game.getPlayer(), true);
					}
				};
			}

			return null;
		}
	};

	public static final DialogueNode DOGMEAT_KNEEL = new DialogueNode("Kneeling", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			int count = getDogmeat().getPlayerSurrenderCount();
			if (count >= 3) {
				return "<p>"
						+ "You've done this enough times that lowering yourself before him feels almost natural."
						+ " He moves to you immediately, no hesitation, pressing the full weight of his broad muzzle against the top of your head."
						+ " A low sound rumbles in his chest — not a growl, but something deeper. A rumble of possession."
						+ "</p>"
						+ "<p>"
						+ "You stay there a long moment, fully aware of what this has become."
						+ " He has claimed you, in whatever way a dog can claim a person."
						+ " When you finally look up at those steady amber eyes, something about it feels permanent."
						+ "</p>";

			} else if (count == 2) {
				return "<p>"
						+ "His amber eyes fix on you the moment you start to lower yourself, as if he expected it."
						+ " He doesn't rush — just waits, tail giving one slow, steady sweep, as you come to your knees before him."
						+ " He presses his broad muzzle to your cheek and exhales warm breath against your skin, then steps back, satisfied."
						+ "</p>"
						+ "<p>"
						+ "Something in his gaze has shifted. He looks at you differently now — like something that belongs to him."
						+ "</p>";

			} else {
				return "<p>"
						+ "You slowly lower yourself to your knees before the powerful dog."
						+ " He watches you with calm, steady eyes as you bow your head, then steps forward to sniff along the side of your face."
						+ " After a long moment, he drags his broad tongue from your chin to your forehead — slow, deliberate, claiming."
						+ "</p>"
						+ "<p>"
						+ "You stay there, heart pounding, until he steps back and sits. He watches you with those amber eyes,"
						+ " as if gauging what you'll do next."
						+ "</p>";
			}
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			int count = getDogmeat().getPlayerSurrenderCount();

			if (index == 1) {
				if (!Main.game.getPlayer().canHaveMoreCompanions()) {
					return new Response("Take him",
							"You'd love to bring this dog along, but your party is already full.",
							null);
				}
				String adoptTitle = count >= 3 ? "Follow him" : "Take him";
				String adoptTooltip = count >= 3
						? "Fall into step behind him. He leads; you follow."
						: "He's clearly taken a liking to you. Invite him to travel with you.";
				return new Response(adoptTitle, adoptTooltip, DOGMEAT_ADOPTED) {
					@Override
					public void effects() {
						Dogmeat dogmeat = getDogmeat();
						dogmeat.setPlayerKnowsName(true);
						Main.game.getPlayer().addCompanion(dogmeat);
					}
				};
			}

			if (index == 2) {
				return new Response("Leave him",
						"Leave the dog here and continue on your way.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}

			if (index == 3 && count >= 3) {
				return new ResponseSex("Offer yourself",
						"Present yourself to your master, letting him take you as he pleases."
								+ "<br/>[style.italicsSex(You offer your body to him, submitting to him completely...)]",
						Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
						null,
						CorruptionLevel.THREE_DIRTY,
						null, null, null,
						true, false,
						new SMGeneric(
								Util.newArrayListOfValues((GameCharacter) getDogmeat()),
								Util.newArrayListOfValues(Main.game.getPlayer()),
								null,
								null) {
							@Override
							public SexControl getSexControl(GameCharacter character) {
								if (character.isPlayer()) {
									return SexControl.ONGOING_PLUS_LIMITED_PENETRATIONS;
								}
								return super.getSexControl(character);
							}
						},
						DOGMEAT_AFTER_SEX,
						"<p>"
								+ "The dog's nostrils flare as you position yourself for him, his amber eyes sharpening with sudden, possessive intent."
								+ " He moves to you without hesitation — deliberate, assured of his right to you."
								+ "</p>") {
					@Override
					public void effects() {
						getDogmeat().setLocation(Main.game.getPlayer(), true);
					}
				};
			}

			return null;
		}
	};

	public static final DialogueNode DOGMEAT_ADOPTED = new DialogueNode("New companion", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 0;
		}

		@Override
		public String getContent() {
			if (getDogmeat().getPlayerSurrenderCount() >= 3) {
				return "<p>"
						+ "You rise and take a step. He's already moving."
						+ " He falls into step just ahead of you — not beside you, but leading — glancing back once to make sure you're following."
						+ "</p>"
						+ "<p>"
						+ "<b>Dogmeat</b> has joined your party."
						+ "</p>";
			}
			return "<p>"
					+ "You crouch down and hold out your hand."
					+ " The dog sniffs it once, then pushes its broad head firmly against your palm."
					+ "</p>"
					+ "<p>"
					+ "You scratch behind his ears and he leans into it with full canine commitment."
					+ " When you stand and start walking, he falls into step beside you without hesitation —"
					+ " as if he's been your companion for years."
					+ "</p>"
					+ "<p>"
					+ "<b>Dogmeat</b> has joined your party."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue", "Head onward with your new companion.", Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};

	public static final DialogueNode DOGMEAT_AFTER_SEX = new DialogueNode("Aftermath", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 0;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "The dog steps back, panting softly, tail wagging in slow, satisfied sweeps."
					+ " He regards you with those calm amber eyes — no judgement, no expectation, only the quiet, uncomplicated warmth of a dog who has gotten exactly what he wanted."
					+ "</p>"
					+ "<p>"
					+ "He nudges your hand with his broad muzzle, then sits, watching to see what you'll do next."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				if (!Main.game.getPlayer().canHaveMoreCompanions()) {
					return new Response("Take him",
							"You'd love to bring this dog along, but your party is already full.",
							null);
				}
				return new Response("Take him",
						"He's clearly taken a liking to you. Invite him to travel with you.",
						DOGMEAT_ADOPTED) {
					@Override
					public void effects() {
						Dogmeat dogmeat = getDogmeat();
						dogmeat.setPlayerKnowsName(true);
						Main.game.getPlayer().addCompanion(dogmeat);
					}
				};
			}

			if (index == 2) {
				return new Response("Leave him",
						"Leave the dog here and continue on your way.",
						Main.game.getDefaultDialogue(false)) {
					@Override
					public void effects() {
						getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
					}
				};
			}

			return null;
		}
	};
}
