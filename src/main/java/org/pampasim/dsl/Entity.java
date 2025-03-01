package org.pampasim.dsl;

/// Each entity instance will have their own state
/// Enum class, which needs to be referenced here somehow.
/// In a perfect world, that association would be encoded in the
/// type system, but Java don't roll like that. So for now I'll
/// try making a member be a reference to the Class instance of
/// the appropriate Enum.
/// [StackOverflow re: generic over Enums](https://stackoverflow.com/a/24466815)
public class Entity {
    protected final Class<? extends Enum<?>> stateEnumClass;
    protected HandlerDescriptionData handlers;

    public Entity(Class<? extends Enum<?>> stateEnumClass) {
        this.stateEnumClass = stateEnumClass;
    }
}
