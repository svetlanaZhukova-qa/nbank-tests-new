package common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)// выполняем во время исполнения теста
@Target(ElementType.METHOD)// цель-метод
public @interface Browsers {
	String[] value();// допустимые браузеры
}
