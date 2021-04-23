package ru.fintech.qa.models.pet;

import java.util.Objects;

public class TagsItem {
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagsItem)) {
            return false;
        }
        TagsItem tagsItem = (TagsItem) o;
        return id == tagsItem.id && Objects.equals(name, tagsItem.name);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, id);
    }

    private String name;
    private int id;

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }
}
