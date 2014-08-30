package utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import play.Logger;

public class NumberUtilities {

	public static Number parse(final String value , final String... formats ) {
		try {
			for( final String format : formats ) {
				final DecimalFormat df = new DecimalFormat(format);
				final Number number = df.parse(value);
				if( number != null ) {
					return number;
				}
			}
			return null;
		} catch (ParseException e) {
			Logger.debug(String.format("erro ao converter o numero %s", value),e);
			return null;
		}
	}

	public static String format(BigDecimal number) {
		return NumberFormat.getCurrencyInstance().format(number);
	}
}
