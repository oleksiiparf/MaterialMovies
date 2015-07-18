package com.roodie.materialmovies.qualifiers;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Roodie on 14.07.2015.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface FilesDirectory {
}
