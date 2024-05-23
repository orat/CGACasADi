package de.orat.math.cgacasadi.caching.annotation.processor.common;

import javax.lang.model.element.Element;

public class IgnoreException extends Exception {

    public final Element element;

    protected IgnoreException(Element element, String message, Object... args) {
        super(String.format(message, args));
        this.element = element;
    }

    protected IgnoreException(Element element, Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
        this.element = element;
    }

    public static IgnoreException create(Element element, String message, Object... args) {
        return new IgnoreException(element, message, args);
    }

    public static IgnoreException create(Element element, Throwable cause, String message, Object... args) {
        return new IgnoreException(element, cause, message, args);
    }
}
