package cc.zgeek.pagelib.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageLayoutName {
  // this is used in Library Projects, because in Library project
  // resource ids(R.id.*) will be non-final, we cannot use the id
  // directly, we have to use string representation of the id, and
  // Paginize will use res.getIdentifier(value(), "id", packageName)
  // to get the id
  String value();
}
