Imports System.Xml.Xsl

Public Interface IDocumentWriter

    Sub generateTempXMLFile(ByVal OutputDir As String, ByVal CMTechPack As Boolean, ByVal Univ As Designer.IUniverse)

    Sub generateSDIFFile(ByVal OutputDir As String, ByVal xslt As XslTransform, ByVal Univ As Designer.IUniverse)

    Sub generateHTMLFile(ByVal OutputDir As String, ByVal xslt As XslTransform, ByVal Univ As Designer.IUniverse)

End Interface
