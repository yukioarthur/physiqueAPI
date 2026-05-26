package senac.tsi.physique.apikey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireApiKey {
    ApiAccessPlan minPlan() default ApiAccessPlan.ALUNO;
}
