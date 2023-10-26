package com.petproject.pokemoncardgenerator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.petproject.pokemoncardgenerator.model.details.Ability;
import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;
import com.petproject.pokemoncardgenerator.model.details.enums.Rarity;

public class Pokemon {

	private String name;
	private List<Ability> abilities = new ArrayList<>();
	private Integer hp;
	private PokemonElement element;
	private Rarity rarity;
	private Style style;
	private String imagePrompt;
	private String visualDescription;
	private String description;

	public Pokemon() {
	}

	private Pokemon(Builder builder) {
		setName(builder.name);
		setAbilities(builder.abilities);
		setHp(builder.hp);
		setElement(builder.element);
		setRarity(builder.rarity);
		setStyle(builder.style);
		setImagePrompt(builder.imagePrompt);
		setVisualDescription(builder.visualDescription);
		setDescription(builder.description);
	}

	public static Builder builder() {
		return new Builder();
	}

	public PokemonElement getElement() {
		return element;
	}

	public void setElement(PokemonElement element) {
		this.element = element;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Ability> getAbilities() {
		return abilities;
	}

	public void setAbilities(List<Ability> abilities) {
		this.abilities = abilities;
	}

	public Integer getHp() {
		return hp;
	}

	public void setHp(Integer hp) {
		this.hp = hp;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public Style getStyle() {
		return style;
	}

	public String getImagePrompt() {
		return imagePrompt;
	}

	public void setImagePrompt(String imagePrompt) {
		this.imagePrompt = imagePrompt;
	}

	public String getVisualDescription() {
		return visualDescription;
	}

	public void setVisualDescription(String visualDescription) {
		this.visualDescription = visualDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Pokemon pokemon = (Pokemon) o;

		if (!Objects.equals(name, pokemon.name)) {
			return false;
		}
		if (!Objects.equals(abilities, pokemon.abilities)) {
			return false;
		}
		if (!Objects.equals(hp, pokemon.hp)) {
			return false;
		}
		if (element != pokemon.element) {
			return false;
		}
		if (rarity != pokemon.rarity) {
			return false;
		}
		if (!Objects.equals(style, pokemon.style)) {
			return false;
		}
		if (!Objects.equals(imagePrompt, pokemon.imagePrompt)) {
			return false;
		}
		if (!Objects.equals(visualDescription, pokemon.visualDescription)) {
			return false;
		}
		return Objects.equals(description, pokemon.description);
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (abilities != null ? abilities.hashCode() : 0);
		result = 31 * result + (hp != null ? hp.hashCode() : 0);
		result = 31 * result + (element != null ? element.hashCode() : 0);
		result = 31 * result + (rarity != null ? rarity.hashCode() : 0);
		result = 31 * result + (style != null ? style.hashCode() : 0);
		result = 31 * result + (imagePrompt != null ? imagePrompt.hashCode() : 0);
		result = 31 * result + (visualDescription != null ? visualDescription.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Pokemon{" + "name='" + name + '\'' + ", abilities=" + abilities + ", hp=" + hp + ", element=" + element
				+ ", rarity=" + rarity + ", style=" + style + ", imagePrompt='" + imagePrompt + '\''
				+ ", visualDescription='" + visualDescription + '\'' + ", description='" + description + '\'' + '}';
	}

	public static final class Builder {

		private String name;
		private List<Ability> abilities = new ArrayList<>();
		private Integer hp;
		private PokemonElement element;
		private Rarity rarity;
		private Style style;
		private String imagePrompt;
		private String visualDescription;
		private String description;

		private Builder() {
		}

		public Builder name(String val) {
			name = val;
			return this;
		}

		public Builder abilities(List<Ability> val) {
			abilities = val;
			return this;
		}

		public Builder hp(Integer val) {
			hp = val;
			return this;
		}

		public Builder element(PokemonElement val) {
			element = val;
			return this;
		}

		public Builder rarity(Rarity val) {
			rarity = val;
			return this;
		}

		public Builder style(Style val) {
			style = val;
			return this;
		}

		public Builder imagePrompt(String val) {
			imagePrompt = val;
			return this;
		}

		public Builder visualDescription(String val) {
			visualDescription = val;
			return this;
		}

		public Builder description(String val) {
			description = val;
			return this;
		}

		public Pokemon build() {
			return new Pokemon(this);
		}
	}
}
