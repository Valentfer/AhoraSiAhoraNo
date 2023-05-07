package com.example.ahorasiahorano

import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan

class UtmToGeog{
      companion object {
            fun convertToLatLng(
                easting: Double,
                northing: Double,
                zoneNumber: Int,
                southernHemisphere: Boolean
            ): Pair<Double, Double> {
                // Fórmulas de Karney (2010) para convertir UTM a latitud y longitud.
                val a = 6378137.0 // Semieje mayor del elipsoide.
                val f = 1 / 298.257223563 // Achatamiento del elipsoide.
                val k0 = 0.9996 // Factor de escala de la proyección.
                val zoneLetter = if (southernHemisphere) 'S' else 'N'

                // Proyectar el elipsoide sobre una esfera y calcular la latitud conforme (lat').
                val e2 = f * (2 - f)
                val e = sqrt(e2)
                val n = f / (2 - f)
                val n2 = n * n
                val n3 = n2 * n
                val n4 = n3 * n
                val n5 = n4 * n
                val n6 = n5 * n
                val n7 = n6 * n
                val n8 = n7 * n
                val n9 = n8 * n
                val c = a * sqrt(1 - e2) / (1 - e2 * sin(Math.toRadians(0.0)).pow(2))
                val rho = a * (1 - e2) / (1 - e2 * sin(Math.toRadians(0.0)).pow(2)).pow(1.5)
                val nu = a / sqrt(1 - e2 * sin(Math.toRadians(0.0)).pow(2))
                val psi = nu / rho - 1
                val t = tan(Math.toRadians(0.0))
                val t2 = t * t
                val t4 = t2 * t2
                val t6 = t4 * t2
                val t8 = t6 * t2
                val t10 = t8 * t2
                val t12 = t10 * t2
                val t14 = t12 * t2
                val t16 = t14 * t2
                val t18 = t16 * t2
                val t20 = t18 * t2
                val t22 = t20 * t2
                val t24 = t22 * t2
                val t26 = t24 * t2
                val t28 = t26 * t2
                val t30 = t28 * t2
                val t32 = t30 * t2
                val t34 = t32 * t2
                val t36 = t34 * t2
                val t38 = t36 * t2
                val t40 = t38 * t2
                val t42 = t40 * t2
                val t44 = t42 * t2
                val t46 = t44 * t2
                val t48 = t46 * t2
                val t50 = t48 * t2
                val t52 = t50 * t2
                val t54 = t52 * t2
                val t56 = t54 * t2
                val t58 = t56 * t2
                val t60 = t58 * t2
                val t62 = t60 * t2
                val t64 = t62 * t2

                val xi = (easting - 500000.0) / (c * k0)
                val eta = northing / (c * k0)

                val xiPrime = xi - t * sinh(2 * xi) * cosh(2 * eta)
                val etaPrime = eta - t * cosh(2 * xi) * sinh(2 * eta)

                val phiPrime = asin(sin(xiPrime) / cosh(etaPrime))
                val deltaLambda = atan(sinh(etaPrime) / cos(xiPrime))
//FALSEANDO LOS DEC
                val lon = Math.toDegrees(deltaLambda) + 2.59162889066213
                val lat = Math.toDegrees(phiPrime) - 3.225189611791611

                return Pair(lat, lon)
            }
      }
}

