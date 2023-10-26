package com.petproject.pokemoncardgenerator.model;

import java.util.Objects;

import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;

/**
 * Parameters, which user should set to generate a pokemon
 */
public class PokemonParameters {

	private PokemonElement element;
	private String pokemonConcept;

	public PokemonParameters() {
	}

	private PokemonParameters(Builder builder) {
		setElement(builder.element);
		setPokemonConcept(builder.pokemonConcept);
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getPokemonConcept() {
		return pokemonConcept;
	}

	public void setPokemonConcept(String pokemonConcept) {
		this.pokemonConcept = pokemonConcept;
	}

	@Override
	public String toString() {
		return "PokemonParameters{" + "element='" + element + '\'' + ", pokemonConcept='" + pokemonConcept + '\'' + '}';
	}

	public PokemonElement getElement() {
		return element;
	}

	public void setElement(PokemonElement element) {
		this.element = element;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PokemonParameters that = (PokemonParameters) o;

		if (element != that.element) {
			return false;
		}
		return Objects.equals(pokemonConcept, that.pokemonConcept);
	}

	@Override
	public int hashCode() {
		int result = element != null ? element.hashCode() : 0;
		result = 31 * result + (pokemonConcept != null ? pokemonConcept.hashCode() : 0);
		return result;
	}

	public static final class Builder {

		private PokemonElement element;
		private String pokemonConcept;

		private Builder() {
		}

		public Builder element(PokemonElement val) {
			element = val;
			return this;
		}

		public Builder pokemonConcept(String val) {
			pokemonConcept = val;
			return this;
		}

		public PokemonParameters build() {
			return new PokemonParameters(this);
		}
	}
}
