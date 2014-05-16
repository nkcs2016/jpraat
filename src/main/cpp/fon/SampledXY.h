#ifndef _SampledXY_h_
#define _SampledXY_h_
/* SampledXY.h
 *
 * Copyright (C) 1992-2011,2013 Paul Boersma
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

#include "Sampled.h"

#ifdef PRAAT_LIB
#include "praatlib.h"
#endif

#include "SampledXY_def.h"
oo_CLASS_CREATE (SampledXY, Sampled);

PRAAT_LIB_EXPORT double Matrix_columnToX (I, double column);   /* Return my x1 + (column - 1) * my dx.	 */

PRAAT_LIB_EXPORT double Matrix_rowToY (I, double row);   /* Return my y1 + (row - 1) * my dy. */

PRAAT_LIB_EXPORT double Matrix_xToColumn (I, double x);   /* Return (x - xmin) / my dx + 1. */

PRAAT_LIB_EXPORT long Matrix_xToLowColumn (I, double x);   /* Return floor (Matrix_xToColumn (me, x)). */

PRAAT_LIB_EXPORT long Matrix_xToHighColumn (I, double x);   /* Return ceil (Matrix_xToColumn (me, x)). */

PRAAT_LIB_EXPORT long Matrix_xToNearestColumn (I, double x);   /* Return floor (Matrix_xToColumn (me, x) + 0.5). */

PRAAT_LIB_EXPORT double Matrix_yToRow (I, double y);   /* Return (y - ymin) / my dy + 1. */

PRAAT_LIB_EXPORT long Matrix_yToLowRow (I, double y);   /* Return floor (Matrix_yToRow (me, y)). */

PRAAT_LIB_EXPORT long Matrix_yToHighRow (I, double x);   /* Return ceil (Matrix_yToRow (me, y)). */

PRAAT_LIB_EXPORT long Matrix_yToNearestRow (I, double y);   /* Return floor (Matrix_yToRow (me, y) + 0.5). */

PRAAT_LIB_EXPORT long Matrix_getWindowSamplesX (I, double xmin, double xmax, long *ixmin, long *ixmax);
/*
	Function:
		return the number of samples with x values in [xmin, xmax].
		Put the first of these samples in ixmin.
		Put the last of these samples in ixmax.
	Postconditions:
		*ixmin >= 1;
		*ixmax <= my nx;
		if (result != 0) *ixmin <= *ixmax; else *ixmin > *ixmax;
		if (result != 0) result == *ixmax - *ixmin + 1;
*/
PRAAT_LIB_EXPORT long Matrix_getWindowSamplesY (I, double ymin, double ymax, long *iymin, long *iymax);

#ifdef PRAAT_LIB
PRAAT_LIB_EXPORT double SampledXY_getYMin(SampledXY me);
PRAAT_LIB_EXPORT double SampledXY_getYMax(SampledXY me);
PRAAT_LIB_EXPORT long SampledXY_getNy(SampledXY me);
PRAAT_LIB_EXPORT double SampledXY_GetDy(SampledXY me);
PRAAT_LIB_EXPORT double SampledXY_getY1(SampledXY me);
#endif

/* End of file SampledXY.h */
#endif
