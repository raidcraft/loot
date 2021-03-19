package de.raidcraft.loot.annotations;

import de.raidcraft.loot.RewardType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every {@link RewardType} must be annotated with this
 * annotation and provide a unique identifier that is used to reference the loot type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RewardInfo {

    /**
     * @return the unique identifier of this loot type
     */
    String value();
}
