package com.example.ahorasiahorano

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root


@Root(name = "gmlPoint", strict = false)
data class RfXml constructor(
    val gmlpos: String?,
    val srsName: String?,
    val gmlid: String?
)
@Root(name = "cpreferencePoint", strict = false)
data class CpreferencePoint constructor(
    val rfXml: RfXml?
)
@Root(name = "cpendLifespanVersion", strict = false)
data class CpendLifespanVersion constructor(
    val xsinil: String?,
    val nilReason: String?
)
@Root(name = "gmlposList", strict = false)
data class GmlposList constructor(

    val srsDimension: String?,
    val count: String?,
    //@field:Element(name = "content")
    @field:Element(data=false, name="content", required=true)
    var content: String?
)
@Root(name = "gmlLinearRing", strict = false)
data class GmlLinearRing constructor(
    @field:Element(name = "gmlposList")
    val gmlposList: GmlposList?
)
@Root(name = "gmlexterior", strict = false)
data class Gmlexterior constructor(
    @field:Element(name = "gmlLinearRing")
    val gmlLinearRing: GmlLinearRing?
)
@Root(name = "gmlPolygonPatch", strict = false)
data class GmlPolygonPatch constructor(
    @field:Element(name = "gmlexterior")
    val gmlexterior: Gmlexterior?
)
@Root(name = "gmlpatches", strict = false)
data class Gmlpatches constructor(
   @field:Element(name = "gmlPolygonPatch")
    val gmlPolygonPatch: GmlPolygonPatch?
)
@Root(name = "gmlSurface", strict = false)
data class GmlSurface constructor(
    val srsName: String?,
    val gmlid: String?,
    @field:Element(name = "gmlpatches")
    val gmlpatches: Gmlpatches?
)
@Root(name = "gmlSurfaceMember", strict = false)
data class GmlsurfaceMember constructor(
    @field:Element(name = "gmlSurface")
    val gmlSurface: GmlSurface?
)
@Root(name = "gmlMultiSurface", strict = false)
data class GmlMultiSurface constructor(
    val srsName: String?,
    @field:Element(name = "gmlSurfaceMember")
    val gmlsurfaceMember: GmlsurfaceMember?,
    val gmlid: String?
)
@Root(name = "cpgeometry", strict = false)
data class Cpgeometry constructor(
    @field:Element(name = "gmlMultiSurface")
    val gmlMultiSurface: GmlMultiSurface?
)
@Root(name = "identifier", strict = false)
data class Identifier constructor(
    val xmlns: String?,
    val namespace: String?,
    val localId: String?
)
@Root(name = "cpinspired", strict = false)
data class CpinspireId constructor(
    val Identifier: Identifier?
)
@Root(name = "cpareaValue", strict = false)
data class CpareaValue constructor(
    val uom: String?,
    val content: String?
)
@Root(name = "cpCadastralParcel", strict = false)
data class CpCadastralParcel constructor(
    val cpreferencePoint: CpreferencePoint?,
    val cplabel: String?,
    val cpendLifespanVersion: CpendLifespanVersion?,
    @field:Element(name = "cpgeometry")
    val cpgeometry: Cpgeometry?,
    val cpbeginLifespanVersion: String?,
    val cpinspireId: CpinspireId?,
    val cpnationalCadastralReference: String?,
    val gmlid: String?,
    val cpareaValue: CpareaValue?
)
@Root(name = "member", strict = false)
data class Member constructor(
    @field:Element(name = "cpCadastralParcel")
    val cpCadastralParcel: CpCadastralParcel?
)
@Root(name = "FeatureCollection", strict = false)
data class FeatureCollection constructor(
    val timeStamp: String?,
    val xmlnsgml: String?,
    val xmlns: String?,
    val numberReturned: String?,
    val xmlnsxlink: String?,
    val xsischemaLocation: String?,
    @field:Element(name = "member")
    val member: FeatureCollection?,
    val xmlnscp: String?,
    val xmlnsxsi: String?,
    val xmlnsgmd: String?,
    val numberMatched: String?
)
@Root(name = "base", strict = false)
data class Base constructor(
    val FeatureCollection: FeatureCollection?
)

