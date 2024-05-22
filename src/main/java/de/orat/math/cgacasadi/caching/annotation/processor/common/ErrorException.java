package de.orat.math.cgacasadi.caching.annotation.processor.common;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class ErrorException extends Exception {

    public static Exception create(TypeElement correspondingElement) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

	public final Element element;

	protected ErrorException(Element element, String message, Object... args) {
		super(String.format(message, args));
		this.element = element;
	}

	protected ErrorException(Element element, Throwable cause, String message, Object... args) {
		super(String.format(message, args), cause);
		this.element = element;
	}

	public static ErrorException create(Element element, String message, Object... args) {
		return new ErrorException(element, message, args);
	}

	public static ErrorException create(Element element, Throwable cause, String message, Object... args) {
		return new ErrorException(element, cause, message, args);
	}
}
