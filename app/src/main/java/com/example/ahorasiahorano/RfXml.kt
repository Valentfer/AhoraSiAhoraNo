package com.example.ahorasiahorano

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root


//@Root(name = "gmlPoint", strict = false)
data class RfXml constructor(
    val gmlpos: String?,
    val srsName: String?,
    val gmlid: String?
)
//@Root(name = "cpreferencePoint", strict = false)
data class CpreferencePoint constructor(
    val rfXml: RfXml?
)
//@Root(name = "cpendLifespanVersion", strict = false)
data class CpendLifespanVersion constructor(
    val xsinil: String?,
    val nilReason: String?
)
@Root(name = "gml:posList", strict = false)
@Namespace(reference = "http://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat=4770801VH4147S&amp;srsname=EPSG::25830")
data class GmlposList (

    //@get:Attribute(name = "srsDimension", required = false)
   // @field:Element(name = "srsDimension")
   // @param:Element(name = "srsDimension")
    @field:Attribute(name = "srsDimension", required = false)
    //@param:Attribute(name = "srsDimension")
    var srsDimension: String,
    //@get:Attribute(name = "count", required = false)
    //@field:Element(name = "count")
    //@param:Element(name = "count")
    @field:Attribute(name = "count", required = false)
    //@param:Attribute(name = "count")
    var count: String?,
    //@get:Element(name = "content", required = false)
    @field:Element(name = "content")
    //@param:Element(name = "content")
    //@field:Attribute(name = "content")
    //@param:Attribute(name = "content")
    @field:Namespace(reference = "http://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat=4770801VH4147S&amp;srsname=EPSG::25830")
    var content: String?
    /*
    @field:Path("FeatureCollection/member/cpCadastralParcel/cpgeometry/gmlMultiSurface/" +
            "gmlSurfaceMember/gmlSurface/gmlpatches/gmlPolygonPatch/gmlexterior/gmlLinearRing/gmlLinearRing")
    @param:Path("FeatureCollection/member/cpCadastralParcel/cpgeometry/gmlMultiSurface/" +
            "gmlSurfaceMember/gmlSurface/gmlpatches/gmlPolygonPatch/gmlexterior/gmlLinearRing/gmlLinearRing")
    var content: String?
    * */
)
@Root(strict = false, name = "gml:LinearRing")
data class GmlLinearRing (
    @field:Element(data=false, name="gml:posList", required=true, type = GmlposList::class)
    //var gmlposList: GmlposList?
    var gmlposList: GmlposList?
)

//@Root(name = "gml:exterior", strict = false)
data class Gmlexterior (
    val gmlLinearRing: GmlLinearRing?
)
//@Root(name = "gml:PolygonPatch", strict = false)
data class GmlPolygonPatch (
    val gmlexterior: Gmlexterior?
)
//@Root(name = "gml:patches", strict = false)
data class Gmlpatches (
    val gmlPolygonPatch: GmlPolygonPatch?
)
//@Root(name = "gml:Surface", strict = false)
data class GmlSurface (
    val srsName: String?,
    val gmlid: String?,
    val gmlpatches: Gmlpatches?
)
//@Root(name = "gml:SurfaceMember", strict = false)
data class GmlsurfaceMember (
    val gmlSurface: GmlSurface?
)
//@Root(name = "gml:MultiSurface", strict = false)
data class GmlMultiSurface (
    val srsName: String?,
    val gmlsurfaceMember: GmlsurfaceMember?,
    val gmlid: String?
)
//@Root(name = "cp:geometry", strict = false)
data class Cpgeometry (
    val gmlMultiSurface: GmlMultiSurface?
)
//@Root(name = "identifier", strict = false)
data class Identifier (
    val xmlns: String?,
    val namespace: String?,
    val localId: String?
)
//@Root(name = "cpinspired", strict = false)
data class CpinspireId constructor(
    val Identifier: Identifier?
)
//@Root(name = "cpareaValue", strict = false)
data class CpareaValue(
    val uom: String?,
    val content: String?
)
//@Root(name = "cp:CadastralParcel", strict = false)
data class CpCadastralParcel constructor(
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
//@Root(name = "member", strict = false)
data class Member (
    val cpCadastralParcel: CpCadastralParcel?
)
//@Root(name = "FeatureCollection", strict = false)
data class FeatureCollection (
    val timeStamp: String?,
    val xmlnsgml: String?,
    val xmlns: String?,
    val numberReturned: String?,
    val xmlnsxlink: String?,
    @field:Attribute(name = "xsi:schemaLocation")
    val xsischemaLocation: String?,
    val member: Member?,
    val xmlnscp: String?,
    val xmlnsxsi: String?,
    val xmlnsgmd: String?,
    val numberMatched: String?
)
//@Root(name = "base", strict = false)
data class Base constructor(
    val FeatureCollection: FeatureCollection?
)

