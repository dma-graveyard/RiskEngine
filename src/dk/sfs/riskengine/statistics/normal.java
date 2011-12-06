package dk.sfs.riskengine.statistics;

    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 Ross Ihaka
     *
     *  This program is free software; you can redistribute it and/or modify
     *  it under the terms of the GNU General Public License as published by
     *  the Free Software Foundation; either version 2 of the License, or
     *  (at your option) any later version.
     *
     *  This program is distributed in the hope that it will be useful,
     *  but WITHOUT ANY WARRANTY; without even the implied warranty of
     *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     *  GNU General Public License for more details.
     *
     *  You should have received a copy of the GNU General Public License
     *  along with this program; if not, write to the Free Software
     *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
     */


/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class normal 
  { 
    
    /*  Mathematical Constants */
    static private double  SIXTEN = 1.6;   /* Magic Cutoff */


    /*
     * 	M_1_SQRT_2PI = 1 / sqrt(2 * pi)
     */
    
    /** The Normal Density Function */
    public static double  density(double x, double mu, double sigma)
    {
        if (Double.isNaN(x) || Double.isNaN(mu) || Double.isNaN(sigma))
    	return x + mu + sigma;
        if (sigma <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }
    
        x = (x - mu) / sigma;
        return Constants.M_1_SQRT_2PI * 
               java.lang.Math.exp(-0.5 * x * x) / sigma;
    }

    /**  DESCRIPTION 
     *    The main computation evaluates near-minimax approximations derived
     *    from those in "Rational Chebyshev approximations for the error
     *    function" by W. J. Cody, Math. Comp., 1969, 631-637.  This
     *    transportable program uses rational functions that theoretically
     *    approximate the normal distribution function to at least 18
     *    significant decimal digits.  The accuracy achieved depends on the
     *    arithmetic system, the compiler, the intrinsic functions, and
     *    proper selection of the machine-dependent constants.
     *
     *  REFERENCE
     *
     *    Cody, W. D. (1993).
     *    ALGORITHM 715: SPECFUN - A Portable FORTRAN Package of
     *    Special Function Routines and Test Drivers".
     *    ACM Transactions on Mathematical Software. 19, 22-32.
     */

    public static double cumulative(double x, double mu, double sigma)
    {
        final double c[] = {
    	0.39894151208813466764,
    	8.8831497943883759412,
    	93.506656132177855979,
    	597.27027639480026226,
    	2494.5375852903726711,
    	6848.1904505362823326,
    	11602.651437647350124,
    	9842.7148383839780218,
    	1.0765576773720192317e-8
        };
    
        final double d[] = {
    	22.266688044328115691,
    	235.38790178262499861,
    	1519.377599407554805,
    	6485.558298266760755,
    	18615.571640885098091,
    	34900.952721145977266,
    	38912.003286093271411,
    	19685.429676859990727
        };
    
        final double p[] = {
    	0.21589853405795699,
    	0.1274011611602473639,
    	0.022235277870649807,
    	0.001421619193227893466,
    	2.9112874951168792e-5,
    	0.02307344176494017303
        };
    
        final double q[] = {
    	1.28426009614491121,
    	0.468238212480865118,
    	0.0659881378689285515,
    	0.00378239633202758244,
    	7.29751555083966205e-5
        };
    
        final double a[] = {
    	2.2352520354606839287,
    	161.02823106855587881,
    	1067.6894854603709582,
    	18154.981253343561249,
    	0.065682337918207449113
        };
    
        final double b[] = {
    	47.20258190468824187,
    	976.09855173777669322,
    	10260.932208618978205,
    	45507.789335026729956
        };
    
        double xden, temp, xnum, result, ccum;
        double del, min, eps, xsq;
        double y;
        int i;
    
        /* Note: The structure of these checks has been */
        /* carefully thought through.  For example, if x == mu */
        /* and sigma == 0, we still get the correct answer. */
    
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isNaN(x) || Double.isNaN(mu) || Double.isNaN(sigma))
    	return x + mu + sigma;
    /*!* #endif /*4!*/
        if (sigma < 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        x = (x - mu) / sigma;
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isInfinite(x)) {
    	if(x < 0) return 0;
    	else return 1;
        }
    /*!* #endif /*4!*/
    
        eps = Constants.DBL_EPSILON * 0.5;
        min = Double.MIN_VALUE;
/*!*     y = fabs(x); *!*/
        y = java.lang.Math.abs(x);
        if (y <= 0.66291) {
    	xsq = 0.0;
    	if (y > eps) {
    	    xsq = x * x;
    	}
    	xnum = a[4] * xsq;
    	xden = xsq;
    	for (i = 1; i <= 3; ++i) {
    	    xnum = (xnum + a[i - 1]) * xsq;
    	    xden = (xden + b[i - 1]) * xsq;
    	}
    	result = x * (xnum + a[3]) / (xden + b[3]);
    	temp = result;
    	result = 0.5 + temp;
    	ccum = 0.5 - temp;
        }
        else if (y <= Constants.M_SQRT_32) {
    
    	/* Evaluate pnorm for 0.66291 <= |z| <= sqrt(32) */
    
    	xnum = c[8] * y;
    	xden = y;
    	for (i = 1; i <= 7; ++i) {
    	    xnum = (xnum + c[i - 1]) * y;
    	    xden = (xden + d[i - 1]) * y;
    	}
    	result = (xnum + c[7]) / (xden + d[7]);
/*!* 	xsq = floor(y * SIXTEN) / SIXTEN; *!*/
    	xsq = java.lang.Math.floor(y * SIXTEN) / SIXTEN;
    	del = (y - xsq) * (y + xsq);
/*!* 	result = exp(-xsq * xsq * 0.5) * exp(-del * 0.5) * result; *!*/
    	result = java.lang.Math.exp(-xsq * xsq * 0.5) * java.lang.Math.exp(-del * 0.5) * result;
    	ccum = 1.0 - result;
    	if (x > 0.0) {
    	    temp = result;
    	    result = ccum;
    	    ccum = temp;
    	}
        }
        else if(y < 50) {
    
    	/* Evaluate pnorm for sqrt(32) < |z| < 50 */
    
    	result = 0.0;
    	xsq = 1.0 / (x * x);
    	xnum = p[5] * xsq;
    	xden = xsq;
    	for (i = 1; i <= 4; ++i) {
    	    xnum = (xnum + p[i - 1]) * xsq;
    	    xden = (xden + q[i - 1]) * xsq;
    	}
    	result = xsq * (xnum + p[4]) / (xden + q[4]);
    	result = (Constants.M_1_SQRT_2PI - result) / y;
/*!* 	xsq = floor(x * SIXTEN) / SIXTEN; *!*/
    	xsq = java.lang.Math.floor(x * SIXTEN) / SIXTEN;
    	del = (x - xsq) * (x + xsq);
/*!* 	result = exp(-xsq * xsq * 0.5) * exp(-del * 0.5) * result; *!*/
    	result = java.lang.Math.exp(-xsq * xsq * 0.5) * java.lang.Math.exp(-del * 0.5) * result;
    	ccum = 1.0 - result;
    	if (x > 0.0) {
    	    temp = result;
    	    result = ccum;
    	    ccum = temp;
    	}
        }
        else {
    	if(x > 0) {
    	    result = 1.0;
    	    ccum = 0.0;
    	}
    	else {
    	    result = 0.0;
    	    ccum = 1.0;
    	}
        }
        if (result < min) {
    	result = 0.0;
        }
        if (ccum < min) {
    	ccum = 0.0;
        }
        return result;
    }
    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 Ross Ihaka
     *
     *  This program is free software; you can redistribute it and/or modify
     *  it under the terms of the GNU General Public License as published by
     *  the Free Software Foundation; either version 2 of the License, or
     *  (at your option) any later version.
     *
     *  This program is distributed in the hope that it will be useful,
     *  but WITHOUT ANY WARRANTY; without even the implied warranty of
     *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     *  GNU General Public License for more details.
     *
     *  You should have received a copy of the GNU General Public License
     *  along with this program; if not, write to the Free Software
     *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
     *
     *  SYNOPSIS
     *
     *    double cumulative(double p, double mu, double sigma);
     *
     *  DESCRIPTION
     *
     *    Compute the quantile function for the normal distribution.
     *
     *    For small to moderate probabilities, algorithm referenced
     *    below is used to obtain an initial approximation which is
     *    polished with a final Newton step.
     *
     *    For very large arguments, an algorithm of Wichura is used.
     *
     *  REFERENCE
     *
     *    Beasley, J. D. and S. G. Springer (1977).
     *    Algorithm AS 111: The percentage points of the normal distribution,
     *    Applied Statistics, 26, 118-121.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    
    public static double  quantile(double p, double mu, double sigma)
    {
        double q, r, val;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(p) || Double.isNaN(mu) || Double.isNaN(sigma))
    	return p + mu + sigma;
    /*!* #endif /*4!*/
        if (p < 0.0 || p > 1.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    
        q = p - 0.5;
    
/*!*     if (fabs(q) <= 0.42) { *!*/
        if (java.lang.Math.abs(q) <= 0.42) {
    
    	/* 0.08 < p < 0.92 */
    
    	r = q * q;
    	val = q * (((-25.44106049637 * r + 41.39119773534) * r
    		    - 18.61500062529) * r + 2.50662823884)
    	    / ((((3.13082909833 * r - 21.06224101826) * r
    		 + 23.08336743743) * r + -8.47351093090) * r + 1.0);
        }
        else {
    
    	/* p < 0.08 or p > 0.92, set r = min(p, 1 - p) */
    
    	r = p;
    	if (q > 0.0)
    	    r = 1.0 - p;
    
    	if(r > Constants.DBL_EPSILON) {
/*!* 	    r = sqrt(-log(r)); *!*/
    	    r = java.lang.Math.sqrt(-java.lang.Math.log(r));
    	    val = (((2.32121276858 * r + 4.85014127135) * r
    		    - 2.29796479134) * r - 2.78718931138)
    		/ ((1.63706781897 * r + 3.54388924762) * r + 1.0);
    	    if (q < 0.0)
    		val = -val;
    	}
    	else if(r > 1e-300) {		/* Assuming IEEE here? */
/*!* 	    val = -2 * log(p); *!*/
    	    val = -2 * java.lang.Math.log(p);
/*!* 	    r = log(6.283185307179586476925286766552 * val); *!*/
    	    r = java.lang.Math.log(6.283185307179586476925286766552 * val);
    	    r = r/val + (2 - r)/(val * val)
    		+ (-14 + 6 * r - r * r)/(2 * val * val * val);
/*!* 	    val = sqrt(val * (1 - r)); *!*/
    	    val = java.lang.Math.sqrt(val * (1 - r));
    	    if(q < 0.0)
    		val = -val;
    	    return val;
    	}
    	else {
    	    throw new java.lang.ArithmeticException("Math Error: RANGE");
	    //    	    if(q < 0.0) {
	    //    		return Double.NEGATIVE_INFINITY;
	    //    	    }
	    //    	    else {
	    //    		return Double.POSITIVE_INFINITY;
	    //    	    }
    	}
        }
        val = val - (cumulative(val, 0.0, 1.0) - p) / normal.density(val, 0.0, 1.0);
        return mu + sigma * val;
    }
    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 Ross Ihaka
     *
     *  This program is free software; you can redistribute it and/or modify
     *  it under the terms of the GNU General Public License as published by
     *  the Free Software Foundation; either version 2 of the License, or
     *  (at your option) any later version.
     *
     *  This program is distributed in the hope that it will be useful,
     *  but WITHOUT ANY WARRANTY; without even the implied warranty of
     *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     *  GNU General Public License for more details.
     *
     *  You should have received a copy of the GNU General Public License
     *  along with this program; if not, write to the Free Software
     *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double random(double mu, double sigma, uniform PRNG );
     *
     *  DESCRIPTION
     *
     *    Random variates from the normal distribution.
     *
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double mu, double sigma, double randomValue)
    {
        if(
    /*!* #ifdef IEEE_754 /*4!*/
            Double.isInfinite(mu) || Double.isInfinite(sigma) ||
    /*!* #endif /*4!*/
    	sigma < 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN; 
        } else
        if (sigma == 0.0)
    	return mu;
        else
    	return mu + sigma * randomValue;
    }

  }
