package com.health.util.event;

/**
 * Represents a user in the system.
 * This model follows the structure from JSONPlaceholder API.
 */
public record User(
        Long id,
        String name,
        String username,
        String email,
        Address address,
        String phone,
        String website,
        Company company
) {

    /**
     * Represents a user's address.
     */
    public record Address(
            String street,
            String suite,
            String city,
            String zipcode,
            Geo geo
    ) {}

    /**
     * Represents geographic coordinates.
     */
    public record Geo(
            String lat,
            String lng
    ) {}

    /**
     * Represents a company associated with a user.
     */
    public record Company(
            String name,
            String catchPhrase,
            String bs
    ) {}
}
