package com.example.ahorasiahorano

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

data class RfXml(
    val gmlpos: String?,
    val srsName: String?,
    val gmlid: String?
)

data class CpreferencePoint(
    val rfXml: RfXml?
)

data class CpendLifespanVersion(
    val xsinil: String?,
    val nilReason: String?
)
@Root(name = "GmlposList", strict = false)
data class GmlposList @JvmOverloads constructor(
    @field:Element(name = "content")
    @param:Element(name = "content")
    val srsDimension: String?,
    @field:Element(name = "content")
    @param:Element(name = "content")
    val count: String?,
    @field:Element(name = "content")
    @param:Element(name = "content")
    var content: String?
)

data class GmlLinearRing(
    val gmlposList: GmlposList?
)

data class Gmlexterior(
    val gmlLinearRing: GmlLinearRing?
)

data class GmlPolygonPatch(
    val gmlexterior: Gmlexterior?
)

data class Gmlpatches(
    val gmlPolygonPatch: GmlPolygonPatch?
)

data class GmlSurface(
    val srsName: String?,
    val gmlid: String?,
    val gmlpatches: Gmlpatches?
)

data class GmlsurfaceMember(
    val gmlSurface: GmlSurface?
)

data class GmlMultiSurface(
    val srsName: String?,
    val gmlsurfaceMember: GmlsurfaceMember?,
    val gmlid: String?
)

data class Cpgeometry(
    val gmlMultiSurface: GmlMultiSurface?
)

data class Identifier(
    val xmlns: String?,
    val namespace: String?,
    val localId: String?
)

data class CpinspireId(
    val Identifier: Identifier?
)

data class CpareaValue(
    val uom: String?,
    val content: String?
)

data class CpCadastralParcel(
    val cpreferencePoint: CpreferencePoint?,
    val cplabel: String?,
    val cpendLifespanVersion: CpendLifespanVersion?,
    val cpgeometry: Cpgeometry?,
    val cpbeginLifespanVersion: String?,
    val cpinspireId: CpinspireId?,
    val cpnationalCadastralReference: String?,
    val gmlid: String?,
    val cpareaValue: CpareaValue?
)

data class Member(
    val cpCadastralParcel: CpCadastralParcel?
)

data class FeatureCollection(
    val timeStamp: String?,
    val xmlnsgml: String?,
    val xmlns: String?,
    val numberReturned: String?,
    val xmlnsxlink: String?,
    val xsischemaLocation: String?,
    val member: FeatureCollection?,
    val xmlnscp: String?,
    val xmlnsxsi: String?,
    val xmlnsgmd: String?,
    val numberMatched: String?
)

data class Base(
    val FeatureCollection: FeatureCollection?
)

