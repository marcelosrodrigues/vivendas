package binders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import play.data.binding.As;
import play.data.binding.Global;
import play.data.binding.TypeBinder;
import utils.NumberUtilities;

@Global
public class BigDecimalTypeBinder implements TypeBinder<BigDecimal> {

	@Override
	public Object bind(String name, Annotation[] annotations, String value,
			Class actualClass, Type genericType) throws Exception {
		
		for(final Annotation annotation : annotations ){
			if( annotation instanceof As ){
				As as = (As) annotation;
				return new BigDecimal(NumberUtilities.parse(value,as.format()).doubleValue());
			}
			
		}
		
		return null;
	}

}
