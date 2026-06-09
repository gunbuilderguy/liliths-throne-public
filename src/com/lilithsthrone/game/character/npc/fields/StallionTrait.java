package com.lilithsthrone.game.character.npc.fields;

import java.util.Collections;
import java.util.List;

import com.lilithsthrone.utils.Util;

/**
 * Independent traits that a stallion can have. Each rolls separately and
 * carries its own effect on semen value, yield, difficulty, or time.
 *
 * @since 0.4.15
 * @version 0.4.15
 */
public enum StallionTrait {

	// Value-affecting traits
	CHAMPION_BLOODLINE("champion bloodline", "Descended from a line of competition winners.", 5, 1f, 0, 0),
	PEDIGREE("pedigreed", "A well-documented breeding history fetches a premium.", 3, 1f, 0, 0),
	RARE_COAT("rare coat", "An unusual coat pattern that breeders prize.", 2, 1f, 0, 0),

	// Yield-affecting traits
	HIGH_YIELD("high-yield", "Produces an unusually large volume.", 0, 1.5f, 0, 0),
	HEAVY_HUNG("heavy-hung", "Oversized testicles produce more than usual.", 0, 1.3f, 0, 0),
	LOW_YIELD("low-yield", "Smaller output than most stallions.", 0, 0.7f, 0, 0),

	// Difficulty-affecting traits
	SKITTISH("skittish", "Spooks easily and needs extra calming.", 0, 1f, 1, 30),
	STUBBORN("stubborn", "Reluctant to get started without coaxing.", 0, 1f, 1, 20),
	AGGRESSIVE("aggressive", "Can be dangerous if not handled with care.", 0, 1f, 2, 30),
	PICKY("picky", "Refuses to perform without the right stimulation.", 0, 1f, 1, 0),

	// Time-affecting traits
	QUICK_FINISHER("quick finisher", "Doesn't take long to get going.", 0, 1f, 0, -30),
	SLOW_BURN("slow burn", "Takes his time before finishing.", 0, 1f, 0, 30),

	// Easy / positive traits
	DOCILE("docile", "Calm, well-behaved, easy to work with.", 0, 1f, -1, -10),
	WELL_TRAINED("well-trained", "Knows the routine and cooperates.", 0, 1f, -1, -10),
	EAGER("eager", "Enjoys the attention and gets going quickly.", 0, 1.2f, 0, -15);

	private final String name;
	private final String description;
	private final int valueBonus;
	private final float yieldMultiplier;
	private final int difficultyDelta;
	private final int timeDeltaMinutes;

	StallionTrait(String name, String description, int valueBonus, float yieldMultiplier, int difficultyDelta, int timeDeltaMinutes) {
		this.name = name;
		this.description = description;
		this.valueBonus = valueBonus;
		this.yieldMultiplier = yieldMultiplier;
		this.difficultyDelta = difficultyDelta;
		this.timeDeltaMinutes = timeDeltaMinutes;
	}

	public String getName() { return name; }
	public String getDescription() { return description; }
	public int getValueBonus() { return valueBonus; }
	public float getYieldMultiplier() { return yieldMultiplier; }
	public int getDifficultyDelta() { return difficultyDelta; }
	public int getTimeDeltaMinutes() { return timeDeltaMinutes; }

	public static List<StallionTrait> rollTraits() {
		List<StallionTrait> pool = Util.newArrayListOfValues(values());
		Collections.shuffle(pool, Util.random);
		return pool.subList(0, 2 + Util.random.nextInt(2));
	}
}
