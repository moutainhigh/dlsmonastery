/*
 * Copyright (c) 2008, 2009, 2011 Oracle, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.  The Eclipse Public License is available
 * at http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution License
 * is available at http://www.eclipse.org/org/documents/edl-v10.php.
 */
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies the version field or property of an entity class that
 * serves as its optimistic lock value.  The version is used to ensure
 * integrity when performing the merge operation and for optimistic
 * concurrency control.
 *
 * <p> Only a single <code>Version</code> property or field
 * should be used per class; applications that use more than one
 * <code>Version</code> property or field will not be portable.
 *
 * <p> The <code>Version</code> property should be mapped to
 * the primary table for the entity class; applications that
 * map the <code>Version</code> property to a table other than
 * the primary table will not be portable.
 *
 * <p> The following types are supported for version properties:
 * <code>int</code>, <code>Integer</code>, <code>short</code>,
 * <code>Short</code>, <code>long</code>, <code>Long</code>,
 * <code>java.sql.Timestamp</code>.
 *
 * <pre>
 *    Example:
 *
 *    &#064;Version
 *    &#064;Column(name="OPTLOCK")
 *    protected int getVersionNum() { return versionNum; }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({FIELD })
@Retention(RUNTIME)
public @interface AutoAuditing {
}
