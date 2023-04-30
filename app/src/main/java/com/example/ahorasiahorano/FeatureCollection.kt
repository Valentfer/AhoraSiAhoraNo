package com.example.ahorasiahorano

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text
import java.util.Date
@Root(name = "areaValue")
class areaValue {
    @field:Attribute(name = "uom")
    var uom: String? = null
    @Text
    var text = 0
}
@Root(name = "endLifespanVersion")
class endLifespanVersion {
    @field:Attribute(name = "nil")
    var nil = false
    @field:Attribute(name = "nilReason")
    var nilReason: String? = null
}
@Root(name = "posList")
class posList {
    @field:Attribute(name = "srsDimension")
    var srsDimension = 0

    @field:Attribute(name = "count")
    var count = 0

    @Text
    var text: String = ""
}

@Root(name = "LinearRing")
class LinearRing {
    @field:Element(name = "posList")
    var posList: posList? = null
}
@Root(name = "exterior")
class exterior {
    @field:Element(name = "LinearRing")
    var LinearRing: LinearRing? = null
}
@Root(name = "PolygonPatch")
class PolygonPatch {
    @field:Element(name = "exterior")
    var exterior: exterior? = null
}
@Root(name = "patches")
class patches {
    @field:Element(name = "PolygonPatch")
    var PolygonPatch: PolygonPatch? = null
}
@Root(name = "Surface")
class Surface {
    @field:Element(name = "patches")
    var patches: patches? = null
    @field:Attribute(name = "id")
    var id: String? = null
    @field:Attribute(name = "srsName")
    var srsName: String? = null
    @Text
    var text: String? = null
}
@Root(name = "surfaceMember")
class surfaceMember {
    @field:Element(name = "Surface")
    var Surface: Surface? = null
}
@Root(name = "MultiSurface")
class MultiSurface {
    @field:Element(name = "surfaceMember")
    var surfaceMember: surfaceMember? = null
    @field:Attribute(name = "id")
    var id: String? = null
    @field:Attribute(name = "srsName")
    var srsName: String? = null
    @Text
    var text: String? = null
}
@Root(name = "geometry")
class geometry {
    @field:Element(name = "MultiSurface")
    var MultiSurface: MultiSurface? = null
}
@Root(name = "Identifier")
class Identifier {
    @field:Element(name = "localId")
    var localId: String? = null
    @field:Element(name = "namespace")
    var namespace: String? = null
    @field:Attribute(name = "xmlns")
    var xmlns: String? = null
    @Text
    var text: String? = null
}
@Root(name = "inspireId")
class inspireId {
    @field:Element(name = "Identifier")
    var Identifier: Identifier? = null
}
@Root(name = "Point")
class Point {
    @field:Element(name = "pos")
    var pos: String? = null
    @field:Attribute(name = "id")
    var id: String? = null
    @field:Attribute(name = "srsName")
    var srsName: String? = null
    @Text
    var text: String? = null
}
@Root(name = "referencePoint")
class referencePoint {
    @field:Element(name = "Point")
    var Point: Point? = null
}
@Root(name = "CadastralParcel")
class CadastralParcel {
    @field:Element(name = "areaValue")
    var areaValue: areaValue? = null
    @field:Element(name = "beginLifespanVersion")
    var beginLifespanVersion: Date? = null
    @field:Element(name = "endLifespanVersion")
    var endLifespanVersion: endLifespanVersion? = null
    @field:Element(name = "geometry")
    var geometry: geometry? = null
    @field:Element(name = "inspireId")
    var inspireId: inspireId? = null
    @field:Element(name = "label")
    var label = 0
    @field:Element(name = "nationalCadastralReference")
    var nationalCadastralReference: String? = null
    @field:Element(name = "referencePoint")
    var referencePoint: referencePoint? = null
    @field:Attribute(name = "id")
    var id: String? = null
    @Text
    var text: String? = null
}
@Root(name = "member")
class member {
    @field:Element(name = "CadastralParcel")
    var CadastralParcel: CadastralParcel? = null
}
@Root(name = "FeatureCollection")
class FeatureCollection {
    @field:Element(name = "member")
    var member: member? = null
    @field:Attribute(name = "xsi")
    var xsi: String? = null
    @field:Attribute(name = "gml")
    var gml: String? = null
    @field:Attribute(name = "xlink")
    var xlink: String? = null
    @field:Attribute(name = "cp")
    var cp: String? = null
    @field:Attribute(name = "gmd")
    var gmd: String? = null
    @field:Attribute(name = "xlmns")
    var xmlns: String? = null
    @field:Attribute(name = "schemaLocation")
    var schemaLocation: String? = null
    @field:Attribute(name = "timeStamp")
    var timeStamp: Date? = null
    @field:Attribute(name = "numbreMatched")
    var numberMatched = 0
    @field:Attribute(name = "numberReturned")
    var numberReturned = 0
    @Text
    var text: String? = null
}