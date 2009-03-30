/**
 * Copyright (c) 2009 Espen Wiborg <espenhw@grumblesmurf.org>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */ 
package org.grumblesmurf.malabar

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.*;

class SemanticReflectorTest
{
    def sr;

    @Before
    void setup() {
        sr = new SemanticReflector();
    }

    @Test
    void fieldGivesSemanticTag() throws Exception {
        Field f = FilterReader.getDeclaredField("in");
        assertThat(sr.asSemanticTag(f), is('("in" variable (:typemodifiers ("protected") :type "java.io.Reader"))'));
    }

    @Test
    void fieldGivesSemanticTagPrimitive() throws Exception {
        Field f = javax.swing.AbstractAction.getDeclaredField("enabled");
        assertThat(sr.asSemanticTag(f), is('("enabled" variable (:typemodifiers ("protected") :type "boolean"))'));
    }

    @Test
    void fieldGivesSemanticTagStaticFinal() throws Exception {
        Field f = javax.swing.AbstractButton.getDeclaredField("BORDER_PAINTED_CHANGED_PROPERTY");
        assertThat(sr.asSemanticTag(f), is('("BORDER_PAINTED_CHANGED_PROPERTY" variable (:typemodifiers ("public" "static" "final") :type "java.lang.String"))'));
    }

    @Test
    void simpleConstructor() throws Exception {
        Constructor c = ArrayList.getDeclaredConstructor();
        assertThat(sr.asSemanticTag(c), is('("ArrayList" function (:constructor-flag t :typemodifiers ("public") :arguments ()))'));
    }

    @Test
    void complexConstructor() throws Exception {
        Constructor c = ArrayList.getDeclaredConstructor(Integer.TYPE);
        assertThat(sr.asSemanticTag(c), is('("ArrayList" function (:constructor-flag t :typemodifiers ("public") :arguments (("arg0" variable (:type "int")))))'));
    }

    @Test
    void simpleMethod() throws Exception {
        Method m = List.getDeclaredMethod("clear");
        assertThat(sr.asSemanticTag(m), is('("clear" function (:typemodifiers ("public" "abstract") :arguments () :type "void"))'));
    }

    @Test
    void methodWithOneArgument() throws Exception {
        Method m = List.getDeclaredMethod("contains", Object);
        assertThat(sr.asSemanticTag(m), is('("contains" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.lang.Object"))) :type "boolean"))'));
    }

    @Test
    void methodWithGenericArgument() throws Exception {
        Method m = List.getDeclaredMethod("add", Integer.TYPE, Object);
        assertThat(sr.asSemanticTag(m), is('("add" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "int")) ("arg1" variable (:type "E"))) :type "void"))'));
    }

    @Test
    void methodWithComplicatedGenericArgument() throws Exception {
        Method m = List.getDeclaredMethod("addAll", Collection);
        assertThat(sr.asSemanticTag(m), is('("addAll" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.util.Collection<? extends E>"))) :type "boolean"))'));
    }

    @Test
    void methodWithGenericReturnAndTypeArgument() throws Exception {
        Method m = List.getDeclaredMethod("toArray", Object[]);
        assertThat(sr.asSemanticTag(m), is('("toArray" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "T" :dereference 1))) :type "T[]" :template-specifier "<T>"))'));
    }

    @Test
    void staticMethodWithException() throws Exception {
        Method m = Thread.getDeclaredMethod("sleep", Long.TYPE);
        assertThat(sr.asSemanticTag(m), is('("sleep" function (:typemodifiers ("public" "static" "native") :arguments (("arg0" variable (:type "long"))) :type "void" :throws ("java.lang.InterruptedException")))'));
    }

    @Test
    void simpleInterface() throws Exception {
        assertThat(sr.asSemanticTag(Serializable), is('("java.io.Serializable" type (:typemodifiers ("public" "abstract" "interface") :members () :type "interface"))'))
    }

    @Test
    void anotherSimpleInterface() throws Exception {
        assertThat(sr.asSemanticTag(Iterable), is('("java.lang.Iterable" type (:typemodifiers ("public" "abstract" "interface") :template-specifier "<T>" :members (("iterator" function (:typemodifiers ("public" "abstract") :arguments () :type "java.util.Iterator<T>"))) :type "interface"))'))
    }

    @Test
    void subInterfaceWithMembers() throws Exception {
        assertThat(sr.asSemanticTag(Collection), is('("java.util.Collection" type (:typemodifiers ("public" "abstract" "interface") :interfaces ("java.lang.Iterable<E>") :template-specifier "<E>" :members (("add" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "E"))) :type "boolean")) ("hashCode" function (:typemodifiers ("public" "abstract") :arguments () :type "int")) ("clear" function (:typemodifiers ("public" "abstract") :arguments () :type "void")) ("equals" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.lang.Object"))) :type "boolean")) ("contains" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.lang.Object"))) :type "boolean")) ("isEmpty" function (:typemodifiers ("public" "abstract") :arguments () :type "boolean")) ("addAll" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.util.Collection<? extends E>"))) :type "boolean")) ("iterator" function (:typemodifiers ("public" "abstract") :arguments () :type "java.util.Iterator<E>")) ("size" function (:typemodifiers ("public" "abstract") :arguments () :type "int")) ("toArray" function (:typemodifiers ("public" "abstract") :arguments () :type "java.lang.Object[]")) ("toArray" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "T" :dereference 1))) :type "T[]" :template-specifier "<T>")) ("remove" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.lang.Object"))) :type "boolean")) ("containsAll" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.util.Collection<?>"))) :type "boolean")) ("removeAll" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.util.Collection<?>"))) :type "boolean")) ("retainAll" function (:typemodifiers ("public" "abstract") :arguments (("arg0" variable (:type "java.util.Collection<?>"))) :type "boolean"))) :type "interface"))'))
    }

    @Test
    void subClassWithInterface() throws Exception {
        assertThat(sr.asSemanticTag(AbstractSet), is('("java.util.AbstractSet" type (:typemodifiers ("public" "abstract") :superclasses "java.util.AbstractCollection<E>" :interfaces ("java.util.Set<E>") :template-specifier "<E>" :members (("AbstractSet" function (:constructor-flag t :typemodifiers ("protected") :arguments ())) ("hashCode" function (:typemodifiers ("public") :arguments () :type "int")) ("equals" function (:typemodifiers ("public") :arguments (("arg0" variable (:type "java.lang.Object"))) :type "boolean")) ("removeAll" function (:typemodifiers ("public") :arguments (("arg0" variable (:type "java.util.Collection<?>"))) :type "boolean"))) :type "class"))'));
    }
}
