package store.intent.intentguard.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static store.intent.intentguard.annotation.Strategy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target({ TYPE, FIELD, METHOD, CONSTRUCTOR })
public @interface Exclude {

	public Strategy[] value() default {NO_STRATEGY};
	
}
