package es.degrassi.mmreborn.common.crafting.helper;

import java.util.Objects;
import javax.annotation.Nonnull;

// Basically a super fancy wrapped string.
public record ComponentSelectorTag(String tag) {
    public ComponentSelectorTag {
        if (tag == null || tag.isEmpty()) {
            throw new IllegalArgumentException("Tried to create tag object will null or empty tag!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentSelectorTag that = (ComponentSelectorTag) o;
        return Objects.equals(tag, that.tag);
    }

}
