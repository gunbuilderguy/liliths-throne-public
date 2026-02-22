package com.lilithsthrone.game.dialogue.npcDialogue.dominion;

import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.attributes.CorruptionLevel;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.npc.dominion.Dogmeat;
import com.lilithsthrone.game.character.npc.dominion.FeralStrayDog;
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
 * After the player kneels before him three times, they become his bitch;
 * subsequent encounters trigger random escalating events.
 *
 * Random event indices (stored in dogmeat_event_index):
 *   0 = Walkies   (he leads you for a walk)
 *   1 = Pack      (he's brought another stray; sex scene with two dogs)
 *   2 = Mounted   (he takes you directly; sex scene)
 *   3 = Knotted   (mounting + knotted drag aftermath)
 *   4 = Claimed   (he pins you against the wall and refuses to let you leave)
 *
 * @since 0.4.11.3
 * @version 0.4.11.3
 */
public class DogmeatDialogue {

	private static Dogmeat getDogmeat() {
		return Main.game.getNpc(Dogmeat.class);
	}

	private static FeralStrayDog getStray() {
		return Main.game.getNpc(FeralStrayDog.class);
	}

	/** True if this is the very first meeting with Dogmeat. */
	private static boolean isFirstMeeting;

	// -------------------------------------------------------------------------
	// ENCOUNTER
	// -------------------------------------------------------------------------

	public static final DialogueNode DOGMEAT_ENCOUNTER = new DialogueNode("A stray dog", ".", true) {

		@Override
		public int getSecondsPassed() {
			return 2 * 60;
		}

		@Override
		public void applyPreParsingEffects() {
			isFirstMeeting = !Main.game.getDialogueFlags().hasSavedLong("dogmeat_found");
			Main.game.getDialogueFlags().setSavedLong("dogmeat_found", Main.game.getMinutesPassed());

			if (getDogmeat().getPlayerSurrenderCount() >= 3) {
				int eventIndex = Util.random.nextInt(5);
				Main.game.getDialogueFlags().setSavedLong("dogmeat_event_index", eventIndex);

				// Pre-spawn the pack stray so it exists when getResponse() runs
				if (eventIndex == 1) {
					FeralStrayDog stray = getStray();
					stray.setLocation(Main.game.getPlayer(), true);
				}
			}
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
				long eventIndex = Main.game.getDialogueFlags().getSavedLong("dogmeat_event_index");
				String base = "<p>"
						+ "He's waiting. That's the impression you get as you round the corner — he was already watching the entrance of the alley,"
						+ " already knew you'd come. He rises before you've fully stopped walking, stretching once with languid confidence, then fixes you with those amber eyes."
						+ "</p>";
				if (eventIndex == 0) {
					return base + "<p>"
							+ "He pads deliberately past you toward the far end of the alley, then stops and looks back over his shoulder."
							+ " The message is clear: <i>follow.</i>"
							+ "</p>";
				} else if (eventIndex == 1) {
					return base + "<p>"
							+ "He's not alone. A second dog — larger, with a rough grey-and-brown coat — detaches itself from the shadows behind him,"
							+ " watching you with the same quiet, appraising intensity. Dogmeat's tail sweeps once, slow and deliberate."
							+ "</p>"
							+ "<p>"
							+ "He's brought a friend."
							+ "</p>";
				} else if (eventIndex == 2 || eventIndex == 3) {
					return base + "<p>"
							+ "His gaze drops to your hips, then returns to your face. He steps closer, nostrils flaring."
							+ " He is not here to go for a walk."
							+ "</p>";
				} else {
					return base + "<p>"
							+ "Instead of moving toward you, he simply sits, blocking your path with his bulk."
							+ " His amber eyes are steady. Patient. He isn't going anywhere, and his posture makes it clear:"
							+ " neither are you."
							+ "</p>";
				}

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

			// ---- Bitch event responses (count >= 3) ----
			if (count >= 3) {
				long eventIndex = Main.game.getDialogueFlags().getSavedLong("dogmeat_event_index");

				if (index == 1) {
					// Event-specific main response
					if (eventIndex == 0) {
						return new Response("Follow him",
								"Fall into step behind him and go wherever he leads."
										+ "<br/>[style.italicsSubmissive(You follow your master through the alley...)]",
								DOGMEAT_WALKIES,
								Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
								CorruptionLevel.THREE_DIRTY,
								null, null, null) {
							@Override
							public void effects() {
								getDogmeat().incrementPlayerSurrenderCount(1);
							}
						};

					} else if (eventIndex == 1) {
						// Pack: sex scene with Dogmeat + the stray
						return new ResponseSex("Submit to both",
								"Present yourself to your master and his companion, letting them take turns with you."
										+ "<br/>[style.italicsSex(You submit to both dogs, letting them use you as they please...)]",
								Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE, Fetish.FETISH_BESTIALITY),
								null,
								CorruptionLevel.FIVE_CORRUPT,
								null, null, null,
								true, false,
								new SMGeneric(
										Util.newArrayListOfValues((GameCharacter) getDogmeat(), (GameCharacter) getStray()),
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
										+ "They close in on you together — Dogmeat at your face, his companion pressing in from behind."
										+ " You find yourself bracketed between two large, warm bodies, each insisting on your full attention."
										+ "</p>") {
							@Override
							public void effects() {
								getDogmeat().incrementPlayerSurrenderCount(1);
								if (!Main.game.getPlayer().hasFetish(Fetish.FETISH_BESTIALITY)) {
									Main.game.getPlayer().addFetish(Fetish.FETISH_BESTIALITY);
								}
							}
						};

					} else if (eventIndex == 2) {
						// Mounted
						return new ResponseSex("Present yourself",
								"Get down and present yourself to him, letting him mount you."
										+ "<br/>[style.italicsSex(You offer yourself to your master...)]",
								Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE, Fetish.FETISH_BESTIALITY),
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
										+ "He mounts you heavily, his forelegs locking around your hips with practiced certainty."
										+ " His amber eyes are focused somewhere past your head — you are a surface for him to use, and he uses you thoroughly."
										+ "</p>") {
							@Override
							public void effects() {
								getDogmeat().incrementPlayerSurrenderCount(1);
								if (!Main.game.getPlayer().hasFetish(Fetish.FETISH_BESTIALITY)) {
									Main.game.getPlayer().addFetish(Fetish.FETISH_BESTIALITY);
								}
							}
						};

					} else if (eventIndex == 3) {
						// Knotted — same sex start, different post-sex
						return new ResponseSex("Present yourself",
								"Get down and present yourself to him, letting him mount you fully."
										+ "<br/>[style.italicsSex(You offer yourself to your master — there's no telling when he'll decide to let go...)]",
								Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE, Fetish.FETISH_BESTIALITY),
								null,
								CorruptionLevel.FOUR_LUSTFUL,
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
								DOGMEAT_KNOTTED_AFTERMATH,
								"<p>"
										+ "He mounts you with the same blunt confidence, driving forward until the broad knot of him catches and holds."
										+ " He is completely still for a moment — then begins to move, slow and deliberate, utterly unhurried."
										+ "</p>") {
							@Override
							public void effects() {
								getDogmeat().incrementPlayerSurrenderCount(1);
								if (!Main.game.getPlayer().hasFetish(Fetish.FETISH_BESTIALITY)) {
									Main.game.getPlayer().addFetish(Fetish.FETISH_BESTIALITY);
								}
							}
						};

					} else {
						// Claimed
						return new Response("Stay",
								"Let him keep you here. Don't fight it."
										+ "<br/>[style.italicsSubmissive(You stay where you are, because he won't let you leave anyway.)]",
								DOGMEAT_CLAIMED,
								Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE),
								CorruptionLevel.THREE_DIRTY,
								null, null, null) {
							@Override
							public void effects() {
								getDogmeat().incrementPlayerSurrenderCount(1);
							}
						};
					}

				} else if (index == 2) {
					return new Response("Push past him",
							"Assert yourself. You're not his."
									+ "<br/>[style.italicsSubmissive(He'll make his displeasure known — but you push through anyway.)]",
							DOGMEAT_RESIST) {
						@Override
						public void effects() {
							getDogmeat().setLocation(WorldType.DOMINION, PlaceType.DOMINION_BACK_ALLEYS, false);
						}
					};
				}

				return null;
			}

			// ---- Normal encounter responses (count < 3) ----
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
						// Introduce the bestiality fetish organically on first submission
						if (getDogmeat().getPlayerSurrenderCount() == 1
								&& !Main.game.getPlayer().hasFetish(Fetish.FETISH_BESTIALITY)) {
							Main.game.getPlayer().addFetish(Fetish.FETISH_BESTIALITY);
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
						if (!Main.game.getPlayer().hasFetish(Fetish.FETISH_BESTIALITY)) {
							Main.game.getPlayer().addFetish(Fetish.FETISH_BESTIALITY);
						}
					}
				};
			}

			return null;
		}
	};

	// -------------------------------------------------------------------------
	// KNEEL
	// -------------------------------------------------------------------------

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
						if (!Main.game.getPlayer().hasFetish(Fetish.FETISH_BESTIALITY)) {
							Main.game.getPlayer().addFetish(Fetish.FETISH_BESTIALITY);
						}
					}
				};
			}

			return null;
		}
	};

	// -------------------------------------------------------------------------
	// ADOPTION
	// -------------------------------------------------------------------------

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

	// -------------------------------------------------------------------------
	// AFTER SEX
	// -------------------------------------------------------------------------

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

	// -------------------------------------------------------------------------
	// EVENT: WALKIES
	// -------------------------------------------------------------------------

	public static final DialogueNode DOGMEAT_WALKIES = new DialogueNode("Walkies", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 20 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "You follow him. There's nothing else to do — he moves with quiet authority, pausing now and then"
					+ " to ensure you're keeping up, his amber gaze sweeping back with the calm expectation of something that has"
					+ " never once considered the possibility of being disobeyed."
					+ "</p>"
					+ "<p>"
					+ "He leads you in a long, circuitous route through the back alleys. Past the rubbish bins, around a crumbling"
					+ " wall, along the shadowed edge of a courtyard. At one point a pair of street merchants glance over and"
					+ " watch you follow a large dog in thoughtful silence. You don't meet their eyes."
					+ "</p>"
					+ "<p>"
					+ "Eventually he circles back to his usual spot. He sits. Looks at you. His tail sweeps once across the"
					+ " cobblestones — satisfied, unhurried, completely assured of where the two of you stand."
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
						"After that little walk, bringing him along feels natural.",
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
						"Go your separate ways — for now.",
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

	// -------------------------------------------------------------------------
	// EVENT: CLAIMED
	// -------------------------------------------------------------------------

	public static final DialogueNode DOGMEAT_CLAIMED = new DialogueNode("Claimed", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 30 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "He doesn't move from the spot he's chosen. He simply sits across your path and waits — massive, warm, and completely immovable."
					+ " When you try to edge around him, he shifts without effort, blocking your route again. He's not threatening."
					+ " He doesn't need to threaten. He just doesn't let you leave."
					+ "</p>"
					+ "<p>"
					+ "You end up sitting with your back against the alley wall, Dogmeat pressed solidly against your side. His breathing"
					+ " slows. He is spectacularly comfortable. This is, apparently, what he wanted: you, here, not going anywhere."
					+ " His property, resting where his property belongs."
					+ "</p>"
					+ "<p>"
					+ "Eventually — on his schedule, not yours — he rises, stretches, and pads aside. You're free to go."
					+ " He watches you leave with the expression of someone who knows exactly where you'll be the next time he wants you."
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
				return new Response("Follow him",
						"You're going to end up following him anyway. May as well make it official.",
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
				return new Response("Go your way",
						"Take your leave — now that he's given you permission.",
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

	// -------------------------------------------------------------------------
	// EVENT: KNOTTED AFTERMATH
	// -------------------------------------------------------------------------

	public static final DialogueNode DOGMEAT_KNOTTED_AFTERMATH = new DialogueNode("Knotted", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 15 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "He doesn't slow. Doesn't wait. The moment his finish overtakes him, the knot swells thick and unyielding,"
					+ " locking the two of you together. You are not going anywhere. He seems completely at peace with this."
					+ "</p>"
					+ "<p>"
					+ "He begins to move — not urgently, but with an unhurried, possessive confidence — dragging you with him"
					+ " as he paces a short distance across the alley. You have no choice but to scramble along with him, bent"
					+ " and ungainly, his property on a very intimate leash. He stops, shifts, drags you back the other way."
					+ " He is not trying to go anywhere in particular. He is just reminding you of the arrangement."
					+ "</p>"
					+ "<p>"
					+ "After a long stretch of this — long enough that your knees are sore and your face is hot — he finally"
					+ " stills. The swelling eases slowly. When it releases, he steps away without ceremony, tail swinging once."
					+ " He sits and looks at you with those amber eyes, entirely satisfied with himself."
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
						"After that, leaving him behind feels unlikely.",
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
						"Stagger off and pretend this alley doesn't exist.",
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

	// -------------------------------------------------------------------------
	// RESIST
	// -------------------------------------------------------------------------

	public static final DialogueNode DOGMEAT_RESIST = new DialogueNode("Pushing back", ".", false) {

		@Override
		public int getSecondsPassed() {
			return 5 * 60;
		}

		@Override
		public String getContent() {
			return "<p>"
					+ "You step forward. He shifts to block you. You step again — firmly, with intent — and press past him."
					+ " He lets out a low, displeased sound, deep in his chest, and for a moment his amber eyes are very sharp."
					+ "</p>"
					+ "<p>"
					+ "But he doesn't stop you. He watches you go with the patient, unhurried look of something that has all the time"
					+ " in the world. Whatever this cost you in his estimation, he's already decided you'll be back."
					+ "</p>"
					+ "<p>"
					+ "He's probably right."
					+ "</p>";
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Leave", "Walk away.", Main.game.getDefaultDialogue(false));
			}
			return null;
		}
	};
}
