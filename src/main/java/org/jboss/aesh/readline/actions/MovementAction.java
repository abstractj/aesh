/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.aesh.readline.actions;

import org.jboss.aesh.readline.Action;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public abstract class MovementAction implements Action{

    protected boolean isSpace(char c) {
        return Character.isWhitespace(c);
    }

    protected boolean isDelimiter(char c) {
        return !Character.isLetterOrDigit(c);
    }

}
