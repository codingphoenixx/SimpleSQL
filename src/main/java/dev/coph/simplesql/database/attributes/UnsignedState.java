package dev.coph.simplesql.database.attributes;

/**
 * Represents the unsigned state attribute, which can indicate whether
 * an entity is active or inactive.
 * <p>
 * This enum is primarily intended for use in scenarios where a binary
 * state needs to be represented. For example, it can be used to designate
 * whether a particular feature, component, or account is in an ACTIVE
 * or INACTIVE state.
 * <p>
 * The possible values are:
 * - ACTIVE: Indicates the entity is in an active state.
 * - INACTIVE: Indicates the entity is in an inactive state.
 */
public enum UnsignedState {
    /**
     * Represents the active state in the {@code UnsignedState} enum.
     * <p>
     * This value indicates that an entity, feature, or component is in an
     * active or enabled state. Typically, this state is used in contexts where
     * a binary distinction needs to be made between active and inactive states.
     */
    ACTIVE,
    /**
     * Represents the inactive state in the {@code UnsignedState} enum.
     * <p>
     * This value indicates that an entity, feature, or component is in an
     * inactive or disabled state. Typically, this state is used in contexts
     * where a binary distinction needs to be made between active and inactive states.
     */
    INACTIVE
}
