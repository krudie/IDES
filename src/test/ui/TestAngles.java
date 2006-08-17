/**
 * 
 */
package test.ui;

//import junit.framework.TestCase;
import presentation.Geometry;
import java.awt.geom.Point2D.Float;

/**
 * @author helen
 *
 */
public class TestAngles {//extends TestCase {

//	Float v = new Float(1, 0);
//	final double delta = 0.0001;  // tolerated difference in precision 
//	
//	public void testAngleFrom()
//	{
//		Float source, target;
//		double expected, result;
//		
//		source = v;
//		
//		// test unit vectors
//		expected = 0;	
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = Math.PI/8;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -Math.PI/8;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = Math.PI/4;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -Math.PI/4;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = Math.PI/2;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -Math.PI/2;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = 3*Math.PI/4;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -3*Math.PI/4;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = Math.PI;
//		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		float angle = (float)(2*Math.PI);
//		expected = 0;
//		
//		target = Geometry.rotate(source, angle);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		
//		// non-unit vectors of same length
//		source = Geometry.scale(v, 2);
//		expected = 0;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = Math.PI/8;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -Math.PI/8;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = Math.PI/4;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -Math.PI/4;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = Math.PI/2;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -Math.PI/2;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = 3*Math.PI/4;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -3*Math.PI/4;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = Math.PI;		
//		target = Geometry.rotate(source, expected);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		angle = (float)(2*Math.PI);
//		expected = 0;		
//		target = Geometry.rotate(source, angle);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//				
//		// non-unit vectors of different lengths
//		expected = 3*Math.PI/4;		
//		target = Geometry.scale(Geometry.rotate(source, expected), (float)Math.PI);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);
//		
//		expected = -3*Math.PI/4;		
//		target = Geometry.scale(Geometry.rotate(source, expected), (float)Math.PI);
//		result = Geometry.angleFrom(source, target);
//		assertEquals(expected, result, delta);		
//		
//	}
//	
//	public void testRotate()
//	{
//		double angle;
//		Float expected, result;
//		
//		expected = new Float(0,1);
//		angle = Math.PI/2;
//		result = Geometry.rotate(v, angle);
//		assertEquals(expected.x, result.x, delta);
//		assertEquals(expected.y, result.y, delta);
//		
//		expected = new Float(0,-1);
//		angle = -Math.PI/2;
//		result = Geometry.rotate(v, angle);
//		assertEquals(expected.x, result.x, delta);
//		assertEquals(expected.y, result.y, delta);
//		
//		expected = new Float(-1, 0);
//		angle = Math.PI;
//		result = Geometry.rotate(v, angle);
//		assertEquals(expected.x, result.x, delta);
//		assertEquals(expected.y, result.y, delta);
//		
//		expected = new Float(-1, 0);
//		angle = -Math.PI;
//		result = Geometry.rotate(v, angle);
//		assertEquals(expected.x, result.x, delta);
//		assertEquals(expected.y, result.y, delta);		
//		
//		expected = new Float((float)(1/Math.sqrt(2)), (float)(1/Math.sqrt(2)));
//		angle = Math.PI/4;
//		result = Geometry.rotate(v, angle);
//		assertEquals(expected.x, result.x, delta);
//		assertEquals(expected.y, result.y, delta);
//		
//		expected = new Float((float)(1/Math.sqrt(2)), -(float)(1/Math.sqrt(2)));
//		angle = -Math.PI/4;
//		result = Geometry.rotate(v, angle);
//		assertEquals(expected.x, result.x, delta);
//		assertEquals(expected.y, result.y, delta);
//		
//	}
//
//	
	
}
