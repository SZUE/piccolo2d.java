/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.umd.cs.piccolo.util;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

import junit.framework.TestCase;

public class PAffineTransformTest extends TestCase {

    private PAffineTransform at;

    public PAffineTransformTest(String aName) {
        super(aName);
    }
    
    public void setUp() {
        at = new PAffineTransform();        
    }

    public void testRotation() {
        at.rotate(Math.toRadians(45));
        assertEquals(at.getRotation(), Math.toRadians(45), 0.000000001);
        at.setRotation(Math.toRadians(90));
        assertEquals(at.getRotation(), Math.toRadians(90), 0.000000001);
    }

    public void testScale() {        
        at.scaleAboutPoint(0.45, 0, 1);
        assertEquals(at.getScale(), 0.45, 0.000000001);
        at.setScale(0.11);
        assertEquals(at.getScale(), 0.11, 0.000000001);
    }

    public void testTransformRectLeavesEmptyBoundsEmpty() {
        PBounds b1 = new PBounds();       
        at.scale(0.5, 0.5);
        at.translate(100, 50);

        at.transform(b1, b1);
        assertTrue(b1.isEmpty());
    }
    
    public void testTransformRect() {
        PBounds b1 = new PBounds(0, 0, 100, 80);
        PBounds b2 = new PBounds(100, 100, 100, 80);
        
        at.scale(0.5, 0.5);
        at.translate(100, 50);

        at.transform(b1, b1);
        at.transform(b2, b2);
        
        assertSameBounds(new PBounds(50, 25, 50, 40), b1);
        assertSameBounds(new PBounds(100, 75, 50, 40), b2);
       
        at.inverseTransform(b1, b1);
        at.inverseTransform(b2, b2);

        assertSameBounds(new PBounds(0, 0, 100, 80), b1);       
        assertSameBounds(new PBounds(100, 100, 100, 80), b2);
    }
    
    public void testThrowsExceptionWhenSetting0Scale() {
        try {            
            at.setScale(0);
            fail("Setting 0 scale should throw exception");
        } catch (RuntimeException e) {
            // expected
        }
    }
    
    public void testSetOffsetLeavesRotationUntouched() {
        at.setRotation(Math.PI);       
        at.setOffset(100, 50);
        assertEquals(Math.PI, at.getRotation(), 0.001);
    }
    
    public void testTransformDimensionWorks() {
        Dimension d1 = new Dimension(100, 50);
        at.setScale(2);
        Dimension d2 = new Dimension(0, 0);
        at.transform(d1, d2);
        assertEquals(new Dimension(200, 100), d2);
    }
    
    public void testTransformDimensionWorksWithSecondParamNull() {
        Dimension d1 = new Dimension(100, 50);
        at.setScale(2);
        Dimension2D d2 = at.transform(d1, null);
        assertEquals(new Dimension(200, 100), d2);
    }
        
    private final void assertSameBounds(PBounds expected, PBounds actual) {
        assertSameBounds(expected, actual, 0.0000001);
    }
    
    private final void assertSameBounds(PBounds expected, PBounds actual, double errorRate) {
        assertTrue("Expected " + expected + " but was " + actual, comparisonScore(expected, actual) > (1d-errorRate));
    }
    
    
    // % of area within full bounds covered by intersection or the two bounds.
    // exactly covering would be 1 no overlap would be 0
    private final double comparisonScore(PBounds b1, PBounds b2) {        
        PBounds intersection = new PBounds();
        PBounds union = new PBounds();
        PBounds.intersect(b1, b2, intersection);
        PBounds.intersect(b1, b2, union);        
        
        return area(intersection) / area(union);
    }
    
    private final double area(PBounds b) {
        return b.getWidth() * b.getHeight();
    }
}
