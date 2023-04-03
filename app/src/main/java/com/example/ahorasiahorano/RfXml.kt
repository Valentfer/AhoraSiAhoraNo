package com.example.ahorasiahorano

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

data class GmlposList(
    val srsDimension: String?,
    val count: String?,
    val content: String?
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
    val member: Member?,
    val xmlnscp: String?,
    val xmlnsxsi: String?,
    val xmlnsgmd: String?,
    val numberMatched: String?
)

data class Base(
    val FeatureCollection: FeatureCollection?
)