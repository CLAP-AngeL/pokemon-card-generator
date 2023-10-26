package com.petproject.pokemoncardgenerator.model.details;

import static com.petproject.pokemoncardgenerator.model.contentpool.DetailsContentPool.HOLDABLE_WEAPONS;

import java.util.Objects;

public class PokemonDetail {

	private String relation;
	private String detail;
	private String quantifier;

	public PokemonDetail() {
	}

	private PokemonDetail(Builder builder) {
		setRelation(builder.relation);
		setDetail(builder.detail);
		setQuantifier(builder.quantifier);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static PokemonDetail withHoldableWeapon(String detail, String quantifier) {
		PokemonDetail newDetail = PokemonDetail.builder().relation("holding").detail(detail).quantifier(quantifier)
				.build();
		HOLDABLE_WEAPONS.add(newDetail);
		return newDetail;
	}

	public static PokemonDetail withDetail(String detail, String quantifier) {
		return PokemonDetail.builder().relation("with").detail(detail).quantifier(quantifier).build();
	}

	public static PokemonDetail wearingDetail(String detail, String quantifier) {
		return PokemonDetail.builder().relation("wearing").detail(detail).quantifier(quantifier).build();
	}

	public String generateDetailWithAdjective(String adjective) {
		if (adjective != null && !adjective.isBlank()) {
			if (quantifier != null && !quantifier.isBlank()) {
				return String.format("%s %s %s %s", relation, quantifier, adjective, detail);
			}
			return String.format("%s %s %s", relation, adjective, detail);
		}

		if (quantifier != null && !quantifier.isBlank()) {
			return String.format("%s %s %s", relation, quantifier, detail);
		}

		return String.format("%s %s", relation, detail);
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getQuantifier() {
		return quantifier;
	}

	public void setQuantifier(String quantifier) {
		this.quantifier = quantifier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PokemonDetail detail1 = (PokemonDetail) o;

		if (!Objects.equals(relation, detail1.relation)) {
			return false;
		}
		if (!Objects.equals(detail, detail1.detail)) {
			return false;
		}
		return Objects.equals(quantifier, detail1.quantifier);
	}

	@Override
	public int hashCode() {
		int result = relation != null ? relation.hashCode() : 0;
		result = 31 * result + (detail != null ? detail.hashCode() : 0);
		result = 31 * result + (quantifier != null ? quantifier.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Detail{" + "relation='" + relation + '\'' + ", detail='" + detail + '\'' + ", quantifier='" + quantifier
				+ '\'' + '}';
	}

	public static final class Builder {

		private String relation;
		private String detail;
		private String quantifier;

		private Builder() {
		}

		public Builder relation(String val) {
			relation = val;
			return this;
		}

		public Builder detail(String val) {
			detail = val;
			return this;
		}

		public Builder quantifier(String val) {
			quantifier = val;
			return this;
		}

		public PokemonDetail build() {
			return new PokemonDetail(this);
		}
	}
}
