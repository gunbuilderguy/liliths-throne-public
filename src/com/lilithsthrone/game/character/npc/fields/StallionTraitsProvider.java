package com.lilithsthrone.game.character.npc.fields;

import java.util.List;

/**
 * Implemented by NPCs at Sally's Stables to expose their stallion traits.
 *
 * @since 0.4.15
 * @version 0.4.15
 */
public interface StallionTraitsProvider {
	List<StallionTrait> getStallionTraits();
}
