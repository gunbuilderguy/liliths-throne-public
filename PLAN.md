# Plan: Rework Fetish Variants in condomdialogueV3.txt

## Two changes needed:

### 1. Add `returnStringAtRandom()` with 2 variants to every narrative line

Currently: fetish variant blocks only randomize the opening line, with the rest as fixed strings.
Target: Every narrative "beat" should use `UtilText.returnStringAtRandom()` with 2 variant lines.

**Pattern to follow (matching existing CUM_ADDICT/generic structure):**
```java
} else if(target.hasFetish(Fetish.FETISH_MASOCHIST)) {
    return UtilText.parse(target, user,
        "<p>"
            + UtilText.returnStringAtRandom(
                "Opener variant A.",
                "Opener variant B.")
            + UtilText.returnStringAtRandom(
                " Action variant A.",
                " Action variant B.")
            + UtilText.returnStringAtRandom(
                " Reaction variant A.",
                " Reaction variant B.")
            + "</p>"
            + target.ingestFluid(SexAreaOrifice.MOUTH, storedFluids));
```

**Blocks to rewrite (all non-slime fetish variant blocks):**

- ZERO_NONE: MASOCHIST (L18-27), SUBMISSIVE (L28-37), PURE_VIRGIN (L38-47)
  - Currently 1 random + 2 fixed lines → rewrite to 3x `returnStringAtRandom(2 variants)`
- ONE_TRICKLE: MASOCHIST (L77-86), SUBMISSIVE (L87-96), PURE_VIRGIN (L97-106)
  - Same pattern → 3x random(2)
- TWO_SMALL_AMOUNT: MASOCHIST (L136-), SUBMISSIVE, PURE_VIRGIN
  - Same → 3x random(2)
- THREE_AVERAGE: MASOCHIST, SUBMISSIVE, PURE_VIRGIN
  - Same → 3x random(2)
- FOUR_LARGE: MASOCHIST, SUBMISSIVE, PURE_VIRGIN
  - 4x random(2) due to more content
- FIVE_HUGE: MASOCHIST, SUBMISSIVE, PURE_VIRGIN
  - 4x random(2)
- SIX_EXTREME: MASOCHIST, SUBMISSIVE, PURE_VIRGIN
  - 4x random(2) + incorporate scent references
- SEVEN_MONSTROUS sub-tier 1 (<3000ml): MASOCHIST, SUBMISSIVE, PURE_VIRGIN
  - 5x random(2) + scent focus
- SEVEN_MONSTROUS sub-tier 2 (<10000ml): MASOCHIST, SUBMISSIVE, PURE_VIRGIN
  - 5x random(2) + heavy scent focus
- SEVEN_MONSTROUS sub-tier 3 (>=10000ml): MASOCHIST, SUBMISSIVE, PURE_VIRGIN
  - 6x random(2) + scent overwhelm/trance

**Total: 30 fetish variant blocks to rewrite.**

### 2. Scent progression for SIX_EXTREME and SEVEN_MONSTROUS

Starting at SIX_EXTREME, the scent of cum becomes a narrative element. The three fetish types should react to it differently:

**MASOCHIST:** The scent is intoxicating — they love how it assaults their senses. They actively breathe it in, the overwhelming aroma feeding their arousal. At SEVEN_MONSTROUS endpoint, the scent drives them into a daze of painful ecstasy.

**SUBMISSIVE:** They let the scent overpower them and submit to it. They don't fight the dizzying effect — they yield to it, letting it wash over them as part of their submission. At SEVEN_MONSTROUS endpoint, they surrender entirely to the scent, drinking mechanically.

**PURE_VIRGIN:** The scent is revolting/violating, an assault on their senses they desperately try to resist. But once their belly is visibly distorted from the volume, it becomes too much and they fall into a state of indifference/unconsciousness — the body continues but the mind has checked out.

**Scent escalation by tier:**
- SIX_EXTREME: Scent introduced as notable sensory element (1 mention, in the "bringing to lips" beat)
- SEVEN_MONSTROUS <3000ml: Scent becomes strong, starts affecting concentration (1-2 mentions)
- SEVEN_MONSTROUS <10000ml: Scent dominates, clouding mind and driving behavior (2-3 mentions)
- SEVEN_MONSTROUS >=10000ml: Scent overwhelms consciousness entirely, drives trance-like state (3+ mentions across multiple beats)

## Execution order

1. Rewrite ZERO_NONE through FIVE_HUGE (18 blocks) — add returnStringAtRandom(2) to each narrative line, keep content similar
2. Rewrite SIX_EXTREME (3 blocks) — add returnStringAtRandom(2) + introduce scent
3. Rewrite SEVEN_MONSTROUS sub-tier 1 (3 blocks) — returnStringAtRandom(2) + scent builds
4. Rewrite SEVEN_MONSTROUS sub-tier 2 (3 blocks) — returnStringAtRandom(2) + scent dominates
5. Rewrite SEVEN_MONSTROUS sub-tier 3 (3 blocks) — returnStringAtRandom(2) + scent trance
6. Commit and push
