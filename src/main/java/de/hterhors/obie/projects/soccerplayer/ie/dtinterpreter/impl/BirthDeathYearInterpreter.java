package de.hterhors.obie.projects.soccerplayer.ie.dtinterpreter.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hterhors.obie.ml.dtinterpreter.AbstractInterpreterBuilder;
import de.hterhors.obie.ml.dtinterpreter.AbstractNumericInterpreter;
import de.hterhors.obie.ml.dtinterpreter.ISingleUnit;

public class BirthDeathYearInterpreter extends AbstractNumericInterpreter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static public enum EYearUnits implements ISingleUnit {

		year;

		public static EYearUnits getDefault() {
			return year;
		}

		@Override
		public double getFactor() {
			return 1;
		}

		@Override
		public String getName() {
			return this.name();
		}

	}

	final static String pattern1GroupName = "pattern1GroupName";

	final private static String numGroup1 = "numGroup1";

	final private static String writtenNumGroup1 = "writtenNumGroup1";

	final static String numbers1_ = "(?<" + numGroup1 + ">[1-2][0-9]{3})";

	final static String pattern1_ = numbers1_;

	public final static Pattern PATTERN = Pattern
			.compile(PRE_BOUNDS + "(?<" + pattern1GroupName + ">" + pattern1_ + ")" + POST_BOUNDS, PATTERN_BITMASK);

	final public int value;

	private BirthDeathYearInterpreter(String surfaceForm, int value) {
		super(surfaceForm);
		this.value = value;
	}

	public BirthDeathYearInterpreter normalize() {
		return this;
	}

	@Override
	public String asFormattedString() {
		return String.valueOf(value);
	}

	@Override
	public ISingleUnit getUnit() {
		return EYearUnits.year;
	}

	@Override
	public double getMeanValue() {
		return value;
	}

	public static class Builder extends AbstractInterpreterBuilder {

		final public static int defaultValue = 0;

		public String surfaceForm;
		private int value = defaultValue;

		public String getSurfaceForm() {
			return surfaceForm;
		}

		public Builder setSurfaceForm(String surfaceForm) {
			this.surfaceForm = surfaceForm;
			return this;
		}

		public int getValue() {
			return value;
		}

		public Builder setValue(int value) {
			this.value = value;
			return this;
		}

		public BirthDeathYearInterpreter build() {
			return new BirthDeathYearInterpreter(surfaceForm, value);
		}

		public Builder interprete(final String surfaceForm) {
			Matcher matcher = BirthDeathYearInterpreter.PATTERN.matcher(surfaceForm);
			if (!matcher.find())
				return this;

			return fromMatcher(matcher);
		}

		public Builder fromMatcher(final Matcher matcher) {

			surfaceForm = matcher.group();
			if (matcher.group(BirthDeathYearInterpreter.pattern1GroupName) != null) {
				if (matcher.group(BirthDeathYearInterpreter.numGroup1) != null)
					value = Integer.parseInt(matcher.group(BirthDeathYearInterpreter.numGroup1));
				else if (matcher.group(BirthDeathYearInterpreter.writtenNumGroup1) != null)
					value = (int) mapWrittenNumbertoInt(matcher.group(BirthDeathYearInterpreter.writtenNumGroup1));
			}
			return this;
		}

		@Override
		public String toString() {
			return "Builder [surfaceForm=" + surfaceForm + ", value=" + value + "]";
		}

	}

	@Override
	public String toString() {
		return "SemanticNNumber [value=" + value + "]";
	}

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

}
