package com.petproject.pokemoncardgenerator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Style {

	private String subject;
	private List<String> subjectAdjectives = new ArrayList<>();

	private String detail;
	private String detailAdjective;

	private String environment;
	private String ambience;

	private String styleSuffix;

	public Style() {
	}

	private Style(Builder builder) {
		setSubject(builder.subject);
		setSubjectAdjectives(builder.subjectAdjectives);
		setDetail(builder.detail);
		setDetailAdjective(builder.detailAdjective);
		setEnvironment(builder.environment);
		setAmbience(builder.ambience);
		setStyleSuffix(builder.styleSuffix);
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<String> getSubjectAdjectives() {
		return subjectAdjectives;
	}

	public void setSubjectAdjectives(List<String> subjectAdjectives) {
		this.subjectAdjectives = subjectAdjectives;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDetailAdjective() {
		return detailAdjective;
	}

	public void setDetailAdjective(String detailAdjective) {
		this.detailAdjective = detailAdjective;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getAmbience() {
		return ambience;
	}

	public void setAmbience(String ambience) {
		this.ambience = ambience;
	}

	public String getStyleSuffix() {
		return styleSuffix;
	}

	public void setStyleSuffix(String styleSuffix) {
		this.styleSuffix = styleSuffix;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Style style = (Style) o;

		if (!Objects.equals(subject, style.subject)) {
			return false;
		}
		if (!Objects.equals(subjectAdjectives, style.subjectAdjectives)) {
			return false;
		}
		if (!Objects.equals(detail, style.detail)) {
			return false;
		}
		if (!Objects.equals(detailAdjective, style.detailAdjective)) {
			return false;
		}
		if (!Objects.equals(environment, style.environment)) {
			return false;
		}
		if (!Objects.equals(ambience, style.ambience)) {
			return false;
		}
		return Objects.equals(styleSuffix, style.styleSuffix);
	}

	@Override
	public int hashCode() {
		int result = subject != null ? subject.hashCode() : 0;
		result = 31 * result + (subjectAdjectives != null ? subjectAdjectives.hashCode() : 0);
		result = 31 * result + (detail != null ? detail.hashCode() : 0);
		result = 31 * result + (detailAdjective != null ? detailAdjective.hashCode() : 0);
		result = 31 * result + (environment != null ? environment.hashCode() : 0);
		result = 31 * result + (ambience != null ? ambience.hashCode() : 0);
		result = 31 * result + (styleSuffix != null ? styleSuffix.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Style{" + "subject='" + subject + '\'' + ", subjectAdjectives=" + subjectAdjectives + ", detail='"
				+ detail + '\'' + ", detailAdjective='" + detailAdjective + '\'' + ", environment='" + environment
				+ '\'' + ", ambience='" + ambience + '\'' + ", styleSuffix='" + styleSuffix + '\'' + '}';
	}

	public static final class Builder {

		private String subject;
		private List<String> subjectAdjectives = new ArrayList<>();
		private String detail;
		private String detailAdjective;
		private String environment;
		private String ambience;
		private String styleSuffix;

		private Builder() {
		}

		public Builder subject(String val) {
			subject = val;
			return this;
		}

		public Builder subjectAdjectives(List<String> val) {
			subjectAdjectives = val;
			return this;
		}

		public Builder detail(String val) {
			detail = val;
			return this;
		}

		public Builder detailAdjective(String val) {
			detailAdjective = val;
			return this;
		}

		public Builder environment(String val) {
			environment = val;
			return this;
		}

		public Builder ambience(String val) {
			ambience = val;
			return this;
		}

		public Builder styleSuffix(String val) {
			styleSuffix = val;
			return this;
		}

		public Style build() {
			return new Style(this);
		}
	}
}
