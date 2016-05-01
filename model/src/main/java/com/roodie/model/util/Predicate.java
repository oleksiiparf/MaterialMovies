package com.roodie.model.util;

/**
 * Created by Roodie on 12.03.2016.
 */
/**
 * Determines a true or false value for a given input.
 *
 * @param <T>
 */
public interface Predicate<T> {

    /**
     * Returns the result of applying this predicate to {@code input}. This method is <i>generally expected</i>, but not
     * absolutely required, to have the following properties:
     *
     * <ul>
     * <li>Its execution does not cause any observable side effects.
     * </ul>
     *
     * @param input The input to evaluate
     * @return True or false
     */
    Boolean apply(T input);

}
