/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.tuple;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;

/**
 * Describes the generation of values of a certain field or property of an entity. A generated
 * value might be generated in Java, or by the database. Every instance must implement either
 * {@link InMemoryGenerator} or {@link InDatabaseGenerator} depending on whether values are
 * generated in Java code, or by the database.
 * <ul>
 * <li>Value generation via arbitrary code written in Java is the responsibility of the method
 *     {@link InMemoryGenerator#generate(SharedSessionContractImplementor, Object, Object)}.
 *	   In this case, the generated value is written to the database just like any other field
 *	   or property value.
 * <li>A value generated by the database might be generated implicitly, by a trigger, or using
 *	   a {@code default} column value specified in DDL, for example, or it might be generated
 *	   by a SQL expression occurring explicitly in the SQL {@code insert} or {@code update}
 *	   statement. In this case, the generated value is retrieved from the database using a SQL
 *	   {@code select}.
 * </ul>
 * Generically, a generator may be integrated with the program using the meta-annotation
 * {@link org.hibernate.annotations.ValueGenerationType}, which associates the generator with
 * a Java annotation type, called the <em>generator annotation</em>. A generator may receive
 * parameters from its generator annotation. The generator may either:
 * <ul>
 * <li>implement {@link AnnotationBasedGenerator}, and receive the annotation as an argument to
 *     {@link AnnotationBasedGenerator#initialize},
 * <li>declare a constructor with the same signature as {@link AnnotationBasedGenerator#initialize},
 * <li>declare a constructor which accepts just the annotation instance, or
 * <li>declare a only default constructor, in which case it will not receive parameters.
 * </ul>
 *
 * @see org.hibernate.annotations.ValueGenerationType
 * @see org.hibernate.annotations.IdGeneratorType
 * @see org.hibernate.annotations.Generated
 *
 * @author Steve Ebersole
 * @author Gavin King
 *
 * @since 6.2
 */
public interface Generator extends Serializable {
	/**
	 * Determines if the property value is generated in Java, or by the database.
	 *
	 * @return {@code true} if the value is generated by the database, or false if it is
	 *		   generated in Java using a {@link ValueGenerator}.
	 */
	boolean generatedByDatabase();

	boolean generatedOnInsert();

	boolean generatedOnUpdate();

	default boolean isNotNever() {
		return generatedOnInsert() || generatedOnUpdate();
	}
}
