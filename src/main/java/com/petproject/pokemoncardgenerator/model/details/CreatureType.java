package com.petproject.pokemoncardgenerator.model.details;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreatureType {

	private String name;
	private List<PokemonDetail> details = new ArrayList<>();

	public CreatureType() {
	}

	private CreatureType(Builder builder) {
		setName(builder.name);
		setDetails(builder.details);
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PokemonDetail> getDetails() {
		return details;
	}

	public void setDetails(List<PokemonDetail> details) {
		this.details = details;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CreatureType that = (CreatureType) o;

		if (!Objects.equals(name, that.name)) {
			return false;
		}
		return Objects.equals(details, that.details);
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (details != null ? details.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CreatureType{" + "name='" + name + '\'' + ", details=" + details + '}';
	}

	public static final class Builder {

		private String name;
		private List<PokemonDetail> details = new ArrayList<>();

		private Builder() {
		}

		public Builder name(String val) {
			name = val;
			return this;
		}

		public Builder details(List<PokemonDetail> val) {
			details = val;
			return this;
		}

		public CreatureType build() {
			return new CreatureType(this);
		}
	}
}
