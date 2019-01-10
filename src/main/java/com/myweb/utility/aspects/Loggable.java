package com.myweb.utility.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate <b>@Loggable</b> in method to log method related info
 * 
 * @author Jegatheesh <br>
 *         <b>Created</b> On Sep 3, 2018
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {

}
