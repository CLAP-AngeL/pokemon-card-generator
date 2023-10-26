package com.petproject.pokemoncardgenerator.model.details;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;

public class Ability {

	private String name;
	private PokemonElement element;
	private int cost;
	private boolean isMixedElement;

	public Ability() {
	}

	private Ability(Builder builder) {
		setName(builder.name);
		setElement(builder.element);
		setCost(builder.cost);
		setMixedElement(builder.isMixedElement);
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * @return power of the ability, that is being rendered on the card
	 */
	public int getPower() {
		int basePower = cost * 10;
		int elementalBonusPoints = 0;

		if (!element.isNeutral()) {
			if (isMixedElement || cost == 1) {
				elementalBonusPoints = 10;
			} else {
				elementalBonusPoints = 20;
			}
		}

		return basePower + elementalBonusPoints;
	}

	/**
	 * @return list of elements of the ability, that is being rendered on the card. Elements can be mixed - that means they contain the original element and neutral
	 */
	public List<String> costsAsElements() {
		List<String> namesOfElements = new ArrayList<>();

		for (int i = 0; i < elementalCost(); i++) {
			namesOfElements.add(element.getElementName());
		}
		for (int i = 0; i < cost - elementalCost(); i++) {
			namesOfElements.add(PokemonElement.NEUTRAL.getElementName());
		}

		return namesOfElements;
	}

	public int elementalCost() {
		if (element.isNeutral()) {
			return 0;
		} else if (isMixedElement) {
			return (cost / 2);
		} else {
			return cost;
		}
	}

	/**
	 * @return a key that can be used to get ability name from pre loaded pool
	 */
	public String getKey() {
		String mixed = isMixedElement ? "mixed" : "pure";
		String extraPower = "standard";
		return String.format("%s_%s_%s_%s", element.getElementName(), cost, mixed, extraPower).toLowerCase();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PokemonElement getElement() {
		return element;
	}

	public void setElement(PokemonElement element) {
		this.element = element;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public boolean isMixedElement() {
		return isMixedElement;
	}

	public void setMixedElement(boolean mixedElement) {
		isMixedElement = mixedElement;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Ability ability = (Ability) o;

		if (cost != ability.cost) {
			return false;
		}
		if (isMixedElement != ability.isMixedElement) {
			return false;
		}
		if (!Objects.equals(name, ability.name)) {
			return false;
		}
		return element == ability.element;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (element != null ? element.hashCode() : 0);
		result = 31 * result + cost;
		result = 31 * result + (isMixedElement ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Ability{" + "name='" + name + '\'' + ", element=" + element + ", cost=" + cost + ", isMixedElement="
				+ isMixedElement + '}';
	}

	public static final class Builder {

		private String name;
		private PokemonElement element;
		private int cost;
		private boolean isMixedElement;

		private Builder() {
		}

		public Builder name(String val) {
			name = val;
			return this;
		}

		public Builder element(PokemonElement val) {
			element = val;
			return this;
		}

		public Builder cost(int val) {
			cost = val;
			return this;
		}

		public Builder isMixedElement(boolean val) {
			isMixedElement = val;
			return this;
		}

		public Ability build() {
			return new Ability(this);
		}
	}
}
