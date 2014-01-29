package org.vaadin.spring;

import com.vaadin.ui.UI;

import java.io.Serializable;

/**
 * Uniquely identifies a UI instance for a given window/tab (basically a wrapper for {@link com.vaadin.ui.UI#getUIId()}).
 *
 * @author petter@vaadin.com
 * @author Josh Long (josh@joshlong.com)
 */
public class VaadinUIIdentifier implements Serializable {
    private final int uiId;
    private final String sessionId;

    public VaadinUIIdentifier(int uiId, String sessionId) {
        this.uiId = uiId;
        this.sessionId = sessionId;
    }

    public VaadinUIIdentifier(UI ui) {
        this.uiId = ui.getUIId();
        this.sessionId = ui.getSession().getSession().getId();
    }

    public int getUIId() {
        return uiId;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VaadinUIIdentifier that = (VaadinUIIdentifier) o;

        if (uiId != that.uiId) return false;
        if (!sessionId.equals(that.sessionId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uiId;
        result = 31 * result + sessionId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s(%s:%d)", VaadinUIIdentifier.class.getSimpleName(), sessionId, uiId);
    }
}
