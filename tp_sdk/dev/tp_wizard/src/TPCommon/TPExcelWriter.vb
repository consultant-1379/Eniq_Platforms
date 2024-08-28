Option Strict Off

''
' TPUtilities class is a collection of support functions for technology package creation.
'
Public NotInheritable Class TPExcelWriter

    Public aConfig As Configuration.ConfigurationSettings
    Dim doc As Xml.XmlDataDocument
    Dim node As Xml.XmlNode
    Dim elem As Xml.XmlElement

    Public Function updateBuildNumber(ByRef tpDefinition As String, ByRef content As String, ByRef OutputDir As String) As Boolean

        Dim oldBuild As String
        Dim newBuild As String

        Dim ExcelApp = New Excel.Application
        ExcelApp.Visible = False
        If ExcelApp Is Nothing Then
            MsgBox("Couldn't start Excel")
            Exit Function
        End If
        Dim oldCI As System.Globalization.CultureInfo = System.Threading.Thread.CurrentThread.CurrentCulture
        System.Threading.Thread.CurrentThread.CurrentCulture = New System.Globalization.CultureInfo("en-US")
        ExcelApp.Workbooks.Open(tpDefinition)

        Dim Wksht = ExcelApp.ActiveWorkbook.Worksheets("Coversheet")
        Dim Rng = Wksht.Range("A:C")

        oldBuild = Rng.Cells(7, 3).Value
        If oldBuild = "" Then
            newBuild = System.Convert.ToString(1)
        Else
            newBuild = System.Convert.ToString(System.Convert.ToInt32(oldBuild) + 1)
        End If
        Rng.Cells(7, 3).Value = newBuild

        ExcelApp.ActiveWorkbook.Close(SaveChanges:=True)
        ExcelApp.Quit()

        System.Threading.Thread.CurrentThread.CurrentCulture = oldCI

        ExcelApp = Nothing

        writeXMLValue(OutputDir & "\build.xml", content, newBuild)
        Return True

    End Function

    Private Function readXMLValue(ByVal filePath As String, ByVal key As String) As String
        Dim xd As New Xml.XmlDocument

        createEmptyBuild(filePath)
        'load the xml file
        xd.Load(filePath)

        'query for a value
        Dim Node As Xml.XmlNode = xd.DocumentElement.SelectSingleNode("/Builds/" & key)

        'return the value or nothing if it doesn't exist
        If Not Node Is Nothing Then
            Return Node.Attributes.GetNamedItem("build").Value
        Else
            Return ""
        End If
    End Function

    Private Function writeXMLValue(ByVal filePath As String, ByVal key As String, ByVal NewValue As String) As Boolean
        Try
            Dim xd As New Xml.XmlDocument

            createEmptyBuild(filePath)
            'load the xml file
            xd.Load(filePath)

            'save value
            Dim Node As Xml.XmlNode = xd.DocumentElement.SelectSingleNode("/builds/" & key)
            If Not Node Is Nothing Then
                'key found, set the value
                Node.Attributes.GetNamedItem("build").Value = NewValue
                xd.Save(filePath)
                Return True
            Else
                'add new node
                Dim parentNode As Xml.XmlNode = xd.DocumentElement.SelectSingleNode("/builds")
                'create a new child element
                Dim childNode As Xml.XmlNode = xd.CreateNode(Xml.XmlNodeType.Element, key, "")
                parentNode.AppendChild(childNode)
                'create a new attribute to append to this element
                Dim attribute As Xml.XmlAttribute = xd.CreateAttribute("build")
                attribute.Value = NewValue
                childNode.Attributes.Append(attribute)
                Return True
            End If
        Catch ex As Exception
            Return False
        End Try
    End Function

    Private Sub createEmptyBuild(ByVal filePath As String)
        If Not IO.File.Exists(filePath) Then
            Dim fn As New IO.StreamWriter(IO.File.Open(filePath, IO.FileMode.Create))
            fn.WriteLine("<?xml version=""1.0"" encoding=""utf-8""?>")
            fn.WriteLine("<builds>")
            fn.WriteLine("    <!--   Build information go here.-->")
            fn.WriteLine("    <metadata build=""0""/>")
            fn.WriteLine("    <universe build=""0""/>")
            fn.WriteLine("    <reports build=""0""/>")
            fn.WriteLine("    <document build=""0""/>")
            fn.WriteLine("</builds>")
            fn.Close() 'all done
        End If
    End Sub

End Class
