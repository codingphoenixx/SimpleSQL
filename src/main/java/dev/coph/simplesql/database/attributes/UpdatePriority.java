package dev.coph.simplesql.database.attributes;

/**
 * Represents the priority level for updating operations.
 * The priority can be either {@code LOW} or {@code NORMAL}.
 * This enum can be used to differentiate the significance or urgency
 * of update operations in various contexts.
 */
public enum UpdatePriority {
    /**
     * Represents the lowest priority level in the {@link UpdatePriority} enumeration.
     * This value indicates tasks or updates of minimal urgency or importance,
     * which can be deferred or processed after higher-priority operations.
     */
    LOW,
    /**
     * Represents the default or standard priority level in the {@link UpdatePriority} enumeration.
     * This value is used to indicate updates of regular importance, where no specific urgency or
     * deferment is required. It is the middle-ground priority for update operations.
     */
    NORMAL
}
